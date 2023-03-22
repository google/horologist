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

import androidx.compose.animation.core.LinearEasing
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
import androidx.wear.compose.material.LocalContentColor
import androidx.wear.compose.material.LocalTextStyle
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private enum class MarqueeComponents {
    First,
    Second,
}

private enum class AnimationState {
    // Text will scroll after some delay
    WaitingToScroll,

    // Text is scrolling
    Scrolling,

    // Initial state before or assuming it is determined that scrolling is not required
    NotNeeded,
}

private data class ElementWidths(
    val text: Dp,
    val container: Dp
) {
    val isScrollRequired: Boolean = text > container
}

/**
 * Show a single line Marquee text, with a pause (initial and between cycles) and speed.
 *
 * Otherwise is mostly the same as the [Text] composable, without params that don't apply for
 * marquee, such as maxLines.
 *
 * Only scrolls if required, and otherwise uses textAlign to show the content in a
 * stationary position.
 *
 * @param text The text to be displayed.
 * @param modifier [Modifier] to apply to this layout node.
 * @param color [Color] to apply to the text. If [Color.Unspecified], and [style] has no color set,
 * this will be [LocalContentColor].
 * @param style Style configuration for the text such as color, font, line height etc.
 * @param textAlign The alignment of the text within the lines of the paragraph.
 * See [TextStyle.textAlign].
 * @param followGap the width between end of each scrolling text and the start of the following one.
 * @param edgeGradientWidth the width of the fade out zone on the edges, so text isn't cut off
 * harshly.
 * @param marqueeDpPerSecond the speed of scrolling in dp per second.
 * @param pauseTime the duration before initially scrolling and each additional scroll.
 */
@ExperimentalHorologistApi
@Composable
public fun MarqueeText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    textAlign: TextAlign = TextAlign.Left,
    followGap: Dp = 96.dp,
    edgeGradientWidth: Dp = 16.dp,
    marqueeDpPerSecond: Dp = 64.dp,
    pauseTime: Duration = 4.seconds
) {
    val textFn = @Composable {
        Text(
            text = text,
            modifier = modifier,
            textAlign = textAlign,
            color = color,
            style = style,
            maxLines = 1
        )
    }

    var measuredWidths by remember {
        mutableStateOf(
            ElementWidths(
                text = 0.dp,
                container = 0.dp
            )
        )
    }

    val transitionState = remember { MutableTransitionState(AnimationState.NotNeeded) }
    val transition = updateTransition(transitionState, label = "Animation State")

    val durationMillis = remember(measuredWidths, marqueeDpPerSecond, followGap) {
        ((measuredWidths.text + followGap).value / marqueeDpPerSecond.value * 1000).roundToInt()
    }

    val firstTextStartOffset by transition.animateDp(
        label = "Marquee Offset",
        transitionSpec = {
            if (this.targetState == AnimationState.Scrolling) {
                tween(durationMillis = durationMillis, easing = LinearEasing)
            } else {
                snap()
            }
        }
    ) { state ->
        if (state == AnimationState.Scrolling) {
            edgeGradientWidth - (measuredWidths.text + followGap)
        } else {
            edgeGradientWidth
        }
    }

    // Reset animation to the initial state, before we even know (based on width) if scrolling
    // is required.  It should also reset the timer, since the launched effect below will cancel
    // because current state cahnges.
    LaunchedEffect(text) {
        transitionState.targetState = AnimationState.NotNeeded
    }

    // Reset from completed marquee to pause, whenever we reach Marquee state, it's time to
    // Move back to paused state.
    LaunchedEffect(transitionState.currentState) {
        if (transitionState.currentState == AnimationState.Scrolling) {
            transitionState.targetState = AnimationState.WaitingToScroll
        }
    }

    // Run marquee after a delay, this is the main scrolling loop.
    // While we are in idle / paused state, wait the pause time, then start the animation.
    LaunchedEffect(transitionState.currentState) {
        if (transitionState.currentState == AnimationState.WaitingToScroll && transitionState.isIdle) {
            delay(pauseTime)
            transitionState.targetState = AnimationState.Scrolling
        }
    }

    fun ContentDrawScope.drawFadeGradient(
        leftEdge: Boolean
    ) {
        val width = edgeGradientWidth.toPx()
        drawRect(
            size = Size(width, size.height),
            topLeft = Offset(
                if (leftEdge) 0f else size.width - width,
                0f
            ),
            brush = Brush.horizontalGradient(
                listOf(
                    Color.Transparent,
                    Color.Black
                ),
                startX = if (leftEdge) 0f else size.width,
                endX = if (leftEdge) width else size.width - width
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
        val firstTextPlaceable = subcompose(MarqueeComponents.First) {
            textFn()
        }.first().measure(Constraints())

        measuredWidths = ElementWidths(
            text = firstTextPlaceable.width.toDp(),
            container = constraints.maxWidth.toDp()
        )

        if (transitionState.currentState == AnimationState.NotNeeded && measuredWidths.isScrollRequired) {
            transitionState.targetState = AnimationState.WaitingToScroll
        }

        if (measuredWidths.isScrollRequired) {
            val secondTextPlaceable = subcompose(MarqueeComponents.Second) {
                textFn()
            }.first().measure(Constraints())

            val secondTextStartOffset = firstTextStartOffset + measuredWidths.text + followGap

            layout(
                width = constraints.maxWidth,
                height = firstTextPlaceable.height
            ) {
                firstTextPlaceable.place(firstTextStartOffset.toPx().roundToInt(), 0)

                val secondTextSpace = constraints.maxWidth.toDp() - secondTextStartOffset
                if (secondTextSpace > 0.dp) {
                    secondTextPlaceable.place(secondTextStartOffset.toPx().roundToInt(), 0)
                }
            }
        } else {
            // Render a fixed position single text since it fits

            val x = when (textAlign) {
                TextAlign.Right -> constraints.maxWidth - firstTextPlaceable.width
                TextAlign.Center -> (constraints.maxWidth - firstTextPlaceable.width) / 2
                else -> 0
            }

            layout(
                width = constraints.maxWidth,
                height = firstTextPlaceable.height
            ) {
                firstTextPlaceable.place(x, 0)
            }
        }
    }
}
