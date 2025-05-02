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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import com.google.android.horologist.media.ui.material3.components.controls.SeekToNextButton
import com.google.android.horologist.media.ui.material3.components.controls.SeekToPreviousButton
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel

/**
 * Standard media control buttons with no progress indicator, showing [SeekToPreviousButton],
 * [PlayPauseButton] and [SeekToNextButton].
 */

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
    )
}

/**
 * Standard media control buttons, showing [SeekToPreviousButton], [PlayPauseProgressButton] and
 * [SeekToNextButton].
 */

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
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
) {
    val leftButtonPadding = ButtonGroupLayoutDefaults.getSideButtonsPadding(isLeftButton = true)
    val rightButtonPadding = ButtonGroupLayoutDefaults.getSideButtonsPadding(isLeftButton = false)

    ButtonGroupLayout(
        modifier = modifier,
        leftButton = {
            SeekToPreviousButton(
                modifier = Modifier.padding(leftButtonPadding).fillMaxSize(),
                onClick = onSeekToPreviousButtonClick,
                enabled = seekToPreviousButtonEnabled,
                colorScheme = colorScheme,
            )
        },
        middleButton = {
            PlayPauseProgressButton(
                modifier = Modifier.fillMaxSize(),
                onPlayClick = onPlayButtonClick,
                onPauseClick = onPauseButtonClick,
                enabled = playPauseButtonEnabled,
                playing = playing,
                trackPositionUiModel = trackPositionUiModel,
                colorScheme = colorScheme,
                iconSize = IconButtonDefaults.LargeIconSize,
            )
        },
        rightButton = {
            SeekToNextButton(
                modifier = Modifier.padding(rightButtonPadding).fillMaxSize(),
                onClick = onSeekToNextButtonClick,
                enabled = seekToNextButtonEnabled,
                colorScheme = colorScheme,
            )
        },
    )
}
