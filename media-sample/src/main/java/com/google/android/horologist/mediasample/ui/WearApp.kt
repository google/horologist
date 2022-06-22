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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.android.horologist.audio.BluetoothSettings.launchBluetoothSettings
import com.google.android.horologist.audio.ui.VolumeScreen
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.compose.layout.StateUtils.rememberStateWithLifecycle
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.wearNavComposable
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
            wearNavComposable(Navigation.Volume.route) { _, viewModel ->
                VolumeScreen(focusRequester = viewModel.focusRequester)
            }
            wearNavComposable(Navigation.MediaPlayer.route) { _, viewModel ->
                val context = LocalContext.current

                MediaPlayerScreen(
                    mediaPlayerScreenViewModel = viewModel(
                        factory = MediaPlayerScreenViewModel.Factory,
                        extras = creationExtras()
                    ),
                    volumeViewModel = viewModel(factory = VolumeViewModel.Factory),
                    onVolumeClick = {
                        navController.navigate(Navigation.Volume.route)
                    },
                    onOutputClick = {
                        context.launchBluetoothSettings()
                    },
                    playerFocusRequester = viewModel.focusRequester
                )
            }
        }
    }
}
