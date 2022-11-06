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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ProgressIndicatorDefaults
import java.lang.Float.max
import java.lang.Float.min

/**
 * SquareSegmentedProgressIndicator represents a segmented progress indicator with
 * square shape.
 *
 * @param modifier
 * @param progress The current progress of the indicator. This must be between 0f and 1f
 * @param strokeWidth The stroke width for the progress indicator.
 * @param trackSegments A list of [ProgressIndicatorSegment] definitions, specifying the properties
 * of each segment.
 * @param trackColor The color of the track that is drawn behind the indicator color of the
 * track sections.
 */
@ExperimentalHorologistComposablesApi
@Composable
public fun SquareSegmentedProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float,
    strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
    trackColor: Color = MaterialTheme.colors.onBackground.copy(alpha = 0.1f),
    trackSegments: List<ProgressIndicatorSegment>
) {
    require(progress in 0.0..1.0) {
        Error("The progress must be between 0 and 1")
    }

    val localDensity = LocalDensity.current

    val stroke = with(localDensity) {
        Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Square)
    }

    val totalWeight = remember(trackSegments) {
        trackSegments.sumOf { it.weight.toDouble() }.toFloat()
    }

    val strokeEndDelta = stroke.width

    Canvas(
        modifier = modifier
            .background(color = MaterialTheme.colors.background)
            .progressSemantics(progress)
    ) {
        // Start pos here is the length of the circumference
        // of the object that we are starting on.
        var sectionStartOffset: Float
        val sectionSpace = 8f // space between each section

        val possibleTracks = mutableSetOf(0, 1, 2, 3, 4, 5, 6, 7)
        trackSegments.forEach { trackSection ->

            sectionStartOffset = sectionSpace

            // The sections that should be drawn in this round.
            // Should range from 0 - 7 for each possible section of the rounded square.
            val drawSections = mutableSetOf<Int>()
            repeat(trackSection.weight.toInt()) {
                if (possibleTracks.size > 0) {
                    drawSections.add(possibleTracks.elementAt(0))
                    possibleTracks.remove(possibleTracks.elementAt(0))
                }
            }

            // Draw track
            drawRoundedSquare(
                canvasSize = this.size,
                progress = 1f,
                trackColor = trackColor,
                stroke = stroke,
                strokeEndDelta = strokeEndDelta,
                totalWeight = totalWeight,
                startPos = sectionStartOffset,
                drawSections = drawSections
            )

            // Draw progress
            drawRoundedSquare(
                canvasSize = this.size,
                progress = progress,
                trackColor = trackSection.indicatorColor,
                stroke = stroke,
                strokeEndDelta = strokeEndDelta,
                totalWeight = totalWeight,
                startPos = sectionStartOffset,
                drawSections = drawSections
            )
        }
    }
}

private fun DrawScope.drawRoundedSquare(
    canvasSize: Size,
    progress: Float,
    trackColor: Color,
    stroke: Stroke,
    strokeEndDelta: Float,
    totalWeight: Float,
    startPos: Float,
    drawSections: Set<Int>
) {
    val cornerRadius = canvasSize.height / 4f // 1/4 of the line height
    val cornerDiameter = cornerRadius * 2

    val arcSize = Size(width = cornerDiameter, height = cornerDiameter)

    val sectionWeight = min(8 / totalWeight, 1f)

    val sectionProgressWeight = 1f / totalWeight // 10 parts for 8 sections 0.125 is one part

    // Top End Corner
    if (progress > 0f && drawSections.contains(0)) {
        val hasEndSection = drawSections.indexOf(0) == drawSections.size
        val startOffset = if (hasEndSection) 0f else startPos
        val startAngle = SquareSegmentedProgress().calculateStartAngle(
            cornerType = CornerType.TopEnd,
            startOffset = startOffset,
            cornerRadius = cornerRadius
        )
        drawArc(
            color = trackColor,
            startAngle = startAngle,
            sweepAngle = SquareSegmentedProgress().calculateSweepDegrees(
                CornerType.TopEnd,
                progress = progress,
                segmentProgress = sectionProgressWeight,
                sectionProgress = sectionProgressWeight,
                startDegreeOffset = startAngle - 270f
            ),
            useCenter = false,
            style = stroke,
            size = arcSize,
            topLeft = Offset(canvasSize.width - cornerDiameter, 0f)
        )
    }

    // End line
    if (progress > sectionWeight * sectionProgressWeight && drawSections.contains(1)) {
        val hasEndSection = drawSections.indexOf(1) == drawSections.size
        val startOffset = if (hasEndSection) startPos else 0f
        val lineOffsetEnd = SquareSegmentedProgress().calculateLineProgress(
            progress = progress,
            lineType = LineType.End,
            size = canvasSize,
            cornerRadius = cornerRadius,
            strokeEndDelta = strokeEndDelta,
            sectionEndProgress = sectionWeight * sectionProgressWeight,
            startOffset = startOffset
        )
        // End Line
        drawLine(
            color = trackColor,
            start = lineOffsetEnd.first,
            end = lineOffsetEnd.second,
            strokeWidth = stroke.width,
            cap = stroke.cap
        )
    }

    // Bottom End Corner
    if (progress > sectionWeight * sectionProgressWeight * 2 && drawSections.contains(2)) {
        val hasEndSection = drawSections.indexOf(2) != drawSections.size - 1

        val startOffset = if (hasEndSection) startPos else 0f
        val startAngle = SquareSegmentedProgress().calculateStartAngle(
            cornerType = CornerType.BottomEnd,
            startOffset = startOffset,
            cornerRadius = cornerRadius
        )
        drawArc(
            color = trackColor,
            startAngle = startAngle,
            sweepAngle = SquareSegmentedProgress().calculateSweepDegrees(
                CornerType.BottomEnd,
                progress = progress,
                sectionProgress = sectionProgressWeight,
                segmentProgress = sectionWeight * sectionProgressWeight * 2,
                startDegreeOffset = startAngle - 0f
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
    if (progress > sectionWeight * sectionProgressWeight * 3 && drawSections.contains(3)) {
        val startOffset = if (drawSections.indexOf(3) == drawSections.size - 1) 0f else startPos
        val lineOffsetBottom = SquareSegmentedProgress().calculateLineProgress(
            progress = progress,
            lineType = LineType.Bottom,
            size = canvasSize,
            cornerRadius = cornerRadius,
            strokeEndDelta = strokeEndDelta,
            sectionEndProgress = sectionWeight * sectionProgressWeight * 3,
            startOffset = startOffset
        )
        // Bottom Line
        drawLine(
            color = trackColor,
            start = lineOffsetBottom.first,
            end = lineOffsetBottom.second,
            strokeWidth = stroke.width,
            cap = stroke.cap
        )
    }

    // Bottom Start Corner
    if (progress > sectionWeight * sectionProgressWeight * 4 && drawSections.contains(4)) {
        val startOffset = if (drawSections.indexOf(4) == drawSections.size - 1) 0f else startPos
        val startDegree = SquareSegmentedProgress().calculateStartAngle(
            cornerType = CornerType.BottomStart,
            startOffset = startOffset,
            cornerRadius = cornerRadius
        )
        drawArc(
            color = trackColor,
            startAngle = startDegree,
            sweepAngle = SquareSegmentedProgress().calculateSweepDegrees(
                CornerType.BottomStart,
                progress,
                segmentProgress = sectionProgressWeight * 4 * sectionWeight,
                sectionProgress = sectionProgressWeight,
                startDegreeOffset = startDegree - 90f
            ),
            useCenter = false,
            style = stroke,
            size = arcSize,
            topLeft = Offset(0f, canvasSize.height - cornerDiameter)
        )
    }

    // Start Line
    if (progress > sectionWeight * sectionProgressWeight * 5 && drawSections.contains(5)) {
        val startOffset = if (drawSections.indexOf(5) == drawSections.size - 1) 0f else startPos

        val lineOffsetLeft = SquareSegmentedProgress().calculateLineProgress(
            progress = progress,
            lineType = LineType.Start,
            size = canvasSize,
            cornerRadius = cornerRadius,
            strokeEndDelta = strokeEndDelta,
            sectionEndProgress = sectionWeight * sectionProgressWeight * 5,
            startOffset = startOffset
        )
        // Start Line
        drawLine(
            color = trackColor,
            start = lineOffsetLeft.first,
            end = lineOffsetLeft.second,
            strokeWidth = stroke.width,
            cap = stroke.cap
        )
    }

    // Top Start Corner
    if (progress > sectionWeight * sectionProgressWeight * 6 && drawSections.contains(6)) {
        val startOffset = if (drawSections.indexOf(6) == drawSections.size - 1) 0f else startPos
        val startDegree = SquareSegmentedProgress().calculateStartAngle(
            cornerType = CornerType.TopStart,
            startOffset = startOffset,
            cornerRadius = cornerRadius
        )
        drawArc(
            color = trackColor,
            startAngle = startDegree,
            sweepAngle = SquareSegmentedProgress().calculateSweepDegrees(
                cornerType = CornerType.TopStart,
                progress = progress,
                sectionProgress = sectionProgressWeight,
                segmentProgress = sectionProgressWeight * 6 * sectionWeight,
                startDegreeOffset = startDegree - 180f
            ),
            useCenter = false,
            style = stroke,
            size = arcSize
        )
    }

    // Top Line
    if (progress > sectionWeight * sectionProgressWeight * 7 && drawSections.contains(7)) {
        val startOffset = if (drawSections.indexOf(7) == drawSections.size - 1) 0f else startPos
        val lineOffsetTop = SquareSegmentedProgress().calculateLineProgress(
            progress = progress,
            lineType = LineType.Top,
            size = canvasSize,
            cornerRadius = cornerRadius,
            strokeEndDelta = strokeEndDelta,
            sectionEndProgress = sectionWeight * sectionProgressWeight * 7,
            startOffset = startOffset
        )
        drawLine(
            color = trackColor,
            start = lineOffsetTop.first,
            end = lineOffsetTop.second,
            strokeWidth = stroke.width,
            cap = stroke.cap
        )
    }
}

internal class SquareSegmentedProgress {

    /**
     * Returns Start and End Offset for the line.
     *
     * @param progress The current progress
     * @param lineType The line type. This is describing the position and angle of the line.
     * @param size The size of the total canvas without the padding added in the end.
     * @param strokeEndDelta This is the delta added by the stroke to make the stroke connect.
     * @param sectionEndProgress This is the progress defined for the line.
     */
    fun calculateLineProgress(
        progress: Float,
        lineType: LineType,
        size: Size,
        cornerRadius: Float,
        strokeEndDelta: Float,
        sectionEndProgress: Float,
        startOffset: Float
    ): Pair<Offset, Offset> {
        return when (lineType) {
            LineType.Top -> {
                val startX = cornerRadius + strokeEndDelta + startOffset
                val startY = 0f
                val endX = min(
                    ((progress - sectionEndProgress) * 4) * size.width + cornerRadius + strokeEndDelta + startOffset,
                    size.width - cornerRadius - strokeEndDelta + startOffset
                )
                val endY = 0f

                Offset(startX, startY) to Offset(endX, endY)
            }
            LineType.End -> {
                val startX = size.width
                val startY = cornerRadius + strokeEndDelta + startOffset
                val endX = size.width
                val endY = min(
                    ((progress) * 4) * size.height - cornerRadius + strokeEndDelta + startOffset,
                    size.height - cornerRadius - strokeEndDelta + startOffset
                )

                Offset(startX, startY) to Offset(endX, endY)
            }
            LineType.Start -> {
                val startX = 0f
                val startY = size.height - cornerRadius - strokeEndDelta - startOffset
                val endX = 0f
                val endY = max(
                    size.height - cornerRadius - ((progress - sectionEndProgress) * 4) * size.height + strokeEndDelta - startOffset,
                    cornerRadius + strokeEndDelta - startOffset
                )

                Offset(startX, startY) to Offset(endX, endY)
            }
            LineType.Bottom -> {
                val startX = size.width - cornerRadius - strokeEndDelta + startOffset
                val startY = size.height

                val endX = max(
                    size.width - cornerRadius - ((progress - sectionEndProgress) * 4) * size.width + strokeEndDelta - startOffset,
                    cornerRadius + strokeEndDelta - startOffset
                )
                val endY = size.height

                Offset(startX, startY) to Offset(endX, endY)
            }
        }
    }

    /**
     * This is the sweep degree offset for the corner arch. So that means that
     * the corner arch will start the drawing angle at this calculated angle.
     */
    internal fun calculateStartAngle(
        cornerType: CornerType,
        startOffset: Float,
        cornerRadius: Float
    ): Float {
        return when (cornerType) {
            CornerType.TopStart -> {
                (180f + ((180 * startOffset) / (Math.PI * cornerRadius))).toFloat()
            }
            CornerType.TopEnd -> {
                (270f + ((180 * startOffset) / (Math.PI * cornerRadius))).toFloat()
            }
            CornerType.BottomStart -> {
                (90f + ((180 * startOffset) / (Math.PI * cornerRadius))).toFloat()
            }
            CornerType.BottomEnd -> {
                (0f + ((180 * startOffset) / (Math.PI * cornerRadius))).toFloat()
            }
        }
    }

    /**
     * Returns the sweepDegrees for the corners of
     * the rounded rectangle.
     *
     * @param cornerType Which of the corners that we are looking at.
     * @param progress The current progress of the segment
     * @param sectionProgress This is the progress weight for each section. (8 sections makes
     * this 0.125 since 8 sections per 10 parts)
     * @param segmentProgress This is the end progress of the segment.
     * @param startDegreeOffset This is the offset that the corner started with relative to
     * the angle assigned to the corner. We need this to subtract it from the sweep degrees
     * so that the corner will not draw over the assigned 90 degrees it should.
     */
    internal fun calculateSweepDegrees(
        cornerType: CornerType,
        progress: Float,
        sectionProgress: Float,
        segmentProgress: Float,
        startDegreeOffset: Float
    ): Float {
        return when (cornerType) {
            CornerType.TopStart -> {
                if (progress < sectionProgress + segmentProgress) {
                    min(((progress - segmentProgress) * 10) * 90, 90f) - startDegreeOffset
                } else {
                    90f - startDegreeOffset
                }
            }
            CornerType.TopEnd -> {
                if (progress < segmentProgress) {
                    min(((progress) * 10) * 90, 90f) - startDegreeOffset
                } else {
                    90f - startDegreeOffset
                }
            }
            CornerType.BottomStart -> {
                // sectionProgress - 0.125
                // segmentProgress - 0.5
                // progress > 0.5
                if (progress < sectionProgress + segmentProgress) {
                    min(((progress - segmentProgress) * 10) * 90, 90f) - startDegreeOffset
                } else {
                    90f - startDegreeOffset
                }
            }
            CornerType.BottomEnd -> {
                if (progress < sectionProgress + segmentProgress) {
                    min(((progress - segmentProgress) * 10) * 90, 90f) - startDegreeOffset
                } else {
                    90f - startDegreeOffset
                }
            }
        }
    }
}

internal enum class LineType {
    Top, End, Start, Bottom,
}

internal enum class CornerType {
    TopStart, TopEnd, BottomStart, BottomEnd,
}
