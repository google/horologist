/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.screensizes

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeSource
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.curvedText
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.material.Chip

@Composable
fun ScalingLazyColumnDecoder(factory: ScalingLazyColumnState.Factory) {
    val columnState = factory.create()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        positionIndicator = {
            PositionIndicator(columnState.state)
        },
        timeText = {
            val size = LocalConfiguration.current.screenWidthDp
            val listState = columnState.state
            TimeText(
                timeSource = FixedTimeSource,
                startCurvedContent = { curvedText("${listState.centerItemIndex}/${listState.centerItemScrollOffset}") },
                endCurvedContent = { curvedText("${size}dp") },
                startLinearContent = { Text("${listState.centerItemIndex}/${listState.centerItemScrollOffset}") },
                endLinearContent = { Text("${size}dp") },
            )
        },
    ) {
        ScalingLazyColumn(columnState = columnState) {
            items(10) {
                Chip(label = "Item $it", onClick = { })
            }
        }
        val paint = remember {
            Paint().apply {
                this.textSize = 16f
                this.color = android.graphics.Color.WHITE
            }
        }
        val layoutDirection = LocalLayoutDirection.current
        val density = LocalDensity.current
        val leftPadding =
            with(density) { state.contentPadding.calculateLeftPadding(layoutDirection).toPx() }
        val rightPadding =
            with(density) { state.contentPadding.calculateRightPadding(layoutDirection).toPx() }
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                Color.LightGray,
                Offset(0f, size.height / 2f),
                Offset(size.width, size.height / 2f),
            )
            val minTransition = columnState.scalingParams.minTransitionArea * size.height
            val maxTransition = columnState.scalingParams.maxTransitionArea * size.height
            drawLine(
                Color.Green,
                Offset(0f, minTransition),
                Offset(size.width, minTransition),
            )
            drawLine(
                Color.Green,
                Offset(0f, maxTransition),
                Offset(size.width, maxTransition),
            )
            drawLine(
                Color.Red,
                Offset(0f, size.height - minTransition),
                Offset(size.width, size.height - minTransition),
            )
            drawLine(
                Color.Red,
                Offset(0f, size.height - maxTransition),
                Offset(size.width, size.height - maxTransition),
            )
            drawLine(
                Color.Green,
                Offset(leftPadding, 0f),
                Offset(leftPadding, size.height),
            )
            drawLine(
                Color.Green,
                Offset(rightPadding, 0f),
                Offset(rightPadding, size.height),
            )
            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    "Min Height ${columnState.scalingParams.minElementHeight}",
                    30f,
                    size.height / 2,
                    paint,
                )
                it.nativeCanvas.drawText(
                    "Max Height ${columnState.scalingParams.maxElementHeight}",
                    size.width / 2,
                    size.height / 2,
                    paint,
                )
                it.nativeCanvas.drawText(
                    "Min Transition ${columnState.scalingParams.minTransitionArea}",
                    30f,
                    minTransition,
                    paint,
                )
                it.nativeCanvas.drawText(
                    "Max Transition ${columnState.scalingParams.maxTransitionArea}",
                    30f,
                    maxTransition,
                    paint,
                )
                it.nativeCanvas.drawText(
                    "Padding ${
                        columnState.contentPadding.calculateRightPadding(
                            layoutDirection,
                        )
                    }",
                    size.width - 150,
                    size.height / 2 + 25,
                    paint,
                )
            }
        }
    }
}

public object FixedTimeSource : TimeSource {
    override val currentTime: String
        @Composable get() = "10:10"
}

@WearPreviewDevices
@Composable
fun Standard() {
    ScalingLazyColumnDecoder(factory = ScalingLazyColumnDefaults.scalingLazyColumnDefaults())
}

@WearPreviewDevices
@Composable
fun BelowTimeText() {
    ScalingLazyColumnDecoder(factory = ScalingLazyColumnDefaults.belowTimeText())
}

@WearPreviewDevices
@Composable
fun Responsive() {
    ScalingLazyColumnDecoder(factory = ScalingLazyColumnDefaults.responsive())
}
