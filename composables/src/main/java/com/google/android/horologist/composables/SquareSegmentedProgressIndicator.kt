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

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ProgressIndicatorDefaults
import androidx.wear.compose.material.Text
import java.lang.Float.max
import java.lang.Float.min

data class TrackSection(
    val progressColor: Color = Color.Red.copy(alpha = 0.55f).compositeOver(Color.Gray),
    val weight: Float,
)

val progressSections = listOf(
    TrackSection(weight = 2f),
    TrackSection(weight = 2f),
    TrackSection(weight = 2f),
    TrackSection(weight = 2f),
)

@Composable
fun SquareSegmentedProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float,
    strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
    indicatorColor: Color,
) {
    require(progress in 0.0..1.0) {
        Error("The progress must be between 0 and 1")
    }

    val trackColor = MaterialTheme.colors.onBackground.copy(alpha = 0.1f)

    val localDensity = LocalDensity.current

    val stroke = with(localDensity) {
        Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
    }

    val totalWeight = remember(progressSections) {
        progressSections.sumOf { it.weight.toDouble() }.toFloat()
    }

    val strokeEndDelta = stroke.width

    Canvas(
        modifier = modifier
            .background(color = MaterialTheme.colors.background)
            .padding(38.dp)
    ) {

        // Draw track
        drawRoundedSquare(
            canvasSize = this.size,
            progress = 1f,
            trackColor = trackColor,
            stroke = stroke,
            strokeEndDelta = strokeEndDelta,
            totalWeight = totalWeight,
        )

        // Draw progress
        drawRoundedSquare(
            canvasSize = this.size,
            progress = progress,
            trackColor = indicatorColor,
            stroke = stroke,
            strokeEndDelta = strokeEndDelta,
            totalWeight = totalWeight
        )
    }
}

private fun DrawScope.drawRoundedSquare(
    canvasSize: Size,
    progress: Float,
    trackColor: Color,
    stroke: Stroke,
    strokeEndDelta: Float,
    totalWeight: Float,
) {
    val cornerRadius = canvasSize.height / 4f // 1/4 of the line height
    val cornerDiameter = cornerRadius * 2

    val arcSize = Size(width = cornerDiameter, height = cornerDiameter)

    val sectionWeight = min(8 / totalWeight, 1f)

    val sectionProgress = 1f / 8f // 10 parts for 8 sections 0.125 is one part

    // Top Right Corner
    if (progress > 0f) {
        drawArc(
            color = trackColor,
            startAngle = 270f,
            sweepAngle = SquareSegmentedProgress().calculateSweepDegrees(
                CornerType.TopEnd,
                progress = progress,
                segmentAngle = sectionProgress,
                sectionAngle = sectionProgress,
            ),
            useCenter = false,
            style = stroke,
            size = arcSize,
            topLeft = Offset(canvasSize.width - cornerDiameter, 0f)
        )
    }

    // Right line
    if (progress > sectionWeight * sectionProgress) {
        val lineOffsetEnd = SquareSegmentedProgress().calculateLineProgress(
            progress = progress,
            lineType = LineType.End,
            size = canvasSize,
            cornerRadius = cornerRadius,
            strokeEndDelta = strokeEndDelta,
            sectionEndProgress = sectionWeight * sectionProgress
        )
        // Right Line
        drawLine(
            color = trackColor,
            start = lineOffsetEnd.first,
            end = lineOffsetEnd.second,
            strokeWidth = stroke.width,
            cap = stroke.cap,
        )
    }

    // Bottom Right Corner
    if (progress > sectionWeight * sectionProgress * 2) {
        drawArc(
            color = trackColor,
            startAngle = 0f,
            sweepAngle = SquareSegmentedProgress().calculateSweepDegrees(
                CornerType.BottomEnd,
                progress = progress,
                sectionAngle = sectionProgress,
                segmentAngle = sectionWeight * sectionProgress * 2
            ),
            useCenter = false,
            style = stroke,
            size = arcSize,
            topLeft = Offset(
                canvasSize.width - cornerDiameter,
                canvasSize.height - cornerDiameter
            )
        )
    }

    // Bottom Line
    if (progress > sectionWeight * sectionProgress * 3) {
        val lineOffsetBottom = SquareSegmentedProgress().calculateLineProgress(
            progress = progress,
            lineType = LineType.Bottom,
            size = canvasSize,
            cornerRadius = cornerRadius,
            strokeEndDelta = strokeEndDelta,
            sectionEndProgress = sectionWeight * sectionProgress * 3
        )
        // Bottom Line
        drawLine(
            color = trackColor,
            start = lineOffsetBottom.first,
            end = lineOffsetBottom.second,
            strokeWidth = stroke.width,
            cap = stroke.cap,
        )
    }

    // Bottom Left Corner
    if (progress > sectionWeight * sectionProgress * 4) {
        drawArc(
            color = trackColor,
            startAngle = 90f,
            sweepAngle = SquareSegmentedProgress().calculateSweepDegrees(
                CornerType.BottomStart,
                progress,
                segmentAngle = sectionProgress * 4 * sectionWeight,
                sectionAngle = sectionProgress
            ),
            useCenter = false,
            style = stroke,
            size = arcSize,
            topLeft = Offset(0f, canvasSize.height - cornerDiameter)
        )
    }

    // Start Line
    if (progress > sectionWeight * sectionProgress * 5) {
        val lineOffsetLeft = SquareSegmentedProgress().calculateLineProgress(
            progress = progress,
            lineType = LineType.Start,
            size = canvasSize,
            cornerRadius = cornerRadius,
            strokeEndDelta = strokeEndDelta,
            sectionEndProgress = sectionWeight * sectionProgress * 5
        )
        // Left Line
        drawLine(
            color = trackColor,
            start = lineOffsetLeft.first,
            end = lineOffsetLeft.second,
            strokeWidth = stroke.width,
            cap = stroke.cap,
        )
    }

    // Top Left Corner
    if (progress > sectionWeight * sectionProgress * 6) {
        drawArc(
            color = trackColor,
            startAngle = 180f,
            sweepAngle = SquareSegmentedProgress().calculateSweepDegrees(
                cornerType = CornerType.TopStart,
                progress = progress,
                sectionAngle = sectionProgress,
                segmentAngle = sectionProgress * 6 * sectionWeight
            ),
            useCenter = false,
            style = stroke,
            size = arcSize
        )
    }

    // Top Line
    if (progress > sectionWeight * sectionProgress * 7) {
        val lineOffsetTop = SquareSegmentedProgress().calculateLineProgress(
            progress = progress,
            lineType = LineType.Top,
            size = canvasSize,
            cornerRadius = cornerRadius,
            strokeEndDelta = strokeEndDelta,
            sectionEndProgress = sectionWeight * sectionProgress * 7
        )
        drawLine(
            color = trackColor,
            start = lineOffsetTop.first,
            end = lineOffsetTop.second,
            strokeWidth = stroke.width,
            cap = stroke.cap,
        )
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
        strokeEndDelta: Float,
        sectionEndProgress: Float,
    ): Pair<Offset, Offset> {
        return when (lineType) {
            LineType.Top -> {
                val startX = cornerRadius + strokeEndDelta
                val startY = 0f
                val endX = min(
                    ((progress - sectionEndProgress) * 4) * size.width + cornerRadius + strokeEndDelta,
                    size.width - cornerRadius - strokeEndDelta
                )
                val endY = 0f

                Offset(startX, startY) to Offset(endX, endY)
            }
            LineType.End -> {
                val startX = size.width
                val startY = cornerRadius + strokeEndDelta
                val endX = size.width
                val endY =
                    min(
                        ((progress) * 4) * size.height - cornerRadius + strokeEndDelta,
                        size.height - cornerRadius - strokeEndDelta
                    )

                Offset(startX, startY) to Offset(endX, endY)
            }
            LineType.Start -> {
                val startX = 0f
                val startY = size.height - cornerRadius - strokeEndDelta
                val endX = 0f
                val endY = max(
                    size.height - cornerRadius - ((progress - sectionEndProgress) * 4) * size.height + strokeEndDelta,
                    cornerRadius + strokeEndDelta
                )

                Offset(startX, startY) to Offset(endX, endY)
            }
            LineType.Bottom -> {
                val startX = size.width - cornerRadius - strokeEndDelta
                val startY = size.height

                val endX = max(
                    size.width - cornerRadius - ((progress - sectionEndProgress) * 4) * size.width + strokeEndDelta,
                    cornerRadius + strokeEndDelta
                )
                val endY = size.height

                Offset(startX, startY) to Offset(endX, endY)
            }
        }
    }

    /**
     * Returns the sweepDegrees for the corners of
     * the rounded rectangle.
     *
     * @param cornerType Which of the corners that we are looking at.
     * @param progress The current progress of the segment.
     */
    fun calculateSweepDegrees(
        cornerType: CornerType,
        progress: Float,
        sectionAngle: Float,
        segmentAngle: Float,
    ): Float {
        return when (cornerType) {
            CornerType.TopStart -> {
                if (progress < sectionAngle + segmentAngle) {
                    min(((progress - segmentAngle) * 10) * 90, 90f)
                } else {
                    90f
                }
            }
            CornerType.TopEnd -> {
                if (progress < segmentAngle) {
                    min(((progress) * 10) * 90, 90f)
                } else {
                    90f
                }
            }
            CornerType.BottomStart -> {
                // sectionAngle - 0.125
                // segmentAngle - 0.5
                // progress > 0.5
                if (progress < sectionAngle + segmentAngle) {
                    min(((progress - segmentAngle) * 10) * 90, 90f)
                } else {
                    90f
                }
            }
            CornerType.BottomEnd -> {
                if (progress < sectionAngle + segmentAngle) {
                    min(((progress - segmentAngle) * 10) * 90, 90f)
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
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val color = animateColorAsState(
        targetValue = if (progress > 0.7f) Color.Red.copy(alpha = 0.55f)
            .compositeOver(Color.Gray) else if (progress > 0.35) Color.Yellow.copy(alpha = 0.35f)
            .compositeOver(Color.Gray) else Color.Green.copy(alpha = 0.35f)
            .compositeOver(Color.Gray)
    )
    Box(modifier = Modifier.size(300.dp)) {

        SquareSegmentedProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .height(300.dp)
                .width(300.dp)
                .clickable {
                    switch.value = !switch.value
                    progressState.value = if (switch.value) 1f else 0f
                },
            progress = progress,
            indicatorColor = color.value,
        )
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = progress.toString().dropLast(5),
            color = color.value
        )
    }
}

@Preview(device = Devices.WEAR_OS_SQUARE)
@Composable
fun PreviewNoAnim() {

    SquareSegmentedProgressIndicator(
        modifier = Modifier
            .height(300.dp)
            .width(300.dp),
        progress = 0.6125f,
        indicatorColor = Color.Yellow.copy(alpha = 0.36f)
            .compositeOver(Color.Gray)
    )
}
