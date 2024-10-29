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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.navigation.SwipeDismissableNavHostState
import androidx.wear.compose.navigation.rememberSwipeDismissableNavHostState
import com.google.android.horologist.audio.ui.VolumeScreen
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ResponsiveTimeText
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.nav.SwipeDismissableNavHost
import com.google.android.horologist.compose.nav.composable
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
 * @param navHostState the [SwipeDismissableNavHostState] including swipe to dismiss state.
 * @param settingsScreen the settings screen.
 * @param timeText the TimeText() composable.
 * @param volumeScreen the volume screen.
 */
@Composable
public fun MediaPlayerScaffold(
    snackbarViewModel: SnackbarViewModel,
    volumeViewModel: VolumeViewModel,
    playerScreen: @Composable () -> Unit,
    libraryScreen: @Composable () -> Unit,
    categoryEntityScreen: @Composable (NavigationScreen.Collection) -> Unit,
    mediaEntityScreen: @Composable () -> Unit,
    playlistsScreen: @Composable () -> Unit,
    settingsScreen: @Composable () -> Unit,
    deepLinkPrefix: String,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    volumeScreen: @Composable () -> Unit = { VolumeScreen(volumeViewModel = volumeViewModel) },
    timeText: @Composable () -> Unit = { ResponsiveTimeText() },
    navHostState: SwipeDismissableNavHostState = rememberSwipeDismissableNavHostState(),
    additionalNavRoutes: NavGraphBuilder.() -> Unit = {},
) {
    AppScaffold(
        timeText = { timeText() },
    ) {
        SwipeDismissableNavHost(
            startDestination = NavigationScreen.Player(page = 0),
            navController = navController,
            modifier = modifier.background(Color.Transparent),
            state = navHostState,
        ) {
            composable<NavigationScreen.Player>(
                deepLinks = NavigationScreen.Player.deepLinks(deepLinkPrefix),
            ) {
                val volumeState by volumeViewModel.volumeUiState.collectAsStateWithLifecycle()
                val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })

                PlayerLibraryPagerScreen(
                    pagerState = pagerState,
                    volumeUiState = { volumeState },
                    displayVolumeIndicatorEvents = volumeViewModel.displayIndicatorEvents,
                    playerScreen = {
                        playerScreen()
                    },
                    libraryScreen = {
                        libraryScreen()
                    },
                    backStack = it,
                )
            }

            composable<NavigationScreen.Collections>(
                deepLinks = NavigationScreen.Collections.deepLinks(deepLinkPrefix),
            ) {
                playlistsScreen()
            }

            composable<NavigationScreen.Settings>(
                deepLinks = NavigationScreen.Settings.deepLinks(deepLinkPrefix),
            ) {
                settingsScreen()
            }

            composable<NavigationScreen.Volume>(
                deepLinks = NavigationScreen.Volume.deepLinks(deepLinkPrefix),
            ) {
                ScreenScaffold(timeText = {}) {
                    volumeScreen()
                }
            }

            composable<NavigationScreen.MediaItem>(
                deepLinks = NavigationScreen.MediaItem.deepLinks(deepLinkPrefix),
            ) {
                mediaEntityScreen()
            }

            composable<NavigationScreen.Collection>(
                deepLinks = NavigationScreen.Collection.deepLinks(deepLinkPrefix),
            ) {
                val arguments = it.toRoute<NavigationScreen.Collection>()

                categoryEntityScreen(arguments)
            }

            additionalNavRoutes()
        }

        DialogSnackbarHost(
            modifier = Modifier.fillMaxSize(),
            hostState = snackbarViewModel.snackbarHostState,
        )
    }
}
