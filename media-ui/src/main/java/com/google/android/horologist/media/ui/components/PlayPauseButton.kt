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

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.components.controls.PauseButton
import com.google.android.horologist.media.ui.components.controls.PlayButton
import com.google.android.horologist.media.ui.util.ifNan

@ExperimentalHorologistMediaUiApi
@Composable
public fun PlayPauseButton(
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    playing: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.iconButtonColors(),
    iconSize: Dp = 30.dp,
    tapTargetSize: DpSize = DpSize(60.dp, 60.dp),
    progress: @Composable () -> Unit = {}
) {
    Box(modifier = modifier.size(tapTargetSize), contentAlignment = Alignment.Center) {
        progress()

        if (playing) {
            PauseButton(
                onClick = onPauseClick,
                enabled = enabled,
                modifier = modifier,
                colors = colors,
                iconSize = iconSize,
                tapTargetSize = tapTargetSize
            )
        } else {
            PlayButton(
                onClick = onPlayClick,
                enabled = enabled,
                modifier = modifier,
                colors = colors,
                iconSize = iconSize,
                tapTargetSize = tapTargetSize
            )
        }
    }
}

@ExperimentalHorologistMediaUiApi
@Composable
public fun PlayPauseProgressButton(
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    playing: Boolean,
    percent: Float,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.iconButtonColors(),
    iconSize: Dp = 30.dp,
    tapTargetSize: DpSize = DpSize(60.dp, 60.dp),
    progressColour: Color = MaterialTheme.colors.primary,
    trackColor: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.10f),
    backgroundColor: Color = MaterialTheme.colors.onBackground.copy(alpha = 0.10f)
) {
    PlayPauseButton(
        onPlayClick = onPlayClick,
        onPauseClick = onPauseClick,
        enabled = enabled,
        playing = playing,
        modifier = modifier,
        colors = colors,
        iconSize = iconSize,
        tapTargetSize = tapTargetSize
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(backgroundColor)
        ) {
            val targetValue = percent.ifNan(0f)
            val progress by animateFloatAsState(
                targetValue = targetValue,
                animationSpec =
                if (targetValue < 1 / 1000f) {
                    TweenSpec(easing = LinearOutSlowInEasing)
                } else {
                    TweenSpec(durationMillis = 1000, easing = LinearEasing)
                }
            )
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize(),
                progress = progress,
                indicatorColor = progressColour,
                trackColor = trackColor
            )
        }
    }
}
