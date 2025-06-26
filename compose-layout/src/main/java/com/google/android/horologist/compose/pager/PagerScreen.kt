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

@file:OptIn(ExperimentalFoundationApi::class, ExperimentalWearFoundationApi::class)

package com.google.android.horologist.compose.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.hierarchicalFocusGroup
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.PagerState
import androidx.wear.compose.material.PageIndicatorState
import com.google.android.horologist.compose.layout.PagerScaffold

/**
 * A Wear Material Compliant Pager screen.
 *
 * Combines the Compose Foundation Pager, with the Wear Compose HorizontalPageIndicator.
 *
 * The current page gets the Hierarchical Focus.
 */
@Composable
public fun PagerScreen(
    state: PagerState,
    modifier: Modifier = Modifier,
    beyondViewportPageCount: Int = 0,
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    key: ((index: Int) -> Any)? = null,
    content: @Composable (Int) -> Unit,
) {
    PagerScaffold(
        modifier = modifier.fillMaxSize(),
        pagerState = state,
    ) {
        HorizontalPager(
            state = state,
            beyondViewportPageCount = beyondViewportPageCount,
            userScrollEnabled = userScrollEnabled,
            reverseLayout = reverseLayout,
            key = key,
        ) { page ->
            ClippedBox(state, modifier = Modifier.hierarchicalFocusGroup(page == state.currentPage)) {
                content(page)
            }
        }
    }
}

@Composable
internal fun ClippedBox(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val shape = rememberClipWhenScrolling(pagerState)
    Box(
        modifier = modifier
            .fillMaxSize()
            .optionalClip(shape),
    ) {
        content()
    }
}

@Composable
private fun rememberClipWhenScrolling(state: PagerState): State<RoundedCornerShape?> {
    val shape = if (LocalConfiguration.current.isScreenRound) CircleShape else null
    return remember(state) {
        derivedStateOf {
            if (shape != null && state.currentPageOffsetFraction != 0f) {
                shape
            } else {
                null
            }
        }
    }
}

private fun Modifier.optionalClip(shapeState: State<RoundedCornerShape?>): Modifier {
    val shape = shapeState.value

    return if (shape != null) {
        clip(shape)
    } else {
        this
    }
}

/**
 * Bridge between Foundation PagerState and the Wear Compose PageIndicatorState.
 */
public class PageScreenIndicatorState(
    private val state: PagerState,
) : PageIndicatorState {
    override val pageCount: Int
        get() = state.pageCount

    override val pageOffset: Float
        get() = state.currentPageOffsetFraction.takeIf { it.isFinite() } ?: 0f

    override val selectedPage: Int
        get() = state.currentPage
}
