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
import androidx.compose.foundation.pager.rememberPagerState
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
import com.google.android.horologist.compose.navscaffold.composable
import com.google.android.horologist.compose.navscaffold.scrollable
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToCollection
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToCollections
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToLibrary
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToPlayer
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToSettings
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToVolume
import com.google.android.horologist.media.ui.navigation.MediaPlayerScaffold
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
import com.google.android.horologist.mediasample.ui.navigation.AudioDebug
import com.google.android.horologist.mediasample.ui.navigation.DeveloperOptions
import com.google.android.horologist.mediasample.ui.navigation.GoogleSignInPromptScreen
import com.google.android.horologist.mediasample.ui.navigation.GoogleSignInScreen
import com.google.android.horologist.mediasample.ui.navigation.GoogleSignOutScreen
import com.google.android.horologist.mediasample.ui.navigation.Samples
import com.google.android.horologist.mediasample.ui.navigation.navigateToGoogleSignInPrompt
import com.google.android.horologist.mediasample.ui.player.UampMediaPlayerScreen
import com.google.android.horologist.mediasample.ui.playlists.UampPlaylistsScreen
import com.google.android.horologist.mediasample.ui.playlists.UampPlaylistsScreenViewModel
import com.google.android.horologist.mediasample.ui.settings.DeveloperOptionsScreen
import com.google.android.horologist.mediasample.ui.settings.UampSettingsScreen
import kotlinx.coroutines.delay

@Composable
fun UampWearApp(
    navController: NavHostController,
    intent: Intent
) {
    val appViewModel: MediaPlayerAppViewModel = hiltViewModel()
    val volumeViewModel: VolumeViewModel = hiltViewModel()
    val mediaInfoTimeTextViewModel: MediaInfoTimeTextViewModel = hiltViewModel()

    val pagerState = rememberPagerState(initialPage = 0)
    val navHostState = rememberSwipeDismissableNavHostState()

    val appState by appViewModel.appState.collectAsStateWithLifecycle()

    val timeText: @Composable (Modifier) -> Unit = { modifier ->
        MediaInfoTimeText(
            modifier = modifier,
            mediaInfoTimeTextViewModel = mediaInfoTimeTextViewModel
        )
    }

    UampTheme {
        MediaPlayerScaffold(
            playerScreen = {
                UampMediaPlayerScreen(
                    modifier = Modifier.fillMaxSize(),
                    mediaPlayerScreenViewModel = hiltViewModel(),
                    volumeViewModel = volumeViewModel,
                    onVolumeClick = {
                        navController.navigateToVolume()
                    }
                )
            },
            libraryScreen = { columnState ->
                if (appState.streamingMode == true) {
                    UampStreamingBrowseScreen(
                        onPlaylistsClick = { navController.navigateToCollections() },
                        onSettingsClick = { navController.navigateToSettings() },
                        columnState = columnState
                    )
                } else {
                    UampBrowseScreen(
                        uampBrowseScreenViewModel = hiltViewModel(),
                        onDownloadItemClick = {
                            navController.navigateToCollection(
                                it.playlistUiModel.id,
                                it.playlistUiModel.title
                            )
                        },
                        onPlaylistsClick = { navController.navigateToCollections() },
                        onSettingsClick = { navController.navigateToSettings() },
                        columnState = columnState
                    )
                }
            },
            categoryEntityScreen = { _, name, columnState ->
                if (appState.streamingMode == true) {
                    val viewModel: UampStreamingPlaylistScreenViewModel = hiltViewModel()

                    UampStreamingPlaylistScreen(
                        playlistName = name,
                        viewModel = viewModel,
                        onDownloadItemClick = {
                            navController.navigateToPlayer()
                        },
                        onShuffleClick = { navController.navigateToPlayer() },
                        onPlayClick = { navController.navigateToPlayer() },
                        columnState = columnState
                    )
                } else {
                    val uampEntityScreenViewModel: UampEntityScreenViewModel = hiltViewModel()

                    UampEntityScreen(
                        playlistName = name,
                        uampEntityScreenViewModel = uampEntityScreenViewModel,
                        onDownloadItemClick = {
                            navController.navigateToPlayer()
                        },
                        onShuffleClick = { navController.navigateToPlayer() },
                        onPlayClick = { navController.navigateToPlayer() },
                        onErrorDialogCancelClick = { navController.popBackStack() },
                        columnState = columnState
                    )
                }
            },
            mediaEntityScreen = { _ ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Media XXX")
                }
            },
            playlistsScreen = { columnState ->
                val uampPlaylistsScreenViewModel: UampPlaylistsScreenViewModel =
                    hiltViewModel()

                UampPlaylistsScreen(
                    uampPlaylistsScreenViewModel = uampPlaylistsScreenViewModel,
                    onPlaylistItemClick = { playlistUiModel ->
                        navController.navigateToCollection(
                            playlistUiModel.id,
                            playlistUiModel.title
                        )
                    },
                    onErrorDialogCancelClick = { navController.popBackStack() },
                    columnState = columnState
                )
            },
            settingsScreen = { columnState ->
                UampSettingsScreen(
                    columnState = columnState,
                    viewModel = hiltViewModel(),
                    navController = navController
                )
            },
            navHostState = navHostState,
            pagerState = pagerState,
            snackbarViewModel = hiltViewModel<SnackbarViewModel>(),
            volumeViewModel = volumeViewModel,
            timeText = timeText,
            deepLinkPrefix = appViewModel.deepLinkPrefix,
            navController = navController,
            additionalNavRoutes = {
                scrollable(
                    route = AudioDebug.navRoute,

                    arguments = AudioDebug.arguments,
                    deepLinks = AudioDebug.deepLinks(appViewModel.deepLinkPrefix)
                ) {
                    AudioDebugScreen(
                        columnState = it.columnState,
                        audioDebugScreenViewModel = hiltViewModel()
                    )
                }

                scrollable(
                    route = Samples.navRoute,

                    arguments = Samples.arguments,
                    deepLinks = Samples.deepLinks(appViewModel.deepLinkPrefix)
                ) {
                    SamplesScreen(
                        columnState = it.columnState,
                        samplesScreenViewModel = hiltViewModel(),
                        navController = navController
                    )
                }

                scrollable(
                    route = DeveloperOptions.navRoute,

                    arguments = DeveloperOptions.arguments,
                    deepLinks = DeveloperOptions.deepLinks(appViewModel.deepLinkPrefix)
                ) {
                    DeveloperOptionsScreen(
                        columnState = it.columnState,
                        developerOptionsScreenViewModel = hiltViewModel(),
                        navController = navController
                    )
                }

                scrollable(
                    route = GoogleSignInPromptScreen.navRoute
                ) {
                    GoogleSignInPromptScreen(
                        navController = navController,
                        columnState = it.columnState,
                        viewModel = hiltViewModel()
                    )
                }

                composable(route = GoogleSignInScreen.navRoute) {
                    GoogleSignInScreen(
                        onAuthCancelled = { navController.popBackStack() },
                        onAuthSucceed = { navController.navigateToLibrary() },
                        viewModel = hiltViewModel<UampGoogleSignInViewModel>()
                    )
                }

                composable(route = GoogleSignOutScreen.navRoute) {
                    GoogleSignOutScreen(
                        navController = navController,
                        viewModel = hiltViewModel()
                    )
                }
            }
        )
    }

    LaunchedEffect(Unit) {
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
//                navController.navigateToLibrary()
                navController.navigateToPlayer()
            })
        }

        if (appViewModel.shouldShowLoginPrompt()) {
            // Allow screen to settle so it feels like a distinct step
            delay(200)
            navController.navigateToGoogleSignInPrompt()
        }
    }
}

private fun Intent.getAndRemoveKey(key: String): String? =
    getStringExtra(key).also {
        removeExtra(key)
    }
