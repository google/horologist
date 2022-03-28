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

package com.google.android.horologist.compose.pager

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.PageIndicatorState
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerScope
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

/**
 * A Wear Material Compliant Pager screen.
 *
 * Combines the Accompanist Pager, with the Wear Compose HorizontalPageIndicator.
 * Also uses lifecycle, which allows attaching logic such requesting focus
 * to page events.
 */
@Composable
public fun PagerScreen(
    count: Int,
    modifier: Modifier = Modifier,
    state: PagerState = rememberPagerState(),
    content: @Composable (PagerScope.(Int) -> Unit)
) {
    val coroutineScope = LocalLifecycleOwner.current.lifecycleScope

    val scopes = remember(Unit) {
        mutableMapOf<Int, PagerScreenScopeImpl>()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        HorizontalPager(modifier = modifier, count = count, state = state) { page ->
            val scope =
                remember { scopes.getOrPut(page) { PagerScreenScopeImpl(this@HorizontalPager) } }

            /**
             * When page is initially composed or released send lifecycle events,
             * these logically happen around the individual page resume/pause events.
             */
            DisposableEffect(Unit) {
                coroutineScope.launch {
                    scope.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
                    if (state.currentPage == page) {
                        scope.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
                    }
                }
                scopes[page] = scope
                onDispose {
                    coroutineScope.launch {
                        scope.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                    }
                    scopes.remove(page)
                }
            }

            /**
             * Wire up lifecycle owner so that subscriptions on dormant pages are not running.
             */
            CompositionLocalProvider(LocalLifecycleOwner.provides(scope)) {
                content(page)
            }
        }

        val pagerScreenState = remember { PageScreenIndicatorState(state) }
        if (pagerScreenState.pageCount > 0) {
            HorizontalPageIndicator(pageIndicatorState = pagerScreenState)
        }
    }

    LaunchedEffect(Unit) {
        var last: Int? = null
        // When currentPage changes, send the appropriate lifecycle events to
        // resume the new target page, and pause the previous.
        snapshotFlow { state.currentPage }.collect { page ->
            if (last != null) {
                scopes[last]?.lifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            }
            if (scopes.containsKey(page)) {
                scopes[page]!!.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            }
            last = page
        }
    }
}

/**
 * The scopes for each page, combining Accompanist PagerScope and a custom
 * lifecycle.
 */
internal class PagerScreenScopeImpl(private val scope: PagerScope) : LifecycleOwner, PagerScope {
    var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    override val currentPage: Int
        get() = scope.currentPage

    override val currentPageOffset: Float
        get() = scope.currentPageOffset
}

/**
 * Bridge between Accompanist PagerState and the Wear Compose
 * PageIndicatorState.
 *
 * n.b. Currently fails for 0 pageCount, so enclose the HorizontalPageIndicator
 * in an if statement.
 */
public class PageScreenIndicatorState(private val state: PagerState) : PageIndicatorState {
    override val pageCount: Int
        get() = state.pageCount

    override val pageOffset: Float
        get() = state.currentPageOffset

    override val selectedPage: Int
        get() = state.currentPage
}

/**
 * Utility to Focus the page when it is resumed.
 */
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
