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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.os.bundleOf
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation3.runtime.NavBackStack
import com.google.android.horologist.auth.ui.googlesignin.signin.GoogleSignInScreen
import com.google.android.horologist.media.ui.material3.navigation.CustomRoute
import com.google.android.horologist.media.ui.material3.navigation.MediaRoute
import com.google.android.horologist.media.ui.material3.navigation.MediaNavController.navigateToCollection
import com.google.android.horologist.media.ui.material3.navigation.MediaNavController.navigateToCollections
import com.google.android.horologist.media.ui.material3.navigation.MediaNavController.navigateToLibrary
import com.google.android.horologist.media.ui.material3.navigation.MediaNavController.navigateToPlayer
import com.google.android.horologist.media.ui.material3.navigation.MediaNavController.navigateToSettings
import com.google.android.horologist.media.ui.material3.navigation.MediaNavController.navigateToVolume
import com.google.android.horologist.media.ui.material3.navigation.MediaPlayerScaffold
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
import com.google.android.horologist.mediasample.ui.navigation.UampNavigationScreen
import com.google.android.horologist.mediasample.ui.newhotness.NewHotnessPlayerScreen
import com.google.android.horologist.mediasample.ui.player.UampMediaPlayerScreen
import com.google.android.horologist.mediasample.ui.playlists.UampPlaylistsScreen
import com.google.android.horologist.mediasample.ui.settings.DeveloperOptionsScreen
import com.google.android.horologist.mediasample.ui.settings.UampSettingsScreen

@Suppress("UNCHECKED_CAST")
@Composable
fun UampWearApp(
    backStack: NavBackStack<MediaRoute>,
    intent: Intent,
) {
    val appViewModel: MediaPlayerAppViewModel = hiltViewModel()
    val volumeViewModel: VolumeViewModel = hiltViewModel()
    val mediaInfoTimeTextViewModel: MediaInfoTimeTextViewModel = hiltViewModel()

    val appState by appViewModel.appState.collectAsStateWithLifecycle()

    UampTheme {
        MediaPlayerScaffold(
            playerScreen = {
                UampMediaPlayerScreen(
                    modifier = Modifier.fillMaxSize(),
                    mediaPlayerScreenViewModel = hiltViewModel(),
                    volumeViewModel = volumeViewModel,
                    onVolumeClick = {
                        backStack.navigateToVolume()
                    },
                )
            },
            libraryScreen = {
                if (appState.streamingMode == true) {
                    UampStreamingBrowseScreen(
                        onPlaylistsClick = {
                            backStack.navigateToCollections()
                        },
                        onSettingsClick = {
                            backStack.navigateToSettings()
                        },
                    )
                } else {
                    UampBrowseScreen(
                        uampBrowseScreenViewModel = hiltViewModel(),
                        onDownloadItemClick = {
                            backStack.navigateToCollection(
                                collectionId = it.playlistUiModel.id,
                                collectionName = it.playlistUiModel.title,
                            )
                        },
                        onPlaylistsClick = {
                            backStack.navigateToCollections()
                        },
                        onSettingsClick = {
                            backStack.navigateToSettings()
                        },
                    )
                }
            },
            categoryEntityScreen = { id, name ->
                val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current)
                val defaultExtras = if (viewModelStoreOwner is HasDefaultViewModelProviderFactory) {
                    viewModelStoreOwner.defaultViewModelCreationExtras
                } else {
                    androidx.lifecycle.viewmodel.CreationExtras.Empty
                }
                val extras = MutableCreationExtras(defaultExtras).apply {
                    set(DEFAULT_ARGS_KEY, bundleOf("id" to id, "name" to name))
                }
                val customOwner = object : ViewModelStoreOwner by viewModelStoreOwner, HasDefaultViewModelProviderFactory {
                    override val defaultViewModelCreationExtras: androidx.lifecycle.viewmodel.CreationExtras
                        get() = extras
                    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
                        get() = (viewModelStoreOwner as? HasDefaultViewModelProviderFactory)?.defaultViewModelProviderFactory
                            ?: ViewModelProvider.NewInstanceFactory()
                }

                CompositionLocalProvider(LocalViewModelStoreOwner provides customOwner) {
                    if (appState.streamingMode == true) {
                        val viewModel: UampStreamingPlaylistScreenViewModel = hiltViewModel()

                        UampStreamingPlaylistScreen(
                            playlistName = name,
                            viewModel = viewModel,
                            onDownloadItemClick = {
                                backStack.navigateToPlayer()
                            },
                            onShuffleClick = { backStack.navigateToPlayer() },
                            onPlayClick = { backStack.navigateToPlayer() },
                        )
                    } else {
                        val uampEntityScreenViewModel: UampEntityScreenViewModel = hiltViewModel()

                        UampEntityScreen(
                            playlistName = name,
                            uampEntityScreenViewModel = uampEntityScreenViewModel,
                            onDownloadItemClick = {
                                backStack.navigateToPlayer()
                            },
                            onShuffleClick = { backStack.navigateToPlayer() },
                            onPlayClick = { backStack.navigateToPlayer() },
                            onErrorDialogCancelClick = { backStack.removeLastOrNull() },
                        )
                    }
                }
            },
            mediaEntityScreen = {
                UampPlaylistsScreen(
                    uampPlaylistsScreenViewModel = hiltViewModel(),
                    onPlaylistItemClick = {
                        backStack.navigateToCollection(it.id, it.title)
                    },
                    onErrorDialogCancelClick = { backStack.removeLastOrNull() },
                )
            },
            playlistsScreen = {
                UampPlaylistsScreen(
                    uampPlaylistsScreenViewModel = hiltViewModel(),
                    onPlaylistItemClick = {
                        backStack.navigateToCollection(it.id, it.title)
                    },
                    onErrorDialogCancelClick = { backStack.removeLastOrNull() },
                )
            },
            settingsScreen = {
                UampSettingsScreen(
                    viewModel = hiltViewModel(),
                    backStack = backStack as NavBackStack<CustomRoute>,
                )
            },
            volumeViewModel = volumeViewModel,
            timeText = {
                MediaInfoTimeText(
                    mediaInfoTimeTextViewModel = mediaInfoTimeTextViewModel,
                )
            },
            deepLinkPrefix = appViewModel.deepLinkPrefix,
            backStack = backStack,
            additionalEntries = {
                entry(CustomRoute(UampNavigationScreen.AudioDebug.navRoute)) {
                    AudioDebugScreen(
                        audioDebugScreenViewModel = hiltViewModel(),
                    )
                }

                entry(CustomRoute(UampNavigationScreen.Samples.navRoute)) {
                    SamplesScreen(
                        samplesScreenViewModel = hiltViewModel(),
                        backStack = backStack as NavBackStack<CustomRoute>,
                    )
                }

                entry(CustomRoute(UampNavigationScreen.DeveloperOptions.navRoute)) {
                    DeveloperOptionsScreen(
                        developerOptionsScreenViewModel = hiltViewModel(),
                        backStack = backStack as NavBackStack<CustomRoute>,
                    )
                }

                entry(CustomRoute(UampNavigationScreen.GoogleSignInPromptScreen.navRoute)) {
                    GoogleSignInPromptScreen(
                        backStack = backStack as NavBackStack<CustomRoute>,
                        viewModel = hiltViewModel(),
                    )
                }

                entry(CustomRoute(UampNavigationScreen.GoogleSignInScreen.navRoute)) {
                    GoogleSignInScreen(
                        onAuthCancelled = { backStack.removeLastOrNull() },
                        onAuthSucceed = { backStack.navigateToLibrary() },
                        viewModel = hiltViewModel<UampGoogleSignInViewModel>(),
                    )
                }

                entry(CustomRoute(UampNavigationScreen.GoogleSignOutScreen.navRoute)) {
                    GoogleSignOutScreen(
                        backStack = backStack as NavBackStack<CustomRoute>,
                        viewModel = hiltViewModel(),
                    )
                }

                entry(CustomRoute(UampNavigationScreen.NewHotness.navRoute)) {
                    NewHotnessPlayerScreen()
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
    }
}
