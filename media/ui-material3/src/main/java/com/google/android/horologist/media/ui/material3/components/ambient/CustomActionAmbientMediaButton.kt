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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.IconButtonColors
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import com.google.android.horologist.images.base.paintable.Paintable
import com.google.android.horologist.media.ui.material3.components.CustomActionMediaButton
import com.google.android.horologist.media.ui.material3.components.controls.MediaButtonDefaults

/**
 * A base button to display in ambient mode for custom action media controls.
 *
 * @param onClick Will be called when the user clicks the button.
 * @param icon The icon to display inside the button.
 * @param contentDescription The content description for the icon.
 * @param modifier Modifier to be applied to the button.
 * @param enabled Controls the enabled state of the button. When `false`, this button will not be
 *   clickable (Optional).
 * @param shape Defines the shape for this button (Optional).
 * @param colorScheme The [ColorScheme] used for the button (Optional).
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 *   emitting interactions for this button (Optional).
 * @param buttonSize The size of the button (Optional).
 * @param buttonPadding The padding around the button (Optional).
 * @param colors [IconButtonColors] that will be used to resolve the background and icon color for
 *   this button in different states (Optional).
 * @param border Optional [BorderStroke] to be applied to the button. If null, no border is drawn.
 *   Defaults to a thin border with primary-dim color and `0.5f` alpha.
 */
@Composable
public fun CustomActionAmbientMediaButton(
    onClick: () -> Unit,
    icon: Paintable,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CircleShape,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    interactionSource: MutableInteractionSource? = null,
    buttonSize: Dp = IconButtonDefaults.DefaultButtonSize,
    buttonPadding: PaddingValues = PaddingValues.Zero,
    colors: IconButtonColors = MediaButtonDefaults.mediaButtonAmbientColors(colorScheme),
    border: BorderStroke? =
        BorderStroke(1.dp, colorScheme.primaryDim.copy(alpha = 0.5f)),
) {
    CustomActionMediaButton(
        onClick = onClick,
        icon = icon,
        contentDescription = contentDescription,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        buttonPadding = buttonPadding,
        interactionSource = interactionSource,
        buttonSize = buttonSize,
        border = border,
    )
}
