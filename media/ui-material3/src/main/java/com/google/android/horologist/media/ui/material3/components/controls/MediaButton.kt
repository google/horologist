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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Forward30
import androidx.compose.material.icons.filled.Forward5
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Replay30
import androidx.compose.material.icons.filled.Replay5
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButtonColors
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import com.google.android.horologist.media.ui.components.controls.SeekButtonIncrement
import com.google.android.horologist.media.ui.material3.colorscheme.DisabledContainerAlpha
import com.google.android.horologist.media.ui.material3.colorscheme.toDisabledColor
import com.google.android.horologist.media.ui.material3.composables.UnboundedRippleIconButton

/** A base button for media controls. */
@Composable
public fun MediaButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    buttonPadding: PaddingValues = PaddingValues(0.dp),
    iconSize: Dp = IconButtonDefaults.LargeIconSize,
    shape: Shape = CircleShape,
    iconAlign: Alignment = Alignment.Center,
    colors: IconButtonColors = MediaButtonDefaults.mediaButtonDefaultColors(colorScheme),
    border: BorderStroke? = null,
) {
    UnboundedRippleIconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        border = border,
        shape = shape,
        interactionSource = interactionSource,
        rippleRadius = null,
        buttonPadding = buttonPadding,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize)
                .align(iconAlign),
        )
    }
}

/**
 * Provides default configurations for media buttons, including colors and icons.
 */
public object MediaButtonDefaults {
    /**
     * Provides default color configurations for standard media buttons.
     *
     * @return [IconButtonColors] representing the default colors for a media button.
     */
    @Composable
    public fun mediaButtonDefaultColors(
        colorScheme: ColorScheme = MaterialTheme.colorScheme,
    ): IconButtonColors = IconButtonDefaults.filledIconButtonColors(
        containerColor = colorScheme.primaryDim,
        contentColor = colorScheme.onPrimary,
        disabledContainerColor = colorScheme.onSurface.toDisabledColor(DisabledContainerAlpha),
        disabledContentColor = colorScheme.onSurface.toDisabledColor(),
    )

    /**
     * Provides ambient color configurations for media buttons, suitable for background-less
     * scenarios.
     *
     * @return [IconButtonColors] representing the ambient colors for a media button.
     */
    @Composable
    public fun mediaButtonAmbientColors(
        colorScheme: ColorScheme = MaterialTheme.colorScheme,
    ): IconButtonColors = IconButtonDefaults.filledIconButtonColors(
        containerColor = Color.Transparent,
        contentColor = colorScheme.primaryDim,
        disabledContainerColor = Color.Transparent,
        disabledContentColor = colorScheme.primaryDim,
    )

    /**
     * Provides default color configurations for play/pause buttons.
     *
     * @return [IconButtonColors] representing the default colors for a play/pause button.
     */
    @Composable
    public fun playPauseButtonDefaultColors(
        colorScheme: ColorScheme = MaterialTheme.colorScheme,
    ): IconButtonColors = IconButtonDefaults.filledIconButtonColors(
        containerColor = colorScheme.primary,
        contentColor = colorScheme.onPrimary,
        disabledContainerColor = colorScheme.onSurface.toDisabledColor(DisabledContainerAlpha),
        disabledContentColor = colorScheme.onSurface.toDisabledColor(),
    )

    /**
     * Provides the appropriate [ImageVector] for a seek back button based on the seek increment.
     *
     * @param seekButtonIncrement The [SeekButtonIncrement] indicating the amount of time to seek
     *   back.
     * @return The [ImageVector] representing the seek back action.
     */
    public fun seekBackIcon(seekButtonIncrement: SeekButtonIncrement): ImageVector =
        when (seekButtonIncrement) {
            SeekButtonIncrement.Five -> Icons.Default.Replay5
            SeekButtonIncrement.Ten -> Icons.Default.Replay10
            SeekButtonIncrement.Thirty -> Icons.Default.Replay30
            else -> Icons.Default.Replay
        }

    /**
     * Provides the appropriate [ImageVector] for a seek forward button based on the seek increment.
     *
     * @param seekButtonIncrement The [SeekButtonIncrement] indicating the amount of time to seek
     *   forward.
     * @return The [ImageVector] representing the seek forward action.
     */
    public fun seekForwardIcon(seekButtonIncrement: SeekButtonIncrement): ImageVector =
        when (seekButtonIncrement) {
            SeekButtonIncrement.Five -> Icons.Default.Forward5
            SeekButtonIncrement.Ten -> Icons.Default.Forward10
            SeekButtonIncrement.Thirty -> Icons.Default.Forward30
            else -> ForwardEmpty
        }

    // Icons.Default.Forward is not the same group as 5, 10 and 30 variant
    private val ForwardEmpty = materialIcon(name = "Filled.ForwardEmpty") {
        materialPath {
            moveTo(18.0f, 13.0f)
            curveToRelative(0.0f, 3.31f, -2.69f, 6.0f, -6.0f, 6.0f)
            reflectiveCurveToRelative(-6.0f, -2.69f, -6.0f, -6.0f)
            reflectiveCurveToRelative(2.69f, -6.0f, 6.0f, -6.0f)
            verticalLineToRelative(4.0f)
            lineToRelative(5.0f, -5.0f)
            lineToRelative(-5.0f, -5.0f)
            verticalLineToRelative(4.0f)
            curveToRelative(-4.42f, 0.0f, -8.0f, 3.58f, -8.0f, 8.0f)
            curveToRelative(0.0f, 4.42f, 3.58f, 8.0f, 8.0f, 8.0f)
            reflectiveCurveToRelative(8.0f, -3.58f, 8.0f, -8.0f)
            horizontalLineTo(18.0f)
            close()
        }
    }
}
