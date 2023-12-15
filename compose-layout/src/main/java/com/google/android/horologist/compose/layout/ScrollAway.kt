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

package com.google.android.horologist.compose.layout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.google.android.horologist.compose.navscaffold.ScalingLazyColumnScrollableState
import kotlinx.coroutines.launch

/**
 * Scroll an item vertically in/out of view based on a [ScrollState].
 * Typically used to scroll a [TimeText] item out of view as the user starts to scroll a
 * vertically scrollable [Column] of items upwards and bring additional items into view.
 *
 * @param scrollState The [ScrollState] to used as the basis for the scroll-away.
 * @param offset Adjustment to the starting point for scrolling away. Positive values result in
 * the scroll away starting later.
 */
public fun Modifier.scrollAway(
    scrollableState: () -> ScrollableState?,
): Modifier = scrollAwayImpl {
    when (val scrollState = scrollableState()) {
        is ScalingLazyColumnScrollableState -> {
            val initialOffsetDp = scrollState.initialOffsetPx.toDp()

            ScrollParams(
                valid = scrollState.initialIndex < scrollState.scalingLazyListState.layoutInfo.totalItemsCount,
                isScrollInProgress = scrollState.isScrollInProgress,
                yPx = scrollState.scalingLazyListState.layoutInfo.visibleItemsInfo.find { it.index == scrollState.initialIndex }
                    ?.let {
                        -it.offset - initialOffsetDp.toPx()
                    },
            )
        }

        is ScalingLazyColumnState -> {
            val initialOffsetDp = scrollState.initialScrollPosition.offsetPx.toDp()

            ScrollParams(
                valid = scrollState.initialScrollPosition.index < scrollState.state.layoutInfo.totalItemsCount,
                isScrollInProgress = scrollState.isScrollInProgress,
                yPx = scrollState.state.layoutInfo.visibleItemsInfo.find { it.index == scrollState.initialScrollPosition.index }
                    ?.let {
                        -it.offset - initialOffsetDp.toPx()
                    },
            )
        }

        is LazyListState -> {
            ScrollParams(
                valid = 0 < scrollState.layoutInfo.totalItemsCount,
                isScrollInProgress = scrollState.isScrollInProgress,
                yPx = scrollState.layoutInfo.visibleItemsInfo.find { it.index == 0 }?.let {
                    -it.offset - 0f
                },
            )
        }

        is ScrollState -> {
            ScrollParams(
                valid = true,
                isScrollInProgress = scrollState.isScrollInProgress,
                yPx = scrollState.value.toFloat(),
            )
        }

        else -> {
            // Hide by display as offscreen
            ScrollParams(
                true,
                false,
                10000f,
            )
        }
    }
}

private fun Modifier.scrollAwayImpl(
    scrollFn: Density.() -> ScrollParams,
): Modifier = composed {
    val coroutineScope = rememberCoroutineScope()
    var animatable by remember {
        mutableStateOf<Animatable<Float, AnimationVector1D>?>(null)
    }
    var prevProgress by remember {
        mutableFloatStateOf(0f)
    }
    this.then(
        @Suppress("ModifierInspectorInfo")
        object : LayoutModifier {
            override fun MeasureScope.measure(
                measurable: Measurable,
                constraints: Constraints,
            ): MeasureResult {
                val placeable = measurable.measure(constraints)
                return layout(placeable.width, placeable.height) {
                    placeable.placeWithLayer(0, 0) {
                        val scrollParams = scrollFn()
                        val (motionFraction: Float, offsetY) =
                            if (!scrollParams.valid) {
                                // When the itemIndex is invalid, just show the content anyway.
                                1f to 0f
                            } else if (scrollParams.yPx == null) {
                                // When itemIndex is valid but yPx is null, we infer that
                                // the item is not in the visible items list, so hide it.
                                0f to 0f
                            } else {
                                // Scale, fade and scroll the content to scroll it away.
                                val anim: Animatable<Float, AnimationVector1D> =
                                    animatable ?: Animatable(prevProgress).also { animatable = it }
                                val targetProgress: Float =
                                    (scrollParams.yPx / maxScrollOut.toPx()).coerceIn(0f, 1f)
                                var progress = 0f
                                if (scrollParams.isScrollInProgress) {
                                    if (anim.targetValue != targetProgress) {
                                        coroutineScope.launch {
                                            anim.snapTo(targetProgress)
                                        }
                                    }
                                } else {
                                    if (anim.targetValue != targetProgress) {
                                        coroutineScope.launch {
                                            anim.animateTo(
                                                targetProgress,
                                                tween(durationMillis = SHORT_4, easing = STANDARD),
                                            )
                                        }
                                    }
                                }
                                animatable?.let {
                                    progress = it.value
                                }
                                prevProgress = targetProgress
                                val motionFraction: Float =
                                    lerp(minMotionOut, maxMotionOut, progress)
                                val offsetY = -(maxOffset.toPx() * progress)
                                motionFraction to offsetY
                            }

                        alpha = motionFraction
                        scaleX = motionFraction
                        scaleY = motionFraction
                        translationY = offsetY
                        transformOrigin =
                            TransformOrigin(pivotFractionX = 0.5f, pivotFractionY = 0.0f)
                    }
                }
            }
        },
    )
}

private data class ScrollParams(
    val valid: Boolean,
    val isScrollInProgress: Boolean,
    val yPx: Float?,
)

// The scroll motion effects take place between 0dp and 36dp.
internal val maxScrollOut = 36.dp

// The max offset to apply.
internal val maxOffset = 24.dp

// Fade and scale motion effects are between 100% and 50%.
internal const val minMotionOut = 1f
internal const val maxMotionOut = 0.5f

// See Wear Motion durations: https://carbon.googleplex.com/wear-os-3/pages/speed
internal const val SHORT_1 = 50
internal const val SHORT_2 = 100
internal const val SHORT_3 = 150
internal const val SHORT_4 = 200

internal const val MEDIUM_1 = 250
internal const val MEDIUM_2 = 300
internal const val MEDIUM_3 = 350
internal const val MEDIUM_4 = 400

internal const val LONG_1 = 450
internal const val LONG_2 = 500
internal const val LONG_3 = 550
internal const val LONG_4 = 600

internal const val EXTRA_LONG_1 = 700
internal const val EXTRA_LONG_2 = 800
internal const val EXTRA_LONG_3 = 900
internal const val EXTRA_LONG_4 = 1000

internal val STANDARD = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
internal val STANDARD_ACCELERATE = CubicBezierEasing(0.3f, 0.0f, 1.0f, 1.0f)
internal val STANDARD_DECELERATE = CubicBezierEasing(0.0f, 0.0f, 0.0f, 1.0f)
