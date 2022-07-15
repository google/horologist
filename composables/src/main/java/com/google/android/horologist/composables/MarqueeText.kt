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
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
    NotRequired,
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

    val currentState = remember { MutableTransitionState(AnimationState.Pause) }
    val transition = updateTransition(currentState, label = "Animation State")

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

    LaunchedEffect(text) {
        currentState.targetState = AnimationState.Pause
        delay(pauseTime)
        if (measuredWidths.isScrollRequired) {
            currentState.targetState = AnimationState.Marquee
        }
    }

    LaunchedEffect(currentState) {
        if (measuredWidths.isScrollRequired)
    }

    SubcomposeLayout(
        modifier = modifier
            .clipToBounds()
            .drawWithContent {
                drawContent()

                if (measuredWidths.isScrollRequired) {
                    drawRect(
                        topLeft = Offset.Zero,
                        size = Size(edgeGradientWidth.toPx(), this.size.height),
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black
                            ),
                            startX = 0f,
                            endX = edgeGradientWidth.toPx()
                        ),
                        blendMode = BlendMode.DstIn
                    )

                    drawRect(
                        size = Size(edgeGradientWidth.toPx(), this.size.height),
                        topLeft = Offset(
                            measuredWidths.container.toPx() - edgeGradientWidth.toPx(),
                            0f
                        ),
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color.Black,
                                Color.Transparent
                            ),
                            startX = this.size.width - edgeGradientWidth.toPx(),
                            endX = this.size.width
                        ),
                        blendMode = BlendMode.DstIn
                    )
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

@OptIn(ExperimentalHorologistComposablesApi::class)
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
