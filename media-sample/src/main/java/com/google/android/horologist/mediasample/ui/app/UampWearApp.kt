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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.compose.layout.StateUtils.rememberStateWithLifecycle
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToLibrary
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToPlayer
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToSettings
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToVolume
import com.google.android.horologist.media.ui.navigation.MediaPlayerScaffold
import com.google.android.horologist.mediasample.ui.debug.MediaInfoTimeText
import com.google.android.horologist.mediasample.ui.library.LibraryScreenViewModel
import com.google.android.horologist.mediasample.ui.library.UampLibraryScreen
import com.google.android.horologist.mediasample.ui.player.MediaPlayerScreenViewModel
import com.google.android.horologist.mediasample.ui.player.UampMediaPlayerScreen
import com.google.android.horologist.mediasample.ui.settings.SettingsScreenViewModel
import com.google.android.horologist.mediasample.ui.settings.UampSettingsScreen
import com.google.android.horologist.mediasample.ui.settings.VolumeViewModelFactory

@Composable
fun UampWearApp(
    navController: NavHostController,
    creationExtras: () -> CreationExtras
) {
    val appViewModel: MediaPlayerAppViewModel = viewModel(factory = MediaPlayerAppViewModel.Factory)

    val volumeViewModel: VolumeViewModel =
        viewModel(factory = VolumeViewModelFactory, extras = creationExtras())

    val timeText: @Composable (Modifier) -> Unit = {
        val networkUsage by rememberStateWithLifecycle(appViewModel.networkUsage)
        val networkStatus by rememberStateWithLifecycle(appViewModel.networkStatus)
        val offloadState by rememberStateWithLifecycle(appViewModel.offloadState)

        MediaInfoTimeText(
            showData = appViewModel.showTimeTextInfo,
            networkStatus = networkStatus,
            networkUsage = networkUsage,
            offloadState = offloadState
        )
    }

    UampTheme {
        MediaPlayerScaffold(
            playerScreen = { focusRequester ->
                val mediaPlayerScreenViewModel = viewModel<MediaPlayerScreenViewModel>(
                    factory = MediaPlayerScreenViewModel.Factory,
                    extras = creationExtras()
                )

                UampMediaPlayerScreen(
                    modifier = Modifier.fillMaxSize(),
                    mediaPlayerScreenViewModel = mediaPlayerScreenViewModel,
                    volumeViewModel = volumeViewModel,
                    onVolumeClick = {
                        navController.navigateToVolume()
                    },
                    playerFocusRequester = focusRequester
                )
            },
            libraryScreen = { focusRequester, state ->
                val libraryScreenViewModel: LibraryScreenViewModel =
                    viewModel(factory = LibraryScreenViewModel.Factory, extras = creationExtras())

                UampLibraryScreen(
                    focusRequester = focusRequester,
                    state = state,
                    onSettingsClick = {
                        navController.navigateToSettings()
                    },
                    libraryScreenViewModel = libraryScreenViewModel,
                    onPlayClick = {
                        navController.navigateToPlayer()
                    }
                )
            },
            categoryEntityScreen = { focusRequester, state ->
                TODO()
            },
            mediaEntityScreen = { focusRequester, state ->
                TODO()
            },
            playlistsScreen = { focusRequester, state ->
                TODO()
            },
            settingsScreen = { focusRequester, state ->
                UampSettingsScreen(
                    focusRequester = focusRequester,
                    state = state,
                    settingsScreenViewModel = viewModel(
                        factory = SettingsScreenViewModel.Factory,
                        extras = creationExtras()
                    )
                )
            },
            timeText = timeText,
            deepLinkPrefix = appViewModel.deepLinkPrefix,
            navController = navController
        )
    }

    LaunchedEffect(Unit) {
        appViewModel.startupSetup(navigateToLibrary = {
            navController.navigateToLibrary()
        })
    }
}
