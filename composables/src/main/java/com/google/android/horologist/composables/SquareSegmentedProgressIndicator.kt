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

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ProgressIndicatorDefaults
import java.lang.Float.max
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Represents a square segmented progress indicator.
 *
 * @param modifier [Modifier] to be applied to the [SegmentedProgressIndicator]
 * @param trackSegments A list of [ProgressIndicatorSegment] definitions, specifying the properties
 * of each segment.
 * @param progress The progress of this progress indicator where 0.0 represents no progress and 1.0
 * represents completion. Values outside of this range are coerced into the range 0..1.
 * @param startAngle The starting position of the progress arc,
 * measured clockwise in degrees (0 to 360) from the 3 o'clock position. For example, 0 and 360
 * represent 3 o'clock, 90 and 180 represent 6 o'clock and 9 o'clock respectively.
 * Default is -90 degrees (top of the screen)
 * @param endAngle The ending position of the progress arc,
 * measured clockwise in degrees (0 to 360) from the 3 o'clock position. For example, 0 and 360
 * represent 3 o'clock, 90 and 180 represent 6 o'clock and 9 o'clock respectively.
 * By default equal to 270 degrees.
 * @param strokeWidth The stroke width for the progress indicator.
 * @param paddingAngle The gap to place between segments. Defaults to 0 degrees.
 * @param trackColor The background track color. If a segment specifies [trackColor] then the
 * segment value takes preference. Defaults to [Color.Black]
 */
@ExperimentalHorologistComposablesApi
@Composable
public fun SquareSegmentedProgressIndicator(
    modifier: Modifier = Modifier,
    trackSegments: List<ProgressIndicatorSegment>,
    progress: Float,
    startAngle: Float = -90.0f,
    endAngle: Float = 270.0f,
    strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
    paddingAngle: Float = 0.0f,
    trackColor: Color = MaterialTheme.colors.onBackground.copy(alpha = 0.1f)
) {
    var boxMinDimension by remember { mutableStateOf(0) }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.onSizeChanged { boxMinDimension = min(it.width, it.height) }
    ) {
        val localDensity = LocalDensity.current

        val boxMinDimensionDp = with(localDensity) {
            boxMinDimension.toDp()
        }
        // The progress bar uses rounded ends. The small delta requires calculation to take account
        // for the rounded end to ensure that neighbouring segments meet correctly.
        val endDelta = remember(strokeWidth, boxMinDimensionDp) {
            (
                asin(
                    strokeWidth.value /
                        (boxMinDimensionDp.value - strokeWidth.value)
                ) * 180 / Math.PI
                ).toFloat()
        }
        val totalWeight = remember(trackSegments) {
            trackSegments.sumOf { it.weight.toDouble() }.toFloat()
        }
        // The degrees of the circle that are available for progress indication, once any padding
        // between segments is accounted for. This will be shared by weighting across the segments.
        val segmentableAngle = (endAngle - startAngle) - trackSegments.size * paddingAngle

        // The first segment needs half a padding between it and the start point.
        var currentStartAngle = startAngle + paddingAngle / 2

        var remainingProgress = progress.coerceIn(0.0f, 1.0f) * segmentableAngle

        val stroke = with(localDensity) {
            Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        }

        Canvas(
            modifier = modifier
                .fillMaxSize()
                .progressSemantics(progress)
        ) {
            trackSegments.forEach { segment ->
                val segmentAngle = (segment.weight * segmentableAngle) / totalWeight

                val currentEndAngle = currentStartAngle + segmentAngle
                val startAngleWithDelta = currentStartAngle
                val backgroundSweep =
                    360f - ((startAngleWithDelta - currentEndAngle) % 360 + 360) % 360
                val sectionProgress = remainingProgress / segmentAngle
                val progressSweep = backgroundSweep * sectionProgress.coerceIn(0f..1f)

                val diameter = min(size.width, size.height)
                val diameterOffset = stroke.width / 2
                val arcDimen = diameter - 2 * diameterOffset
                drawRectangularProgressIndicator(
                    segment = segment,
                    currentStartAngle = currentStartAngle,
                    endDelta = endDelta,
                    backgroundSweep = backgroundSweep,
                    diameter = diameter,
                    stroke = stroke,
                    progressSweep = progressSweep,
                    trackColor = trackColor,
                    width = size.width,
                    height = size.height,
                )

                currentStartAngle += segmentAngle + paddingAngle
                remainingProgress -= segmentAngle
            }
        }
    }
}

private fun DrawScope.drawRectangularProgressIndicator(
    segment: ProgressIndicatorSegment,
    currentStartAngle: Float,
    endDelta: Float,
    backgroundSweep: Float,
    diameter: Float,
    stroke: Stroke,
    progressSweep: Float,
    trackColor: Color,
    width: Float,
    height: Float,
) {

    val left = 0f
    val top = 0f
    val cornerRadius = (diameter / 4).toInt()
    val right = width
    val bottom = height

    val endAngle = (currentStartAngle + backgroundSweep).toInt()

    val currentStartAngleInt = currentStartAngle.toInt()
    val angleSection = currentStartAngleInt..endAngle

    drawPath(
        color = segment.trackColor ?: trackColor,
        path = Path().apply {

            // top left
            moveTo(left, (top + cornerRadius))
            if ((225 - cornerRadius / 2..225 + cornerRadius / 2).isAnElementOf(angleSection)) {
                arcTo(
                    Rect(
                        left = left,
                        top = top,
                        right = left + cornerRadius,
                        bottom = top + cornerRadius
                    ),
                    180f,
                    min(
                        backgroundSweep, 90f
                    ),
                    true
                )
            }

            if ((225 + cornerRadius / 2..315 - cornerRadius / 2).isAnElementOf(angleSection) || (-135 + cornerRadius / 2..-45 - cornerRadius / 2).isAnElementOf(angleSection)) {

                // The angle within we are defining the line
                val lineAngleSection = (315 - cornerRadius / 2) - (225 + cornerRadius / 2)
                val actualLineSection = min(lineAngleSection.toFloat(), backgroundSweep)

                val actualLineSectionStart = max(abs(currentStartAngle), 225 + cornerRadius / 2f)

                moveTo(left + (cornerRadius / 2f) + transformAngleToCoordinate(angle = actualLineSectionStart, width = width, height = height).first , top)
                lineTo(left + (cornerRadius / 2f) + transformAngleToCoordinate(angle = actualLineSection, width = width, height = height).first, top)
            }

            // top right
            moveTo(right - cornerRadius, top)
            if ((315 - cornerRadius / 2..315 + cornerRadius / 2).isAnElementOf(angleSection) || (-45 - cornerRadius / 2..-45 + cornerRadius / 2).isAnElementOf(angleSection)) {

                arcTo(
                    Rect(
                        left = right - cornerRadius,
                        top = top,
                        right = right,
                        bottom = top + cornerRadius
                    ),
                    270f,
                    min(
                        backgroundSweep, 90f
                    ),
                    true
                )
            }

            moveTo(right, top + (cornerRadius / 2f))
            //if ((0..45 - cornerRadius / 2).containsSomeOf(angleSection)) {
                lineTo(right, top + (cornerRadius / 2f) + diameter - cornerRadius)
            //}

            // bottom right
            moveTo(left, bottom - cornerRadius)
            //if ((45 - cornerRadius / 2..45 + cornerRadius / 2).containsSomeOf(angleSection)) {
                arcTo(
                    Rect(
                        left = right - cornerRadius,
                        top = bottom - cornerRadius,
                        right = right,
                        bottom = bottom
                    ),
                    0f,
                    min(90f, 90f),
                    true
                )
            //}

            moveTo(right - (cornerRadius / 2f), bottom)
            //if ((45 + cornerRadius / 2..90).containsSomeOf(angleSection)) {
                lineTo(right - (cornerRadius / 2f) - diameter + cornerRadius, bottom)
            //}

            // bottom left
            moveTo(left, bottom - cornerRadius)
            //if ((135 - cornerRadius / 2..135 + cornerRadius / 2).containsSomeOf(angleSection)) {
                arcTo(
                    Rect(
                        left = left,
                        top = bottom - cornerRadius,
                        right = left + cornerRadius,
                        bottom = bottom
                    ),
                    90f,
                    min(
                        90f, 90f
                    ),
                    true
                )
            //}

            moveTo(left, bottom - (cornerRadius / 2f))
            //if ((180..225 - cornerRadius / 2).containsSomeOf(angleSection)) {
                lineTo(left, bottom - (cornerRadius / 2f) - diameter + cornerRadius)
            //}

        },
        style = stroke,
    )
}

@VisibleForTesting
fun IntRange.isAnElementOf(section: IntRange): Boolean {
    for (i in this) {
        for (k in section) {
            if (i == k) return true
        }
    }
    return false
}

private fun transformAngleToCoordinate(angle: Float, width: Float, height: Float): Pair<Float, Float> {
    val angleInRad = angle * PI / 180
    val sinY = sin(angleInRad) * height
    val cosX = cos(angleInRad) * width
    return elipticalDiscToSquare(cosX.toFloat(), sinY.toFloat())
}

private fun elipticalDiscToSquare(u: Float, v: Float) : Pair<Float, Float> {
    val u2: Float = u * u
    val v2: Float = v * v
    val twosqrt2: Float = 2.0f * sqrt(2.0f)
    val subtermx = 2.0f + u2 - v2
    val subtermy = 2.0f - u2 + v2
    val termx1: Float = subtermx + u * twosqrt2
    val termx2: Float = subtermx - u * twosqrt2
    val termy1: Float = subtermy + v * twosqrt2
    val termy2: Float = subtermy - v * twosqrt2

    val x = 0.5f * sqrt(termx1) - 0.5f * sqrt(termx2)
    val y = 0.5f * sqrt(termy1) - 0.5f * sqrt(termy2)

    return Pair(x,y)
}

@OptIn(ExperimentalHorologistComposablesApi::class)
@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true)
@Composable
private fun SquareSegmentedProgressIndicatorSquarePreview() {
    val segments = listOf(
        ProgressIndicatorSegment(1f, Color.Cyan),
        ProgressIndicatorSegment(1f, Color.Magenta),
        ProgressIndicatorSegment(1f, Color.Yellow),
        ProgressIndicatorSegment(1f, Color.Green)
    )

    SquareSegmentedProgressIndicator(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        trackSegments = segments,
        progress = 0.45f,
        strokeWidth = 15.dp,
        trackColor = Color.Gray,
        paddingAngle = 0f,
    )
}
