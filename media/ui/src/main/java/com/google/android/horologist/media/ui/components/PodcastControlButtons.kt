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

package com.google.android.horologist.media.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.ButtonColors
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.ui.components.controls.MediaButtonDefaults
import com.google.android.horologist.media.ui.components.controls.SeekBackButton
import com.google.android.horologist.media.ui.components.controls.SeekButtonIncrement
import com.google.android.horologist.media.ui.components.controls.SeekForwardButton
import com.google.android.horologist.media.ui.state.PlayerUiController
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel

/**
 * Convenience wrapper of [PodcastControlButtons].
 *
 * This version passes events to the provided [PlayerUiController].
 */
@ExperimentalHorologistApi
@Composable
public fun PodcastControlButtons(
    playerController: PlayerUiController,
    playerUiState: PlayerUiState,
    modifier: Modifier = Modifier,
    colors: ButtonColors = MediaButtonDefaults.mediaButtonDefaultColors,
) {
    PodcastControlButtons(
        onPlayButtonClick = playerController::play,
        onPauseButtonClick = playerController::pause,
        playPauseButtonEnabled = playerUiState.playPauseEnabled,
        playing = playerUiState.playing,
        onSeekBackButtonClick = playerController::seekBack,
        seekBackButtonIncrement = playerUiState.seekBackButtonIncrement,
        seekBackButtonEnabled = playerUiState.seekBackEnabled,
        onSeekForwardButtonClick = playerController::seekForward,
        seekForwardButtonIncrement = playerUiState.seekForwardButtonIncrement,
        seekForwardButtonEnabled = playerUiState.seekForwardEnabled,
        trackPositionUiModel = playerUiState.trackPositionUiModel,
        modifier = modifier,
        colors = colors,
    )
}

/**
 * Standard Podcast control buttons with no progress indicator, showing [SeekBackButton],
 * [PlayPauseProgressButton] and [SeekForwardButton].
 */
@ExperimentalHorologistApi
@Composable
public fun PodcastControlButtons(
    onPlayButtonClick: () -> Unit,
    onPauseButtonClick: () -> Unit,
    playPauseButtonEnabled: Boolean,
    playing: Boolean,
    onSeekBackButtonClick: () -> Unit,
    seekBackButtonEnabled: Boolean,
    onSeekForwardButtonClick: () -> Unit,
    seekForwardButtonEnabled: Boolean,
    modifier: Modifier = Modifier,
    seekBackButtonIncrement: SeekButtonIncrement = SeekButtonIncrement.Unknown,
    seekForwardButtonIncrement: SeekButtonIncrement = SeekButtonIncrement.Unknown,
    colors: ButtonColors = MediaButtonDefaults.mediaButtonDefaultColors,
) {
    PodcastControlButtons(
        onPlayButtonClick = onPlayButtonClick,
        onPauseButtonClick = onPauseButtonClick,
        playPauseButtonEnabled = playPauseButtonEnabled,
        playing = playing,
        onSeekBackButtonClick = onSeekBackButtonClick,
        seekBackButtonIncrement = seekBackButtonIncrement,
        seekBackButtonEnabled = seekBackButtonEnabled,
        onSeekForwardButtonClick = onSeekForwardButtonClick,
        seekForwardButtonIncrement = seekForwardButtonIncrement,
        seekForwardButtonEnabled = seekForwardButtonEnabled,
        trackPositionUiModel = TrackPositionUiModel.Hidden,
        modifier = modifier,
        colors = colors,
    )
}

/**
 * Standard Podcast control buttons showing [SeekBackButton], [PlayPauseProgressButton] and [SeekForwardButton].
 */
@ExperimentalHorologistApi
@Composable
public fun PodcastControlButtons(
    onPlayButtonClick: () -> Unit,
    onPauseButtonClick: () -> Unit,
    playPauseButtonEnabled: Boolean,
    playing: Boolean,
    onSeekBackButtonClick: () -> Unit,
    seekBackButtonEnabled: Boolean,
    onSeekForwardButtonClick: () -> Unit,
    seekForwardButtonEnabled: Boolean,
    trackPositionUiModel: TrackPositionUiModel,
    modifier: Modifier = Modifier,
    seekBackButtonIncrement: SeekButtonIncrement = SeekButtonIncrement.Unknown,
    seekForwardButtonIncrement: SeekButtonIncrement = SeekButtonIncrement.Unknown,
    colors: ButtonColors = MediaButtonDefaults.mediaButtonDefaultColors,
) {
    ControlButtonLayout(
        modifier = modifier,
        leftButton = {
            SeekBackButton(
                onClick = onSeekBackButtonClick,
                seekButtonIncrement = seekBackButtonIncrement,
                colors = colors,
                enabled = seekBackButtonEnabled,
            )
        },
        middleButton = {
            PlayPauseProgressButton(
                onPlayClick = onPlayButtonClick,
                onPauseClick = onPauseButtonClick,
                enabled = playPauseButtonEnabled,
                playing = playing,
                trackPositionUiModel = trackPositionUiModel,
                colors = colors,
            )
        },
        rightButton = {
            SeekForwardButton(
                onClick = onSeekForwardButtonClick,
                seekButtonIncrement = seekForwardButtonIncrement,
                colors = colors,
                enabled = seekForwardButtonEnabled,
            )
        },
    )
}
