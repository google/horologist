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

@file:OptIn(ExperimentalHorologistApi::class)

package com.google.android.horologist.composables

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.screenshots.ScreenshotTest
import org.junit.Test

class SegmentedProgressIndicatorTest: ScreenshotTest() {
    init {
        screenTimeText = {}
    }

    @OptIn(ExperimentalHorologistApi::class)
    @Test
    fun segmentedPicker() {
        takeScreenshot {
            val segments = listOf(
                ProgressIndicatorSegment(1f, Color.Green),
                ProgressIndicatorSegment(1f, Color.Cyan),
                ProgressIndicatorSegment(1f, Color.Magenta),
                ProgressIndicatorSegment(
                    weight = 1f,
                    indicatorColor = Color.Yellow,
                    inProgressTrackColor = Color.Yellow.copy(alpha = 0.35f)
                        .compositeOver(Color.Gray)
                ),
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
    }

    @OptIn(ExperimentalHorologistApi::class)
    @Test
    fun segmentedPickerWithBrushColors() {
        takeScreenshot {
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
                progress = 0.5833f,
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 10.dp,
                paddingAngle = 2f,
                trackColor = Color.Gray
            )
        }
    }

    @OptIn(ExperimentalHorologistApi::class)
    @Test
    fun segmentedPickerWithBrushColorsAndColorsCombined() {
        takeScreenshot {
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
                progress = 0.5833f,
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 10.dp,
                paddingAngle = 2f,
                trackColor = Color.Gray
            )
        }
    }
}
