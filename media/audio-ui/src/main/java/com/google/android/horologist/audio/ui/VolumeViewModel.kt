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
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.AudioOutputRepository
import com.google.android.horologist.audio.SystemAudioRepository
import com.google.android.horologist.audio.VolumeRepository
import com.google.android.horologist.audio.ui.mapper.VolumeUiStateMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn

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
@ExperimentalHorologistApi
public open class VolumeViewModel(
    internal val volumeRepository: VolumeRepository,
    internal val audioOutputRepository: AudioOutputRepository,
    private val onCleared: () -> Unit = {},
    private val vibrator: Vibrator
) : ViewModel() {
    private val userActionEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    public val volumeUiState: StateFlow<VolumeUiState> =
        volumeRepository.volumeState.map(VolumeUiStateMapper::map).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = VolumeUiState()
        )

    public val displayIndicatorEvents: Flow<Unit> = merge(userActionEvents, volumeUiState.drop(1)).map { }

    public val audioOutput: StateFlow<AudioOutput> = audioOutputRepository.audioOutput
    public fun increaseVolumeWithHaptics() {
        increaseVolume()
        if (!volumeUiState.value.isMax) {
            performHaptics()
        }
    }

    public fun decreaseVolumeWithHaptics() {
        decreaseVolume()
        if (!volumeUiState.value.isMin) {
            performHaptics()
        }
    }

    public fun increaseVolume() {
        this.userActionEvents.tryEmit(Unit)
        volumeRepository.increaseVolume()
    }

    public fun decreaseVolume() {
        this.userActionEvents.tryEmit(Unit)
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

    public fun setVolume(volume: Int) {
        this.userActionEvents.tryEmit(Unit)
        if (volume != volumeRepository.volumeState.value.current) {
            volumeRepository.setVolume(volume)
        }
    }

    @ExperimentalHorologistApi
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
