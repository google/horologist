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

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.HierarchicalFocusCoordinator
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.PageIndicatorState
import com.google.android.horologist.annotations.ExperimentalHorologistApi

/**
 * A Wear Material Compliant Pager screen.
 *
 * Combines the Accompanist Pager, with the Wear Compose HorizontalPageIndicator.
 * Also uses lifecycle, which allows attaching logic such requesting focus
 * to page events.
 */
@Composable
public fun PagerScreen(
    modifier: Modifier = Modifier,
    state: PagerState,
    content: @Composable ((Int) -> Unit)
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        HorizontalPager(
            modifier = modifier,
            state = state,
            flingBehavior = HorizontalPagerDefaults.flingParams(state)
        ) { page ->
            ClippedBox(state) {
                HierarchicalFocusCoordinator(requiresFocus = { page == state.currentPage }) {
                    content(page)
                }
            }
        }

        val pagerScreenState = remember(state) { PageScreenIndicatorState(state) }
        HorizontalPageIndicator(
            modifier = Modifier.padding(6.dp),
            pageIndicatorState = pagerScreenState
        )
    }
}

@Composable
private fun ClippedBox(pagerState: PagerState, content: @Composable () -> Unit) {
    val shape = rememberClipWhenScrolling(pagerState)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .optionalClip(shape)
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
    private val state: PagerState
) : PageIndicatorState {
    override val pageCount: Int
        get() = state.pageCount

    override val pageOffset: Float
        get() = state.currentPageOffsetFraction.takeIf { it.isFinite() } ?: 0f

    override val selectedPage: Int
        get() = state.currentPage
}

/**
 * Utility to Focus the page when it is resumed.
 */
@Deprecated(
    message = "Use RequestFocusWhenActive",
    replaceWith = ReplaceWith(
        "RequestFocusWhenActive(focusRequester=focusRequester)",
        "androidx.wear.compose.foundation.RequestFocusWhenActive"
    )
)
@ExperimentalHorologistApi
@Composable
public fun FocusOnResume(focusRequester: FocusRequester) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(state = Lifecycle.State.RESUMED) {
            try {
                focusRequester.requestFocus()
            } catch (ise: IllegalStateException) {
                Log.w("pager", "Focus Requestor not installed", ise)
            }
        }
    }
}
