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

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.LocalTextStyle
import androidx.wear.compose.material.Text
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private enum class MarqueeComponents {
    Main,
    Second,
}

private enum class AnimationState {
    Pause,
    Marquee,
    NotNeeded,
}

private data class ElementWidths(
    val text: Dp,
    val container: Dp
) {
    val isScrollRequired: Boolean = text > container
}

@ExperimentalHorologistComposablesApi
@Composable
public fun MarqueeText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    textAlign: TextAlign = TextAlign.Left,
    followGap: Dp = 96.dp,
    edgeGradientWidth: Dp = 10.dp,
    marqueeDpPerSecond: Dp = 64.dp,
    pauseTime: Duration = 4.seconds
) {
    val textFn = @Composable {
        Text(
            text = text,
            modifier = modifier,
            color = color,
            style = style,
            maxLines = 1
        )
    }

    var measuredWidths by remember {
        mutableStateOf(
            ElementWidths(
                text = 0.dp, container = 0.dp
            )
        )
    }

    val transitionState = remember { MutableTransitionState(AnimationState.Pause) }
    val transition = updateTransition(transitionState, label = "Animation State")

    val durationMillis = remember(measuredWidths, marqueeDpPerSecond, followGap) {
        ((measuredWidths.text + followGap).value / marqueeDpPerSecond.value * 1000).roundToInt()
    }

    val offset by transition.animateDp(
        label = "Marquee Offset",
        transitionSpec = {
            if (this.targetState == AnimationState.Marquee) {
                tween(durationMillis = durationMillis)
            } else {
                snap()
            }
        }
    ) { state ->
        if (state == AnimationState.Marquee) {
            edgeGradientWidth - (measuredWidths.text + followGap)
        } else {
            edgeGradientWidth
        }
    }

    // Reset animation
    LaunchedEffect(text) {
        transitionState.targetState = AnimationState.NotNeeded
    }

    // Reset from completed marquee to pause
    LaunchedEffect(transitionState.currentState) {
        if (transitionState.currentState == AnimationState.Marquee) {
            transitionState.targetState = AnimationState.Pause
        }
    }

    // Run marquee after a delay
    LaunchedEffect(transitionState.currentState) {
        if (transitionState.currentState == AnimationState.Pause && transitionState.isIdle) {
            delay(pauseTime)
            transitionState.targetState = AnimationState.Marquee
        }
    }

    fun ContentDrawScope.drawFadeGradient(
        leftEdge: Boolean,
    ) {
        drawRect(
            size = Size(edgeGradientWidth.toPx(), size.height),
            topLeft = Offset(
                if (leftEdge) 0f else size.width - edgeGradientWidth.toPx(),
                0f
            ),
            brush = Brush.horizontalGradient(
                listOf(
                    Color.Transparent,
                    Color.Black
                ),
                startX = if (leftEdge) 0f else size.width,
                endX = if (leftEdge) edgeGradientWidth.toPx() else size.width - edgeGradientWidth.toPx()
            ),
            blendMode = BlendMode.DstIn
        )
    }

    SubcomposeLayout(
        modifier = modifier
            .clipToBounds()
            .drawWithContent {
                drawContent()

                if (measuredWidths.isScrollRequired) {
                    drawFadeGradient(leftEdge = true)
                    drawFadeGradient(leftEdge = false)
                }
            }
    ) { constraints ->
        val textPlaceable = subcompose(MarqueeComponents.Main) {
            textFn()
        }.first().measure(Constraints())

        measuredWidths = ElementWidths(
            text = textPlaceable.width.toDp(),
            container = constraints.maxWidth.toDp()
        )

        if (transitionState.currentState == AnimationState.NotNeeded && measuredWidths.isScrollRequired) {
            transitionState.targetState = AnimationState.Pause
        }

        if (measuredWidths.isScrollRequired) {
            val secondTextPlaceable = subcompose(MarqueeComponents.Second) {
                textFn()
            }.first().measure(Constraints())

            val firstTextOffset = offset
            val secondTextOffset = firstTextOffset + measuredWidths.text + followGap

            layout(
                width = constraints.maxWidth,
                height = textPlaceable.height
            ) {
                textPlaceable.place(firstTextOffset.toPx().roundToInt(), 0)

                val secondTextSpace = constraints.maxWidth.toDp() - secondTextOffset
                if (secondTextSpace > 0.dp) {
                    secondTextPlaceable.place(secondTextOffset.toPx().roundToInt(), 0)
                }
            }
        } else {
            val x = when (textAlign) {
                TextAlign.Right -> constraints.maxWidth - textPlaceable.width
                TextAlign.Center -> (constraints.maxWidth - textPlaceable.width) / 2
                else -> 0
            }

            layout(
                width = constraints.maxWidth,
                height = textPlaceable.height
            ) {
                textPlaceable.place(x, 0)
            }
        }
    }
}
