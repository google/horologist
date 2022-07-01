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

import com.google.android.horologist.media.model.Command
import com.google.android.horologist.media.model.MediaItem
import com.google.android.horologist.media.model.MediaItemPosition
import com.google.android.horologist.media.model.PlayerState
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.components.controls.SeekButtonIncrement
import com.google.android.horologist.media.ui.state.PlayerUiState

/**
 * Map [PlayerState], [Command] plus other set of properties into a [PlayerUiState].
 */
@ExperimentalHorologistMediaUiApi
public object PlayerUiStateMapper {

    public fun map(
        currentState: PlayerState,
        availableCommands: Set<Command>,
        mediaItem: MediaItem?,
        mediaItemPosition: MediaItemPosition?,
        shuffleModeEnabled: Boolean,
        connected: Boolean,
        seekBackButtonIncrement: SeekButtonIncrement,
        seekForwardButtonIncrement: SeekButtonIncrement
    ): PlayerUiState {
        val playPauseCommandAvailable = availableCommands.contains(Command.PlayPause)

        return PlayerUiState(
            playEnabled = playPauseCommandAvailable,
            pauseEnabled = playPauseCommandAvailable,
            seekBackEnabled = availableCommands.contains(Command.SeekBack),
            seekForwardEnabled = availableCommands.contains(Command.SeekForward),
            seekToPreviousEnabled = availableCommands.contains(Command.SkipToPreviousMediaItem),
            seekToNextEnabled = availableCommands.contains(Command.SkipToNextMediaItem),
            shuffleEnabled = availableCommands.contains(Command.SetShuffle),
            shuffleOn = shuffleModeEnabled,
            playPauseEnabled = playPauseCommandAvailable,
            playing = currentState == PlayerState.Playing,
            mediaItem = mediaItem?.let(MediaItemUiModelMapper::map),
            trackPosition = mediaItemPosition?.let(TrackPositionUiModelMapper::map),
            connected = connected,
            seekBackButtonIncrement = seekBackButtonIncrement,
            seekForwardButtonIncrement = seekForwardButtonIncrement
        )
    }
}
