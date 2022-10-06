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

package com.google.android.horologist.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.lang.Float.max
import java.lang.Float.min

@Composable
fun SquareSegmentedProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float,
) {
    // TODO: Require progress to be more than 0 and less or 1

    val trackColor = Color.Red

    Canvas(
        modifier = modifier
            .background(color = Color.Blue)
            .padding(36.dp)
    ) {
        val cornerRadius = this.size.height / 8f // 1/8 of the line
        val cornerDiameter = cornerRadius * 2

        val arcSize = Size(width = cornerDiameter, height = cornerDiameter)

        // Top Right Corner
        if (progress > 0f) {
            drawArc(
                color = trackColor,
                startAngle = 270f,
                sweepAngle = SquareSegmentedProgress().calculateSweepDegrees(
                    CornerType.TopEnd,
                    progress = progress
                ),
                useCenter = false,
                style = Stroke(width = 20f),
                size = arcSize,
                topLeft = Offset(this.size.width - cornerDiameter, 0f)
            )
        }

        // Right line
        if (progress > 0.1f) {
            val lineOffsetEnd = SquareSegmentedProgress().calculateLineProgress(
                progress = progress,
                lineType = LineType.End,
                size = this.size,
                cornerRadius = cornerRadius,
            )
            // Right Line
            drawLine(
                color = trackColor,
                start = lineOffsetEnd.first,
                end = lineOffsetEnd.second,
                strokeWidth = 20f
            )


        }
        // Bottom Right Corner
        if (progress > 0.2) {
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = SquareSegmentedProgress().calculateSweepDegrees(
                    CornerType.BottomEnd,
                    progress = progress
                ),
                useCenter = false,
                style = Stroke(width = 20f),
                size = arcSize,
                topLeft = Offset(
                    this.size.width - cornerDiameter,
                    this.size.height - cornerDiameter
                )
            )
        }

        // Bottom Line
        if (progress > 0.3f) {
            val lineOffsetBottom = SquareSegmentedProgress().calculateLineProgress(
                progress = progress,
                lineType = LineType.Bottom,
                size = this.size,
                cornerRadius = cornerRadius,
            )
            // Bottom Line
            drawLine(
                color = trackColor,
                start = lineOffsetBottom.first,
                end = lineOffsetBottom.second,
                strokeWidth = 20f
            )
        }

        // Bottom Right Corner
        if (progress > 0.45) {
            // Bottom Left Corner
            drawArc(
                color = trackColor,
                startAngle = 90f,
                sweepAngle = SquareSegmentedProgress().calculateSweepDegrees(
                    CornerType.BottomStart,
                    progress
                ),
                useCenter = false,
                style = Stroke(width = 20f),
                size = arcSize,
                topLeft = Offset(0f, this.size.height - cornerDiameter)
            )
        }

        // Start Line
        if (progress > 0.55f) {
            val lineOffsetLeft = SquareSegmentedProgress().calculateLineProgress(
                progress = progress,
                lineType = LineType.Start,
                size = this.size,
                cornerRadius = cornerRadius,
            )
            // Left Line
            drawLine(
                color = trackColor,
                start = lineOffsetLeft.first,
                end = lineOffsetLeft.second,
                strokeWidth = 20f
            )
        }

        // Top Right Corner
        if (progress > 0.70f) {
            drawArc(
                color = trackColor,
                startAngle = 180f,
                sweepAngle = SquareSegmentedProgress().calculateSweepDegrees(
                    cornerType = CornerType.TopStart,
                    progress = progress,
                ),
                useCenter = false,
                style = Stroke(width = 20f),
                size = arcSize
            )
        }

        // Top Line
        if (progress > 0.80f) {
            val lineOffsetTop = SquareSegmentedProgress().calculateLineProgress(
                progress = progress,
                lineType = LineType.Top,
                size = this.size,
                cornerRadius = cornerRadius,
            )
            drawLine(
                color = trackColor,
                start = lineOffsetTop.first,
                end = lineOffsetTop.second,
                strokeWidth = 20f,
            )
        }
    }
}

class SquareSegmentedProgress {

    /**
     * Returns Start and End Offset for the line.
     *
     * @param progress
     * @param lineType
     */
    fun calculateLineProgress(
        progress: Float,
        lineType: LineType,
        size: Size,
        cornerRadius: Float,
    ): Pair<Offset, Offset> {
        return when (lineType) {
            LineType.Top -> {
                val startX = cornerRadius
                val startY = 0f
                val endX = min(
                    ((progress - 0.75f) * 4) * size.width - cornerRadius,
                    size.width - cornerRadius
                )
                val endY = 0f

                Offset(startX, startY) to Offset(endX, endY)
            }
            LineType.End -> {
                val startX = size.width
                val startY = cornerRadius
                val endX = size.width
                val endY =
                    min((progress * 4) * size.height + cornerRadius, size.height - cornerRadius)

                Offset(startX, startY) to Offset(endX, endY)
            }
            LineType.Start -> {
                val startX = 0f
                val startY = size.height - cornerRadius
                val endX = 0f
                val endY = max(
                    size.height - cornerRadius - ((progress - 0.5f) * 4) * size.height,
                    cornerRadius
                )

                Offset(startX, startY) to Offset(endX, endY)
            }
            LineType.Bottom -> {
                val startX = size.width - cornerRadius
                val startY = size.height

                val endX = max(
                    size.width + cornerRadius - ((progress - 0.2f) * 4) * size.width,
                    cornerRadius
                )
                val endY = size.height

                Offset(startX, startY) to Offset(endX, endY)
            }
        }
    }

    /**
     * Returns the startSweep and sweepDegrees for the corners of
     * the rounded rectangle.
     */
    fun calculateSweepDegrees(
        cornerType: CornerType,
        progress: Float,
    ): Float {
        return when (cornerType) {
            CornerType.TopStart -> {
                if (progress < 0.8f) {
                    (progress - 0.7f) * 10 * 90
                } else {
                    90f
                }
            }
            CornerType.TopEnd -> {
                if (progress < 0.10) {
                    (progress) * 10 * 90
                } else {
                    90f
                }
            }
            CornerType.BottomStart -> {
                if (progress < 0.55f) {
                    (progress - 0.45f) * 10 * 90
                } else {
                    90f
                }
            }
            CornerType.BottomEnd -> {
                if (progress < 0.3) {
                    (progress - 0.2f) * 10 * 90
                } else {
                    90f
                }
            }
        }
    }
}

enum class LineType {
    Top, End, Start, Bottom,
}

enum class CornerType {
    TopStart, TopEnd, BottomStart, BottomEnd,
}

@Preview(device = Devices.WEAR_OS_SQUARE)
@Composable
fun Preview() {
    val switch = remember {
        mutableStateOf(false)
    }
    val progressState = remember {
        mutableStateOf(0f)
    }
    val progress by animateFloatAsState(
        targetValue = progressState.value,
        animationSpec = spring(
            dampingRatio = 0.2f,
            stiffness = 0.1f,
        )
    )

    SquareSegmentedProgressIndicator(
        modifier = Modifier
            .height(300.dp)
            .width(300.dp)
            .clickable {
                switch.value = !switch.value
                progressState.value = if (switch.value) 1f else 0f
            },
        progress = progress
    )
}

@Preview(device = Devices.WEAR_OS_SQUARE)
@Composable
fun PreviewNoAnim() {

    SquareSegmentedProgressIndicator(
        modifier = Modifier
            .height(300.dp)
            .width(300.dp),
        progress = 0.55f
    )
}
