/*
 * Copyright 2022 The Android Open Source Project
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

package com.google.android.horologist.compose.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Shapes
import androidx.wear.compose.material.Typography

/**
 * WearMaterialTheme extends [MaterialTheme] to support Dimensions.
 */
@Composable
public fun WearMaterialTheme(
    colors: Colors = MaterialTheme.colors,
    typography: Typography = MaterialTheme.typography,
    shapes: Shapes = MaterialTheme.shapes,
    dimensions: Dimensions = roundDimensions(),
    content: @Composable () -> Unit
) {
    val rememberDimensions = remember {
        dimensions.copy()
    }
    CompositionLocalProvider(
        LocalDimensions provides rememberDimensions
    ) {
        MaterialTheme(colors, typography, shapes, content)
    }
}

public object WearMaterialTheme {
    public val colors: Colors
        @ReadOnlyComposable
        @Composable
        get() = MaterialTheme.colors

    public val typography: Typography
        @ReadOnlyComposable
        @Composable
        get() = MaterialTheme.typography

    public val shapes: Shapes
        @ReadOnlyComposable
        @Composable
        get() = MaterialTheme.shapes

    public val dimensions: Dimensions
        @ReadOnlyComposable
        @Composable
        get() = LocalDimensions.current
}
