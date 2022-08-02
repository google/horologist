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

package com.google.android.horologist.media.ui.components.controls

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Forward30
import androidx.compose.material.icons.filled.Forward5
import androidx.compose.material.icons.filled.Replay
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ButtonColors
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.R

@ExperimentalHorologistMediaUiApi
@Composable
public fun SeekForwardButton(
    onClick: () -> Unit,
    seekButtonIncrement: SeekButtonIncrement,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = MediaButtonDefaults.mediaButtonDefaultColors,
    iconSize: Dp = 30.dp,
    tapTargetSize: DpSize = DpSize(48.dp, 60.dp),
) {
    val (icon, buttonModifier) = when (seekButtonIncrement) {
        SeekButtonIncrement.Five -> Pair(Icons.Default.Forward5, modifier)
        SeekButtonIncrement.Ten -> Pair(Icons.Default.Forward10, modifier)
        SeekButtonIncrement.Thirty -> Pair(Icons.Default.Forward30, modifier)
        else -> Pair(Icons.Default.Replay, modifier.graphicsLayer(scaleX = -1f))
    }

    val contentDescription = when (seekButtonIncrement) {
        SeekButtonIncrement.Unknown -> stringResource(id = R.string.horologist_seek_forward_button_content_description)
        else -> stringResource(
            id = R.string.horologist_seek_forward_button_seconds_content_description,
            seekButtonIncrement.seconds
        )
    }

    MediaButton(
        onClick = onClick,
        icon = icon,
        contentDescription = contentDescription,
        modifier = buttonModifier,
        enabled = enabled,
        colors = colors,
        iconSize = iconSize,
        tapTargetSize = tapTargetSize
    )
}
