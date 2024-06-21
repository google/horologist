/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.compose.material.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.LocalContentAlpha
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PlaceholderState
import androidx.wear.compose.material.placeholder
import androidx.wear.compose.material.placeholderShimmer
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.Icon
import com.google.android.horologist.images.base.paintable.Paintable
import com.google.android.horologist.images.base.paintable.PaintableIcon

@ExperimentalHorologistApi
@Composable
internal fun ChipIcon(
    icon: Paintable,
    largeIcon: Boolean,
    placeholderState: PlaceholderState?,
    contentDescription: String? = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
) {
    val iconSize = if (largeIcon) {
        ChipDefaults.LargeIconSize
    } else {
        ChipDefaults.IconSize
    }

    val iconModifier = Modifier
        .size(iconSize)
        .clip(CircleShape)
        .placeholderIf(placeholderState)

    if (icon is PaintableIcon) {
        Icon(
            paintable = icon,
            contentDescription = contentDescription,
            modifier = iconModifier,
        )
    } else {
        Image(
            painter = icon.rememberPainter(),
            contentDescription = contentDescription,
            modifier = iconModifier,
            contentScale = ContentScale.Crop,
            alpha = LocalContentAlpha.current,
        )
    }
}

@Composable
fun Modifier.placeholderIf(
    placeholderState: PlaceholderState?,
    shape: Shape = MaterialTheme.shapes.small,
    color: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
        .compositeOver(MaterialTheme.colors.surface),
): Modifier {
    return if (placeholderState != null) {
        this.placeholder(placeholderState, shape, color)
    } else {
        this
    }
}

@Composable
fun Modifier.placeholderShimmerIf(
    placeholderState: PlaceholderState?,
    shape: Shape = MaterialTheme.shapes.small,
    color: Color = MaterialTheme.colors.onSurface,
): Modifier {
    return if (placeholderState != null) {
        this.placeholderShimmer(placeholderState, shape, color)
    } else {
        this
    }
}
