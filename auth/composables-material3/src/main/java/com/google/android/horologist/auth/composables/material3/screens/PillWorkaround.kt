/*
 * Copyright 2026 The Android Open Source Project
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

package com.google.android.horologist.auth.composables.material3.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

internal data class PointNRound(val o: Offset, val r: CornerRounding = CornerRounding.Unrounded)

private fun Float.toRadians(): Float = (this * Math.PI / 180f).toFloat()

private fun Offset.angleDegrees(): Float =
    atan2(y, x) * 180f / Math.PI.toFloat()

private fun Offset.rotateDegrees(angle: Float, center: Offset): Offset {
    val rad = angle.toRadians()
    val cos = cos(rad)
    val sin = sin(rad)
    val dx = x - center.x
    val dy = y - center.y
    return Offset(
        dx * cos - dy * sin + center.x,
        dx * sin + dy * cos + center.y
    )
}

internal fun doRepeat(points: List<PointNRound>, reps: Int, center: Offset, mirroring: Boolean) =
    if (mirroring) {
        buildList {
            val angles = points.map { (it.o - center).angleDegrees() }
            val distances = points.map { (it.o - center).getDistance() }
            val actualReps = reps * 2
            val sectionAngle = 360f / actualReps
            repeat(actualReps) {
                points.indices.forEach { index ->
                    val i = if (it % 2 == 0) index else points.lastIndex - index
                    if (i > 0 || it % 2 == 0) {
                        val a =
                            (sectionAngle * it +
                                    if (it % 2 == 0) angles[i] else sectionAngle - angles[i] + 2 * angles[0])
                                .toRadians()
                        val finalPoint = Offset(cos(a), sin(a)) * distances[i] + center
                        add(PointNRound(finalPoint, points[i].r))
                    }
                }
            }
        }
    } else {
        points.size.let { np ->
            (0 until np * reps).map {
                val point = points[it % np].o.rotateDegrees((it / np) * 360f / reps, center)
                PointNRound(point, points[it % np].r)
            }
        }
    }

internal fun customPolygon(
    points: List<PointNRound>,
    reps: Int,
    mirroring: Boolean,
    center: Offset = Offset(0.5f, 0.5f)
): RoundedPolygon {
    val repeatedPoints = doRepeat(points, reps, center, mirroring)
    val vertices = FloatArray(repeatedPoints.size * 2)
    val roundings = mutableListOf<CornerRounding>()
    for (i in repeatedPoints.indices) {
        vertices[2 * i] = repeatedPoints[i].o.x
        vertices[2 * i + 1] = repeatedPoints[i].o.y
        roundings.add(repeatedPoints[i].r)
    }
    return RoundedPolygon(
        vertices = vertices,
        perVertexRounding = roundings,
        centerX = center.x,
        centerY = center.y
    )
}

internal fun pill(): RoundedPolygon {
    return customPolygon(
        listOf(
            PointNRound(Offset(0.961f, 0.039f), CornerRounding(0.426f)),
            PointNRound(Offset(1.001f, 0.428f)),
            PointNRound(Offset(1.000f, 0.609f), CornerRounding(1.000f)),
        ),
        reps = 2,
        mirroring = true,
    )
}

@Composable
internal fun RoundedPolygon.toShape(): Shape {
    return remember(this) {
        object : Shape {
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
                workPath.transform(scaleMatrix)
                workPath.translate(size.center - workPath.getBounds().center)
                return Outline.Generic(workPath)
            }
        }
    }
}
