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
import androidx.media3.exoplayer.ExoPlayer
import com.google.android.horologist.media3.ExperimentalHorologistMedia3BackendApi
import com.google.android.horologist.media3.logging.ErrorReporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@ExperimentalHorologistMedia3BackendApi
/**
 * Background Strategy for enabling or disabling the audio offload mode,
 * that is only enabled when backgrounded.
 */
object BackgroundAudioOffloadStrategy: AudioOffloadStrategy {
    lateinit var listener: LifecycleObserver

    override fun close() {
        if (this::listener.isInitialized) {
            val lifecycleOwner = ProcessLifecycleOwner.get()
            lifecycleOwner.lifecycle.removeObserver(listener)
        }
        _status.value = "Foreground: Closed"
    }

    fun createListener(exoPlayer: ExoPlayer) = object : DefaultLifecycleObserver {
        override fun onResume(owner: LifecycleOwner) {
            _status.value = "Foreground: true"
            exoPlayer.experimentalSetOffloadSchedulingEnabled(false)

            errorReporter.logMessage("app foregrounded")
        }

        override fun onPause(owner: LifecycleOwner) {
            _status.value = "Foreground: false"
            exoPlayer.experimentalSetOffloadSchedulingEnabled(true)

            errorReporter.logMessage("app backgrounded")
        }
    }

    override suspend fun connect(exoPlayer: ExoPlayer, errorReporter: ErrorReporter) {
        // Disabled when at least one activity is foregrounded
        val lifecycleOwner = ProcessLifecycleOwner.get()
        val foreground = lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
        _status.value = "Foreground: $foreground"

        exoPlayer.experimentalSetOffloadSchedulingEnabled(!foreground)

        listener = createListener(exoPlayer)
        lifecycleOwner.lifecycle.addObserver(listener)
    }
}