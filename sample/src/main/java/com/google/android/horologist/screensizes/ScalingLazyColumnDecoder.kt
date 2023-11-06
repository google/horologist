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
    val state = factory.create()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        positionIndicator = {
            PositionIndicator(state.state)
        },
        timeText = {
            val size = LocalConfiguration.current.screenWidthDp
            TimeText(
                modifier = Modifier, // .scrollAway(state),
                timeSource = FixedTimeSource,
                startCurvedContent = { curvedText("${state.state.centerItemIndex}/${state.state.centerItemScrollOffset}") },
                endCurvedContent = { curvedText("${size}dp") },
                startLinearContent = { Text("${state.state.centerItemIndex}/${state.state.centerItemScrollOffset}") },
                endLinearContent = { Text("${size}dp") },
            )
        },
    ) {
        ScalingLazyColumn(columnState = state) {
            items(10) {
                Chip(label = "Item $it", onClick = { /*TODO*/ })
            }
        }
        val paint = remember {
            Paint().apply {
                this.textSize = 16f
                this.color = android.graphics.Color.WHITE
            }
        }
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                Color.Red,
                Offset(0f, size.height / 2f),
                Offset(size.width, size.height / 2f),
            )
            val minTransition = state.scalingParams.minTransitionArea * size.height
            val maxTransition = state.scalingParams.maxTransitionArea * size.height
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
                Color.Green,
                Offset(0f, size.height - minTransition),
                Offset(size.width, size.height - minTransition),
            )
            drawLine(
                Color.Green,
                Offset(0f, size.height - maxTransition),
                Offset(size.width, size.height - maxTransition),
            )
            drawIntoCanvas {
                it.nativeCanvas.drawText("Min Height " + state.scalingParams.minElementHeight, 30f, minTransition, paint)
                it.nativeCanvas.drawText("Max Height " + state.scalingParams.maxElementHeight, 30f, maxTransition, paint)
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
