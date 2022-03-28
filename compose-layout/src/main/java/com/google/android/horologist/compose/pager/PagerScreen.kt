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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.PageIndicatorState
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerScope
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

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
        HorizontalPager(modifier = modifier, count = count, state = state) {
            val scope = PagerScreenScopeImpl(this)

            DisposableEffect(Unit) {
                coroutineScope.launch {
                    scope.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
                    if (state.currentPage == it) {
                        scope.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
                    }
                }
                scopes[it] = scope
                onDispose {
                    coroutineScope.launch {
                        scope.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                    }
                    scopes.remove(it)
                }
            }

            CompositionLocalProvider(LocalLifecycleOwner.provides(scope)) {
                content(it)
            }
        }

        val pagerScreenState = remember { PageScreenIndicatorState(state) }
        if (pagerScreenState.pageCount > 0) {
            HorizontalPageIndicator(pageIndicatorState = pagerScreenState)
        }
    }

    LaunchedEffect(Unit) {
        var last: Int? = null
        snapshotFlow { state.currentPage }.collect {
            if (last != null) {
                scopes[last]?.lifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            }
            if (scopes.containsKey(it)) {
                scopes[it]!!.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            }
            last = it
        }
    }
}

public interface PagerScreenScope : LifecycleOwner, PagerScope

public class PagerScreenScopeImpl(private val scope: PagerScope) : PagerScreenScope {
    internal var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    override val currentPage: Int
        get() = scope.currentPage

    override val currentPageOffset: Float
        get() = scope.currentPageOffset
}

public class PageScreenIndicatorState(private val state: PagerState) : PageIndicatorState {
    override val pageCount: Int
        get() = state.pageCount

    override val pageOffset: Float
        get() = state.currentPageOffset

    override val selectedPage: Int
        get() = state.currentPage
}
