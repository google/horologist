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

package com.google.android.horologist.mediasample.di

import android.annotation.SuppressLint
import androidx.media3.exoplayer.ExoPlayer.AudioOffloadListener

@SuppressLint("UnsafeOptInUsageError")
class AudioOffloadListenerList : AudioOffloadListener {
    private val audioOffloadListeners = mutableListOf<AudioOffloadListener>()

    fun addListener(audioOffloadListener: AudioOffloadListener) {
        synchronized(audioOffloadListeners) {
            audioOffloadListeners.add(audioOffloadListener)
        }
    }

    fun removeListener(audioOffloadListener: AudioOffloadListener) {
        synchronized(audioOffloadListeners) {
            audioOffloadListeners.remove(audioOffloadListener)
        }
    }

    override fun onExperimentalOffloadSchedulingEnabledChanged(offloadSchedulingEnabled: Boolean) {
        synchronized(audioOffloadListeners) {
            for (it in audioOffloadListeners) {
                it.onExperimentalOffloadSchedulingEnabledChanged(offloadSchedulingEnabled)
            }
        }
    }

    override fun onExperimentalSleepingForOffloadChanged(sleepingForOffload: Boolean) {
        synchronized(audioOffloadListeners) {
            for (it in audioOffloadListeners) {
                it.onExperimentalSleepingForOffloadChanged(sleepingForOffload)
            }
        }
    }

    override fun onExperimentalOffloadedPlayback(offloadedPlayback: Boolean) {
        synchronized(audioOffloadListeners) {
            for (it in audioOffloadListeners) {
                it.onExperimentalOffloadedPlayback(offloadedPlayback)
            }
        }
    }
}
