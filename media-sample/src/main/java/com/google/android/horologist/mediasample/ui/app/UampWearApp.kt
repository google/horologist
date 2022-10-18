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

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.rememberSwipeDismissableNavHostState
import com.google.accompanist.pager.rememberPagerState
import com.google.android.horologist.compose.navscaffold.scalingLazyColumnComposable
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToCollection
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToCollections
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToLibrary
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToPlayer
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToSettings
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToVolume
import com.google.android.horologist.media.ui.navigation.MediaPlayerScaffold
import com.google.android.horologist.mediasample.R
import com.google.android.horologist.mediasample.ui.browse.UampBrowseScreen
import com.google.android.horologist.mediasample.ui.debug.AudioDebugScreen
import com.google.android.horologist.mediasample.ui.debug.MediaInfoTimeText
import com.google.android.horologist.mediasample.ui.debug.MediaInfoTimeTextViewModel
import com.google.android.horologist.mediasample.ui.debug.SamplesScreen
import com.google.android.horologist.mediasample.ui.entity.UampEntityScreen
import com.google.android.horologist.mediasample.ui.entity.UampEntityScreenViewModel
import com.google.android.horologist.mediasample.ui.navigation.AudioDebug
import com.google.android.horologist.mediasample.ui.navigation.DeveloperOptions
import com.google.android.horologist.mediasample.ui.navigation.Samples
import com.google.android.horologist.mediasample.ui.player.UampMediaPlayerScreen
import com.google.android.horologist.mediasample.ui.playlists.UampPlaylistsScreen
import com.google.android.horologist.mediasample.ui.playlists.UampPlaylistsScreenViewModel
import com.google.android.horologist.mediasample.ui.settings.DeveloperOptionsScreen
import com.google.android.horologist.mediasample.ui.settings.UampSettingsScreen

@Composable
fun UampWearApp(
    context: Context,
    navController: NavHostController,
    intent: Intent
) {
    val preferences by lazy {
        context.getSharedPreferences(
            context.getString(R.string.sample_shared_preferences),
            Context.MODE_PRIVATE
        )
    }
    val appViewModel: MediaPlayerAppViewModel = hiltViewModel()
    val volumeViewModel: VolumeViewModel = hiltViewModel()
    val mediaInfoTimeTextViewModel: MediaInfoTimeTextViewModel = hiltViewModel()

    val pagerState = rememberPagerState(initialPage = 0)
    val navHostState = rememberSwipeDismissableNavHostState()

    val timeText: @Composable (Modifier) -> Unit = { modifier ->
        MediaInfoTimeText(
            modifier = modifier,
            mediaInfoTimeTextViewModel = mediaInfoTimeTextViewModel
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
                    playerFocusRequester = focusRequester
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
                    scalingLazyListState = scalingLazyListState
                )
            },
            categoryEntityScreen = { _, name, focusRequester, scalingLazyListState ->
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
                    focusRequester = focusRequester,
                    scalingLazyListState = scalingLazyListState
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
                    onErrorDialogCancelClick = { navController.popBackStack() },
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
            navHostState = navHostState,
            pagerState = pagerState,
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

                scalingLazyColumnComposable(
                    route = DeveloperOptions.navRoute,
                    arguments = DeveloperOptions.arguments,
                    deepLinks = DeveloperOptions.deepLinks(appViewModel.deepLinkPrefix),
                    scrollStateBuilder = { ScalingLazyListState() }
                ) {
                    DeveloperOptionsScreen(
                        focusRequester = it.viewModel.focusRequester,
                        state = it.scrollableState,
                        developerOptionsScreenViewModel = hiltViewModel(),
                        navController = navController
                    )
                }
            }
        )
    }

    LaunchedEffect(Unit) {
        val lastPlayedCollection: String? = preferences.getString(context.getString(R.string.sample_current_media_list_id), null)
        val lastPlayedMedia: String? = preferences.getString(context.getString(R.string.sample_current_media_item), null)

        var collectionId = intent.getAndRemoveKey(MediaActivity.CollectionKey)
        if (collectionId == null && lastPlayedCollection != null) {
            collectionId = lastPlayedCollection
        }
        var mediaId = intent.getAndRemoveKey(MediaActivity.MediaIdKey)
        if (mediaId == null && lastPlayedMedia != null) {
            mediaId = lastPlayedMedia
        }

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
