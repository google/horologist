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
import androidx.compose.runtime.mutableStateOf
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
import com.google.android.horologist.audio.clone
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
    private var clonedState = MutableStateFlow<VolumeState?>(null)
    public val volumeState: StateFlow<VolumeState> =
        combine(volumeRepository.volumeState, clonedState) { it, clonedState ->
            clonedState ?: it
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = VolumeState(0, 0)
        )
    public val audioOutput: StateFlow<AudioOutput> = audioOutputRepository.audioOutput


    public fun increaseVolumeWithHaptics() {
        increaseVolume()
        performHaptics()
    }

    public fun decreaseVolumeWithHaptics() {
        decreaseVolume()
        performHaptics()
    }

    public fun increaseVolume() {
        if (volumeState.value.isMax) {
            clonedState.update { volumeState.value.clone() }
        } else {
            clonedState.update { null }
            volumeRepository.increaseVolume()
        }
    }

    public fun decreaseVolume() {
        if (volumeState.value.current == 0) {
            clonedState.update { volumeState.value.clone() }
        } else {
            clonedState.update { null }
            volumeRepository.decreaseVolume()
        }
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
}
