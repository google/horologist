/*
 * Copyright 2021 The Android Open Source Project
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

package com.google.android.horologist.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Build
import androidx.activity.ComponentActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

public class VolumeRepository(
    private val audioManager: AudioManager,
    private val application: Context
) : AutoCloseable {
    private val _volume = MutableStateFlow(readVolumeState())

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            refresh()
        }
    }

    init {
        val filter = IntentFilter(VOLUME_ACTION)
        application.registerReceiver(receiver, filter)
    }

    public val volumeState: StateFlow<VolumeState>
        get() = _volume

    private fun refresh() {
        _volume.value = readVolumeState()
    }

    private fun readVolumeState(): VolumeState {
        val streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val minVolume =
            if (Build.VERSION.SDK_INT >= 28) audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC) else 0
        val maxValue = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val isMuted = audioManager.isStreamMute(AudioManager.STREAM_MUSIC)

        return VolumeState(streamVolume, minVolume, maxValue, isMuted)
    }

    public fun increaseVolume() {
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_RAISE,
            0
        )
        refresh()
    }

    public fun decreaseVolume() {
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_LOWER,
            0
        )
        refresh()
    }

    override fun close() {
        application.unregisterReceiver(receiver)
    }

    companion object {
        public fun fromContext(application: Context): VolumeRepository {
            val audioManager =
                application.getSystemService(ComponentActivity.AUDIO_SERVICE) as AudioManager

            return VolumeRepository(audioManager, application)
        }

        internal const val VOLUME_ACTION = "android.media.VOLUME_CHANGED_ACTION"
    }
}
