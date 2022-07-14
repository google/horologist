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

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.LocalTextStyle
import androidx.wear.compose.material.Text

enum class MarqueState {
    Initial,
    End
}

enum class MarqueeComponents {
    Main,
    Second,
}

private data class ElementWidths(
    val text: Int,
    val textWithSpacing: Int,
    val container: Int
) {
    val isScrollRequired: Boolean = text > container
}

@Composable
fun MarqueeText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    textAlign: TextAlign = TextAlign.Left,
    spacing: Int = 50,
    edgeGradientWidth: Int = 10
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

    val widths = remember { mutableStateOf<ElementWidths?>(null) }

    val transitionState = remember {
        MutableTransitionState(MarqueState.Initial).apply {
            targetState = MarqueState.End
        }
    }

    val transition = updateTransition(
        transitionState = transitionState,
        label = "Marquee State"
    )

    val offset = transition.animateInt(
        transitionSpec = { tween(
            durationMillis = 3000,
            easing = LinearEasing
        ) },
        label = "Offset"
    ) {
        if (it == MarqueState.Initial) {
            0
        } else {
            -(widths.value?.textWithSpacing ?: 0)
        }
    }

    SubcomposeLayout(
        modifier = modifier
            .clipToBounds()
            .drawWithContent {
                drawContent()

                val calculatedWidths = widths.value
                if (calculatedWidths != null && calculatedWidths.isScrollRequired) {
                    drawRect(
                        topLeft = Offset.Zero,
                        size = Size(edgeGradientWidth.toFloat(), this.size.height),
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black
                            )
                        ),
                        blendMode = BlendMode.DstIn
                    )

                    drawRect(
                        size = Size(edgeGradientWidth.toFloat(), this.size.height),
                        topLeft = Offset(
                            calculatedWidths.container - edgeGradientWidth.toFloat(),
                            0f
                        ),
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color.Black,
                                Color.Transparent
                            )
                        ),
                        blendMode = BlendMode.DstIn
                    )
                }
            }
    ) { constraints ->
        val textPlaceable = subcompose(MarqueeComponents.Main) {
            textFn()
        }.first().measure(Constraints())

        val calculatedWidths = ElementWidths(
            text = textPlaceable.width,
            textWithSpacing = textPlaceable.width + spacing,
            container = constraints.maxWidth
        )
        widths.value = calculatedWidths
        if (!calculatedWidths.isScrollRequired) {
            val x = fixedPlacement(textAlign, textPlaceable.width, constraints.maxWidth)

            layout(
                width = constraints.maxWidth,
                height = textPlaceable.height
            ) {
                textPlaceable.place(x, 0)
            }
        } else {
            val secondTextPlaceable = subcompose(MarqueeComponents.Second) {
                textFn()
            }.first().measure(Constraints())

            val firstElementWidth = textPlaceable.width + spacing
            val secondTextOffset = firstElementWidth + offset.value + edgeGradientWidth

            layout(
                width = constraints.maxWidth,
                height = textPlaceable.height
            ) {
                textPlaceable.place(offset.value + edgeGradientWidth, 0)

                val secondTextSpace = constraints.maxWidth - secondTextOffset
                if (secondTextSpace > 0) {
                    secondTextPlaceable.place(secondTextOffset, 0)
                }
            }
        }
    }
}

fun fixedPlacement(textAlign: TextAlign, width: Int, maxWidth: Int): Int = when (textAlign) {
    TextAlign.Right -> maxWidth - width
    TextAlign.Center -> (maxWidth - width) / 2
    else -> 0
}

@Preview(
    widthDp = 100,
    heightDp = 20,
    backgroundColor = 0xFF000000,
    showBackground = true
)
@Composable
fun MarqueeTextPreview() {
    MarqueeText(
        text = "A very long text strings",
        modifier = Modifier
            .fillMaxWidth(),
        textAlign = TextAlign.Center,
    )
}
