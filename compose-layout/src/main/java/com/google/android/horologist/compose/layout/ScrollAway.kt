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

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.wear.compose.material.ScalingLazyListState

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
    scrollState: ScrollState,
    offset: Dp = 0.dp
): Modifier = scrollAway { scrollState.value - offset.toPx() }

/**
 * Scroll an item vertically in/out of view based on a [LazyListState].
 * Typically used to scroll a [TimeText] item out of view as the user starts to scroll
 * a [LazyColumn] of items upwards and bring additional items into view.
 *
 * @param scrollState The [LazyListState] to used as the basis for the scroll-away.
 * @param itemIndex The item for which the scroll offset will trigger scrolling away.
 * @param offset Adjustment to the starting point for scrolling away. Positive values result in
 * the scroll away starting later.
 */
public fun Modifier.scrollAway(
    scrollState: LazyListState,
    itemIndex: Int = 0,
    offset: Dp = 0.dp
): Modifier {
    val targetItem = scrollState.layoutInfo.visibleItemsInfo.find { it.index == itemIndex }
    return if (targetItem != null) {
        scrollAway { -targetItem.offset - offset.toPx() }
    } else {
        ignore()
    }
}

/**
 * Scroll an item vertically in/out of view based on a [ScalingLazyListState].
 * Typically used to scroll a [TimeText] item out of view as the user starts to scroll
 * a [ScalingLazyColumn] of items upwards and bring additional items into view.
 *
 * @param scrollState The [ScalingLazyListState] to used as the basis for the scroll-away.
 * @param itemIndex The item for which the scroll offset will trigger scrolling away.
 * @param offset Adjustment to the starting point for scrolling away. Positive values result in
 * the scroll away starting later, negative values start scrolling away earlier.
 */
public fun Modifier.scrollAway(
    scrollState: ScalingLazyListState,
    itemIndex: Int = 1,
    offset: Dp = 0.dp
): Modifier {
    val targetItem = scrollState.layoutInfo.visibleItemsInfo.find { it.index == itemIndex }
    return if (targetItem != null) {
        scrollAway { -targetItem.offset - offset.toPx() }
    } else {
        ignore()
    }
}

private fun Modifier.scrollAway(yPxFn: Density.() -> Float): Modifier = this.then(
    object : LayoutModifier {
        override fun MeasureScope.measure(
            measurable: Measurable,
            constraints: Constraints
        ): MeasureResult {
            val placeable = measurable.measure(constraints)
            return layout(placeable.width, placeable.height) {
                val yPx = yPxFn()
                val progress: Float = (yPx / maxScrollOut.toPx()).coerceIn(0f, 1f)
                val motionFraction: Float = lerp(minMotionOut, maxMotionOut, progress)
                val offsetY = -(maxOffset.toPx() * progress).toInt()

                placeable.placeWithLayer(0, offsetY) {
                    alpha = motionFraction
                    scaleX = motionFraction
                    scaleY = motionFraction
                    transformOrigin = TransformOrigin(pivotFractionX = 0.5f, pivotFractionY = 0.0f)
                }
            }
        }
    }
)

// Trivial modifier that neither measures nor places the content.
private fun Modifier.ignore(): Modifier = this.then(
    object : LayoutModifier {
        override fun MeasureScope.measure(
            measurable: Measurable,
            constraints: Constraints
        ): MeasureResult {
            return object : MeasureResult {
                override val width = 0
                override val height = 0
                override val alignmentLines = mapOf<AlignmentLine, Int>()
                override fun placeChildren() {}
            }
        }
    }
)

// The scroll motion effects take place between 0dp and 36dp.
internal val maxScrollOut = 36.dp

// The max offset to apply.
internal val maxOffset = 24.dp

// Fade and scale motion effects are between 100% and 50%.
internal const val minMotionOut = 1f
internal const val maxMotionOut = 0.5f
