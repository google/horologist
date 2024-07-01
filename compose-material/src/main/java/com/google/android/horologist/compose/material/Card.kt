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

@file:OptIn(ExperimentalFoundationApi::class)

package com.google.android.horologist.compose.material

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.CardDefaults
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import androidx.wear.compose.material.Card as MaterialCard

/**
 * This component is an alternative to [Card], adding support for long and double-clicks.
 */
@ExperimentalHorologistApi
@Composable
public fun Card(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    backgroundPainter: Painter = CardDefaults.cardBackgroundPainter(),
    contentColor: Color = MaterialTheme.colors.onSurfaceVariant,
    enabled: Boolean = true,
    contentPadding: PaddingValues = CardDefaults.ContentPadding,
    shape: Shape = MaterialTheme.shapes.large,
    role: Role? = null,
    content: @Composable () -> Unit,
) {
    if (onLongClick != null) {
        val interactionSource = remember { MutableInteractionSource() }
        MaterialCard(
            onClick = onClick,
            modifier = modifier,
            backgroundPainter = backgroundPainter,
            contentColor = contentColor,
            enabled = enabled,
            contentPadding = PaddingValues(0.dp),
            shape = shape,
            interactionSource = interactionSource,
            role = role,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .combinedClickable(
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = enabled,
                        onClick = onClick,
                        onLongClick = onLongClick,
                        role = role,
                    )
                    .padding(contentPadding),
            ) {
                content()
            }
        }
    } else {
        MaterialCard(
            onClick = onClick,
            modifier = modifier,
            backgroundPainter = backgroundPainter,
            contentColor = contentColor,
            enabled = enabled,
            contentPadding = contentPadding,
            shape = shape,
            role = role,
        ) {
            content()
        }
    }
}
