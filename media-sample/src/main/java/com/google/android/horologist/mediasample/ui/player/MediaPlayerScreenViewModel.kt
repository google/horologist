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

import androidx.lifecycle.viewModelScope
import com.google.android.horologist.media.data.repository.PlayerRepositoryImpl
import com.google.android.horologist.media.model.MediaPosition
import com.google.android.horologist.media.ui.state.PlayerViewModel
import com.google.android.horologist.mediasample.domain.SettingsRepository
import com.google.android.horologist.mediasample.domain.proto.SettingsProto.Settings
import com.google.android.horologist.mediasample.domain.proto.copy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MediaPlayerScreenViewModel @Inject constructor(
    playerRepository: PlayerRepositoryImpl,
    settingsRepository: SettingsRepository
) : PlayerViewModel(playerRepository) {

    init {
        viewModelScope.launch {
            playerRepository.currentMedia.collect { media ->
                if (media != null) {
                    settingsRepository.edit {
                        it.copy { currentMediaItemId = media.id }
                    }
                    val position = playerRepository.mediaPosition.value
                    if (position is MediaPosition.KnownDuration) {
                        settingsRepository.edit {
                            it.copy {
                                currentPosition = position.current.inWholeMilliseconds
                            }
                        }
                    }
                }
            }
        }
    }

    val playerState = playerRepository.player

    val settingsState: StateFlow<Settings> = settingsRepository.settingsFlow
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = Settings.getDefaultInstance()
        )
}
