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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.IconButtonColors
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.LocalContentColor
import androidx.wear.compose.material3.LocalTextStyle
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ripple

/**
 * A base button that can send single onClick event or repeated [onRepeatableClick] events by
 * holding it down.
 *
 * Code modified from https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:wear/compose/compose-material3/src/main/java/androidx/wear/compose/material3/IconButton.kt
 *
 * @param onClick the single click event
 * @param onRepeatableClick the repeated click event
 * @param onRepeatableClickEnd the event when the repeated click ends
 * @param enabled whether the button is enabled
 * @param shape the shape of the button
 * @param colors the colors of the button
 * @param border the border of the button
 * @param buttonPadding the padding around the button
 * @param indication the indication of the button
 * @param interactionSource the interaction source of the button
 * @param content the content of the button
 */
@Composable
public fun RepeatableClickableButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onRepeatableClick: () -> Unit = onClick,
    onRepeatableClickEnd: () -> Unit = {},
    enabled: Boolean = true,
    shape: Shape = CircleShape,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    border: BorderStroke? = null,
    buttonPadding: PaddingValues = PaddingValues(0.dp),
    indication: Indication? = ripple(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val contentColor = rememberUpdatedState(
        colors.run { if (enabled) contentColor else disabledContentColor },
    )
    val containerColor = rememberUpdatedState(
        colors.run { if (enabled) containerColor else disabledContainerColor },
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .repeatableClickable(
                    enabled = enabled,
                    role = Role.Button,
                    indication = indication,
                    onClick = onClick,
                    onRepeatableClick = onRepeatableClick,
                    onRepeatableClickEnd = onRepeatableClickEnd,
                    interactionSource = interactionSource,
                )
                .padding(buttonPadding)
                .clip(shape)
                .then(
                    if (border != null) {
                        Modifier.border(border, shape)
                    } else {
                        Modifier
                    },
                )
                .background(
                    color = containerColor.value,
                    shape = shape,
                ),
    ) {
        CompositionLocalProvider(
            LocalContentColor provides contentColor.value,
            LocalTextStyle provides MaterialTheme.typography.labelMedium,
        ) {
            content()
        }
    }
}
