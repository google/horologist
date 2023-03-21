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

@file:OptIn(ExperimentalHorologistApi::class)

package com.google.android.horologist.media.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.repository.PlayerRepository
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@ExperimentalHorologistApi
public open class PlayerViewModel(
    playerRepository: PlayerRepository
) : ViewModel() {

    private val producer = PlayerUiStateProducer(playerRepository)

    public val playerUiState: StateFlow<PlayerUiState> = producer.playerUiStateFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = PlayerUiState.NotConnected
    )

    public val playerUiController: PlayerUiController = PlayerUiController(playerRepository)
}
