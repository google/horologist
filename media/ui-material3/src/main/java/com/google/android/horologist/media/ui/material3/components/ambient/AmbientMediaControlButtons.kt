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

package com.google.android.horologist.media.ui.material3.components.ambient

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material3.ButtonGroupScope
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import com.google.android.horologist.media.ui.material3.components.ButtonGroupLayout
import com.google.android.horologist.media.ui.material3.components.ButtonGroupLayoutDefaults
import com.google.android.horologist.media.ui.state.PlayerUiState

/**
 * Media control buttons for display in the ambient mode, showing [AmbientPlayPauseButton] button in
 * the middle, and allows custom buttons to be passed for left and right.
 *
 * @param onPlayButtonClick Callback invoked when the play button is clicked.
 * @param onPauseButtonClick Callback invoked when the pause button is clicked.
 * @param playPauseButtonEnabled Controls the enabled state of the play/pause button.
 * @param playing Indicates whether the media is currently playing.
 * @param leftButton Composable lambda that defines the left button. It receives a
 *   [MutableInteractionSource] to observe interactions.
 * @param rightButton Composable lambda that defines the right button. It receives a
 *   [MutableInteractionSource] to observe interactions.
 * @param modifier Optional [Modifier] to be applied to the button group.
 * @param colorScheme The [ColorScheme] used to style the play/pause button. Defaults to
 *   [MaterialTheme.colorScheme].
 */
@Composable
public fun AmbientMediaControlButtons(
    onPlayButtonClick: () -> Unit,
    onPauseButtonClick: () -> Unit,
    playPauseButtonEnabled: Boolean,
    playing: Boolean,
    leftButton: @Composable ButtonGroupScope.(MutableInteractionSource) -> Unit,
    rightButton: @Composable ButtonGroupScope.(MutableInteractionSource) -> Unit,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
) {
    ButtonGroupLayout(
        modifier = modifier,
        leftButton = leftButton,
        middleButton = {
            AmbientPlayPauseButton(
                onPlayClick = onPlayButtonClick,
                onPauseClick = onPauseButtonClick,
                enabled = playPauseButtonEnabled,
                playing = playing,
                modifier = Modifier.weight(0.5f) // This is used to constrain the size at minWidth for non-pressed state.
                    .minWidth(ButtonGroupLayoutDefaults.middleButtonSize)
                    .requiredHeight(ButtonGroupLayoutDefaults.middleButtonSize),
                colorScheme = colorScheme,
            )
        },
        rightButton = rightButton,
    )
}

/**
 * Media control buttons for display in the ambient mode, showing [AmbientPlayPauseButton] button in
 * the middle and [AmbientSeekToPreviousButton] and [AmbientSeekToNextButton] buttons on the sides.
 *
 * @param playerUiState The [PlayerUiState] used to determine the state of the buttons.
 * @param onPlayButtonClick Callback invoked when the play button is clicked.
 * @param onPauseButtonClick Callback invoked when the pause button is clicked.
 * @param onSeekToPreviousButtonClick Callback invoked when the seek to previous button is clicked.
 * @param onSeekToNextButtonClick Callback invoked when the seek to next button is clicked.
 * @param modifier Optional [Modifier] to be applied to the button group.
 * @param colorScheme The [ColorScheme] used to style the buttons. Defaults to
 *   [MaterialTheme.colorScheme].
 */
@Composable
public fun AmbientMediaControlButtons(
    playerUiState: PlayerUiState,
    onPlayButtonClick: () -> Unit,
    onPauseButtonClick: () -> Unit,
    onSeekToPreviousButtonClick: () -> Unit,
    onSeekToNextButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
) {
    val leftButtonPadding = ButtonGroupLayoutDefaults.getSideButtonsPadding(isLeftButton = true)
    val rightButtonPadding = ButtonGroupLayoutDefaults.getSideButtonsPadding(isLeftButton = false)
    AmbientMediaControlButtons(
        onPlayButtonClick = onPlayButtonClick,
        onPauseButtonClick = onPauseButtonClick,
        playPauseButtonEnabled = playerUiState.playPauseEnabled,
        playing = playerUiState.playing,
        modifier = modifier,
        colorScheme = colorScheme,
        leftButton = {
            AmbientSeekToPreviousButton(
                onClick = onSeekToPreviousButtonClick,
                buttonPadding = leftButtonPadding,
                colorScheme = colorScheme,
                enabled = playerUiState.seekToPreviousEnabled,
            )
        },
        rightButton = {
            AmbientSeekToNextButton(
                onClick = onSeekToNextButtonClick,
                buttonPadding = rightButtonPadding,
                colorScheme = colorScheme,
                enabled = playerUiState.seekToNextEnabled,
            )
        },
    )
}
