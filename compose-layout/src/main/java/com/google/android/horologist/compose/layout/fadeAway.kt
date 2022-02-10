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
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.wear.compose.material.ScalingLazyListState

public fun Modifier.fadeAway(scrollState: ScrollState): Modifier = composed {
    val y = scrollState.value / LocalDensity.current.density

    fadeEffect(y, fade = false)
}

public fun Modifier.fadeAway(scrollState: LazyListState): Modifier = composed {
    val y = scrollState.firstVisibleItemScrollOffset / LocalDensity.current.density

    if (scrollState.firstVisibleItemIndex == 0) {
        fadeEffect(y, fade = false)
    } else {
        alpha(0.0f)
    }
}

public fun Modifier.fadeAway(scrollState: ScalingLazyListState): Modifier = composed {
    val y = scrollState.centerItemScrollOffset / LocalDensity.current.density

    if (scrollState.centerItemIndex == 0) {
        fadeEffect(y, fade = true)
    } else {
        alpha(0.0f)
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

internal const val maxFadeOutScroll = 40f
internal const val minFadeOut = 1f
internal const val maxFadeOut = 0.5f
