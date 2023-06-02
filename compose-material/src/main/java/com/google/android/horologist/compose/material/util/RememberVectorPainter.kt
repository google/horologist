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

package com.google.android.horologist.compose.material.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.RenderVectorGroup
import androidx.compose.ui.graphics.vector.VectorPainter
import com.google.android.horologist.annotations.ExperimentalHorologistApi

/**
 * Create a [VectorPainter] with the Vector defined by the provided sub-composition and use a
 * different tint color than what is defined in [image].
 *
 * @param [image] ImageVector used to create a vector graphic sub-composition.
 * @param [tintColor] color used to tint the root group of this vector graphic.
 * @param [tintBlendMode] optional BlendMode used in combination with [tintColor], defaults to
 * value defined in [image].
 */
@ExperimentalHorologistApi
@Composable
public fun rememberVectorPainter(
    image: ImageVector,
    tintColor: Color,
    tintBlendMode: BlendMode = image.tintBlendMode
): VectorPainter =
    androidx.compose.ui.graphics.vector.rememberVectorPainter(
        defaultWidth = image.defaultWidth,
        defaultHeight = image.defaultHeight,
        viewportWidth = image.viewportWidth,
        viewportHeight = image.viewportHeight,
        name = image.name,
        tintColor = tintColor,
        tintBlendMode = tintBlendMode,
        autoMirror = image.autoMirror,
        content = { _, _ -> RenderVectorGroup(group = image.root) }
    )
