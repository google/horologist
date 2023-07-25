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

@file:OptIn(ExperimentalFoundationApi::class)

package com.google.android.horologist.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.LocalContentColor
import androidx.wear.compose.material.LocalTextStyle
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

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
    val controller = remember(text, style) { MarqueeController(edgeGradientWidth) }
    controller.edgeGradientWidth = edgeGradientWidth

    Text(
        text = text,
        modifier = modifier
            .then(controller.outsideMarqueeModifier)
            .basicMarquee(
                iterations = Int.MAX_VALUE,
                delayMillis = pauseTime.inWholeMilliseconds.toInt(),
                initialDelayMillis = pauseTime.inWholeMilliseconds.toInt(),
                spacing = MarqueeSpacing(followGap),
                velocity = marqueeDpPerSecond
            )
            .then(controller.insideMarqueeModifier),
        textAlign = textAlign,
        color = color,
        style = style,
        maxLines = 1
    )
}

private class MarqueeController(edgeGradientWidth: Dp) {

    var edgeGradientWidth: Dp by mutableStateOf(edgeGradientWidth)
    private var needsScrolling by mutableStateOf(false)
    private var contentWidth: Int by mutableStateOf(-1)

    val outsideMarqueeModifier: Modifier = Modifier
        .layout { measurable, constraints ->
            // The max intrinsic width is how long the text wants to be, if it had infinite width
            // constraints. If that's larger than the available width, the marquee will scroll.
            if (contentWidth < 0) {
                contentWidth = measurable.maxIntrinsicWidth(0)
                needsScrolling = contentWidth > constraints.maxWidth
            }

            val placeable = measurable.measure(constraints)
            layout(placeable.width, placeable.height) {
                placeable.placeRelative(IntOffset.Zero)
            }
        }
        .drawFadeGradient()

    private val padding = object : PaddingValues {
        override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
            if (needsScrolling && layoutDirection == LayoutDirection.Ltr) edgeGradientWidth else 0.dp

        override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
            if (needsScrolling && layoutDirection != LayoutDirection.Ltr) edgeGradientWidth else 0.dp

        override fun calculateTopPadding(): Dp = 0.dp
        override fun calculateBottomPadding(): Dp = 0.dp
    }
    val insideMarqueeModifier: Modifier = Modifier.padding(padding)

    private fun Modifier.drawFadeGradient() = this.drawWithCache {
        val width = edgeGradientWidth.toPx()
        // Create the brush here and leverage it within the onDrawWithContent block below
        // The brush will only be instantiated on first render and if the size of the composable
        // changes. Otherwise the same brush instance will be used.
        val leftBrush = Brush.horizontalGradient(
            listOf(
                Color.Transparent,
                Color.Black
            ),
            startX = 0f,
            endX = width
        )
        val rightBrush = Brush.horizontalGradient(
            listOf(
                Color.Transparent,
                Color.Black
            ),
            startX = size.width,
            endX = size.width - width
        )
        onDrawWithContent {
            drawContent()

            if (needsScrolling) {
                drawRect(
                    size = Size(width, size.height),
                    topLeft = Offset(
                        0f,
                        0f
                    ),
                    brush = leftBrush,
                    blendMode = BlendMode.DstIn
                )
                drawRect(
                    size = Size(width, size.height),
                    topLeft = Offset(
                        size.width - width,
                        0f
                    ),
                    brush = rightBrush,
                    blendMode = BlendMode.DstIn
                )
            }
        }
    }
}
