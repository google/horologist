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

package com.google.android.horologist.media.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.media.repository.PlayerRepository
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.components.controls.SeekButtonIncrement
import com.google.android.horologist.media.ui.state.mapper.PlayerUiStateMapper
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@ExperimentalHorologistMediaUiApi
public open class PlayerViewModel(
    private val playerRepository: PlayerRepository
) : ViewModel() {

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

    public val playerUiState: StateFlow<PlayerUiState> = combine(
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
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = INITIAL_PLAYER_UI_STATE
    )

    public fun play() {
        // Prepare is needed to ensure playback
        playerRepository.prepare()
        playerRepository.play()
    }

    public fun pause() {
        playerRepository.pause()
    }

    public fun skipToPreviousMedia() {
        playerRepository.skipToPreviousMedia()
    }

    public fun skipToNextMedia() {
        playerRepository.skipToNextMedia()
    }

    public fun seekBack() {
        playerRepository.seekBack()
    }

    public fun seekForward() {
        playerRepository.seekForward()
    }

    public companion object {
        private val INITIAL_MEDIA = MediaUiModel(id = "", title = null, artist = null)

        private val INITIAL_TRACK_POSITION = TrackPositionUiModel(
            current = 0,
            duration = 0,
            percent = 0f
        )

        private val INITIAL_PLAYER_UI_STATE = PlayerUiState(
            playEnabled = false,
            pauseEnabled = false,
            seekBackEnabled = false,
            seekForwardEnabled = false,
            seekToPreviousEnabled = false,
            seekToNextEnabled = false,
            shuffleEnabled = false,
            shuffleOn = false,
            playPauseEnabled = false,
            playing = false,
            media = INITIAL_MEDIA,
            trackPosition = INITIAL_TRACK_POSITION,
            connected = false
        )
    }
}
