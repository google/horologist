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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material3.ButtonGroupScope
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import com.google.android.horologist.media.ui.material3.components.ButtonGroupLayout

/**
 * Media control button for display in the ambient mode, showing [AmbientPlayPauseButton] button in
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
                modifier = Modifier.fillMaxSize(),
                colorScheme = colorScheme,
            )
        },
        rightButton = rightButton,
    )
}
