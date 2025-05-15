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

package com.google.android.horologist.media.ui.material3.components.controls

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.IconButtonColors
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import com.google.android.horologist.media.ui.components.controls.SeekButtonIncrement
import com.google.android.horologist.media.ui.model.R

@Composable
public fun SeekBackButton(
    onClick: () -> Unit,
    seekButtonIncrement: SeekButtonIncrement,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    icon: ImageVector = MediaButtonDefaults.seekBackIcon(seekButtonIncrement),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    buttonPadding: PaddingValues = PaddingValues(0.dp),
    iconSize: Dp = IconButtonDefaults.LargeIconSize,
    colors: IconButtonColors = MediaButtonDefaults.mediaButtonDefaultColors(colorScheme),
) {
    val contentDescription = when (seekButtonIncrement) {
        is SeekButtonIncrement.Known -> stringResource(
            id = R.string.horologist_seek_back_button_seconds_content_description,
            seekButtonIncrement.seconds,
        )
        SeekButtonIncrement.Unknown -> stringResource(id = R.string.horologist_seek_back_button_content_description)
    }

    MediaButton(
        onClick = onClick,
        icon = icon,
        contentDescription = contentDescription,
        modifier = modifier,
        enabled = enabled,
        colorScheme = colorScheme,
        iconSize = iconSize,
        interactionSource = interactionSource,
        buttonPadding = buttonPadding,
        colors = colors,
    )
}
