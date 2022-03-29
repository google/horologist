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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ScalingLazyListState

/**
 * Scroll Away the item based on a regular scrolling item, like a Column.
 * Does not include fading or scaling.
 */
internal fun Modifier.fadeAway(scrollStateFn: () -> ScrollState): Modifier = composed {
    val scrollState = scrollStateFn()
    val y = scrollState.value / LocalDensity.current.density

    fadeEffect(y, fade = false)
}

/**
 * Scroll Away the item based on a lazy list, like a LazyColumn. Does not include fading or scaling.
 *
 * The logic assumes the first item is large enough to fully fade away the item, if this is not the
 * case then a custom implementation should be used.
 */
internal fun Modifier.fadeAwayLazyList(scrollStateFn: () -> LazyListState): Modifier = composed {
    val scrollState = scrollStateFn()
    if (scrollState.firstVisibleItemIndex == 0) {
        val y = scrollState.firstVisibleItemScrollOffset / LocalDensity.current.density

        fadeEffect(y, fade = false)
    } else {
        alpha(0.0f)
    }
}

/**
 * Scroll Away the item based on a ScalingLazyColumn. The item is scaled
 * and faded roughly in line with the default scaling params of ScalingLazyColumn,
 * if this is not the case, a custom implementation should be used.
 *
 * The logic assumes the first item is large enough to fully fade away the item, if this is not the
 * case then a custom implementation should be used.
 *
 * @param initialIndex The initial index must match that provided to [ScalingLazyListState].
 * @param initialOffset The initial offset must match that provided to [ScalingLazyListState].
 */
internal fun Modifier.fadeAwayScalingLazyList(
    initialIndex: Int = 1,
    initialOffset: Int = 0,
    scrollStateFn: () -> ScalingLazyListState,
): Modifier =
    composed {
        val scrollState = remember { scrollStateFn() }

        if (scrollState.centerItemIndex == initialIndex && scrollState.centerItemScrollOffset > initialOffset) {
            val y = scrollState.centerItemScrollOffset / LocalDensity.current.density

            fadeEffect(y, fade = true)
        } else if (scrollState.centerItemIndex > initialIndex) {
            alpha(0.0f)
        } else {
            this
        }
    }

private fun Modifier.fadeEffect(y: Float, fade: Boolean) = composed {
    if (fade) {
        val fadePercent: Float = (y / maxFadeOutScroll).coerceIn(0f, 1f)
        val fadeOut: Float = lerp(minFadeOut, maxFadeOut, fadePercent)

        val height = LocalConfiguration.current.screenHeightDp
        val translationY = (-y).coerceAtMost(0f) - ((height - height * fadeOut) / 2)

        this
            .offset(y = translationY.dp)
            .scale(fadeOut)
            .alpha(fadeOut)
    } else {
        this
            .offset(y = -y.dp)
    }
}

internal fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return (1 - fraction) * start + fraction * stop
}

internal const val maxFadeOutScroll = 40f
internal const val minFadeOut = 1f
internal const val maxFadeOut = 0.5f
