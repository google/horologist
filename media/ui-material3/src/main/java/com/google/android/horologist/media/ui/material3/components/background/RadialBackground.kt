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

package com.google.android.horologist.media.ui.material3.components.background

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.wear.compose.material3.MaterialTheme

@Composable
public fun RadialBackground(
    color: Color?,
    background: Color = MaterialTheme.colorScheme.background,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(radialBackgroundBrush(color = color, background = background)),
    )
}

public fun radialBackgroundBrush(
    color: Color?,
    background: Color,
): Brush = Brush.radialGradient(
    listOf(
        (color ?: Color.Black).copy(alpha = 0.5f).compositeOver(background),
        background,
    ),
)
