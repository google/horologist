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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
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
    val textFn: @Composable (Modifier) -> Unit = {
        Text(
            text = text,
            modifier = it,
            textAlign = textAlign,
            color = color,
            style = style,
            maxLines = 1
        )
    }

    SubcomposeLayout(
        modifier = modifier
    ) { constraints ->
        // Measure without any constraints to get the ideal unconstrained width
        val textPlaceable = subcompose(MarqueeComponents.First) {
            textFn(Modifier)
        }.first().measure(Constraints())

        val textWidth = textPlaceable.width.toDp()
        val containerWidth = constraints.maxWidth.toDp()
        val isScrollRequired = textWidth > containerWidth

        if (isScrollRequired) {
            // Render a fixed position single text since it fits

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
        } else {
            // Render a marquee text since it's too wide
            val marqueeTextPlaceable = subcompose(MarqueeComponents.Second) {
                val textModifier = Modifier
                    .basicMarquee(
                        iterations = Int.MAX_VALUE,
                        delayMillis = pauseTime.inWholeMilliseconds.toInt(),
                        initialDelayMillis = pauseTime.inWholeMilliseconds.toInt(),
                        spacing = MarqueeSpacing(followGap),
                        velocity = marqueeDpPerSecond
                    )
                    // Start unobscured by the edge gradients
                    // and avoid painting past the gradients
                    .offset(edgeGradientWidth)
                    .clipToBounds()

                Box(modifier = Modifier.drawWithContent {
                    drawContent()

                    // Fade out the edges with a gradient
                    drawFadeGradient(leftEdge = true, edgeGradientWidth = edgeGradientWidth)
                    drawFadeGradient(leftEdge = false, edgeGradientWidth = edgeGradientWidth)
                }) {
                    textFn(textModifier)
                }
            }.first().measure(constraints)

            layout(
                width = constraints.maxWidth,
                height = textPlaceable.height
            ) {
                marqueeTextPlaceable.place(0, 0)
            }
        }
    }
}

fun ContentDrawScope.drawFadeGradient(
    leftEdge: Boolean,
    edgeGradientWidth: Dp
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

private enum class MarqueeComponents {
    First,
    Second,
}
