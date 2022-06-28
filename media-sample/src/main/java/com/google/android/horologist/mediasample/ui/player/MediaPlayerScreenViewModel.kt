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

package com.google.android.horologist.mediasample.ui.player

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.horologist.media.data.PlayerRepositoryImpl
import com.google.android.horologist.media.ui.state.PlayerViewModel
import com.google.android.horologist.media3.audio.AudioOutputSelector
import com.google.android.horologist.mediasample.di.MediaApplicationContainer
import com.google.android.horologist.mediasample.ui.settings.Settings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MediaPlayerScreenViewModel(
    playerRepository: PlayerRepositoryImpl,
    private val dataStore: DataStore<Preferences>,
    private val audioOutputSelector: AudioOutputSelector
) : PlayerViewModel(playerRepository) {
    val settingsState: StateFlow<Settings?> = dataStore.data.map {
        Settings(it)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    fun launchBluetoothSettings() {
        audioOutputSelector.launchSelector()
    }

    init {
        viewModelScope.launch {
            // update the track position while app is in foreground
            while (isActive) {
                delay(1000)
                playerRepository.updatePosition()
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                MediaPlayerScreenViewModel(
                    playerRepository = this[MediaApplicationContainer.PlayerRepositoryImplKey]!!,
                    dataStore = this[MediaApplicationContainer.DataStoreKey]!!,
                    audioOutputSelector = this[MediaApplicationContainer.AudioOutputSelectorKey]!!
                )
            }
        }
    }
}
