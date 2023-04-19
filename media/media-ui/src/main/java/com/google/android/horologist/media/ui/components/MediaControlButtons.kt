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
import com.google.android.horologist.media.ui.components.controls.SeekToNextButton
import com.google.android.horologist.media.ui.components.controls.SeekToPreviousButton
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel

/**
 * Standard media control buttons with no progress indicator, showing [SeekToPreviousButton],
 * [PlayPauseButton] and [SeekToNextButton].
 */
@ExperimentalHorologistApi
@Composable
public fun MediaControlButtons(
    onPlayButtonClick: () -> Unit,
    onPauseButtonClick: () -> Unit,
    playPauseButtonEnabled: Boolean,
    playing: Boolean,
    onSeekToPreviousButtonClick: () -> Unit,
    seekToPreviousButtonEnabled: Boolean,
    onSeekToNextButtonClick: () -> Unit,
    seekToNextButtonEnabled: Boolean,
    modifier: Modifier = Modifier,
    colors: ButtonColors = MediaButtonDefaults.mediaButtonDefaultColors
) {
    MediaControlButtons(
        onPlayButtonClick = onPlayButtonClick,
        onPauseButtonClick = onPauseButtonClick,
        playPauseButtonEnabled = playPauseButtonEnabled,
        playing = playing,
        onSeekToPreviousButtonClick = onSeekToPreviousButtonClick,
        seekToPreviousButtonEnabled = seekToPreviousButtonEnabled,
        onSeekToNextButtonClick = onSeekToNextButtonClick,
        seekToNextButtonEnabled = seekToNextButtonEnabled,
        trackPositionUiModel = TrackPositionUiModel.Hidden,
        modifier = modifier,
        colors = colors
    )
}

/**
 * Standard media control buttons, showing [SeekToPreviousButton], [PlayPauseProgressButton] and
 * [SeekToNextButton].
 */
@ExperimentalHorologistApi
@Composable
public fun MediaControlButtons(
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
    colors: ButtonColors = MediaButtonDefaults.mediaButtonDefaultColors
) {
    ControlButtonLayout(
        modifier = modifier,
        leftButton = {
            SeekToPreviousButton(
                onClick = onSeekToPreviousButtonClick,
                enabled = seekToPreviousButtonEnabled,
                colors = colors
            )
        },
        middleButton = {
            PlayPauseProgressButton(
                onPlayClick = onPlayButtonClick,
                onPauseClick = onPauseButtonClick,
                enabled = playPauseButtonEnabled,
                playing = playing,
                trackPositionUiModel = trackPositionUiModel,
                colors = colors
            )
        },
        rightButton = {
            SeekToNextButton(
                onClick = onSeekToNextButtonClick,
                enabled = seekToNextButtonEnabled,
                colors = colors
            )
        }
    )
}
