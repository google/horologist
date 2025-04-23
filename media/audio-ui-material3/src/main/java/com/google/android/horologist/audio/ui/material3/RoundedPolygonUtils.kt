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

package com.google.android.horologist.audio.ui.material3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath

@Composable
internal fun RoundedPolygon.toShape(startAngle: Int = 0): Shape {
    return remember(this, startAngle) {
        object : Shape {
            // Store the Path we convert from the RoundedPolygon here. The path we will be
            // manipulating and using on the createOutline would be a copy of this to ensure we
            // don't mutate the original.
            private val shapePath: Path = toPath().asComposePath()
            private val workPath: Path = Path()

            override fun createOutline(
                size: Size,
                layoutDirection: LayoutDirection,
                density: Density,
            ): Outline {
                workPath.rewind()
                workPath.addPath(shapePath)
                val scaleMatrix = Matrix().apply { scale(x = size.width, y = size.height) }
                // Scale and translate the path to align its center with the available size
                // center.
                workPath.transform(scaleMatrix)
                workPath.translate(size.center - workPath.getBounds().center)
                return Outline.Generic(workPath)
            }
        }
    }
}
