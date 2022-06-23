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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.wear.compose.material.ScalingLazyListState
import com.google.accompanist.pager.rememberPagerState
import com.google.android.horologist.audio.ui.VolumeScreen
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.compose.layout.StateUtils.rememberStateWithLifecycle
import com.google.android.horologist.compose.navscaffold.NavScaffoldViewModel
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.scalingLazyColumnComposable
import com.google.android.horologist.compose.navscaffold.wearNavComposable
import com.google.android.horologist.media.ui.screens.PlayerLibraryPagerScreen
import com.google.android.horologist.mediasample.di.MediaApplicationContainer
import com.google.android.horologist.mediasample.ui.debug.MediaInfoTimeText
import com.google.android.horologist.mediasample.ui.library.LibraryScreenViewModel
import com.google.android.horologist.mediasample.ui.library.UampLibraryScreen
import com.google.android.horologist.mediasample.ui.player.MediaPlayerScreenViewModel
import com.google.android.horologist.mediasample.ui.player.UampMediaPlayerScreen
import com.google.android.horologist.mediasample.ui.settings.SettingsScreenViewModel
import com.google.android.horologist.mediasample.ui.settings.UampSettingsScreen

@Composable
fun WearApp(
    navController: NavHostController,
    creationExtras: () -> CreationExtras
) {
    val appViewModel: MediaPlayerAppViewModel = viewModel(factory = MediaPlayerAppViewModel.Factory)

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
        WearNavScaffold(
            modifier = Modifier.fillMaxSize(),
            startDestination = Navigation.MediaPlayer.route,
            navController = navController,
            timeText = timeText
        ) {
            wearNavComposable(
                route = Navigation.MediaPlayer.route,
                arguments = listOf(
                    navArgument("page") {
                        type = NavType.IntType
                    }
                ),
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "${appViewModel.deepLinkPrefix}/player?page={page}"
                    }
                )
            ) { backStack, viewModel ->
                viewModel.positionIndicatorMode = NavScaffoldViewModel.PositionIndicatorMode.Off
                viewModel.timeTextMode = NavScaffoldViewModel.TimeTextMode.Off

                val volumeViewModel: VolumeViewModel =
                    viewModel(factory = VolumeViewModelFactory, extras = creationExtras())
                val libraryScreenViewModel: LibraryScreenViewModel =
                    viewModel(factory = LibraryScreenViewModel.Factory, extras = creationExtras())

                val pagerState = rememberPagerState()

                val mediaPlayerScreenViewModel = viewModel<MediaPlayerScreenViewModel>(
                    factory = MediaPlayerScreenViewModel.Factory,
                    extras = creationExtras()
                )

                val volumeState by rememberStateWithLifecycle(volumeViewModel.volumeState)

                PlayerLibraryPagerScreen(
                    pagerState = pagerState,
                    playerScreen = { focusRequester ->
                        UampMediaPlayerScreen(
                            modifier = Modifier.fillMaxSize(),
                            mediaPlayerScreenViewModel = mediaPlayerScreenViewModel,
                            volumeViewModel = viewModel(factory = VolumeViewModel.Factory),
                            onVolumeClick = {
                                navController.navigate(Navigation.Volume.route)
                            },
                            onOutputClick = {
                                mediaPlayerScreenViewModel.launchBluetoothSettings()
                            },
                            playerFocusRequester = focusRequester
                        )
                    },
                    libraryScreen = { focusRequester, state ->
                        UampLibraryScreen(
                            focusRequester = focusRequester,
                            state = state,
                            onSettingsClick = {
                                navController.navigate(Navigation.Settings.route)
                            },
                            libraryScreenViewModel = libraryScreenViewModel,
                            onPlayClick = {
                                navController.navigate(Navigation.MediaPlayer.player)
                            }
                        )
                    },
                    volumeScrollableState = volumeViewModel.volumeScrollableState,
                    volumeState = { volumeState },
                    timeText = timeText,
                    backStack = backStack
                )
            }
            wearNavComposable(route = Navigation.Volume.route) { _, viewModel ->
                viewModel.timeTextMode = NavScaffoldViewModel.TimeTextMode.Off
                VolumeScreen(focusRequester = viewModel.focusRequester)
            }
            scalingLazyColumnComposable(
                route = Navigation.Settings.route,
                scrollStateBuilder = {
                    ScalingLazyListState()
                }
            ) {
                UampSettingsScreen(
                    focusRequester = it.viewModel.focusRequester,
                    state = it.scrollableState,
                    settingsScreenViewModel = viewModel(factory = SettingsScreenViewModel.Factory)
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        appViewModel.startupSetup(navigateToLibrary = {
            navController.navigate(Navigation.MediaPlayer.library)
        })
    }
}

public val VolumeViewModelFactory: ViewModelProvider.Factory = viewModelFactory {
    initializer {
        val audioRepository = this[MediaApplicationContainer.SystemAudioRepositoryKey]!!
        val vibrator = this[MediaApplicationContainer.VibratorKey]!!

        VolumeViewModel(
            volumeRepository = audioRepository,
            audioOutputRepository = audioRepository,
            vibrator = vibrator
        )
    }
}
