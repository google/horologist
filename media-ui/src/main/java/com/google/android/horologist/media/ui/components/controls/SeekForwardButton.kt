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
import androidx.compose.material.icons.filled.Redo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import com.google.android.horologist.media.ui.ExperimentalMediaUiApi
import com.google.android.horologist.media.ui.R

@ExperimentalMediaUiApi
@Composable
public fun SeekForwardButton(
    onClick: () -> Unit,
    seekButtonIncrement: SeekButtonIncrement,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.iconButtonColors(),
) {
    val icon = when (seekButtonIncrement) {
        SeekButtonIncrement.Five -> Icons.Default.Forward5
        SeekButtonIncrement.Ten -> Icons.Default.Forward10
        SeekButtonIncrement.Thirty -> Icons.Default.Forward30
        // Forward is a straight arrow
        else -> Icons.Default.Redo
    }

    val contentDescription = when (seekButtonIncrement) {
        SeekButtonIncrement.Unknown -> stringResource(id = R.string.seek_forward_button_forward)
        else -> stringResource(
            id = R.string.seek_forward_button_forward_seconds,
            seekButtonIncrement.seconds
        )
    }

    MediaButton(
        onClick = onClick,
        icon = icon,
        contentDescription = contentDescription,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
    )
}
