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

import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.repository.PlayerRepository
import com.google.android.horologist.media.ui.state.mapper.PlayerUiStateMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlin.time.Duration

/**
 * Produces a flow of [PlayerUiState] based on events produced by a [PlayerRepository].
 *
 * This class should generally always be hosted inside a ViewModel to ensure it's tied to a
 * lifecycle that survives configuration changes.
 */
@ExperimentalHorologistApi
public class PlayerUiStateProducer(
    playerRepository: PlayerRepository
) {
    private data class StaticState(
        val connected: Boolean,
        val shuffleModeEnabled: Boolean,
        val seekBackButtonIncrement: Duration?,
        val seekForwardButtonIncrement: Duration?
    )

    private val staticFlow = combine(
        playerRepository.connected,
        playerRepository.shuffleModeEnabled,
        playerRepository.seekBackIncrement,
        playerRepository.seekForwardIncrement
    ) { connected, shuffleModeEnabled, seekBackIncrement, seekForwardIncrement ->
        StaticState(
            connected = connected,
            shuffleModeEnabled = shuffleModeEnabled,
            seekBackButtonIncrement = seekBackIncrement,
            seekForwardButtonIncrement = seekForwardIncrement
        )
    }

    public val playerUiStateFlow: Flow<PlayerUiState> = combine(
        playerRepository.availableCommands,
        playerRepository.currentMedia,
        playerRepository.latestPlaybackState,
        staticFlow
    ) { availableCommands, media, lastPlaybackStateEvent, staticData ->
        PlayerUiStateMapper.map(
            currentState = lastPlaybackStateEvent.playbackState.playerState,
            availableCommands = availableCommands,
            media = media,
            playbackStateEvent = lastPlaybackStateEvent,
            shuffleModeEnabled = staticData.shuffleModeEnabled,
            connected = staticData.connected,
            seekBackIncrement = staticData.seekBackButtonIncrement,
            seekForwardIncrement = staticData.seekForwardButtonIncrement
        )
    }
}
