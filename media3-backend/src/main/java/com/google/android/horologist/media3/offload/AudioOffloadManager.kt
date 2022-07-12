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

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.media3.common.Format
import androidx.media3.exoplayer.DecoderReuseEvaluation
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import com.google.android.horologist.media3.ExperimentalHorologistMedia3BackendApi
import com.google.android.horologist.media3.logging.ErrorReporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Coordination point for audio status, such as format, offload status.
 *
 * Enables Audio Offload when in the background, and also logs key events.
 */
@ExperimentalHorologistMedia3BackendApi
public class AudioOffloadManager(
    private val errorReporter: ErrorReporter,
) {
    private var _foreground = MutableStateFlow(false)
    public val foreground: StateFlow<Boolean> = _foreground.asStateFlow()

    private val _offloadSchedulingEnabled = MutableStateFlow(false)
    public val offloadSchedulingEnabled: StateFlow<Boolean> =
        _offloadSchedulingEnabled.asStateFlow()

    private val _sleepingForOffload = MutableStateFlow(false)
    public val sleepingForOffload: StateFlow<Boolean> = _sleepingForOffload.asStateFlow()

    private val _format = MutableStateFlow<Format?>(null)
    public val format: StateFlow<Format?> = _format.asStateFlow()

    private val _times = MutableStateFlow(OffloadTimes(0, 0))
    public val times: StateFlow<OffloadTimes> = _times.asStateFlow()

    private fun audioOffloadListener(exoPlayer: ExoPlayer): ExoPlayer.AudioOffloadListener {
        _sleepingForOffload.value = exoPlayer.experimentalIsSleepingForOffload()

        return object : ExoPlayer.AudioOffloadListener {
            /**
             * Logged when the application changes the state of offload scheduling enabled,
             * this is typically only active when the app is in the background.
             */
            override fun onExperimentalOffloadSchedulingEnabledChanged(offloadSchedulingEnabled: Boolean) {
                _offloadSchedulingEnabled.value = offloadSchedulingEnabled

                // accumulate playback time for previous state
                _times.value = times.value.timesToNow(!offloadSchedulingEnabled)

                errorReporter.logMessage("offload scheduling enabled $offloadSchedulingEnabled")
            }

            /**
             * Logged when the app is able to sleep
             *
             * This listener should only run for development builds, since this additional work
             * negates the effect of offload.
             */
            override fun onExperimentalSleepingForOffloadChanged(sleepingForOffload: Boolean) {
                _sleepingForOffload.value = sleepingForOffload

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

//            override fun onAudioSinkError(
//                eventTime: AnalyticsListener.EventTime,
//                audioSinkError: Exception
//            ) {
//                val cause = audioSinkError.cause
//                errorReporter.logMessage("audioSinkError $audioSinkError $cause")
//                audioSinkError.printStackTrace()
//            }
        }
    }

    private fun foregroundListener(exoPlayer: ExoPlayer): LifecycleObserver {
        return object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                _foreground.value = true
                exoPlayer.experimentalSetOffloadSchedulingEnabled(false)

                errorReporter.logMessage("app foregrounded")
            }

            override fun onPause(owner: LifecycleOwner) {
                _foreground.value = false
                exoPlayer.experimentalSetOffloadSchedulingEnabled(true)

                errorReporter.logMessage("app backgrounded")
            }
        }
    }

    /**
     * Connect the AudioOffloadManager to the ExoPlayer instance for both listening to offload
     * state and activating it based on App foreground state.
     */
    public fun connect(exoPlayer: ExoPlayer) {
        // Disabled when at least one activity is foregrounded
        val lifecycleOwner = ProcessLifecycleOwner.get()
        val foreground = lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
        _foreground.value = foreground

        _offloadSchedulingEnabled.value = !foreground
        exoPlayer.experimentalSetOffloadSchedulingEnabled(!foreground)

        _sleepingForOffload.value = exoPlayer.experimentalIsSleepingForOffload()
        errorReporter.logMessage("initial sleeping for offload ${sleepingForOffload.value}")

        lifecycleOwner.lifecycle.addObserver(foregroundListener(exoPlayer))

        exoPlayer.addAudioOffloadListener(audioOffloadListener(exoPlayer))
        exoPlayer.addAnalyticsListener(analyticsListener(exoPlayer))
    }

    public fun snapOffloadTimes(): OffloadTimes = times.value.timesToNow(sleepingForOffload.value)

    public fun printDebugInfo() {
        val sleeping = sleepingForOffload.value
        val updatedTimes = times.value.timesToNow(sleeping)
        errorReporter.logMessage(
            "Offload State: " +
                "foreground: ${foreground.value} " +
                "sleeping: $sleeping format: ${format.value} " +
                "times: $updatedTimes",
            category = ErrorReporter.Category.Playback
        )
    }
}
