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

package com.google.android.horologist.mediasample.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.wear.compose.material.ScalingLazyColumn
import com.google.accompanist.pager.rememberPagerState
import com.google.android.horologist.audio.BluetoothSettings.launchBluetoothSettings
import com.google.android.horologist.audio.ui.VolumeScreen
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.compose.layout.StateUtils.rememberStateWithLifecycle
import com.google.android.horologist.compose.navscaffold.NavScaffoldViewModel
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.scrollableColumn
import com.google.android.horologist.compose.navscaffold.wearNavComposable
import com.google.android.horologist.media.ui.screens.PlayerLibraryPagerScreen
import com.google.android.horologist.mediasample.ui.debug.MediaInfoTimeText

@Composable
fun WearApp(
    navController: NavHostController,
    creationExtras: () -> CreationExtras
) {
    val appViewModel: MediaPlayerAppViewModel = viewModel(factory = MediaPlayerAppViewModel.Factory)

    UampTheme {
        WearNavScaffold(
            modifier = Modifier.fillMaxSize(),
            startDestination = Navigation.MediaPlayer.route,
            navController = navController,
            timeText = {
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
            ) { _, viewModel ->
                val pagerState = rememberPagerState()

                val mediaPlayerScreenViewModel = viewModel<MediaPlayerScreenViewModel>(
                    factory = MediaPlayerScreenViewModel.Factory,
                    extras = creationExtras()
                )

                PlayerLibraryPagerScreen(
                    pagerState = pagerState,
                    playerScreen = {
                        MediaPlayerScreen(
                            onVolumeClick = {
                                navController.navigate(Navigation.Volume.route)
                            },
                            focusRequester = viewModel.focusRequester,
                            mediaPlayerScreenViewModel = mediaPlayerScreenViewModel
                        )
                    },
                    libraryScreen = {

                    }
                )


            }
            wearNavComposable(route = Navigation.Volume.route) { _, viewModel ->
                viewModel.timeTextMode = NavScaffoldViewModel.TimeTextMode.Off
                VolumeScreen(focusRequester = viewModel.focusRequester)
            }
        }
    }
}

@Composable
private fun MediaPlayerScreen(
    onVolumeClick: () -> Unit,
    focusRequester: FocusRequester,
    mediaPlayerScreenViewModel: MediaPlayerScreenViewModel,
    modifier: Modifier = Modifier,
) {
    MediaPlayerScreen(
        mediaPlayerScreenViewModel = mediaPlayerScreenViewModel,
        volumeViewModel = viewModel(factory = VolumeViewModel.Factory),
        onVolumeClick = onVolumeClick,
        onOutputClick = {
            mediaPlayerScreenViewModel.launchBluetoothSettings()
        },
        playerFocusRequester = focusRequester
    )
}

@Composable
private fun LibraryScreen(
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    ScalingLazyColumn(modifier = modifier.scrollableColumn(focusRequester))
}
