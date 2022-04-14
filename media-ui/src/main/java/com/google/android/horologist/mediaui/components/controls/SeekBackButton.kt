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

package com.google.android.horologist.mediaui.components.controls

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Replay30
import androidx.compose.material.icons.filled.Replay5
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import com.google.android.horologist.media.ui.R
import com.google.android.horologist.mediaui.ExperimentalMediaUiApi

@ExperimentalMediaUiApi
@Composable
public fun SeekBackButton(
    onClick: () -> Unit,
    enabled: Boolean,
    seekButtonIncrement: SeekButtonIncrement,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.iconButtonColors(),
) {
    val icon = when (seekButtonIncrement) {
        SeekButtonIncrement.Five -> Icons.Default.Replay5
        SeekButtonIncrement.Ten -> Icons.Default.Replay10
        SeekButtonIncrement.Thirty -> Icons.Default.Replay30
        else -> Icons.Default.Replay
    }

    val contentDescription = when (seekButtonIncrement) {
        SeekButtonIncrement.Unknown -> stringResource(id = R.string.seek_back_button_rewind)
        else -> stringResource(
            id = R.string.seek_back_button_rewind_seconds,
            seekButtonIncrement.seconds
        )
    }

    MediaButton(
        onClick = onClick,
        enabled = enabled,
        icon = icon,
        contentDescription = contentDescription,
        modifier = modifier,
        colors = colors,
    )
}
