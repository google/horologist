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

package com.google.android.horologist.media.ui.state.mapper

import com.google.android.horologist.media.ExperimentalHorologistMediaApi
import com.google.android.horologist.media.model.Command
import com.google.android.horologist.media.model.Media
import com.google.android.horologist.media.model.PlaybackStateEvent
import com.google.android.horologist.media.model.PlayerState
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.components.controls.SeekButtonIncrement
import com.google.android.horologist.media.ui.state.PlayerUiState
import kotlin.time.Duration

/**
 * Map [PlayerState], [Command] plus other set of properties into a [PlayerUiState].
 */
@ExperimentalHorologistMediaUiApi
public object PlayerUiStateMapper {

    public fun map(
        currentState: PlayerState,
        availableCommands: Set<Command>,
        media: Media?,
        playbackStateEvent: PlaybackStateEvent,
        shuffleModeEnabled: Boolean,
        connected: Boolean,
        seekBackIncrement: Duration?,
        seekForwardIncrement: Duration?
    ): PlayerUiState {
        val playPauseCommandAvailable = availableCommands.contains(Command.PlayPause)

        return PlayerUiState(
            playEnabled = playPauseCommandAvailable,
            pauseEnabled = playPauseCommandAvailable,
            seekBackEnabled = availableCommands.contains(Command.SeekBack),
            seekForwardEnabled = availableCommands.contains(Command.SeekForward),
            seekToPreviousEnabled = availableCommands.contains(Command.SkipToPreviousMedia),
            seekToNextEnabled = availableCommands.contains(Command.SkipToNextMedia),
            shuffleEnabled = availableCommands.contains(Command.SetShuffle),
            shuffleOn = shuffleModeEnabled,
            playPauseEnabled = playPauseCommandAvailable,
            playing = currentState == PlayerState.Playing,
            media = media?.let(MediaUiModelMapper::map),
            trackPositionUiModel = TrackPositionUiModelMapper.map(playbackStateEvent),
            connected = connected,
            seekBackButtonIncrement = seekBackIncrement?.let { SeekButtonIncrement.ofDuration(it) } ?: SeekButtonIncrement.Unknown,
            seekForwardButtonIncrement = seekForwardIncrement?.let { SeekButtonIncrement.ofDuration(it) } ?: SeekButtonIncrement.Unknown
        )
    }
}
