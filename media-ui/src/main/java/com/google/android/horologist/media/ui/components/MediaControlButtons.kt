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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.components.controls.SeekToNextButton
import com.google.android.horologist.media.ui.components.controls.SeekToPreviousButton

/**
 * Standard media control buttons, showing [SeekToPreviousButton], [PlayPauseProgressButton] and
 * [SeekToNextButton].
 */
@ExperimentalHorologistMediaUiApi
@Composable
public fun MediaControlButtons(
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    playPauseEnabled: Boolean,
    playing: Boolean,
    percent: Float,
    onSeekToPreviousButtonClick: () -> Unit,
    seekToPreviousButtonEnabled: Boolean,
    onSeekToNextButtonClick: () -> Unit,
    seekToNextButtonEnabled: Boolean,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.iconButtonColors(),
    progressColour: Color = MaterialTheme.colors.primaryVariant,
) {
    MediaControlButtons(
        onPlayClick = onPlayClick,
        onPauseClick = onPauseClick,
        playPauseEnabled = playPauseEnabled,
        playing = playing,
        onSeekToPreviousButtonClick = onSeekToPreviousButtonClick,
        seekToPreviousButtonEnabled = seekToPreviousButtonEnabled,
        onSeekToNextButtonClick = onSeekToNextButtonClick,
        seekToNextButtonEnabled = seekToNextButtonEnabled,
        showProgress = true,
        modifier = modifier,
        colors = colors,
        percent = percent,
        progressColour = progressColour
    )
}

/**
 * Standard media control buttons with no progress indicator, showing [SeekToPreviousButton],
 * [PlayPauseButton] and [SeekToNextButton].
 */
@ExperimentalHorologistMediaUiApi
@Composable
public fun MediaControlButtons(
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    playPauseEnabled: Boolean,
    playing: Boolean,
    onSeekToPreviousButtonClick: () -> Unit,
    seekToPreviousButtonEnabled: Boolean,
    onSeekToNextButtonClick: () -> Unit,
    seekToNextButtonEnabled: Boolean,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.iconButtonColors(),
) {
    MediaControlButtons(
        onPlayClick = onPlayClick,
        onPauseClick = onPauseClick,
        playPauseEnabled = playPauseEnabled,
        playing = playing,
        onSeekToPreviousButtonClick = onSeekToPreviousButtonClick,
        seekToPreviousButtonEnabled = seekToPreviousButtonEnabled,
        onSeekToNextButtonClick = onSeekToNextButtonClick,
        seekToNextButtonEnabled = seekToNextButtonEnabled,
        showProgress = false,
        modifier = modifier,
        colors = colors,
    )
}

@ExperimentalHorologistMediaUiApi
@Composable
internal fun MediaControlButtons(
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    playPauseEnabled: Boolean,
    playing: Boolean,
    onSeekToPreviousButtonClick: () -> Unit,
    seekToPreviousButtonEnabled: Boolean,
    onSeekToNextButtonClick: () -> Unit,
    seekToNextButtonEnabled: Boolean,
    showProgress: Boolean,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.iconButtonColors(),
    percent: Float? = null,
    progressColour: Color = MaterialTheme.colors.primaryVariant,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        SeekToPreviousButton(
            onClick = onSeekToPreviousButtonClick,
            enabled = seekToPreviousButtonEnabled,
            colors = colors,
        )

        Spacer(modifier = Modifier.sizeIn(maxWidth = 12.dp))

        if (showProgress) {
            checkNotNull(percent)

            PlayPauseProgressButton(
                onPlayClick = onPlayClick,
                onPauseClick = onPauseClick,
                enabled = playPauseEnabled,
                playing = playing,
                percent = percent,
                modifier = Modifier.size(ButtonDefaults.LargeButtonSize),
                colors = colors,
                progressColour = progressColour,
            )
        } else {
            PlayPauseButton(
                onPlayClick = onPlayClick,
                onPauseClick = onPauseClick,
                enabled = playPauseEnabled,
                playing = playing,
                modifier = Modifier.size(ButtonDefaults.LargeButtonSize),
                colors = colors,
            )
        }

        Spacer(modifier = Modifier.sizeIn(maxWidth = 12.dp))

        SeekToNextButton(
            onClick = onSeekToNextButtonClick,
            enabled = seekToNextButtonEnabled,
            colors = colors,
        )
    }
}
