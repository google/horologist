/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.wear.compose.material.ButtonBorder
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.LocalContentAlpha
import androidx.wear.compose.material.LocalContentColor
import androidx.wear.compose.material.LocalTextStyle
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.annotations.ExperimentalHorologistApi

/**
 * A base button that can send single onClick event or repeated [onLongRepeatableClick] events by
 * holding it down.
 *
 * Code modified from https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:wear/compose/compose-material/src/main/java/androidx/wear/compose/material/Button.kt
 *
 */
@ExperimentalHorologistApi
@Composable
public fun RepeatableClickableButton(
    onClick: () -> Unit,
    onLongRepeatableClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongRepeatableClickEnd: () -> Unit = {},
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.primaryButtonColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = CircleShape,
    border: ButtonBorder = ButtonDefaults.buttonBorder(),
    content: @Composable BoxScope.() -> Unit,
) {
    val borderStroke = border.borderStroke(enabled = enabled).value

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .defaultMinSize(
                    minWidth = ButtonDefaults.DefaultButtonSize,
                    minHeight = ButtonDefaults.DefaultButtonSize,
                )
                .then(
                    if (borderStroke != null) {
                        Modifier.border(border = borderStroke, shape = shape)
                    } else {
                        Modifier
                    },
                )
                .clip(shape)
                .repeatableClickable(
                    enabled = enabled,
                    role = Role.Button,
                    indication = rememberRipple(),
                    onClick = onClick,
                    onLongRepeatableClick = onLongRepeatableClick,
                    onLongRepeatableClickEnd = onLongRepeatableClickEnd,
                    interactionSource = interactionSource,
                )
                .background(color = colors.backgroundColor(enabled = enabled).value, shape = shape),
    ) {
        val contentColor = colors.contentColor(enabled = enabled).value
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            LocalContentAlpha provides contentColor.alpha,
            LocalTextStyle provides MaterialTheme.typography.button,
        ) {
            content()
        }
    }
}
