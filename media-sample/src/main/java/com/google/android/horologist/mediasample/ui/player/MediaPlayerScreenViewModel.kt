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
import com.google.android.horologist.compose.layout.StateUtils
import com.google.android.horologist.media.data.repository.PlayerRepositoryImpl
import com.google.android.horologist.media.ui.state.PlayerViewModel
import com.google.android.horologist.media3.audio.AudioOutputSelector
import com.google.android.horologist.mediasample.domain.SettingsRepository
import com.google.android.horologist.mediasample.domain.proto.SettingsProto
import com.google.android.horologist.mediasample.domain.proto.SettingsProto.Settings
import com.google.android.horologist.mediasample.ui.debug.MediaInfoTimeTextViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MediaPlayerScreenViewModel @Inject constructor(
    playerRepository: PlayerRepositoryImpl,
    settingsRepository: SettingsRepository,
) : PlayerViewModel(playerRepository) {
    init {
        viewModelScope.launch {
            // update the track position while app is in foreground
            while (isActive) {
                delay(1000)
                playerRepository.updatePosition()
            }
        }
    }

    val settingsState: StateFlow<Settings> = settingsRepository.settingsFlow
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = Settings.getDefaultInstance()
        )
}
