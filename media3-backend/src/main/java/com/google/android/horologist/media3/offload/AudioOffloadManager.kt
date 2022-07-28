/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.horologist.media3.offload

import android.media.AudioManager
import android.os.Build
import androidx.media3.common.Format
import androidx.media3.exoplayer.DecoderReuseEvaluation
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.audio.AudioSink
import com.google.android.horologist.media3.ExperimentalHorologistMedia3BackendApi
import com.google.android.horologist.media3.logging.ErrorReporter
import com.google.android.horologist.media3.util.shortDescription
import com.google.android.horologist.media3.util.toAudioFormat
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Coordination point for audio status, such as format, offload status.
 *
 * Enables Audio Offload when in the background, and also logs key events.
 */
@ExperimentalHorologistMedia3BackendApi
public class AudioOffloadManager(
    private val errorReporter: ErrorReporter,
    private val audioSink: AudioSink,
    private val audioOffloadStrategyFlow: Flow<AudioOffloadStrategy> =
        flowOf(AudioOffloadStrategy.Never)
) {
    private val _offloadSchedulingEnabled = MutableStateFlow(false)
    public val offloadSchedulingEnabled: StateFlow<Boolean> =
        _offloadSchedulingEnabled.asStateFlow()

    private val _sleepingForOffload = MutableStateFlow(false)
    public val sleepingForOffload: StateFlow<Boolean> = _sleepingForOffload.asStateFlow()

    private val _format = MutableStateFlow<Format?>(null)
    public val format: StateFlow<Format?> = _format.asStateFlow()

    private val _times = MutableStateFlow(OffloadTimes(0, 0))
    public val times: StateFlow<OffloadTimes> = _times.asStateFlow()

    private val _playing = MutableStateFlow(false)
    public val playing: StateFlow<Boolean> = _playing.asStateFlow()

    private val _errors = MutableStateFlow(listOf<AudioError>())
    public val errors: StateFlow<List<AudioError>> = _errors.asStateFlow()

    private fun audioOffloadListener(exoPlayer: ExoPlayer): ExoPlayer.AudioOffloadListener {
        _sleepingForOffload.value = exoPlayer.experimentalIsSleepingForOffload()

        return object : ExoPlayer.AudioOffloadListener {
            /**
             * Logged when the application changes the state of offload scheduling enabled,
             * this is typically only active when the app is in the background.
             */
            override fun onExperimentalOffloadSchedulingEnabledChanged(offloadSchedulingEnabled: Boolean) {
                _offloadSchedulingEnabled.value = offloadSchedulingEnabled

                errorReporter.logMessage("offload scheduling enabled $offloadSchedulingEnabled")
            }

            /**
             * Logged when the app is able to sleep
             *
             * This listener should only run for development builds, since this additional work
             * negates the effect of offload.
             */
            override fun onExperimentalSleepingForOffloadChanged(sleepingForOffload: Boolean) {
                val previousSleepingForOffload = _sleepingForOffload.value

                _sleepingForOffload.value = sleepingForOffload

                // accumulate playback time for previous state
                _times.value = times.value.timesToNow(previousSleepingForOffload, playing.value)

                errorReporter.logMessage("sleeping for offload $sleepingForOffload")
            }
        }
    }

    private fun analyticsListener(exoPlayer: ExoPlayer): AnalyticsListener {
        _format.value = exoPlayer.audioFormat

        return object : AnalyticsListener {
            override fun onAudioInputFormatChanged(
                eventTime: AnalyticsListener.EventTime,
                format: Format,
                decoderReuseEvaluation: DecoderReuseEvaluation?
            ) {
                _format.value = format
            }

            override fun onIsPlayingChanged(
                eventTime: AnalyticsListener.EventTime, isPlaying: Boolean
            ) {
                // accumulate playback time for previous state
                _times.value = times.value.timesToNow(sleepingForOffload.value, isPlaying)
                _playing.value = isPlaying
            }

            override fun onAudioUnderrun(
                eventTime: AnalyticsListener.EventTime,
                bufferSize: Int,
                bufferSizeMs: Long,
                elapsedSinceLastFeedMs: Long
            ) {
                addError(eventTime, "Audio Underrun")
            }

            override fun onAudioSinkError(
                eventTime: AnalyticsListener.EventTime, audioSinkError: Exception
            ) {
                addError(eventTime, "Audio Sink Error: " + audioSinkError.message)
            }
        }
    }

    private fun addError(eventTime: AnalyticsListener.EventTime?, message: String) {
        _errors.update {
            it + AudioError(System.currentTimeMillis(), message, eventTime)
        }
    }

    /**
     * Connect the AudioOffloadManager to the ExoPlayer instance for both listening to offload
     * state and activating it based on App foreground state.
     */
    public suspend fun connect(exoPlayer: ExoPlayer) {
        suspendCancellableCoroutine<Unit> {
            _sleepingForOffload.value = exoPlayer.experimentalIsSleepingForOffload()
            errorReporter.logMessage("initial sleeping for offload ${sleepingForOffload.value}")

            val audioOffloadListener = audioOffloadListener(exoPlayer)
            val analyticsListener = analyticsListener(exoPlayer)

            delay(1000)

            audioOffloadStrategyFlow.collect {
                1
            }

            exoPlayer.addAudioOffloadListener(audioOffloadListener)
            exoPlayer.addAnalyticsListener(analyticsListener)

            it.invokeOnCancellation {
                exoPlayer.removeAudioOffloadListener(audioOffloadListener)
                exoPlayer.removeAnalyticsListener(analyticsListener)
            }
        }
    }

    public fun snapOffloadTimes(): OffloadTimes = times.value.timesToNow(
        sleepingForOffload.value, playing.value
    )

    public fun printDebugInfo() {
        val sleeping = sleepingForOffload.value
        val updatedTimes = snapOffloadTimes()
        errorReporter.logMessage(
            "Offload State: " + "sleeping: $sleeping " + "format: ${format.value?.shortDescription} " + "times: ${updatedTimes.shortDescription} " + "strategy: ${_audioOffloadStrategy.value.statusFlow.value} ",
            category = ErrorReporter.Category.Playback
        )
    }

    public fun isFormatSupported(): Boolean? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val format = format.value?.toAudioFormat()

            if (format != null) {
                val audioAttributes = audioSink.audioAttributes?.audioAttributesV21?.audioAttributes

                if (audioAttributes != null) {
                    return AudioManager.isOffloadedPlaybackSupported(format, audioAttributes)
                }
            }

            // No format to check
            return null
        }

        // Not supported before 30
        return false
    }

    fun disconnect(exoPlayer: ExoPlayer) {

    }
}
