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

import android.annotation.SuppressLint
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.media3.exoplayer.ExoPlayer
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media3.logging.ErrorReporter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@SuppressLint("UnsafeOptInUsageError")
@ExperimentalHorologistApi
/**
 * Background Strategy for enabling or disabling the audio offload mode,
 * that is only enabled when backgrounded.
 */
public object BackgroundAudioOffloadStrategy : AudioOffloadStrategy {
    override val offloadEnabled: Boolean
        get() = true

    override fun applyIndefinitely(
        exoPlayer: ExoPlayer,
        errorReporter: ErrorReporter,
    ): Flow<String> = callbackFlow {
        // Disabled when at least one activity is foregrounded
        val lifecycleOwner = ProcessLifecycleOwner.get()
        val foreground = lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)

        trySend("Background: $foreground")
        exoPlayer.experimentalSetOffloadSchedulingEnabled(!foreground)

        val listener = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                trySend("Background: false")
                exoPlayer.experimentalSetOffloadSchedulingEnabled(false)

                errorReporter.logMessage("app foregrounded")
            }

            override fun onPause(owner: LifecycleOwner) {
                trySend("Background: true")
                exoPlayer.experimentalSetOffloadSchedulingEnabled(true)

                errorReporter.logMessage("app backgrounded")
            }
        }

        lifecycleOwner.lifecycle.addObserver(listener)

        awaitClose {
            lifecycleOwner.lifecycle.removeObserver(listener)
        }
    }

    override fun toString(): String = "Background"
}
