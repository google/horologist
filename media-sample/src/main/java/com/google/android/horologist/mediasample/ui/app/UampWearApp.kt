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
import androidx.navigation.NavHostController
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.layout.StateUtils.rememberStateWithLifecycle
import com.google.android.horologist.compose.navscaffold.scalingLazyColumnComposable
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToCollection
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToCollections
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToLibrary
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToPlayer
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToSettings
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToVolume
import com.google.android.horologist.media.ui.navigation.MediaPlayerScaffold
import com.google.android.horologist.mediasample.components.MediaActivity
import com.google.android.horologist.mediasample.ui.browse.UampBrowseScreen
import com.google.android.horologist.mediasample.ui.debug.AudioDebugScreen
import com.google.android.horologist.mediasample.ui.debug.MediaInfoTimeText
import com.google.android.horologist.mediasample.ui.debug.SamplesScreen
import com.google.android.horologist.mediasample.ui.entity.UampEntityScreen
import com.google.android.horologist.mediasample.ui.entity.UampEntityScreenViewModel
import com.google.android.horologist.mediasample.ui.navigation.AudioDebug
import com.google.android.horologist.mediasample.ui.navigation.Samples
import com.google.android.horologist.mediasample.ui.player.UampMediaPlayerScreen
import com.google.android.horologist.mediasample.ui.playlists.UampPlaylistsScreen
import com.google.android.horologist.mediasample.ui.playlists.UampPlaylistsScreenViewModel
import com.google.android.horologist.mediasample.ui.settings.UampSettingsScreen

@Composable
fun UampWearApp(
    navController: NavHostController,
    intent: Intent
) {
    val appViewModel: MediaPlayerAppViewModel = hiltViewModel()
    val settingsState by rememberStateWithLifecycle(flow = appViewModel.settingsState)

    val volumeViewModel: VolumeViewModel = hiltViewModel()

    val timeText: @Composable (Modifier) -> Unit = { modifier ->
        val networkUsage by rememberStateWithLifecycle(appViewModel.networkUsage)
        val networkStatus by rememberStateWithLifecycle(appViewModel.networkStatus)

        MediaInfoTimeText(
            modifier = modifier,
            showData = settingsState?.showTimeTextInfo ?: false,
            networkStatus = networkStatus,
            networkUsage = networkUsage,
        )
    }

    UampTheme {
        MediaPlayerScaffold(
            playerScreen = { focusRequester ->
                UampMediaPlayerScreen(
                    modifier = Modifier.fillMaxSize(),
                    mediaPlayerScreenViewModel = hiltViewModel(),
                    volumeViewModel = volumeViewModel,
                    onVolumeClick = {
                        navController.navigateToVolume()
                    },
                    playerFocusRequester = focusRequester,
                    settingsState = settingsState
                )
            },
            libraryScreen = { focusRequester, scalingLazyListState ->
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
                    focusRequester = focusRequester,
                    scalingLazyListState = scalingLazyListState,
                )
            },
            categoryEntityScreen = { _, _, focusRequester, scalingLazyListState ->
                val uampEntityScreenViewModel: UampEntityScreenViewModel = hiltViewModel()

                UampEntityScreen(
                    uampEntityScreenViewModel = uampEntityScreenViewModel,
                    onDownloadItemClick = { },
                    onShuffleClick = { navController.navigateToPlayer() },
                    onPlayClick = { navController.navigateToPlayer() },
                    focusRequester = focusRequester,
                    scalingLazyListState = scalingLazyListState,
                )
            },
            mediaEntityScreen = { _, _ ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Media XXX")
                }
            },
            playlistsScreen = { focusRequester, scalingLazyListState ->
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
                    focusRequester = focusRequester,
                    scalingLazyListState = scalingLazyListState
                )
            },
            settingsScreen = { focusRequester, state ->
                UampSettingsScreen(
                    focusRequester = focusRequester,
                    state = state,
                    settingsScreenViewModel = hiltViewModel(),
                    navController = navController
                )
            },
            snackbarViewModel = hiltViewModel<SnackbarViewModel>(),
            volumeViewModel = hiltViewModel<VolumeViewModel>(),
            timeText = timeText,
            deepLinkPrefix = appViewModel.deepLinkPrefix,
            navController = navController,
            additionalNavRoutes = {
                scalingLazyColumnComposable(
                    route = AudioDebug.navRoute,
                    arguments = AudioDebug.arguments,
                    deepLinks = AudioDebug.deepLinks(appViewModel.deepLinkPrefix),
                    scrollStateBuilder = { ScalingLazyListState() }
                ) {
                    AudioDebugScreen(
                        focusRequester = it.viewModel.focusRequester,
                        state = it.scrollableState,
                        audioDebugScreenViewModel = hiltViewModel()
                    )
                }

                scalingLazyColumnComposable(
                    route = Samples.navRoute,
                    arguments = Samples.arguments,
                    deepLinks = Samples.deepLinks(appViewModel.deepLinkPrefix),
                    scrollStateBuilder = { ScalingLazyListState() }
                ) {
                    SamplesScreen(
                        focusRequester = it.viewModel.focusRequester,
                        state = it.scrollableState,
                        samplesScreenViewModel = hiltViewModel(),
                        navController = navController
                    )
                }
            }
        )
    }

    LaunchedEffect(Unit) {
        val collectionId = intent.getAndRemoveKey(MediaActivity.CollectionKey)
        val mediaId = intent.getAndRemoveKey(MediaActivity.MediaIdKey)

        if (collectionId != null) {
            appViewModel.playItems(mediaId, collectionId)
        } else {
            appViewModel.startupSetup(navigateToLibrary = {
                navController.navigateToLibrary()
            })
        }
    }
}

private fun Intent.getAndRemoveKey(key: String): String? =
    getStringExtra(key).also {
        removeExtra(key)
    }
