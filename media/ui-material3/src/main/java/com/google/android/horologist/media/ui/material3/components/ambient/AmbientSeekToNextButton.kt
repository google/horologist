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

package com.google.android.horologist.media.ui.material3.components.ambient

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.IconButtonColors
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import com.google.android.horologist.media.ui.material3.R
import com.google.android.horologist.media.ui.material3.components.controls.MediaButton
import com.google.android.horologist.media.ui.material3.components.controls.MediaButtonDefaults

/**
 * An animated seek-to-next button to display in the ambient mode.
 *
 * @param onClick The callback to be invoked when the button is clicked.
 * @param modifier Optional [Modifier] to be applied to the button.
 * @param enabled Controls the enabled state of the button. When `false`, the button is disabled.
 * @param colorScheme The [ColorScheme] used for the button.
 * @param icon Optional [ImageVector] to draw inside this button. If not provided, a default
 *   next icon will be displayed.
 * @param iconSize The size of the icon to be displayed on the button. Defaults to
 *   [IconButtonDefaults.SmallIconSize].
 * @param buttonPadding the padding around the button.
 * @param colors [IconButtonColors] that will be used to resolve the colors used for this button in
 *   different states. Defaults to [MediaButtonDefaults.mediaButtonAmbientColors].
 * @param border Optional [BorderStroke] to be applied to the button. If null, no border is drawn.
 *   Defaults to a thin border with primary-dim color and `0.5f` alpha.
 */
@Composable
public fun AmbientSeekToNextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    icon: ImageVector = ImageVector.vectorResource(R.drawable.rounded_skip_next_24),
    iconSize: Dp = IconButtonDefaults.SmallIconSize,
    buttonPadding: PaddingValues = PaddingValues.Zero,
    colors: IconButtonColors = MediaButtonDefaults.mediaButtonAmbientColors(colorScheme),
    border: BorderStroke? = BorderStroke(1.dp, colorScheme.primaryDim.copy(alpha = 0.5f)),
) {
    MediaButton(
        onClick = onClick,
        icon = icon,
        contentDescription =
            stringResource(com.google.android.horologist.media.ui.model.R.string.horologist_seek_to_next_button_content_description),
        modifier = modifier.padding(buttonPadding),
        enabled = enabled,
        iconSize = iconSize,
        colors = colors,
        border = border,
    )
}
