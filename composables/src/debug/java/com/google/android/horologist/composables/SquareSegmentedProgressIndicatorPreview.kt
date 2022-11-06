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

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.tools.WearPreview
import kotlinx.coroutines.delay

enum class PreviewAnimationState(val target: Float) {
    Start(0f), End(1f)
}

@OptIn(ExperimentalHorologistComposablesApi::class)
@WearPreview
@Composable
fun Preview() {
    var progressState = remember { PreviewAnimationState.Start }

    val transition = updateTransition(targetState = progressState, label = "Square Progress Animation")

    val progress by transition.animateFloat(
        label = "Progress",
        targetValueByState = {
            it.target
        },
        transitionSpec = {
            tween(durationMillis = 1000, easing = LinearEasing)
        }
    )

    val color = animateColorAsState(
        targetValue = if (progress > 0.7f) Color.Red.copy(alpha = 0.55f)
            .compositeOver(Color.Gray) else if (progress > 0.35) Color.Yellow.copy(alpha = 0.35f)
            .compositeOver(Color.Gray) else Color.Green.copy(alpha = 0.35f)
            .compositeOver(Color.Gray)
    )
    Box(modifier = Modifier.size(300.dp)) {
        SquareSegmentedProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .height(300.dp)
                .width(300.dp),
            progress = progress,
            trackSegments = previewProgressSections
        )
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = progress.toString().dropLast(5),
            color = color.value
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            progressState = PreviewAnimationState.End
            delay(1000)
            progressState = PreviewAnimationState.Start
        }
    }
}

@OptIn(ExperimentalHorologistComposablesApi::class)
@Preview(device = Devices.WEAR_OS_SQUARE)
@Composable
fun PreviewNoAnim() {
    SquareSegmentedProgressIndicator(
        modifier = Modifier
            .height(300.dp)
            .width(300.dp),
        progress = 1f,
        trackSegments = previewProgressSections
    )
}

val previewProgressSections = listOf(
    ProgressIndicatorSegment(3f, Color.Cyan),
    ProgressIndicatorSegment(3f, Color.Magenta),
    ProgressIndicatorSegment(3f, Color.Yellow)
)
