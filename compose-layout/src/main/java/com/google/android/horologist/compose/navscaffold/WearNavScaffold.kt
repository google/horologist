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

package com.google.android.horologist.compose.navscaffold

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.currentBackStackEntryAsState
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.horologist.compose.layout.fadeAway
import com.google.android.horologist.compose.layout.fadeAwayLazyList
import com.google.android.horologist.compose.layout.fadeAwayScalingLazyList

/**
 * A Navigation and Scroll aware [Scaffold].
 *
 * In addition to [NavGraphBuilder.composable], 3 additional extensions are supported
 * [scalingLazyColumnComposable], [scrollStateComposable] and
 * [lazyListComposable].
 *
 * These should be used to build the [ScrollableState] or [FocusRequester] as well as
 * configure the behaviour of [TimeText], [PositionIndicator] or [Vignette].
 */
@Composable
public fun WearNavScaffold(
    navController: NavHostController = rememberSwipeDismissableNavController(),
    startDestination: String,
    builder: NavGraphBuilder.() -> Unit
) {
    val currentBackStackEntry: NavBackStackEntry? by navController.currentBackStackEntryAsState()
    val viewModel: NavScaffoldViewModel? = currentBackStackEntry?.let { viewModel(it) }
    val scrollableState: ScrollableState? = viewModel?.scrollableState

    Scaffold(
        timeText = {
            val mode = viewModel?.timeTextMode

            when (mode) {
                NavScaffoldViewModel.TimeTextMode.FadeAway -> {
                    when (viewModel.scrollType) {
                        NavScaffoldViewModel.ScrollType.ScrollState ->
                            TimeText(
                                modifier = Modifier.fadeAway {
                                    viewModel.scrollableState as ScrollState
                                }
                            )
                        NavScaffoldViewModel.ScrollType.ScalingLazyColumn ->
                            TimeText(
                                modifier = Modifier.fadeAwayScalingLazyList {
                                    viewModel.scrollableState as ScalingLazyListState
                                }
                            )
                        NavScaffoldViewModel.ScrollType.LazyList ->
                            TimeText(
                                modifier = Modifier.fadeAwayLazyList {
                                    viewModel.scrollableState as LazyListState
                                }
                            )
                        else -> {}
                    }
                }
                NavScaffoldViewModel.TimeTextMode.On -> {
                    TimeText()
                }
                else -> {
                }
            }
        },
        positionIndicator = {
            val mode = viewModel?.positionIndicatorMode

            if (mode != NavScaffoldViewModel.PositionIndicatorMode.Off) {
                if (mode == NavScaffoldViewModel.PositionIndicatorMode.On ||
                    scrollableState?.isScrollInProgress == true
                ) {
                    when (viewModel.scrollType) {
                        NavScaffoldViewModel.ScrollType.ScrollState ->
                            PositionIndicator(
                                scrollState = viewModel.scrollableState as ScrollState
                            )
                        NavScaffoldViewModel.ScrollType.ScalingLazyColumn ->
                            PositionIndicator(
                                scalingLazyListState =
                                viewModel.scrollableState as ScalingLazyListState
                            )
                        NavScaffoldViewModel.ScrollType.LazyList ->
                            PositionIndicator(
                                lazyListState = viewModel.scrollableState as LazyListState
                            )
                        else -> {}
                    }
                }
            }
        },
        vignette = {
            val vignettePosition = viewModel?.vignettePosition
            if (vignettePosition is NavScaffoldViewModel.VignetteMode.On) {
                Vignette(vignettePosition = vignettePosition.position)
            }
        }
    ) {
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = startDestination,
        ) {
            builder()
        }
    }
}

/**
 * The context items provided to a navigation composable.
 *
 * The [viewModel] can be used to customise the scaffold behaviour.
 */
public data class ScaffoldContext<T : ScrollableState>(
    val backStackEntry: NavBackStackEntry,
    val scrollableState: T,
    val viewModel: NavScaffoldViewModel,
)

/**
 * Add a screen to the navigation graph featuring a ScalingLazyColumn.
 *
 * The scalingLazyListState must be taken from the [ScaffoldContext].
 */
public fun NavGraphBuilder.scalingLazyColumnComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    scrollStateBuilder: () -> ScalingLazyListState,
    content: @Composable (ScaffoldContext<ScalingLazyListState>) -> Unit
) {
    composable(route, arguments, deepLinks) {
        val viewModel: NavScaffoldViewModel = viewModel(it)

        val scrollState = viewModel.initialiseScalingLazyListState(scrollStateBuilder)

        content(ScaffoldContext(it, scrollState, viewModel))

        if (viewModel.focusRequested) {
            LaunchedEffect(Unit) {
                viewModel.focusRequester.requestFocus()
            }
        }
    }
}

/**
 * Add a screen to the navigation graph featuring a Scrollable item.
 *
 * The scrollState must be taken from the [ScaffoldContext].
 */
public fun NavGraphBuilder.scrollStateComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    scrollStateBuilder: () -> ScrollState,
    content: @Composable (ScaffoldContext<ScrollState>) -> Unit
) {
    composable(route, arguments, deepLinks) {
        val viewModel: NavScaffoldViewModel = viewModel(it)

        val scrollState = viewModel.initialiseScrollState(scrollStateBuilder)

        content(ScaffoldContext(it, scrollState, viewModel))

        if (viewModel.focusRequested) {
            LaunchedEffect(Unit) {
                viewModel.focusRequester.requestFocus()
            }
        }
    }
}

/**
 * Add a screen to the navigation graph featuring a Lazy list such as LazyColumn.
 *
 * The scrollState must be taken from the [ScaffoldContext].
 */
public fun NavGraphBuilder.lazyListComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    lazyListStateBuilder: () -> LazyListState,
    content: @Composable (ScaffoldContext<LazyListState>) -> Unit
) {
    composable(route, arguments, deepLinks) {
        val viewModel: NavScaffoldViewModel = viewModel(it)

        val scrollState = viewModel.initialiseLazyList(lazyListStateBuilder)

        content(ScaffoldContext(it, scrollState, viewModel))

        if (viewModel.focusRequested) {
            LaunchedEffect(Unit) {
                viewModel.focusRequester.requestFocus()
            }
        }
    }
}

/**
 * Add non scrolling screen to the navigation graph. The [NavBackStackEntry] and
 * [NavScaffoldViewModel] are passed into the [content] block so that
 * the Scaffold may be customised, such as disabling TimeText.
 */
public fun NavGraphBuilder.wearNavComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable (NavBackStackEntry, NavScaffoldViewModel) -> Unit
) {
    composable(route, arguments, deepLinks) {
        val viewModel: NavScaffoldViewModel = viewModel(it)

        content(it, viewModel)
    }
}
