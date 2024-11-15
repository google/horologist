/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.media3.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.ui.compose.state.rememberPlayPauseButtonState
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.media.ui.components.PlayPauseButton

@Composable
public fun PlayPauseButton(
    player: Player,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.iconButtonColors(),
    iconSize: Dp = 32.dp,
    backgroundColor: Color = MaterialTheme.colors.onBackground.copy(alpha = 0.10f),
    progress: @Composable () -> Unit = {},
) {
    val state = rememberPlayPauseButtonState(player)
    PlayPauseButton(
        onPlayClick = state::onClick,
        onPauseClick = state::onClick,
        playing = !state.showPlay,
        modifier = modifier,
        enabled = state.isEnabled,
        colors = colors,
        iconSize = iconSize,
        backgroundColor = backgroundColor,
        progress = progress,
    )
}
