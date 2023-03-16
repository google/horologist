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

package com.google.android.horologist.audio.ui

import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.AudioOutputRepository
import com.google.android.horologist.audio.SystemAudioRepository
import com.google.android.horologist.audio.VolumeRepository
import com.google.android.horologist.audio.VolumeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update


/**
 * ViewModel for a Volume Control Screen.
 *
 * Holds the state of both Volume ([volumeState]) and AudioOutput (audioOutput).
 *
 * Volume changes can be made via [increaseVolume] and [decreaseVolume].
 *
 * See [AudioManager.setStreamVolume]
 * See [AudioManager.STREAM_MUSIC]
 */
@ExperimentalHorologistAudioUiApi
public open class VolumeViewModel(
    internal val volumeRepository: VolumeRepository,
    internal val audioOutputRepository: AudioOutputRepository,
    private val onCleared: () -> Unit = {},
    private val vibrator: Vibrator
) : ViewModel() {
    public val volumeState: StateFlow<VolumeState> = volumeRepository.volumeState

    // Keep a timestamp that always updates when we attempt ot update volume (even when it's already
    // at min/max)
    private var timestamp = MutableStateFlow<Long>(System.currentTimeMillis())
    public val volumeUiState: StateFlow<VolumeUiState> =
        combine(volumeRepository.volumeState, timestamp) { it, timestamp ->
            VolumeUiState(timestamp = timestamp, volumeState = it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = VolumeUiState()
        )
    public val audioOutput: StateFlow<AudioOutput> = audioOutputRepository.audioOutput
    public fun increaseVolumeWithHaptics() {
        increaseVolume()
        if (!volumeState.value.isMax) {
            performHaptics()
        }
    }

    public fun decreaseVolumeWithHaptics() {
        decreaseVolume()
        if (volumeState.value.current != 0) {
            performHaptics()
        }
    }

    public fun increaseVolume() {
//        clonedState.update { volumeRepository.volumeState.value.copy(updating = true) }
        Log.d("VolumeTest", "increaseVolume")
        if (volumeState.value.isMax) {
            timestamp.update { System.currentTimeMillis() }
        }
        volumeRepository.increaseVolume()
    }

    public fun decreaseVolume() {
//        clonedState.update { volumeRepository.volumeState.value.copy(updating = true) }
        Log.d("VolumeTest", "decreaseVolume")
        if (volumeState.value.current == 0) {
            timestamp.update { System.currentTimeMillis() }
        }
        volumeRepository.decreaseVolume()
    }

    public fun launchOutputSelection() {
        audioOutputRepository.launchOutputSelection(closeOnConnect = false)
    }

    override fun onCleared() {
        onCleared.invoke()
    }

    private fun performHaptics() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
            vibrator.vibrate(effect)
        } else {
            notSupported()
        }
    }

    private fun notSupported() {
        Log.i(TAG, "Effect not supported")
    }

    public fun onVolumeChangeByScroll(pixels: Float) {
        when {
            pixels > 0 -> increaseVolumeWithHaptics()
            pixels < 0 -> decreaseVolumeWithHaptics()
        }
    }

    @ExperimentalHorologistAudioUiApi
    public companion object {
        private const val TAG = "VolumeViewModel"

        public val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY]!!

                val audioRepository = SystemAudioRepository.fromContext(application)
                val vibrator: Vibrator = application.getSystemService(Vibrator::class.java)

                VolumeViewModel(audioRepository, audioRepository, onCleared = {
                    audioRepository.close()
                }, vibrator)
            }
        }
    }

    public data class VolumeUiState(
        var timestamp: Long = System.currentTimeMillis(),
        val current: Int = 0,
        val max: Int = 0,
        val isMax: Boolean = false,
    ) {
        public constructor(volumeState: VolumeUiState): this(
            timestamp = System.currentTimeMillis(),
            current = volumeState.current,
            max = volumeState.max,
            isMax = volumeState.isMax)

        public constructor(timestamp: Long, volumeState: VolumeState): this(
            timestamp = timestamp,
            current = volumeState.current,
            max = volumeState.max,
            isMax = volumeState.isMax)
    }
}
