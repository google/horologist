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

package com.google.android.horologist.mediasample.ui.app

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.rememberSwipeDismissableNavHostState
import com.google.android.horologist.auth.ui.googlesignin.signin.GoogleSignInScreen
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberColumnState
import com.google.android.horologist.compose.nav.composable
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToLibrary
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToPlayer
import com.google.android.horologist.media.ui.navigation.MediaPlayerScaffold
import com.google.android.horologist.media.ui.navigation.NavigationScreen
import com.google.android.horologist.mediasample.BuildConfig
import com.google.android.horologist.mediasample.ui.auth.prompt.GoogleSignInPromptScreen
import com.google.android.horologist.mediasample.ui.auth.signin.UampGoogleSignInViewModel
import com.google.android.horologist.mediasample.ui.auth.signout.GoogleSignOutScreen
import com.google.android.horologist.mediasample.ui.browse.UampBrowseScreen
import com.google.android.horologist.mediasample.ui.browse.UampStreamingBrowseScreen
import com.google.android.horologist.mediasample.ui.debug.AudioDebugScreen
import com.google.android.horologist.mediasample.ui.debug.MediaInfoTimeText
import com.google.android.horologist.mediasample.ui.debug.MediaInfoTimeTextViewModel
import com.google.android.horologist.mediasample.ui.debug.SamplesScreen
import com.google.android.horologist.mediasample.ui.entity.UampEntityScreen
import com.google.android.horologist.mediasample.ui.entity.UampEntityScreenViewModel
import com.google.android.horologist.mediasample.ui.entity.UampStreamingPlaylistScreen
import com.google.android.horologist.mediasample.ui.entity.UampStreamingPlaylistScreenViewModel
import com.google.android.horologist.mediasample.ui.navigation.UampNavigationScreen.AudioDebug
import com.google.android.horologist.mediasample.ui.navigation.UampNavigationScreen.DeveloperOptions
import com.google.android.horologist.mediasample.ui.navigation.UampNavigationScreen.GoogleSignInPromptScreen
import com.google.android.horologist.mediasample.ui.navigation.UampNavigationScreen.GoogleSignInScreen
import com.google.android.horologist.mediasample.ui.navigation.UampNavigationScreen.GoogleSignOutScreen
import com.google.android.horologist.mediasample.ui.navigation.UampNavigationScreen.Samples
import com.google.android.horologist.mediasample.ui.player.UampMediaPlayerScreen
import com.google.android.horologist.mediasample.ui.playlists.UampPlaylistsScreen
import com.google.android.horologist.mediasample.ui.playlists.UampPlaylistsScreenViewModel
import com.google.android.horologist.mediasample.ui.settings.DeveloperOptionsScreen
import com.google.android.horologist.mediasample.ui.settings.UampSettingsScreen
import kotlinx.coroutines.delay

@Composable
fun UampWearApp(
    navController: NavHostController,
    intent: Intent,
) {
    val appViewModel: MediaPlayerAppViewModel = hiltViewModel()
    val volumeViewModel: VolumeViewModel = hiltViewModel()
    val mediaInfoTimeTextViewModel: MediaInfoTimeTextViewModel = hiltViewModel()

    val navHostState = rememberSwipeDismissableNavHostState()

    val appState by appViewModel.appState.collectAsStateWithLifecycle()

    UampTheme {
        MediaPlayerScaffold(
            playerScreen = {
                UampMediaPlayerScreen(
                    modifier = Modifier.fillMaxSize(),
                    mediaPlayerScreenViewModel = hiltViewModel(),
                    volumeViewModel = volumeViewModel,
                    onVolumeClick = {
                        navController.navigate(NavigationScreen.Volume)
                    },
                )
            },
            libraryScreen = {
                if (appState.streamingMode == true) {
                    UampStreamingBrowseScreen(
                        onPlaylistsClick = {
                            navController.navigate(NavigationScreen.Collections)
                        },
                        onSettingsClick = {
                            navController.navigate(NavigationScreen.Settings)
                        },
                    )
                } else {
                    UampBrowseScreen(
                        uampBrowseScreenViewModel = hiltViewModel(),
                        onDownloadItemClick = {
                            navController.navigate(
                                NavigationScreen.Collection(
                                    it.playlistUiModel.id,
                                    it.playlistUiModel.title,
                                ),
                            )
                        },
                        onPlaylistsClick = {
                            navController.navigate(NavigationScreen.Collections)
                        },
                        onSettingsClick = {
                            navController.navigate(NavigationScreen.Settings)
                        },
                    )
                }
            },
            categoryEntityScreen = { category ->
                if (appState.streamingMode == true) {
                    val viewModel: UampStreamingPlaylistScreenViewModel = hiltViewModel()

                    UampStreamingPlaylistScreen(
                        playlistName = category.name.orEmpty(),
                        viewModel = viewModel,
                        onDownloadItemClick = {
                            navController.navigateToPlayer()
                        },
                        onShuffleClick = { navController.navigateToPlayer() },
                        onPlayClick = { navController.navigateToPlayer() },
                    )
                } else {
                    val uampEntityScreenViewModel: UampEntityScreenViewModel = hiltViewModel()

                    UampEntityScreen(
                        playlistName = category.name.orEmpty(),
                        uampEntityScreenViewModel = uampEntityScreenViewModel,
                        onDownloadItemClick = {
                            navController.navigateToPlayer()
                        },
                        onShuffleClick = { navController.navigateToPlayer() },
                        onPlayClick = { navController.navigateToPlayer() },
                        onErrorDialogCancelClick = { navController.popBackStack() },
                    )
                }
            },
            mediaEntityScreen = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Media XXX")
                }
            },
            playlistsScreen = {
                val uampPlaylistsScreenViewModel: UampPlaylistsScreenViewModel =
                    hiltViewModel()

                UampPlaylistsScreen(
                    uampPlaylistsScreenViewModel = uampPlaylistsScreenViewModel,
                    onPlaylistItemClick = { playlistUiModel ->
                        navController.navigate(
                            NavigationScreen.Collection(
                                playlistUiModel.id,
                                playlistUiModel.title,
                            ),
                        )
                    },
                    onErrorDialogCancelClick = { navController.popBackStack() },
                )
            },
            settingsScreen = {
                UampSettingsScreen(
                    viewModel = hiltViewModel(),
                    navController = navController,
                )
            },
            navHostState = navHostState,
            snackbarViewModel = hiltViewModel<SnackbarViewModel>(),
            volumeViewModel = volumeViewModel,
            timeText = {
                MediaInfoTimeText(
                    mediaInfoTimeTextViewModel = mediaInfoTimeTextViewModel,
                )
            },
            deepLinkPrefix = appViewModel.deepLinkPrefix,
            navController = navController,
            additionalNavRoutes = {
                composable<AudioDebug> {
                    val columnState = rememberColumnState()

                    ScreenScaffold(scrollState = columnState) {
                        AudioDebugScreen(
                            audioDebugScreenViewModel = hiltViewModel(),
                        )
                    }
                }

                composable<Samples> {
                    val columnState = rememberColumnState()

                    ScreenScaffold(scrollState = columnState) {
                        SamplesScreen(
                            samplesScreenViewModel = hiltViewModel(),
                            navController = navController,
                        )
                    }
                }

                composable<DeveloperOptions> {
                    val columnState = rememberColumnState()

                    ScreenScaffold(scrollState = columnState) {
                        DeveloperOptionsScreen(
                            developerOptionsScreenViewModel = hiltViewModel(),
                            navController = navController,
                        )
                    }
                }

                composable<GoogleSignInPromptScreen> {
                    GoogleSignInPromptScreen(
                        navController = navController,
                        viewModel = hiltViewModel(),
                    )
                }

                composable<GoogleSignInScreen> {
                    GoogleSignInScreen(
                        onAuthCancelled = { navController.popBackStack() },
                        onAuthSucceed = { navController.navigateToLibrary() },
                        viewModel = hiltViewModel<UampGoogleSignInViewModel>(),
                    )
                }

                composable<GoogleSignOutScreen> {
                    GoogleSignOutScreen(
                        navController = navController,
                        viewModel = hiltViewModel(),
                    )
                }
            },
        )
    }

    if (BuildConfig.BENCHMARK) {
        if (intent.hasExtra("launchAndPlay")) {
            val launchAndPlay = intent.getBooleanExtra("launchAndPlay", false)
            println("Found launchAndPlay = $launchAndPlay")
            intent.removeExtra("launchAndPlay")
            LaunchedEffect(Unit) {
                if (launchAndPlay) {
                    appViewModel.startBenchmarkPlayback()
                } else {
                    appViewModel.stopBenchmarkPlayback()
                }
            }
        }
    } else {
        LaunchedEffect(Unit) {
            startupNavigation(intent, appViewModel, navController)
        }
    }
}

private suspend fun startupNavigation(
    intent: Intent,
    appViewModel: MediaPlayerAppViewModel,
    navController: NavHostController,
) {
    val collectionId = intent.getAndRemoveKey(MediaActivity.CollectionKey)
    val mediaId = intent.getAndRemoveKey(MediaActivity.MediaIdKey)
    val position = intent.getAndRemoveKey(MediaActivity.PositionKey)

    if (collectionId != null) {
        if (position != null) {
            appViewModel.playItems(mediaId, collectionId, position.toLong())
        } else {
            appViewModel.playItems(mediaId, collectionId, 0)
        }
    } else {
        appViewModel.startupSetup(navigateToLibrary = {
            navController.navigateToLibrary()
        })
    }

    if (appViewModel.shouldShowLoginPrompt()) {
        // Allow screen to settle so it feels like a distinct step
        delay(200)
        navController.navigate(GoogleSignInPromptScreen)
    }
}

private fun Intent.getAndRemoveKey(key: String): String? =
    getStringExtra(key).also {
        removeExtra(key)
    }
