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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import com.google.android.horologist.audio.BluetoothSettings.launchBluetoothSettings
import com.google.android.horologist.audio.ui.VolumeScreen
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.compose.pager.FocusOnResume
import com.google.android.horologist.media.ui.uamp.UampTheme

@Composable
fun WearApp(
    navController: NavHostController,
    mediaPlayerScreenViewModelFactory: MediaPlayerScreenViewModel.Factory
) {
    UampTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) {
            SwipeDismissableNavHost(
                navController = navController,
                startDestination = Navigation.MediaPlayer.route,
            ) {
                composable(Navigation.Volume.route) {
                    val focusRequester = remember { FocusRequester() }

                    VolumeScreen(focusRequester = focusRequester)

                    FocusOnResume(focusRequester = focusRequester)
                }
                composable(Navigation.MediaPlayer.route) {
                    val context = LocalContext.current

                    MediaPlayerScreen(
                        mediaPlayerScreenViewModel = viewModel(factory = mediaPlayerScreenViewModelFactory),
                        volumeViewModel = viewModel(factory = VolumeViewModel.Factory),
                        onVolumeClick = {
                            navController.navigate(Navigation.Volume.route)
                        },
                        onOutputClick = {
                            context.launchBluetoothSettings()
                        }
                    )
                }
            }
        }
    }
}