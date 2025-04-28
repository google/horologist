/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.media.ui.material3.navigation

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.SwipeDismissableNavHostState
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavHostState
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.audio.ui.material3.VolumeScreen
import com.google.android.horologist.media.ui.material3.screens.playerlibrarypager.PlayerLibraryPagerScreen

/**
 * A UI scaffold for a Media Player with a subset of the following screens. Structure is a ViewPager
 * with [playerScreen] and [libraryScreen], with navigation to other screens.
 *
 * @param modifier The modifier to be applied to the component
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
    volumeViewModel: VolumeViewModel,
    playerScreen: @Composable () -> Unit,
    libraryScreen: @Composable () -> Unit,
    categoryEntityScreen: @Composable (id: String, name: String) -> Unit,
    mediaEntityScreen: @Composable () -> Unit,
    playlistsScreen: @Composable () -> Unit,
    settingsScreen: @Composable () -> Unit,
    deepLinkPrefix: String,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    volumeScreen: @Composable () -> Unit = { VolumeScreen(volumeViewModel = volumeViewModel) },
    timeText: @Composable () -> Unit = { TimeText() },
    navHostState: SwipeDismissableNavHostState = rememberSwipeDismissableNavHostState(),
    additionalNavRoutes: NavGraphBuilder.() -> Unit = {},
) {
    AppScaffold(timeText = timeText) {
        SwipeDismissableNavHost(
            startDestination = NavigationScreens.Player.navRoute,
            navController = navController,
            modifier = modifier.background(Color.Transparent),
            state = navHostState,
        ) {
            composable(
                route = NavigationScreens.Player.navRoute,
                arguments = NavigationScreens.Player.arguments,
                deepLinks = NavigationScreens.Player.deepLinks(deepLinkPrefix),
            ) {
                val volumeState by volumeViewModel.volumeUiState.collectAsStateWithLifecycle()
                val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })

                PlayerLibraryPagerScreen(
                    pagerState = pagerState,
                    volumeUiState = { volumeState },
                    displayVolumeIndicatorEvents = volumeViewModel.displayIndicatorEvents,
                    playerScreen = { playerScreen() },
                    libraryScreen = { libraryScreen() },
                    backStack = it,
                )
            }

            composable(
                route = NavigationScreens.Collections.navRoute,
                arguments = NavigationScreens.Collections.arguments,
                deepLinks = NavigationScreens.Collections.deepLinks(deepLinkPrefix),
            ) {
                playlistsScreen()
            }

            composable(
                route = NavigationScreens.Settings.navRoute,
                arguments = NavigationScreens.Settings.arguments,
                deepLinks = NavigationScreens.Settings.deepLinks(deepLinkPrefix),
            ) {
                settingsScreen()
            }

            composable(
                route = NavigationScreens.Volume.navRoute,
                arguments = NavigationScreens.Volume.arguments,
                deepLinks = NavigationScreens.Volume.deepLinks(deepLinkPrefix),
            ) {
                ScreenScaffold(timeText = {}) { volumeScreen() }
            }

            composable(
                route = NavigationScreens.MediaItem.navRoute,
                arguments = NavigationScreens.MediaItem.arguments,
                deepLinks = NavigationScreens.MediaItem.deepLinks(deepLinkPrefix),
            ) {
                mediaEntityScreen()
            }

            composable(
                route = NavigationScreens.Collection.navRoute,
                arguments = NavigationScreens.Collection.arguments,
                deepLinks = NavigationScreens.Collection.deepLinks(deepLinkPrefix),
            ) {
                val arguments = it.arguments
                val id = arguments?.getString(NavigationScreens.Collection.id)
                val name = arguments?.getString(NavigationScreens.Collection.name)
                checkNotNull(id)
                checkNotNull(name)

                categoryEntityScreen(id, name)
            }

            additionalNavRoutes()
        }
    }
}
