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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import androidx.wear.compose.ui.tooling.preview.WearPreviewSmallRound
import androidx.wear.compose.ui.tooling.preview.WearPreviewSquare

@WearPreviewLargeRound
@Composable
private fun SegmentedProgressIndicatorRoundPreview() {
    val segments = listOf(
        ProgressIndicatorSegment(1f, Color.Green),
        ProgressIndicatorSegment(1f, Color.Cyan),
        ProgressIndicatorSegment(1f, Color.Magenta),
        ProgressIndicatorSegment(1f, Color.Yellow),
        ProgressIndicatorSegment(2f, Color.Red)
    )

    SegmentedProgressIndicator(
        trackSegments = segments,
        progress = 0.5833f,
        modifier = Modifier.fillMaxSize(),
        strokeWidth = 10.dp,
        paddingAngle = 2f,
        trackColor = Color.Gray
    )
}

@WearPreviewSquare
@Composable
private fun SegmentedProgressIndicatorSquarePreview() {
    val segments = listOf(
        ProgressIndicatorSegment(1f, Color.Cyan),
        ProgressIndicatorSegment(1f, Color.Magenta),
        ProgressIndicatorSegment(1f, Color.Yellow)
    )

    SegmentedProgressIndicator(
        trackSegments = segments,
        progress = 0.75f,
        modifier = Modifier.fillMaxSize(),
        strokeWidth = 15.dp,
        paddingAngle = 2f,
        trackColor = Color.Gray
    )
}

@WearPreviewSmallRound
@Composable
private fun SegmentedProgressIndicatorBrushPreview() {
    val segments = listOf(
        ProgressIndicatorSegment(
            1f,
            Brush.horizontalGradient(listOf(Color.Cyan, Color.Magenta, Color.Cyan))
        ),
        ProgressIndicatorSegment(
            weight = 1f,
            indicatorBrush = Brush.horizontalGradient(
                listOf(Color.Cyan, Color.Magenta, Color.Yellow)
            )
        ),
        ProgressIndicatorSegment(
            weight = 1f,
            indicatorBrush = Brush.horizontalGradient(
                listOf(Color.Yellow, Color.Magenta, Color.Cyan)
            )
        )
    )

    SegmentedProgressIndicator(
        trackSegments = segments,
        progress = 0.75f,
        modifier = Modifier.fillMaxSize(),
        strokeWidth = 15.dp,
        paddingAngle = 2f,
        trackColor = Color.Gray
    )
}

@WearPreviewSmallRound
@Composable
private fun SegmentedProgressIndicatorBrushColorCombinedPreview() {
    val segments = listOf(
        ProgressIndicatorSegment(
            1f,
            Brush.horizontalGradient(listOf(Color.Cyan, Color.Magenta, Color.Cyan))
        ),
        ProgressIndicatorSegment(
            weight = 1f,
            indicatorColor = Color.Cyan
        ),
        ProgressIndicatorSegment(
            weight = 1f,
            indicatorBrush = Brush.horizontalGradient(
                listOf(Color.Yellow, Color.Magenta, Color.Cyan)
            )
        )
    )

    SegmentedProgressIndicator(
        trackSegments = segments,
        progress = 0.75f,
        modifier = Modifier.fillMaxSize(),
        strokeWidth = 15.dp,
        paddingAngle = 2f,
        trackColor = Color.Gray
    )
}
