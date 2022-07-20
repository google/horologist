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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalHorologistComposablesApi::class)
@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
private fun SegmentedProgressIndicatorRoundPreview() {
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

@OptIn(ExperimentalHorologistComposablesApi::class)
@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true)
@Composable
private fun SegmentedProgressIndicatorSquarePreview() {
    val segments = listOf(
        ProgressIndicatorSegment(1f, Color.Cyan),
        ProgressIndicatorSegment(1f, Color.Magenta),
        ProgressIndicatorSegment(1f, Color.Yellow)
    )

    SegmentedProgressIndicator(
        modifier = Modifier.fillMaxSize(),
        trackSegments = segments,
        progress = 0.75f,
        strokeWidth = 15.dp,
        trackColor = Color.Gray,
        paddingAngle = 2f
    )
}
