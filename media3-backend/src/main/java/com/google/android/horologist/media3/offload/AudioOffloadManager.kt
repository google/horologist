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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

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
        flowOf(BackgroundAudioOffloadStrategy)
) {
    private val _offloadStatus = MutableStateFlow(
        AudioOffloadStatus(
            offloadSchedulingEnabled = false,
            sleepingForOffload = false,
            format = null,
            isPlaying = false,
            errors = listOf(),
            offloadTimes = OffloadTimes(),
            strategyStatus = null
        )
    )
    public val offloadStatus: StateFlow<AudioOffloadStatus> = _offloadStatus.asStateFlow()

    private val audioOffloadListener: ExoPlayer.AudioOffloadListener =
        object : ExoPlayer.AudioOffloadListener {
            /**
             * Logged when the application changes the state of offload scheduling enabled,
             * this is typically only active when the app is in the background.
             */
            override fun onExperimentalOffloadSchedulingEnabledChanged(offloadSchedulingEnabled: Boolean) {
                _offloadStatus.update {
                    it.copy(offloadSchedulingEnabled = offloadSchedulingEnabled)
                }

                errorReporter.logMessage("offload scheduling enabled $offloadSchedulingEnabled")
            }

            /**
             * Logged when the app is able to sleep
             *
             * This listener should only run for development builds, since this additional work
             * negates the effect of offload.
             */
            override fun onExperimentalSleepingForOffloadChanged(sleepingForOffload: Boolean) {
                _offloadStatus.update {
                    // accumulate playback time for previous state
                    it.copy(
                        sleepingForOffload = sleepingForOffload,
                        offloadTimes = it.offloadTimes.timesToNow(
                            it.sleepingForOffload,
                            it.isPlaying
                        )
                    )
                }

                errorReporter.logMessage("sleeping for offload $sleepingForOffload")
            }
        }

    private val analyticsListener: AnalyticsListener =
        object : AnalyticsListener {
            override fun onAudioInputFormatChanged(
                eventTime: AnalyticsListener.EventTime,
                format: Format,
                decoderReuseEvaluation: DecoderReuseEvaluation?
            ) {
                _offloadStatus.update {
                    it.copy(format = format)
                }
            }

            override fun onIsPlayingChanged(
                eventTime: AnalyticsListener.EventTime,
                isPlaying: Boolean
            ) {
                // accumulate playback time for previous state
                _offloadStatus.update {
                    it.copy(
                        isPlaying = isPlaying,
                        offloadTimes = it.offloadTimes.timesToNow(
                            sleepingForOffload = offloadStatus.value.sleepingForOffload,
                            updatedIsPlaying = isPlaying
                        )
                    )
                }
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
                eventTime: AnalyticsListener.EventTime,
                audioSinkError: Exception
            ) {
                addError(eventTime, "Audio Sink Error: " + audioSinkError.message)
            }
        }

    private fun addError(eventTime: AnalyticsListener.EventTime?, message: String) {
        _offloadStatus.update {
            it.copy(errors = it.errors + AudioError(System.currentTimeMillis(), message, eventTime))
        }
    }

    /**
     * Connect the AudioOffloadManager to the ExoPlayer instance for both listening to offload
     * state and activating it based on App foreground state.
     */
    public suspend fun connect(exoPlayer: ExoPlayer): Unit = withContext(Dispatchers.Main) {
        _offloadStatus.value = AudioOffloadStatus(
            offloadSchedulingEnabled = false,
            sleepingForOffload = exoPlayer.experimentalIsSleepingForOffload(),
            format = exoPlayer.audioFormat,
            isPlaying = exoPlayer.isPlaying,
            errors = listOf(),
            offloadTimes = OffloadTimes(),
            strategyStatus = null
        )

        exoPlayer.addAudioOffloadListener(audioOffloadListener)
        exoPlayer.addAnalyticsListener(analyticsListener)

        try {
            audioOffloadStrategyFlow.collectLatest { strategy ->
                _offloadStatus.update {
                    it.copy(strategyStatus = null)
                }

                strategy.applyIndefinitely(exoPlayer, errorReporter).collect { strategyStatus ->
                    _offloadStatus.update {
                        it.copy(strategyStatus = strategyStatus)
                    }
                }
                awaitCancellation()
            }
        } finally {
            exoPlayer.removeAudioOffloadListener(audioOffloadListener)
            exoPlayer.removeAnalyticsListener(analyticsListener)
        }
    }

    public suspend fun printDebugLogsLoop() {
        while (true) {
            val status = _offloadStatus.value
            val times = status.updateToNow()
            errorReporter.logMessage(
                "Offload State: " +
                    "sleeping: ${status.sleepingForOffload} " +
                    "format: ${status.format?.shortDescription} " +
                    "times: ${times.shortDescription} " +
                    "strategyStatus: ${status.strategyStatus} ",
                category = ErrorReporter.Category.Playback
            )
            delay(10000)
        }
    }

    public fun isFormatSupported(): Boolean? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val format = _offloadStatus.value.format?.toAudioFormat()

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
}
