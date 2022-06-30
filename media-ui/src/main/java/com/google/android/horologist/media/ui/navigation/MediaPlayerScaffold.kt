/*
 * Copyright 2021 The Android Open Source Project
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

package com.google.android.horologist.media.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.TimeText
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.android.horologist.audio.ui.VolumeScreen
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.compose.layout.StateUtils.rememberStateWithLifecycle
import com.google.android.horologist.compose.navscaffold.NavScaffoldViewModel
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.scalingLazyColumnComposable
import com.google.android.horologist.compose.navscaffold.wearNavComposable
import com.google.android.horologist.compose.snackbar.DialogSnackbarHost
import com.google.android.horologist.media.ui.screens.PlayerLibraryPagerScreen
import com.google.android.horologist.media.ui.snackbar.SnackbarViewModel

/**
 * A UI scaffold for a Media Player with a subset of the following screens.
 * Structure is a ViewPager with [playerScreen] and [libraryScreen],
 * with navigation to other screens.
 *
 * @param modifier The modifier to be applied to the component
 * @param snackbarViewModel Stateful view model for snackbar
 * @param volumeViewModel Stateful view model for volume screens
 * @param playerScreen the first screen with player controls.
 * @param libraryScreen the long scrolling library top screen.
 * @param categoryEntityScreen screen to show details about a particular category.
 * @param mediaEntityScreen screen to show details about a particular media item.
 * @param playlistsScreen screen to show user playlists.
 * @param deepLinkPrefix the app specific prefix for external deeplinks
 * @param navController the media focused navigation controller.
 * @param additionalNavRoutes additional nav routes exposed for extra screens.
 */
@Composable
public fun MediaPlayerScaffold(
    modifier: Modifier = Modifier,
    snackbarViewModel: SnackbarViewModel = viewModel(factory = SnackbarViewModel.Factory),
    volumeViewModel: VolumeViewModel = viewModel(factory = VolumeViewModel.Factory),
    playerScreen: @Composable (FocusRequester) -> Unit,
    libraryScreen: @Composable (FocusRequester, ScalingLazyListState) -> Unit,
    categoryEntityScreen: @Composable (FocusRequester, ScalingLazyListState) -> Unit,
    mediaEntityScreen: @Composable (FocusRequester, ScalingLazyListState) -> Unit,
    playlistsScreen: @Composable (FocusRequester, ScalingLazyListState) -> Unit,
    settingsScreen: @Composable (FocusRequester, ScalingLazyListState) -> Unit,
    volumeScreen: @Composable (FocusRequester) -> Unit = { focusRequester ->
        VolumeScreen(
            focusRequester = focusRequester
        )
    },
    timeText: @Composable (Modifier) -> Unit = {
        TimeText(modifier = it)
    },
    pagerState: PagerState = rememberPagerState(initialPage = 0),
    deepLinkPrefix: String,
    navController: NavHostController,
    additionalNavRoutes: NavGraphBuilder.() -> Unit = {},
) {
    WearNavScaffold(
        modifier = modifier,
        startDestination = NavigationScreens.Player.route,
        timeText = timeText,
        navController = navController,
        snackbar = {
            DialogSnackbarHost(
                modifier = Modifier.fillMaxSize(),
                hostState = snackbarViewModel.snackbarHostState
            )
        }
    ) {
        wearNavComposable(
            route = NavigationScreens.Player.route,
            arguments = listOf(
                navArgument("page") {
                    type = NavType.IntType
                }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "$deepLinkPrefix/player?page={page}"
                }
            )
        ) { backStack, viewModel ->
            viewModel.timeTextMode = NavScaffoldViewModel.TimeTextMode.Off
            viewModel.positionIndicatorMode = NavScaffoldViewModel.PositionIndicatorMode.Off

            val volumeState by rememberStateWithLifecycle(flow = volumeViewModel.volumeState)

            PlayerLibraryPagerScreen(
                pagerState = pagerState,
                volumeScrollableState = volumeViewModel.volumeScrollableState,
                volumeState = { volumeState },
                timeText = timeText,
                playerScreen = {
                    playerScreen(it)
                },
                libraryScreen = { focusRequester, listState ->
                    libraryScreen(focusRequester, listState)
                },
                backStack = backStack
            )
        }

        scalingLazyColumnComposable(
            route = NavigationScreens.Collections.route,
            scrollStateBuilder = { ScalingLazyListState() }
        ) {
            playlistsScreen(it.viewModel.focusRequester, it.scrollableState)
        }

        scalingLazyColumnComposable(
            route = NavigationScreens.Settings.route,
            scrollStateBuilder = { ScalingLazyListState() }
        ) {
            settingsScreen(it.viewModel.focusRequester, it.scrollableState)
        }

        wearNavComposable(NavigationScreens.Volume.route) { _, viewModel ->
            viewModel.timeTextMode = NavScaffoldViewModel.TimeTextMode.Off

            volumeScreen(viewModel.focusRequester)
        }

        scalingLazyColumnComposable(
            route = NavigationScreens.MediaItem.route + "?id={id}&category={category}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                },
                navArgument("category") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "$deepLinkPrefix/mediaItem?id={id}&category={category}"
                }
            ),
            scrollStateBuilder = { ScalingLazyListState() }
        ) {
            mediaEntityScreen(it.viewModel.focusRequester, it.scrollableState)
        }

        scalingLazyColumnComposable(
            route = NavigationScreens.Collection.route + "?category={category}",
            arguments = listOf(
                navArgument("category") {
                    type = NavType.StringType
                }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "$deepLinkPrefix/category?category={category}"
                }
            ),
            scrollStateBuilder = { ScalingLazyListState() }
        ) {
            categoryEntityScreen(it.viewModel.focusRequester, it.scrollableState)
        }

        additionalNavRoutes()
    }
}
