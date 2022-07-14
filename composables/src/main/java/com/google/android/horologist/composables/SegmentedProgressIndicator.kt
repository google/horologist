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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.CircularProgressIndicator
import kotlin.math.asin

/**
 * Represents a segment of the track in a [SegmentedProgressIndicator].
 *
 * @param weight The proportion of the overall progress indicator that this segment should take up,
 * as a proportion of the sum of the weights of all the other segments.
 * @param indicatorColor The color of the indicator bar.
 * @param trackColor The color of the background progress track. This is optional and if not
 * specified will default to the [trackColor] of the overall [SegmentedProgressIndicator].
 */
public data class ProgressIndicatorSegment(
    val weight: Float,
    val indicatorColor: Color,
    val trackColor: Color? = null
)

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
@ExperimentalHorologistComposablesApi
@Composable
public fun SegmentedProgressIndicator(
    modifier: Modifier = Modifier,
    trackSegments: List<ProgressIndicatorSegment>,
    progress: Float,
    startAngle: Float = -90.0f,
    endAngle: Float = 270.0f,
    strokeWidth: Dp = 10.dp,
    paddingAngle: Float = 0.0f,
    trackColor: Color = Color.Black
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    // The progress bar uses rounded ends. The small delta requires calculation to take account for
    // the rounded end to ensure that neighbouring segments meet correctly.
    val endDelta = remember(strokeWidth) {
        (asin(strokeWidth.value / (screenWidth - strokeWidth.value)) * 180 / Math.PI).toFloat()
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
    trackSegments.forEach { segment ->
        val segmentAngle = (segment.weight * segmentableAngle) / totalWeight
        CircularProgressIndicator(
            modifier = modifier,
            progress = remainingProgress / segmentAngle,
            startAngle = currentStartAngle + endDelta,
            endAngle = currentStartAngle + segmentAngle - endDelta,
            indicatorColor = segment.indicatorColor,
            trackColor = segment.trackColor ?: trackColor,
            strokeWidth = strokeWidth
        )
        currentStartAngle += segmentAngle + paddingAngle
        remainingProgress -= segmentAngle
    }
}

@OptIn(ExperimentalHorologistComposablesApi::class)
@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
private fun SegmentedProgressIndicatorPreview() {
    val segments = listOf(
        ProgressIndicatorSegment(1f, Color.Green),
        ProgressIndicatorSegment(1f, Color.Cyan),
        ProgressIndicatorSegment(1f, Color.Magenta),
        ProgressIndicatorSegment(1f, Color.Yellow),
        ProgressIndicatorSegment(2f, Color.Red),
    )

    SegmentedProgressIndicator(
        modifier = Modifier.fillMaxSize(),
        trackSegments = segments,
        progress = 0.5833f,
        strokeWidth = 10.dp,
        trackColor = Color.Gray,
        paddingAngle = 2f
    )
}
