/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.media.ui.material3.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.ui.components.controls.SeekButtonIncrement
import com.google.android.horologist.media.ui.material3.components.animated.AnimatedPlayPauseButton
import com.google.android.horologist.media.ui.material3.components.animated.AnimatedPlayPauseProgressButton
import com.google.android.horologist.media.ui.material3.components.controls.SeekBackButton
import com.google.android.horologist.media.ui.material3.components.controls.SeekForwardButton
import com.google.android.horologist.media.ui.material3.util.BUTTON_GROUP_ITEMS_COUNT
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
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
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
        colorScheme = colorScheme,
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
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    seekBackButtonIncrement: SeekButtonIncrement = SeekButtonIncrement.Unknown,
    seekForwardButtonIncrement: SeekButtonIncrement = SeekButtonIncrement.Unknown,
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
        colorScheme = colorScheme,
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
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    seekBackButtonIncrement: SeekButtonIncrement = SeekButtonIncrement.Unknown,
    seekForwardButtonIncrement: SeekButtonIncrement = SeekButtonIncrement.Unknown,
) {
    val interactionSources =
        remember { Array(BUTTON_GROUP_ITEMS_COUNT) { MutableInteractionSource() } }
    val buttonPressedStateList = interactionSources.map { it.collectIsPressedAsState() }
    val isAnyButtonPressed = remember {
        derivedStateOf { buttonPressedStateList.any { it.value } }
    }

    val leftButtonPadding = ButtonGroupLayoutDefaults.getSideButtonsPadding(isLeftButton = true)
    val rightButtonPadding = ButtonGroupLayoutDefaults.getSideButtonsPadding(isLeftButton = false)

    ButtonGroupLayout(
        modifier = modifier,
        interactionSources = interactionSources,
        leftButton = {
            SeekBackButton(
                modifier = Modifier
                    .animateWidth(it)
                    .fillMaxSize(),
                onClick = onSeekBackButtonClick,
                interactionSource = it,
                buttonPadding = leftButtonPadding,
                seekButtonIncrement = seekBackButtonIncrement,
                colorScheme = colorScheme,
                enabled = seekBackButtonEnabled,
            )
        },
        middleButton = {
            if (trackPositionUiModel.showProgress) {
                AnimatedPlayPauseProgressButton(
                    onPlayClick = onPlayButtonClick,
                    onPauseClick = onPauseButtonClick,
                    enabled = playPauseButtonEnabled,
                    playing = playing,
                    interactionSource = it,
                    trackPositionUiModel = trackPositionUiModel,
                    modifier = Modifier
                        .minWidth(ButtonGroupLayoutDefaults.middleButtonSize)
                        .animateWidth(it)
                        .fillMaxSize(),
                    colorScheme = colorScheme,
                    isAnyButtonPressed = isAnyButtonPressed,
                )
            } else {
                AnimatedPlayPauseButton(
                    onPlayClick = onPlayButtonClick,
                    onPauseClick = onPauseButtonClick,
                    enabled = playPauseButtonEnabled,
                    colorScheme = colorScheme,
                    playing = playing,
                    interactionSource = it,
                    modifier = Modifier
                        .minWidth(ButtonGroupLayoutDefaults.middleButtonSize)
                        .animateWidth(it)
                        .fillMaxSize(),
                )
            }
        },
        rightButton = {
            SeekForwardButton(
                modifier = Modifier
                    .animateWidth(it)
                    .fillMaxSize(),
                onClick = onSeekForwardButtonClick,
                interactionSource = it,
                buttonPadding = rightButtonPadding,
                seekButtonIncrement = seekForwardButtonIncrement,
                colorScheme = colorScheme,
                enabled = seekForwardButtonEnabled,
            )
        },
    )
}
