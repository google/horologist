/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.mediasample.data.service.offload

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.media3.common.Format
import androidx.media3.common.TrackSelectionParameters.AudioOffloadPreferences.DEFAULT
import androidx.media3.exoplayer.DecoderReuseEvaluation
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media3.logging.ErrorReporter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Coordination point for audio status, such as format, offload status.
 *
 * Enables Audio Offload when in the background, and also logs key events.
 */
@SuppressLint("UnsafeOptInUsageError")
@ExperimentalHorologistApi
public class AudioOffloadManager(
    private val errorReporter: ErrorReporter,
) {
    private val _offloadStatus = MutableStateFlow(
        AudioOffloadStatus(
            offloadSchedulingEnabled = false,
            sleepingForOffload = false,
            trackOffload = false,
            format = null,
            isPlaying = false,
            errors = listOf(),
            offloadTimes = OffloadTimes(),
            audioOffloadPreferences = DEFAULT,
        ),
    )
    public val offloadStatus: StateFlow<AudioOffloadStatus> = _offloadStatus.asStateFlow()

    public val audioOffloadListener: ExoPlayer.AudioOffloadListener =
        object : ExoPlayer.AudioOffloadListener {

            /**
             * Logged when the app is able to sleep.
             *
             * This listener should only run for development builds, since this additional work
             * negates the effect of offload.
             */
            override fun onSleepingForOffloadChanged(isSleepingForOffload: Boolean) {
                _offloadStatus.update {
                    // accumulate playback time for previous state
                    it.copy(
                        sleepingForOffload = isSleepingForOffload,
                        offloadTimes = it.offloadTimes.timesToNow(
                            it.sleepingForOffload,
                            it.isPlaying,
                        ),
                    )
                }

                errorReporter.logMessage("sleeping for offload $isSleepingForOffload")
            }

            override fun onOffloadedPlayback(isOffloadedPlayback: Boolean) {
                _offloadStatus.update {
                    it.copy(trackOffload = isOffloadedPlayback)
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.Q)
    private val analyticsListener: AnalyticsListener =
        object : AnalyticsListener {
            override fun onAudioInputFormatChanged(
                eventTime: AnalyticsListener.EventTime,
                format: Format,
                decoderReuseEvaluation: DecoderReuseEvaluation?,
            ) {
                _offloadStatus.update {
                    it.copy(format = format)
                }
            }

            override fun onIsPlayingChanged(
                eventTime: AnalyticsListener.EventTime,
                isPlaying: Boolean,
            ) {
                // accumulate playback time for previous state
                _offloadStatus.update {
                    it.copy(
                        isPlaying = isPlaying,
                        offloadTimes = it.offloadTimes.timesToNow(
                            sleepingForOffload = offloadStatus.value.sleepingForOffload,
                            updatedIsPlaying = isPlaying,
                        ),
                    )
                }
            }

            override fun onAudioUnderrun(
                eventTime: AnalyticsListener.EventTime,
                bufferSize: Int,
                bufferSizeMs: Long,
                elapsedSinceLastFeedMs: Long,
            ) {
                addError(eventTime, "Audio Underrun")
            }

            override fun onAudioSinkError(
                eventTime: AnalyticsListener.EventTime,
                audioSinkError: Exception,
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
    @RequiresApi(29)
    public suspend fun connect(exoPlayer: ExoPlayer) {
        _offloadStatus.value = AudioOffloadStatus(
            offloadSchedulingEnabled = false,
            sleepingForOffload = exoPlayer.isSleepingForOffload,
            trackOffload = false,
            format = exoPlayer.audioFormat,
            isPlaying = exoPlayer.isPlaying,
            errors = listOf(),
            offloadTimes = OffloadTimes(),
            audioOffloadPreferences = exoPlayer.trackSelectionParameters.audioOffloadPreferences,
        )

        exoPlayer.addAudioOffloadListener(audioOffloadListener)
        exoPlayer.addAnalyticsListener(analyticsListener)
    }

    @RequiresApi(29)
    public suspend fun printDebugLogsLoop() {
        while (true) {
            printDebugLogs()
            delay(10000)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    internal fun printDebugLogs() {
        val status = _offloadStatus.value
        val times = status.updateToNow()

        errorReporter.logMessage(
            "Offload State: " +
                "sleeping: ${status.sleepingForOffload} " +
                "audioTrackOffload: ${status.trackOffloadDescription()} " +
                "format: ${status.format?.shortDescription} " +
                "times: ${times.shortDescription} " +
                "audioOffloadPreferences: ${status.audioOffloadPreferences} ",
            category = ErrorReporter.Category.Playback,
        )
    }
}
