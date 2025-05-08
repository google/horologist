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

package com.google.android.horologist.media.ui.material3.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButtonColors
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import com.google.android.horologist.images.base.paintable.Paintable
import com.google.android.horologist.media.ui.material3.colorscheme.DisabledContainerAlpha
import com.google.android.horologist.media.ui.material3.colorscheme.toDisabledColor
import com.google.android.horologist.media.ui.material3.composables.UnboundedRippleIconButton

/**
 * A base button for custom action media controls.
 *
 * @param onClick Will be called when the user clicks the button.
 * @param icon The icon to display inside the button.
 * @param contentDescription The content description for the icon.
 * @param modifier Modifier to be applied to the button.
 * @param enabled Controls the enabled state of the button. When `false`, this button will not be
 *   clickable (Optional).
 * @param shape Defines the shape for this button. Defaults to [CircleShape] (Optional).
 * @param colors [IconButtonColors] that will be used to resolve the background and icon color for
 *   this button in different states (Optional).
 * @param buttonPadding The padding to be applied around the button. Defafults to Zero (Optional).
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 *   emitting [Interaction]s for this button (Optional).
 * @param buttonSize The size of the button (Optional).
 */
@Composable
public fun CustomActionMediaButton(
    onClick: () -> Unit,
    icon: Paintable,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CircleShape,
    colors: IconButtonColors = CustomActionMediaButtonDefaults.buttonColors(),
    buttonPadding: PaddingValues = PaddingValues(0.dp),
    interactionSource: MutableInteractionSource? = null,
    buttonSize: Dp = IconButtonDefaults.DefaultButtonSize,
    border: BorderStroke? = null,
) {
    UnboundedRippleIconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        shape = shape,
        buttonPadding = buttonPadding,
        interactionSource = interactionSource,
        border = border,
    ) {
        Icon(
            painter = icon.rememberPainter(),
            contentDescription = contentDescription,
            modifier = Modifier.size(IconButtonDefaults.iconSizeFor(buttonSize)),
        )
    }
}

/** Default values for [CustomActionMediaButton]. */
public object CustomActionMediaButtonDefaults {
    @Composable
    public fun buttonColors(
        colorScheme: ColorScheme = MaterialTheme.colorScheme,
    ): IconButtonColors = IconButtonDefaults.filledIconButtonColors(
        containerColor = colorScheme.secondary,
        contentColor = colorScheme.onSecondary,
        disabledContainerColor = colorScheme.onSurface.toDisabledColor(DisabledContainerAlpha),
        disabledContentColor = colorScheme.onSurface.toDisabledColor(),
    )
}
