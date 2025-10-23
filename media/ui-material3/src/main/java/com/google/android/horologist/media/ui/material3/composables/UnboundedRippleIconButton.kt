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

package com.google.android.horologist.media.ui.material3.composables

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.IconButtonColors
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.LocalContentColor
import androidx.wear.compose.material3.LocalTextStyle
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.minimumInteractiveComponentSize
import androidx.wear.compose.material3.ripple

/**
 * A button with slot for an icon and a ripple effect that is unbounded.
 *
 * @param onClick The action to perform when the button is clicked.
 * @param modifier The [Modifier] to apply to this button.
 * @param enabled Whether the button is enabled or not.
 * @param colors The [IconButtonColors] to use for the button.
 * @param interactionSource The [MutableInteractionSource] to use for the button.
 * @param rippleRadius The radius of the ripple effect in [Dp].
 * @param shape The [Shape] to use for the button.
 * @param buttonPadding The padding to be applied around the button. Defafults to Zero.
 * @param border The [BorderStroke] to use for the button.
 * @param animationSpec The [FiniteAnimationSpec] to use for the button.
 * @param content The content of the button where the icon is placed.
 */
@Composable
public fun UnboundedRippleIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    interactionSource: MutableInteractionSource? = null,
    rippleRadius: Dp? = Dp.Unspecified,
    shape: Shape = CircleShape,
    buttonPadding: PaddingValues = PaddingValues(0.dp),
    border: BorderStroke? = null,
    animationSpec: FiniteAnimationSpec<Color> = MaterialTheme.motionScheme.slowEffectsSpec(),
    content: @Composable BoxScope.() -> Unit,
) {
    val contentColor = remember(colors, enabled) {
        colors.run { if (enabled) contentColor else disabledContentColor }
    }
    val containerColor = remember(colors, enabled) {
        colors.run { if (enabled) containerColor else disabledContainerColor }
    }

    val animatedContainerColor = remember { Animatable(containerColor) }

    LaunchedEffect(containerColor) {
        animatedContainerColor.animateTo(containerColor, animationSpec)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .size(
                if (rippleRadius == null || rippleRadius == Dp.Unspecified) {
                    IconButtonDefaults.DefaultButtonSize
                } else {
                    (rippleRadius * 2f)
                },
            )
            .clickable(
                interactionSource = interactionSource,
                indication = rippleRadius?.let { ripple(bounded = false, radius = it) },
                onClick = onClick,
                role = Role.Button,
                enabled = enabled,
            )
            .padding(buttonPadding)
            .clip(shape)
            .then(
                if (border != null) {
                    Modifier.border(border = border, shape = shape)
                } else {
                    Modifier
                },
            )
            .background(color = animatedContainerColor.value).clip(shape),
    ) {
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            LocalTextStyle provides MaterialTheme.typography.labelMedium,
        ) {
            content()
        }
    }
}
