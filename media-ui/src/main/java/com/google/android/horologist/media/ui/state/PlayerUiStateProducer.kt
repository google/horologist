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

@file:OptIn(ExperimentalHorologistMediaApi::class)

package com.google.android.horologist.media.ui.state

import com.google.android.horologist.media.ExperimentalHorologistMediaApi
import com.google.android.horologist.media.repository.PlayerRepository
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.components.controls.SeekButtonIncrement
import com.google.android.horologist.media.ui.state.mapper.PlayerUiStateMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Produces a flow of [PlayerUiState] based on events produced by a [PlayerRepository].
 *
 * This class should generally always be hosted inside a ViewModel to ensure it's tied to a
 * lifecycle that survives configuration changes.
 */
@ExperimentalHorologistMediaUiApi
public class PlayerUiStateProducer(
    private val playerRepository: PlayerRepository
) {
    private data class StaticState(
        val connected: Boolean,
        val shuffleModeEnabled: Boolean,
        val seekBackButtonIncrement: SeekButtonIncrement,
        val seekForwardButtonIncrement: SeekButtonIncrement
    )

    private val staticFlow = combine(
        playerRepository.connected,
        playerRepository.shuffleModeEnabled
    ) { connected, shuffleModeEnabled ->
        val seekBackSeconds = playerRepository.getSeekBackIncrement().inWholeSeconds.toInt()
        val seekForwardSeconds = playerRepository.getSeekForwardIncrement().inWholeSeconds.toInt()

        StaticState(
            connected = connected,
            shuffleModeEnabled = shuffleModeEnabled,
            seekBackButtonIncrement = SeekButtonIncrement.ofSeconds(seekBackSeconds),
            seekForwardButtonIncrement = SeekButtonIncrement.ofSeconds(seekForwardSeconds)
        )
    }

    public val playerUiStateFlow: Flow<PlayerUiState> = combine(
        playerRepository.currentState,
        playerRepository.availableCommands,
        playerRepository.currentMedia,
        playerRepository.mediaPosition,
        staticFlow
    ) { currentState, availableCommands, media, mediaPosition, staticData ->
        PlayerUiStateMapper.map(
            currentState = currentState,
            availableCommands = availableCommands,
            media = media,
            mediaPosition = mediaPosition,
            shuffleModeEnabled = staticData.shuffleModeEnabled,
            connected = playerRepository.connected.value,
            seekBackButtonIncrement = staticData.seekBackButtonIncrement,
            seekForwardButtonIncrement = staticData.seekForwardButtonIncrement
        )
    }
}
