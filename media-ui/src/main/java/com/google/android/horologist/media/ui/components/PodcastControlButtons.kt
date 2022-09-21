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
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.components.controls.MediaButtonDefaults
import com.google.android.horologist.media.ui.components.controls.SeekBackButton
import com.google.android.horologist.media.ui.components.controls.SeekButtonIncrement
import com.google.android.horologist.media.ui.components.controls.SeekForwardButton
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.media.ui.state.PlayerViewModel

/**
 * Stateful version of [PodcastControlButtons].
 *
 * This version listens to [PlayerUiState]s emitted from [PlayerViewModel] to update the controls.
 */
@ExperimentalHorologistMediaUiApi
@Composable
public fun PodcastControlButtons(
    playerViewModel: PlayerViewModel,
    playerUiState: PlayerUiState,
    modifier: Modifier = Modifier,
    showProgress: Boolean = true,
    colors: ButtonColors = MediaButtonDefaults.mediaButtonDefaultColors
) {
    val percent = if (showProgress) {
        playerUiState.trackPosition?.percent ?: 0f
    } else {
        null
    }

    PodcastControlButtons(
        onPlayButtonClick = { playerViewModel.play() },
        onPauseButtonClick = { playerViewModel.pause() },
        playPauseButtonEnabled = playerUiState.playPauseEnabled,
        playing = playerUiState.playing,
        onSeekBackButtonClick = { playerViewModel.skipToPreviousMedia() },
        seekBackButtonIncrement = playerUiState.seekBackButtonIncrement,
        seekBackButtonEnabled = playerUiState.seekBackEnabled,
        onSeekForwardButtonClick = { playerViewModel.skipToNextMedia() },
        seekForwardButtonIncrement = playerUiState.seekForwardButtonIncrement,
        seekForwardButtonEnabled = playerUiState.seekForwardEnabled,
        showProgress = showProgress,
        modifier = modifier,
        percent = percent,
        colors = colors
    )
}

/**
 * Standard Podcast control buttons, showing [SeekBackButton], [PlayPauseProgressButton] and
 * [SeekForwardButton].
 */
@ExperimentalHorologistMediaUiApi
@Composable
public fun PodcastControlButtons(
    onPlayButtonClick: () -> Unit,
    onPauseButtonClick: () -> Unit,
    playPauseButtonEnabled: Boolean,
    playing: Boolean,
    percent: Float,
    onSeekBackButtonClick: () -> Unit,
    seekBackButtonEnabled: Boolean,
    onSeekForwardButtonClick: () -> Unit,
    seekForwardButtonEnabled: Boolean,
    modifier: Modifier = Modifier,
    seekBackButtonIncrement: SeekButtonIncrement = SeekButtonIncrement.Unknown,
    seekForwardButtonIncrement: SeekButtonIncrement = SeekButtonIncrement.Unknown,
    colors: ButtonColors = MediaButtonDefaults.mediaButtonDefaultColors
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
        showProgress = true,
        modifier = modifier,
        percent = percent,
        colors = colors
    )
}

/**
 * Standard Podcast control buttons with no progress indicator, showing [SeekBackButton],
 * [PlayPauseProgressButton] and [SeekForwardButton].
 */
@ExperimentalHorologistMediaUiApi
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
    colors: ButtonColors = MediaButtonDefaults.mediaButtonDefaultColors
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
        showProgress = false,
        modifier = modifier,
        colors = colors
    )
}

@ExperimentalHorologistMediaUiApi
@Composable
private fun PodcastControlButtons(
    onPlayButtonClick: () -> Unit,
    onPauseButtonClick: () -> Unit,
    playPauseButtonEnabled: Boolean,
    playing: Boolean,
    onSeekBackButtonClick: () -> Unit,
    seekBackButtonIncrement: SeekButtonIncrement,
    seekBackButtonEnabled: Boolean,
    onSeekForwardButtonClick: () -> Unit,
    seekForwardButtonIncrement: SeekButtonIncrement,
    seekForwardButtonEnabled: Boolean,
    showProgress: Boolean,
    modifier: Modifier = Modifier,
    percent: Float? = null,
    colors: ButtonColors = MediaButtonDefaults.mediaButtonDefaultColors
) {
    ControlButtonLayout(
        modifier = modifier,
        leftButton = {
            SeekBackButton(
                onClick = onSeekBackButtonClick,
                seekButtonIncrement = seekBackButtonIncrement,
                colors = colors,
                enabled = seekBackButtonEnabled
            )
        },
        middleButton = {
            if (showProgress) {
                checkNotNull(percent)

                PlayPauseProgressButton(
                    onPlayClick = onPlayButtonClick,
                    onPauseClick = onPauseButtonClick,
                    enabled = playPauseButtonEnabled,
                    playing = playing,
                    percent = percent,
                    colors = colors
                )
            } else {
                PlayPauseButton(
                    onPlayClick = onPlayButtonClick,
                    onPauseClick = onPauseButtonClick,
                    enabled = playPauseButtonEnabled,
                    playing = playing,
                    colors = colors
                )
            }
        },
        rightButton = {
            SeekForwardButton(
                onClick = onSeekForwardButtonClick,
                seekButtonIncrement = seekForwardButtonIncrement,
                colors = colors,
                enabled = seekForwardButtonEnabled
            )
        }
    )
}
