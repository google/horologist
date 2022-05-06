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

package com.google.android.horologist.media.ui.state.mapper

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.google.android.horologist.media.data.model.TrackPosition
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.state.PlayerUiState

/**
 * Map [Player.Commands] plus other set of properties into a [PlayerUiState]
 */
@ExperimentalHorologistMediaUiApi
public object PlayerUiStateMapper {

    public fun map(
        playerCommands: Player.Commands,
        shuffleModeEnabled: Boolean,
        isPlaying: Boolean,
        mediaItem: MediaItem?,
        trackPosition: TrackPosition?,
    ): PlayerUiState {
        val playPauseCommandAvailable = playerCommands.contains(Player.COMMAND_PLAY_PAUSE)

        return PlayerUiState(
            playEnabled = playPauseCommandAvailable,
            pauseEnabled = playPauseCommandAvailable,
            seekBackEnabled = playerCommands.contains(Player.COMMAND_SEEK_BACK),
            seekForwardEnabled = playerCommands.contains(Player.COMMAND_SEEK_FORWARD),
            seekToPreviousEnabled = playerCommands.contains(Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM),
            seekToNextEnabled = playerCommands.contains(Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM),
            shuffleEnabled = playerCommands.contains(Player.COMMAND_SET_SHUFFLE_MODE),
            shuffleOn = shuffleModeEnabled,
            playPauseEnabled = playPauseCommandAvailable,
            playing = isPlaying,
            mediaItem = mediaItem?.let(MediaItemUiModelMapper::map),
            trackPosition = trackPosition?.let(TrackPositionUiModelMapper::map)
        )
    }
}
