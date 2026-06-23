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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.navigation3.rememberSwipeDismissableSceneStrategy
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
 * @param backStack the media focused navigation backstack.
 * @param settingsScreen the settings screen.
 * @param timeText the TimeText() composable.
 * @param volumeScreen the volume screen.
 * @param additionalEntries additional nav entries exposed for extra screens.
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
    backStack: NavBackStack<MediaRoute>,
    modifier: Modifier = Modifier,
    volumeScreen: @Composable () -> Unit = { VolumeScreen(volumeViewModel = volumeViewModel) },
    timeText: @Composable () -> Unit = { TimeText() },
    additionalEntries: EntryProviderScope<MediaRoute>.() -> Unit = {},
) {
    AppScaffold {
        val entryProvider = entryProvider(
            fallback = { key ->
                NavEntry(key) {
                    when (key) {
                        is PlayerRoute -> {
                            val volumeState by volumeViewModel.volumeUiState.collectAsStateWithLifecycle()
                            val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })

                            PlayerLibraryPagerScreen(
                                pagerState = pagerState,
                                volumeUiState = { volumeState },
                                displayVolumeIndicatorEvents = volumeViewModel.displayIndicatorEvents,
                                playerScreen = { playerScreen() },
                                libraryScreen = { libraryScreen() },
                                page = key.page,
                                modifier = modifier,
                            )
                        }

                        is CollectionRoute -> {
                            categoryEntityScreen(key.id, key.name)
                        }

                        is MediaItemRoute -> {
                            mediaEntityScreen()
                        }

                        is CustomRoute -> {
                            val route = key.route
                            val request = DeepLinkRequest.fromUriString("app://" + route)
                            when {
                                route.startsWith("player") -> {
                                    val volumeState by volumeViewModel.volumeUiState.collectAsStateWithLifecycle()
                                    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
                                    val pageParam = NavigationScreens.Player.getPageParam(route)

                                    PlayerLibraryPagerScreen(
                                        pagerState = pagerState,
                                        volumeUiState = { volumeState },
                                        displayVolumeIndicatorEvents = volumeViewModel.displayIndicatorEvents,
                                        playerScreen = { playerScreen() },
                                        libraryScreen = { libraryScreen() },
                                        page = pageParam,
                                        modifier = modifier,
                                    )
                                }

                                route.startsWith("collection") -> {
                                    val id = request.getQueryParameter("id")
                                    val name = request.getQueryParameter("name")
                                    checkNotNull(id)
                                    checkNotNull(name)
                                    categoryEntityScreen(id, name)
                                }

                                route.startsWith("mediaItem") -> {
                                    mediaEntityScreen()
                                }

                                else -> {
                                    throw IllegalStateException("Unknown route: $route")
                                }
                            }
                        }

                        else -> {
                            throw IllegalStateException("Unknown route key: $key")
                        }
                    }
                }
            },
        ) {
            entry(CollectionsRoute) {
                playlistsScreen()
            }
            entry(SettingsRoute) {
                settingsScreen()
            }
            entry(VolumeRoute) {
                ScreenScaffold(timeText = {}) { volumeScreen() }
            }

            additionalEntries()
        }

        NavDisplay(
            backStack = backStack,
            sceneStrategies = listOf(rememberSwipeDismissableSceneStrategy()),
            entryProvider = entryProvider,
            modifier = modifier,
        )
    }
}

public class DeepLinkRequest private constructor(private val uri: android.net.Uri) {
    public fun getQueryParameter(key: String): String? = uri.getQueryParameter(key)

    public companion object {
        public fun fromUriString(uriString: String): DeepLinkRequest {
            return DeepLinkRequest(uriString.toUri())
        }
    }
}
