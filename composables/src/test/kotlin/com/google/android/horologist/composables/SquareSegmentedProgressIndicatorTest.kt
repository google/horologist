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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.paparazzi.WearPaparazzi
import org.junit.Rule
import org.junit.Test

class SquareSegmentedProgressIndicatorTest {
    @OptIn(ExperimentalHorologistApi::class)
    @get:Rule
    val paparazzi = WearPaparazzi(deviceConfig = DeviceConfig.WEAR_OS_SQUARE)

    @OptIn(ExperimentalHorologistApi::class)
    @Test
    fun squareSegmentedIndicatorLowCornerRadius() {
        paparazzi.snapshot {
            val segments = listOf(
                ProgressIndicatorSegment(
                    weight = 1f,
                    indicatorColor = Color.Cyan
                ),
                ProgressIndicatorSegment(
                    weight = 1f,
                    indicatorColor = Color.Magenta
                ),
                ProgressIndicatorSegment(
                    weight = 1f,
                    indicatorColor = Color.Yellow
                )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                SquareSegmentedProgressIndicator(
                    modifier = Modifier
                        .height(300.dp)
                        .width(300.dp),
                    progress = 0.5833f,
                    trackSegments = segments,
                    cornerRadiusDp = 0.dp,
                    paddingDp = 10.dp
                )
            }
        }
    }

    @OptIn(ExperimentalHorologistApi::class)
    @Test
    fun squareSegmentedIndicatorHighCornerRadius() {
        paparazzi.snapshot {
            val segments = listOf(
                ProgressIndicatorSegment(
                    weight = 1f,
                    indicatorColor = Color.Cyan
                ),
                ProgressIndicatorSegment(
                    weight = 1f,
                    indicatorColor = Color.Magenta
                ),
                ProgressIndicatorSegment(
                    weight = 1f,
                    indicatorColor = Color.Yellow
                )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                SquareSegmentedProgressIndicator(
                    modifier = Modifier
                        .height(300.dp)
                        .width(300.dp),
                    progress = 0.5833f,
                    trackSegments = segments,
                    cornerRadiusDp = 50.dp,
                    paddingDp = 10.dp
                )
            }
        }
    }

    @OptIn(ExperimentalHorologistApi::class)
    @Test
    fun squareSegmentedIndicatorManySegments() {
        paparazzi.snapshot {
            val segments = listOf(
                ProgressIndicatorSegment(
                    weight = 1f,
                    indicatorColor = Color.Cyan
                ),
                ProgressIndicatorSegment(
                    weight = 1f,
                    indicatorColor = Color.Magenta
                ),
                ProgressIndicatorSegment(
                    weight = 1f,
                    indicatorColor = Color.Yellow
                ),
                ProgressIndicatorSegment(
                    weight = 1f,
                    indicatorColor = Color.Black
                ),
                ProgressIndicatorSegment(
                    weight = 1f,
                    indicatorColor = Color.Green
                )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                SquareSegmentedProgressIndicator(
                    modifier = Modifier
                        .height(300.dp)
                        .width(300.dp),
                    progress = 0.5833f,
                    trackSegments = segments,
                    cornerRadiusDp = 50.dp,
                    paddingDp = 10.dp
                )
            }
        }
    }

    @OptIn(ExperimentalHorologistApi::class)
    @Test
    fun squareSegmentedIndicatorFewSegments() {
        paparazzi.snapshot {
            val segments = listOf(
                ProgressIndicatorSegment(
                    weight = 1f,
                    indicatorColor = Color.Yellow
                ),
                ProgressIndicatorSegment(
                    weight = 1f,
                    indicatorColor = Color.Yellow
                )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                SquareSegmentedProgressIndicator(
                    modifier = Modifier
                        .height(300.dp)
                        .width(300.dp),
                    progress = 0.7833f,
                    trackSegments = segments,
                    cornerRadiusDp = 50.dp,
                    paddingDp = 10.dp
                )
            }
        }
    }

    @OptIn(ExperimentalHorologistApi::class)
    @Test
    fun squareSegmentedIndicatorFewSegmentsAndBrushColor() {
        paparazzi.snapshot {
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                SquareSegmentedProgressIndicator(
                    modifier = Modifier
                        .height(300.dp)
                        .width(300.dp),
                    progress = 0.7833f,
                    trackSegments = segments,
                    cornerRadiusDp = 50.dp,
                    paddingDp = 10.dp
                )
            }
        }
    }

    @OptIn(ExperimentalHorologistApi::class)
    @Test
    fun squareSegmentedIndicatorFewSegmentsAndBrushColorAndColorsCombined() {
        paparazzi.snapshot {
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                SquareSegmentedProgressIndicator(
                    modifier = Modifier
                        .height(300.dp)
                        .width(300.dp),
                    progress = 0.7833f,
                    trackSegments = segments,
                    cornerRadiusDp = 50.dp,
                    paddingDp = 10.dp
                )
            }
        }
    }
}
