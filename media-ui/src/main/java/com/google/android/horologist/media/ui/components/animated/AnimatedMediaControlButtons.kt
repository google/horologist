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

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.components.ControlButtonLayout
import com.google.android.horologist.media.ui.components.PlayPauseProgressButton
import com.google.android.horologist.media.ui.components.controls.MediaButtonDefaults
import com.google.android.horologist.media.ui.components.controls.SeekToNextButton
import com.google.android.horologist.media.ui.components.controls.SeekToPreviousButton
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel

/**
 * Standard media control buttons, showing [SeekToPreviousButton], [PlayPauseProgressButton] and
 * [SeekToNextButton].
 */
@ExperimentalHorologistMediaUiApi
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
    trackPositionUiModel: TrackPositionUiModel,
    progressColour: Color = MaterialTheme.colors.primary,
    colors: ButtonColors = MediaButtonDefaults.mediaButtonDefaultColors
) {
    ControlButtonLayout(
        modifier = modifier,
        leftButton = {
            AnimatedSeekToPreviousButton(
                onClick = onSeekToPreviousButtonClick,
                enabled = seekToPreviousButtonEnabled,
                colors = colors
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
                    modifier = Modifier.size(ButtonDefaults.LargeButtonSize),
                    colors = colors,
                    progressColour = progressColour
                )
            } else {
                AnimatedPlayPauseButton(
                    onPlayClick = onPlayButtonClick,
                    onPauseClick = onPauseButtonClick,
                    enabled = playPauseButtonEnabled,
                    playing = playing,
                    modifier = Modifier.size(ButtonDefaults.LargeButtonSize),
                    colors = colors
                )
            }
        },
        rightButton = {
            AnimatedSeekToNextButton(
                onClick = onSeekToNextButtonClick,
                enabled = seekToNextButtonEnabled,
                colors = colors
            )
        }
    )
}
