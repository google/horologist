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

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.tools.WearSquareDevicePreview
import kotlinx.coroutines.delay

enum class PreviewAnimationState(val target: Float) {
    Start(0f), End(1f)
}

@OptIn(ExperimentalHorologistApi::class)
@WearSquareDevicePreview
@Composable
fun PreviewProgressAnimation() {
    var progressState by remember { mutableStateOf(PreviewAnimationState.Start) }

    val transition = updateTransition(
        targetState = progressState,
        label = "Square Progress Indicator"
    )

    val progress by transition.animateFloat(
        label = "Progress",
        targetValueByState = { it.target },
        transitionSpec = {
            tween(durationMillis = 1000, easing = LinearEasing)
        }
    )

    val cornerRadiusDp = 10.dp
    Box(modifier = Modifier.size(300.dp)) {
        SquareSegmentedProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .height(300.dp)
                .width(300.dp)
                .clickable {
                    progressState = if (progressState == PreviewAnimationState.Start) {
                        PreviewAnimationState.End
                    } else {
                        PreviewAnimationState.Start
                    }
                },
            progress = progress,
            trackSegments = previewProgressSections,
            cornerRadiusDp = cornerRadiusDp,
            paddingDp = 8.dp
        )
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "${(progress * 100).toInt()}%",
            color = Color.White
        )
        val cornerRadiusPx: Float = with(LocalDensity.current) { cornerRadiusDp.toPx() }
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                Color.LightGray,
                Offset(size.width / 2, 0f),
                Offset(size.width / 2, size.height),
                strokeWidth = 0.2f
            )
            drawLine(
                Color.LightGray,
                Offset(0f, size.height / 2),
                Offset(size.width, size.height / 2),
                strokeWidth = 0.2f
            )

            drawLine(
                Color.LightGray,
                Offset(cornerRadiusPx, 0f),
                Offset(cornerRadiusPx, size.height),
                strokeWidth = 0.2f
            )
            drawLine(
                Color.LightGray,
                Offset(size.width - cornerRadiusPx, 0f),
                Offset(size.width - cornerRadiusPx, size.height),
                strokeWidth = 0.2f
            )

            drawLine(
                Color.LightGray,
                Offset(0f, cornerRadiusPx),
                Offset(size.width, cornerRadiusPx),
                strokeWidth = 0.2f
            )
            drawLine(
                Color.LightGray,
                Offset(0f, size.height - cornerRadiusPx),
                Offset(size.width, size.height - cornerRadiusPx),
                strokeWidth = 0.2f
            )
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            progressState = PreviewAnimationState.End
            delay(1000)
            progressState = PreviewAnimationState.Start
        }
    }
}

@OptIn(ExperimentalHorologistApi::class)
@WearSquareDevicePreview
@Composable
fun PreviewHighCornerRadius() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        SquareSegmentedProgressIndicator(
            modifier = Modifier
                .height(300.dp)
                .width(300.dp),
            progress = 0.5f,
            trackSegments = previewProgressSections,
            cornerRadiusDp = 50.dp
        )
    }
}

@OptIn(ExperimentalHorologistApi::class)
@WearSquareDevicePreview
@Composable
fun PreviewSquare() {
    SquareSegmentedProgressIndicator(
        modifier = Modifier
            .height(300.dp)
            .width(300.dp),
        progress = 1f,
        trackSegments = previewProgressSections,
        cornerRadiusDp = 0.dp,
        paddingDp = 10.dp
    )
}

@OptIn(ExperimentalHorologistApi::class)
@WearSquareDevicePreview
@Composable
fun PreviewSquareWithBrushColors() {
    SquareSegmentedProgressIndicator(
        modifier = Modifier
            .height(300.dp)
            .width(300.dp),
        progress = 1f,
        trackSegments = previewProgressSectionsBrush,
        cornerRadiusDp = 0.dp,
        paddingDp = 10.dp
    )
}

@OptIn(ExperimentalHorologistApi::class)
@WearSquareDevicePreview
@Composable
fun PreviewSquareWithBrushAndColorsCombined() {
    SquareSegmentedProgressIndicator(
        modifier = Modifier
            .height(300.dp)
            .width(300.dp),
        progress = 1f,
        trackSegments = previewProgressSectionsBrushAndColorCombined,
        cornerRadiusDp = 0.dp,
        paddingDp = 10.dp
    )
}

val previewProgressSections = listOf(
    ProgressIndicatorSegment(
        weight = 3f,
        indicatorColor = Color.Cyan
    ),
    ProgressIndicatorSegment(
        weight = 3f,
        indicatorColor = Color.Magenta
    ),
    ProgressIndicatorSegment(
        weight = 3f,
        indicatorColor = Color.Yellow
    )
)

val previewProgressSectionsBrush = listOf(
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

val previewProgressSectionsBrushAndColorCombined = listOf(
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
