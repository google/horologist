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

package com.google.android.horologist.media.ui.components.animated

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.ui.components.ControlButtonLayout
import com.google.android.horologist.media.ui.components.PlayPauseProgressButton
import com.google.android.horologist.media.ui.components.controls.MediaButtonDefaults
import com.google.android.horologist.media.ui.components.controls.SeekToNextButton
import com.google.android.horologist.media.ui.components.controls.SeekToPreviousButton
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Standard media control buttons, showing [SeekToPreviousButton], [PlayPauseProgressButton] and
 * [SeekToNextButton].
 */
@ExperimentalHorologistApi
@Composable
public fun AnimatedMediaControlButtons(
    onPlayButtonClick: () -> Unit,
    onPauseButtonClick: () -> Unit,
    playPauseButtonEnabled: Boolean,
    playing: Boolean,
    onSeekToPreviousButtonClick: () -> Unit,
    seekToPreviousButtonEnabled: Boolean,
    onSeekToNextButtonClick: () -> Unit,
    seekToNextButtonEnabled: Boolean,
    modifier: Modifier = Modifier,
    onSeekToPreviousLongRepeatableClick: (() -> Unit)? = null,
    onSeekToPreviousLongRepeatableClickEnd: (() -> Unit)? = null,
    onSeekToNextLongRepeatableClick: (() -> Unit)? = null,
    onSeekToNextLongRepeatableClickEnd: (() -> Unit)? = null,
    trackPositionUiModel: TrackPositionUiModel,
    progressColor: Color = MaterialTheme.colors.primary,
    colors: ButtonColors = MediaButtonDefaults.mediaButtonDefaultColors,
    rotateProgressIndicator: Flow<Unit> = flowOf(),
) {
    val isLargeScreen = LocalConfiguration.current.screenHeightDp > 224
    ControlButtonLayout(
        modifier = modifier,
        leftButton = {
            AnimatedSeekToPreviousButton(
                modifier = Modifier.fillMaxSize(),
                onClick = onSeekToPreviousButtonClick,
                enabled = seekToPreviousButtonEnabled,
                colors = colors,
                onLongRepeatableClick = onSeekToPreviousLongRepeatableClick,
                onLongRepeatableClickEnd = onSeekToPreviousLongRepeatableClickEnd,
            )
        },
        middleButton = {
            if (trackPositionUiModel.showProgress) {
                AnimatedPlayPauseProgressButton(
                    onPlayClick = onPlayButtonClick,
                    onPauseClick = onPauseButtonClick,
                    enabled = playPauseButtonEnabled,
                    playing = playing,
                    trackPositionUiModel = trackPositionUiModel,
                    modifier = Modifier.fillMaxSize(),
                    colors = colors,
                    progressColor = progressColor,
                    rotateProgressIndicator = rotateProgressIndicator,
                    iconSize = if (isLargeScreen) 38.dp else 32.dp,
                )
            } else {
                AnimatedPlayPauseButton(
                    onPlayClick = onPlayButtonClick,
                    onPauseClick = onPauseButtonClick,
                    enabled = playPauseButtonEnabled,
                    playing = playing,
                    modifier = Modifier.fillMaxSize(),
                    colors = colors,
                    iconSize = if (isLargeScreen) 38.dp else 32.dp,
                )
            }
        },
        rightButton = {
            AnimatedSeekToNextButton(
                modifier = Modifier.fillMaxSize(),
                onClick = onSeekToNextButtonClick,
                onLongRepeatableClick = onSeekToNextLongRepeatableClick,
                onLongRepeatableClickEnd = onSeekToNextLongRepeatableClickEnd,
                enabled = seekToNextButtonEnabled,
                colors = colors,
            )
        },
    )
}

/**
 * Standard and custom action media control buttons, showing a [PlayPauseProgressButton] as the
 * middle button, and allowing custom buttons to be passed for left and right buttons.
 */
@Composable
public fun AnimatedMediaControlButtons(
    onPlayButtonClick: () -> Unit,
    onPauseButtonClick: () -> Unit,
    playPauseButtonEnabled: Boolean,
    playing: Boolean,
    leftButton: @Composable () -> Unit,
    rightButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    trackPositionUiModel: TrackPositionUiModel,
    progressColor: Color = MaterialTheme.colors.primary,
    colors: ButtonColors = MediaButtonDefaults.mediaButtonDefaultColors,
    rotateProgressIndicator: Flow<Unit> = flowOf(),
) {
    ControlButtonLayout(
        modifier = modifier,
        leftButton = leftButton,
        middleButton = {
            if (trackPositionUiModel.showProgress) {
                AnimatedPlayPauseProgressButton(
                    onPlayClick = onPlayButtonClick,
                    onPauseClick = onPauseButtonClick,
                    enabled = playPauseButtonEnabled,
                    playing = playing,
                    trackPositionUiModel = trackPositionUiModel,
                    modifier = Modifier.size(ButtonDefaults.LargeButtonSize),
                    colors = colors,
                    progressColor = progressColor,
                    rotateProgressIndicator = rotateProgressIndicator,
                )
            } else {
                AnimatedPlayPauseButton(
                    onPlayClick = onPlayButtonClick,
                    onPauseClick = onPauseButtonClick,
                    enabled = playPauseButtonEnabled,
                    playing = playing,
                    modifier = Modifier.size(ButtonDefaults.LargeButtonSize),
                    colors = colors,
                )
            }
        },
        rightButton = rightButton,
    )
}
