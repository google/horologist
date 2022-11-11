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

@file:OptIn(ExperimentalHorologistAudioUiApi::class, ExperimentalPagerApi::class)

package com.google.android.horologist.media.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.navigation.SwipeDismissableNavHostState
import androidx.wear.compose.navigation.rememberSwipeDismissableNavHostState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.android.horologist.audio.ui.ExperimentalHorologistAudioUiApi
import com.google.android.horologist.audio.ui.VolumeScreen
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.compose.navscaffold.NavScaffoldViewModel
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.scalingLazyColumnComposable
import com.google.android.horologist.compose.navscaffold.wearNavComposable
import com.google.android.horologist.compose.snackbar.DialogSnackbarHost
import com.google.android.horologist.media.ui.screens.playerlibrarypager.PlayerLibraryPagerScreen
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
 * @param mediaEntityScreen screen to show details about a particular media.
 * @param playlistsScreen screen to show user playlists.
 * @param deepLinkPrefix the app specific prefix for external deeplinks
 * @param navController the media focused navigation controller.
 * @param additionalNavRoutes additional nav routes exposed for extra screens.
 * @param pagerState the [PagerState] controlling the Player / Browse screen position.
 * @param navHostState the [SwipeDismissableNavHostState] including swipe to dismiss state.
 */
@Composable
public fun MediaPlayerScaffold(
    snackbarViewModel: SnackbarViewModel,
    volumeViewModel: VolumeViewModel,
    playerScreen: @Composable () -> Unit,
    libraryScreen: @Composable (ScalingLazyListState) -> Unit,
    categoryEntityScreen: @Composable (id: String, name: String, ScalingLazyListState) -> Unit,
    mediaEntityScreen: @Composable (ScalingLazyListState) -> Unit,
    playlistsScreen: @Composable (ScalingLazyListState) -> Unit,
    settingsScreen: @Composable (ScalingLazyListState) -> Unit,
    deepLinkPrefix: String,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    volumeScreen: @Composable () -> Unit = {
        VolumeScreen()
    },
    timeText: @Composable (Modifier) -> Unit = {
        TimeText(modifier = it)
    },
    pagerState: PagerState = rememberPagerState(initialPage = 0),
    navHostState: SwipeDismissableNavHostState = rememberSwipeDismissableNavHostState(),
    additionalNavRoutes: NavGraphBuilder.() -> Unit = {}
) {
    WearNavScaffold(
        startDestination = NavigationScreens.Player.navRoute,
        navController = navController,
        modifier = modifier,
        snackbar = {
            DialogSnackbarHost(
                modifier = Modifier.fillMaxSize(),
                hostState = snackbarViewModel.snackbarHostState
            )
        },
        timeText = timeText,
        state = navHostState
    ) {
        wearNavComposable(
            route = NavigationScreens.Player.navRoute,
            arguments = NavigationScreens.Player.arguments,
            deepLinks = NavigationScreens.Player.deepLinks(deepLinkPrefix)
        ) { backStack, viewModel ->
            viewModel.timeTextMode = NavScaffoldViewModel.TimeTextMode.Off
            viewModel.positionIndicatorMode = NavScaffoldViewModel.PositionIndicatorMode.Off

            val volumeState by volumeViewModel.volumeState.collectAsStateWithLifecycle()

            PlayerLibraryPagerScreen(
                pagerState = pagerState,
                onVolumeChangeByScroll = volumeViewModel::onVolumeChangeByScroll,
                volumeState = { volumeState },
                timeText = timeText,
                playerScreen = {
                    playerScreen()
                },
                libraryScreen = { listState ->
                    libraryScreen(listState)
                },
                backStack = backStack
            )
        }

        scalingLazyColumnComposable(
            route = NavigationScreens.Collections.navRoute,
            arguments = NavigationScreens.Collections.arguments,
            deepLinks = NavigationScreens.Collections.deepLinks(deepLinkPrefix),
            scrollStateBuilder = { ScalingLazyListState() }
        ) {
            playlistsScreen(it.scrollableState)
        }

        scalingLazyColumnComposable(
            route = NavigationScreens.Settings.navRoute,
            arguments = NavigationScreens.Settings.arguments,
            deepLinks = NavigationScreens.Settings.deepLinks(deepLinkPrefix),
            scrollStateBuilder = { ScalingLazyListState() }
        ) {
            settingsScreen(it.scrollableState)
        }

        wearNavComposable(
            route = NavigationScreens.Volume.navRoute,
            arguments = NavigationScreens.Volume.arguments,
            deepLinks = NavigationScreens.Volume.deepLinks(deepLinkPrefix)
        ) { _, viewModel ->
            viewModel.timeTextMode = NavScaffoldViewModel.TimeTextMode.Off

            volumeScreen()
        }

        scalingLazyColumnComposable(
            route = NavigationScreens.MediaItem.navRoute,
            arguments = NavigationScreens.MediaItem.arguments,
            deepLinks = NavigationScreens.MediaItem.deepLinks(deepLinkPrefix),
            scrollStateBuilder = { ScalingLazyListState() }
        ) {
            mediaEntityScreen(it.scrollableState)
        }

        scalingLazyColumnComposable(
            route = NavigationScreens.Collection.navRoute,
            arguments = NavigationScreens.Collection.arguments,
            deepLinks = NavigationScreens.Collection.deepLinks(deepLinkPrefix),
            scrollStateBuilder = { ScalingLazyListState() }
        ) { scaffoldContext ->
            val arguments = scaffoldContext.backStackEntry.arguments
            val id = arguments?.getString(NavigationScreens.Collection.id)
            val name = arguments?.getString(NavigationScreens.Collection.name)
            checkNotNull(id)
            checkNotNull(name)

            categoryEntityScreen(
                id,
                name,
                scaffoldContext.scrollableState
            )
        }

        additionalNavRoutes()
    }
}
