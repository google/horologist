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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ProgressIndicatorDefaults
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import kotlin.math.asin

/**
 * Represents a segment of the track in a [SegmentedProgressIndicator].
 *
 * @param weight The proportion of the overall progress indicator that this segment should take up,
 * as a proportion of the sum of the weights of all the other segments.
 * @param indicatorBrush The color brush of the indicator bar.
 * @param trackColor The color of the background progress track. This is optional and if not
 * specified will default to the [trackColor] of the overall [SegmentedProgressIndicator].
 * @param inProgressTrackColor The color of the background progress track when this segment is in
 * progress. This is optional, and if specified will take precedence to the [trackColor] of the
 * segment or the overall [SegmentedProgressIndicator], when the segment is in progress.
 */
public data class ProgressIndicatorSegment(
    val weight: Float,
    val indicatorBrush: Brush,
    val trackColor: Color? = null,
    val inProgressTrackColor: Color? = null
) {
    /**
     * Represents a segment of the track in a [SegmentedProgressIndicator].
     *
     * @param weight The proportion of the overall progress indicator that this segment should take up,
     * as a proportion of the sum of the weights of all the other segments.
     * @param indicatorColor The color of the indicator bar.
     * @param trackColor The color of the background progress track. This is optional and if not
     * specified will default to the [trackColor] of the overall [SegmentedProgressIndicator].
     * @param inProgressTrackColor The color of the background progress track when this segment is in
     * progress. This is optional, and if specified will take precedence to the [trackColor] of the
     * segment or the overall [SegmentedProgressIndicator], when the segment is in progress.
     */
    public constructor(
        weight: Float,
        indicatorColor: Color,
        trackColor: Color? = null,
        inProgressTrackColor: Color? = null
    ) : this(
        weight = weight,
        trackColor = trackColor,
        inProgressTrackColor = inProgressTrackColor,
        indicatorBrush = SolidColor(indicatorColor)
    )
}

/**
 * Represents a segmented progress indicator.
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
@ExperimentalHorologistApi
@Composable
public fun SegmentedProgressIndicator(
    trackSegments: List<ProgressIndicatorSegment>,
    progress: Float,
    modifier: Modifier = Modifier,
    startAngle: Float = -90.0f,
    endAngle: Float = 270.0f,
    strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
    paddingAngle: Float = 0.0f,
    trackColor: Color = MaterialTheme.colors.onBackground.copy(alpha = 0.1f)
) {
    val localDensity = LocalDensity.current

    val totalWeight = remember(trackSegments) {
        trackSegments.sumOf { it.weight.toDouble() }.toFloat()
    }
    // The degrees of the circle that are available for progress indication, once any padding
    // between segments is accounted for. This will be shared by weighting across the segments.
    val segmentableAngle = (endAngle - startAngle) - trackSegments.size * paddingAngle

    // This code is heavily inspired from the implementation of CircularProgressIndicator
    // Please see https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:wear/compose/compose-material/src/commonMain/kotlin/androidx/wear/compose/material/ProgressIndicator.kt?q=CircularProgressIndicator
    val stroke = with(localDensity) {
        Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .progressSemantics(progress)
    ) {
        // The progress bar uses rounded ends. The small delta requires calculation to take account
        // for the rounded end to ensure that neighbouring segments meet correctly.
        val endDelta =
            (
                asin(
                    strokeWidth.toPx() /
                        (size.minDimension - strokeWidth.toPx())
                ) * 180 / Math.PI
                ).toFloat()
        // The first segment needs half a padding between it and the start point.
        var currentStartAngle = startAngle + paddingAngle / 2
        var remainingProgress = progress.coerceIn(0.0f, 1.0f) * segmentableAngle

        for (segment in trackSegments) {
            val segmentAngle = (segment.weight * segmentableAngle) / totalWeight
            val currentEndAngle = currentStartAngle + segmentAngle - endDelta
            val startAngleWithDelta = currentStartAngle + endDelta
            val backgroundSweep =
                360f - ((startAngleWithDelta - currentEndAngle) % 360 + 360) % 360
            val sectionProgress = remainingProgress / segmentAngle
            val progressSweep = backgroundSweep * sectionProgress.coerceIn(0f..1f)

            val diameterOffset = stroke.width / 2
            val arcDimen = size.minDimension - 2 * diameterOffset

            drawCircularProgressIndicator(
                segment = segment,
                currentStartAngle = currentStartAngle,
                endDelta = endDelta,
                backgroundSweep = backgroundSweep,
                diameterOffset = diameterOffset,
                diameter = size.minDimension,
                arcDimen = arcDimen,
                stroke = stroke,
                progressSweep = progressSweep,
                trackColor = trackColor
            )

            currentStartAngle += segmentAngle + paddingAngle
            remainingProgress -= segmentAngle
        }
    }
}

private fun DrawScope.drawCircularProgressIndicator(
    segment: ProgressIndicatorSegment,
    currentStartAngle: Float,
    endDelta: Float,
    backgroundSweep: Float,
    diameterOffset: Float,
    diameter: Float,
    arcDimen: Float,
    stroke: Stroke,
    progressSweep: Float,
    trackColor: Color
) {
    val offset = Offset(
        diameterOffset + (size.width - diameter) / 2,
        diameterOffset + (size.height - diameter) / 2
    )

    val localTrackColor = when {
        segment.inProgressTrackColor != null && progressSweep > 0 && progressSweep < backgroundSweep ->
            segment.inProgressTrackColor

        segment.trackColor != null -> segment.trackColor
        else -> trackColor
    }

    // Draw Track
    drawArc(
        color = localTrackColor,
        startAngle = currentStartAngle + endDelta,
        sweepAngle = backgroundSweep,
        useCenter = false,
        topLeft = offset,
        size = Size(arcDimen, arcDimen),
        style = stroke
    )

    // Draw Progress
    drawArc(
        brush = segment.indicatorBrush,
        startAngle = currentStartAngle + endDelta,
        sweepAngle = progressSweep,
        useCenter = false,
        topLeft = offset,
        size = Size(arcDimen, arcDimen),
        style = stroke
    )
}
