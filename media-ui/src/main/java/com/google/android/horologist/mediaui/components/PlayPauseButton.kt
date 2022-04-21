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

package com.google.android.horologist.mediaui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.mediaui.ExperimentalMediaUiApi
import com.google.android.horologist.mediaui.components.controls.PauseButton
import com.google.android.horologist.mediaui.components.controls.PlayButton

@ExperimentalMediaUiApi
@Composable
public fun PlayPauseButton(
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    playing: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.iconButtonColors(),
    progress: @Composable () -> Unit = {}
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        progress()

        if (playing) {
            PauseButton(
                onClick = onPauseClick,
                enabled = enabled,
                modifier = modifier,
                colors = colors,
            )
        } else {
            PlayButton(
                onClick = onPlayClick,
                enabled = enabled,
                modifier = modifier,
                colors = colors,
            )
        }
    }
}

@ExperimentalMediaUiApi
@Composable
public fun PlayPauseProgressButton(
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    playing: Boolean,
    percent: Float,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.iconButtonColors(),
    progressColour: Color = MaterialTheme.colors.primaryVariant,
) {
    PlayPauseButton(
        onPlayClick = onPlayClick,
        onPauseClick = onPauseClick,
        enabled = enabled,
        playing = playing,
        modifier = modifier,
        colors = colors
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(ButtonDefaults.LargeButtonSize),
            progress = percent.ifNan(0f),
            indicatorColor = progressColour,
        )
    }
}

private fun Float.ifNan(default: Float) = if (isNaN()) default else this
