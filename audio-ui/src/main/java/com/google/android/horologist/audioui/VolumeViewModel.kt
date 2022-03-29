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

package com.google.android.horologist.audioui

import android.media.AudioManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.AudioOutputRepository
import com.google.android.horologist.audio.SystemAudioOutputRepository
import com.google.android.horologist.audio.SystemVolumeRepository
import com.google.android.horologist.audio.VolumeRepository
import com.google.android.horologist.audio.VolumeState
import kotlinx.coroutines.flow.StateFlow

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
public open class VolumeViewModel(
    internal val volumeRepository: VolumeRepository,
    internal val audioOutputRepository: AudioOutputRepository
) : ViewModel() {
    public val volumeState: StateFlow<VolumeState> = volumeRepository.volumeState

    public val audioOutput: StateFlow<AudioOutput> = audioOutputRepository.audioOutput

    public val volumeScrollableState: VolumeScrollableState =
        VolumeScrollableState(volumeRepository)

    public fun increaseVolume() {
        volumeRepository.increaseVolume()
    }

    public fun decreaseVolume() {
        volumeRepository.decreaseVolume()
    }

    public fun launchOutputSelection() {
        audioOutputRepository.launchOutputSelection(closeOnConnect = false)
    }

    override fun onCleared() {
        volumeRepository.close()
        audioOutputRepository.close()
    }

    public object Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            check(modelClass == VolumeViewModel::class.java)

            val application = extras[APPLICATION_KEY]!!

            val volumeRepository = SystemVolumeRepository.fromContext(application)
            val audioOutputRepository = SystemAudioOutputRepository.fromContext(application)

            // onCleared will release repositories
            return VolumeViewModel(volumeRepository, audioOutputRepository) as T
        }
    }
}
