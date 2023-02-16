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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ProgressIndicatorDefaults

/**
 * SquareSegmentedProgressIndicator represents a segmented progress indicator with
 * square shape.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param progress The current progress of the indicator. This must be between 0f and 1f
 * @param strokeWidth The stroke width for the progress indicator.
 * @param trackSegments A list of [ProgressIndicatorSegment] definitions, specifying the properties
 * of each segment.
 * @param trackColor The color of the track that is drawn behind the indicator color of the
 * track sections.
 * @param cornerRadiusDp Radius of the corners that are on the square. If this is 0.dp it
 * is a normal square.
 * @param paddingDp This is the size of the gaps for each segment.
 */
@ExperimentalHorologistComposablesApi
@Composable
public fun SquareSegmentedProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float,
    strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
    trackColor: Color = MaterialTheme.colors.onBackground.copy(alpha = 0.1f),
    cornerRadiusDp: Dp = 10.dp,
    trackSegments: List<ProgressIndicatorSegment>,
    paddingDp: Dp = ProgressIndicatorDefaults.StrokeWidth
) {
    check(progress in 0.0..1.0) {
        "Only progress between 0.0 and 1.0 is allowed."
    }

    val localDensity = LocalDensity.current
    val stroke = with(localDensity) {
        Stroke(
            width = strokeWidth.toPx(),
            join = StrokeJoin.Round,
            cap = StrokeCap.Square
        )
    }

    BoxWithConstraints(
        modifier
            .padding(strokeWidth)
            .progressSemantics(progress)
    ) {
        val width = with(localDensity) { maxWidth.toPx() }
        val height = with(localDensity) { maxHeight.toPx() }

        val calculatedSegments = remember(
            trackSegments,
            height,
            width,
            cornerRadiusDp,
            strokeWidth,
            paddingDp
        ) {
            calculateSegments(
                height = height,
                width = width,
                trackSegments = trackSegments,
                trackColor = trackColor,
                cornerRadiusDp = cornerRadiusDp,
                strokeWidth = strokeWidth,
                paddingDp = paddingDp,
                localDensity = localDensity
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .progressSemantics(progress)
        ) {
            for (segmentGroups in calculatedSegments) {
                drawPath(
                    path = Path().apply {
                        for (it in segmentGroups.calculatedSegments) {
                            it.drawIncomplete(this, progress)
                        }
                    },
                    color = segmentGroups.trackColor,
                    style = stroke
                )
                drawPath(
                    path = Path().apply {
                        for (it in segmentGroups.calculatedSegments) {
                            it.drawCompleted(this, progress)
                        }
                    },
                    brush = segmentGroups.indicatorBrush,
                    style = stroke
                )
            }
        }
    }
}

private typealias SegmentDrawable = Path.(ClosedRange<Float>) -> Unit

internal data class CalculatedSegment(
    val progressIndicator: Brush,
    val trackColor: Color,
    val drawFn: SegmentDrawable,
    val range: ClosedRange<Float>
) {
    internal fun drawCompleted(path: Path, progress: Float) {
        if (progress > range.endInclusive) {
            path.drawFn(range)
        } else if (progress >= range.start) {
            path.drawFn(range.start..progress)
        }
    }

    internal fun drawIncomplete(path: Path, progress: Float) {
        if (progress < range.start) {
            path.drawFn(range)
        } else if (progress <= range.endInclusive) {
            path.drawFn(progress..range.endInclusive)
        }
    }
}

internal data class SegmentGroups(
    val groupNumber: Int,
    val indicatorBrush: Brush,
    val trackColor: Color,
    val calculatedSegments: List<CalculatedSegment>
)

internal data class Measures(
    val width: Float,
    val height: Float,
    val cornerRadius: Float,
    val stroke: Stroke
) {
    private val straightWidth: Float = width - (2 * cornerRadius)
    private val straightHeight: Float = height - (2 * cornerRadius)
    private val cornerArcLength: Float = (0.5 * Math.PI * cornerRadius).toFloat()

    private val total = (2 * straightWidth) + (2 * straightHeight) + (4 * cornerArcLength)

    internal val topRightPercent = straightWidth / 2 / total
    internal val rightTopCornerPercent = topRightPercent + (cornerArcLength / total)
    private val rightPercent = rightTopCornerPercent + (straightHeight / total)
    private val rightBottomCornerPercent = rightPercent + (cornerArcLength / total)
    private val bottomPercent = rightBottomCornerPercent + (straightWidth / total)
    internal val leftBottomCornerPercent = bottomPercent + (cornerArcLength / total)
    private val leftPercent = leftBottomCornerPercent + (straightHeight / total)
    private val leftTopCornerPercent = leftPercent + (cornerArcLength / total)
    internal val topLeftPercent = leftTopCornerPercent + (straightWidth / 2 / total)

    private val topRightRange = 0f..topRightPercent
    private val rightTopCornerRange = topRightPercent..rightTopCornerPercent
    private val rightRange = rightTopCornerPercent..rightPercent
    private val rightBottomCornerRange = rightPercent..rightBottomCornerPercent
    private val bottomRange = rightBottomCornerPercent..bottomPercent
    private val leftBottomCornerRange = bottomPercent..leftBottomCornerPercent
    private val leftRange = leftBottomCornerPercent..leftPercent
    private val leftTopCornerRange = leftPercent..leftTopCornerPercent
    private val topLeftRange = leftTopCornerPercent..topLeftPercent

    private val topRight: SegmentDrawable = { range ->
        lineRange(
            drawRange = range,
            segmentRange = topRightRange,
            x1 = width / 2,
            y1 = 0f,
            x2 = width - cornerRadius,
            y2 = 0f
        )
    }

    private val rightTopCorner: SegmentDrawable = { range ->
        arcRange(
            drawRange = range,
            segmentRange = rightTopCornerRange,
            center = Offset(width - cornerRadius, cornerRadius),
            startDegrees = 270f
        )
    }

    private val right: SegmentDrawable = { range ->
        lineRange(
            drawRange = range,
            segmentRange = rightRange,
            x1 = width,
            y1 = cornerRadius,
            x2 = width,
            y2 = height - cornerRadius
        )
    }
    private val rightBottomCorner: SegmentDrawable = { range ->
        arcRange(
            drawRange = range,
            segmentRange = rightBottomCornerRange,
            center = Offset(width - cornerRadius, height - cornerRadius),
            startDegrees = 0f
        )
    }
    val bottom: SegmentDrawable = { range ->
        lineRange(
            drawRange = range,
            segmentRange = bottomRange,
            x1 = width - cornerRadius,
            y1 = height,
            x2 = cornerRadius,
            y2 = height
        )
    }
    private val leftBottomCorner: SegmentDrawable = { range ->
        arcRange(
            drawRange = range,
            segmentRange = leftBottomCornerRange,
            center = Offset(cornerRadius, height - cornerRadius),
            startDegrees = 90f
        )
    }
    private val left: SegmentDrawable = { range ->
        lineRange(
            drawRange = range,
            segmentRange = leftRange,
            x1 = 0f,
            y1 = height - cornerRadius,
            x2 = 0f,
            y2 = cornerRadius
        )
    }
    private val leftTopCorner: SegmentDrawable = { range ->
        arcRange(
            drawRange = range,
            segmentRange = leftTopCornerRange,
            center = Offset(cornerRadius, cornerRadius),
            startDegrees = 180f
        )
    }
    private val topLeft: SegmentDrawable = { range ->
        lineRange(
            drawRange = range,
            segmentRange = topLeftRange,
            x1 = cornerRadius,
            y1 = 0f,
            x2 = width / 2,
            y2 = 0f
        )
    }

    private fun Path.lineRange(
        drawRange: ClosedRange<Float>,
        segmentRange: ClosedRange<Float>,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float
    ) {
        if (drawRange.isZeroWidth()) {
            return
        }

        val width = x2 - x1
        val height = y2 - y1

        val start =
            (drawRange.start - segmentRange.start) / (segmentRange.endInclusive - segmentRange.start)
        val end =
            (drawRange.endInclusive - segmentRange.endInclusive) / (segmentRange.endInclusive - segmentRange.start)

        moveTo(x1 + (start * width), y1 + (start * height))
        lineTo(x2 + (end * width), y2 + (end * height))
    }

    private fun Path.arcRange(
        drawRange: ClosedRange<Float>,
        segmentRange: ClosedRange<Float>,
        center: Offset,
        startDegrees: Float
    ) {
        if (drawRange.isZeroWidth()) {
            return
        }

        val segmentRangePercent = segmentRange.endInclusive - segmentRange.start
        val start = ((drawRange.start - segmentRange.start) / segmentRangePercent)
            .coerceIn(0f, 1f)
        val end = ((segmentRange.endInclusive - drawRange.endInclusive) / segmentRangePercent)
            .coerceIn(0f, 1f)

        val additionalStartDegrees = start * 90f

        arcTo(
            Rect(center = center, radius = cornerRadius),
            startAngleDegrees = startDegrees + additionalStartDegrees,
            sweepAngleDegrees = 90f - (end * 90f) - additionalStartDegrees,
            forceMoveTo = false
        )
    }

    internal fun splitSegments(
        indicatorBrush: Brush,
        trackColor: Color,
        range: ClosedFloatingPointRange<Float>
    ): List<CalculatedSegment> {
        return buildList {
            range.intersect(topRightRange)?.let {
                if (!it.isZeroWidth()) {
                    add(CalculatedSegment(indicatorBrush, trackColor, topRight, it))
                }
            }
            range.intersect(rightTopCornerRange)?.let {
                if (!it.isZeroWidth()) {
                    add(CalculatedSegment(indicatorBrush, trackColor, rightTopCorner, it))
                }
            }
            range.intersect(rightRange)?.let {
                if (!it.isZeroWidth()) {
                    add(CalculatedSegment(indicatorBrush, trackColor, right, it))
                }
            }
            range.intersect(rightBottomCornerRange)?.let {
                if (!it.isZeroWidth()) {
                    add(CalculatedSegment(indicatorBrush, trackColor, rightBottomCorner, it))
                }
            }
            range.intersect(bottomRange)?.let {
                if (!it.isZeroWidth()) {
                    add(CalculatedSegment(indicatorBrush, trackColor, bottom, it))
                }
            }
            range.intersect(leftBottomCornerRange)?.let {
                if (!it.isZeroWidth()) {
                    add(CalculatedSegment(indicatorBrush, trackColor, leftBottomCorner, it))
                }
            }
            range.intersect(leftRange)?.let {
                if (!it.isZeroWidth()) {
                    add(CalculatedSegment(indicatorBrush, trackColor, left, it))
                }
            }
            range.intersect(leftTopCornerRange)?.let {
                if (!it.isZeroWidth()) {
                    add(CalculatedSegment(indicatorBrush, trackColor, leftTopCorner, it))
                }
            }
            range.intersect(topLeftRange)?.let {
                if (!it.isZeroWidth()) {
                    add(CalculatedSegment(indicatorBrush, trackColor, topLeft, it))
                }
            }
        }
    }
}

private fun ClosedRange<Float>.isZeroWidth(): Boolean {
    return endInclusive <= start
}

internal fun ClosedRange<Float>.intersect(with: ClosedRange<Float>): ClosedRange<Float>? {
    return if (this.start < with.endInclusive && with.start < this.endInclusive) {
        (start.coerceAtLeast(with.start)..endInclusive.coerceAtMost(with.endInclusive))
    } else {
        null
    }
}

private fun calculateSegments(
    height: Float,
    width: Float,
    trackSegments: List<ProgressIndicatorSegment>,
    trackColor: Color,
    cornerRadiusDp: Dp,
    strokeWidth: Dp,
    paddingDp: Dp,
    localDensity: Density
): List<SegmentGroups> {
    val cornerRadius = with(localDensity) { cornerRadiusDp.toPx() }
    val stroke = with(localDensity) { Stroke(width = strokeWidth.toPx()) }
    val padding = with(localDensity) { paddingDp.toPx() }
    val naturalWeight = trackSegments.sumOf { it.weight.toDouble() }.toFloat()

    val edgeLength =
        (2 * (height - 2 * cornerRadius)) + (2 * (width - 2 * cornerRadius)) + (2 * Math.PI.toFloat() * cornerRadius)

    val paddingWeight = padding / edgeLength * naturalWeight
    val paddedWeight = (paddingWeight * trackSegments.size) + naturalWeight

    val measures = Measures(width, height, cornerRadius, stroke)

    var startWeight = 0f
    var startPaddingWeight = 0f
    return buildList {
        trackSegments.forEachIndexed { segmentIndex, trackSegment ->
            val localTrackColor = trackSegment.trackColor ?: trackColor
            val indicatorBrush = trackSegment.indicatorBrush

            val paddedRange =
                ((startWeight + startPaddingWeight) / paddedWeight)..((startWeight + startPaddingWeight + trackSegment.weight) / paddedWeight)

            val splitSegments = measures.splitSegments(
                indicatorBrush = indicatorBrush,
                trackColor = localTrackColor,
                range = paddedRange
            )

            add(
                SegmentGroups(
                    groupNumber = segmentIndex,
                    indicatorBrush = indicatorBrush,
                    trackColor = localTrackColor,
                    calculatedSegments = splitSegments
                )
            )

            startWeight += trackSegment.weight
            startPaddingWeight += paddingWeight
        }
    }
}
