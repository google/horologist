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

package com.google.android.horologist.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.horologist.audio.BluetoothSettings.launchBluetoothSettings
import com.google.android.horologist.audio.ui.VolumeScreen
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.composables.DatePicker
import com.google.android.horologist.composables.TimePicker
import com.google.android.horologist.composables.TimePickerWith12HourClock
import com.google.android.horologist.sample.di.SampleAppDI
import com.google.android.horologist.sample.media.MediaPlayerScreen
import com.google.android.horologist.sample.media.MediaPlayerScreenViewModel
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {

    lateinit var mediaPlayerScreenViewModelFactory: MediaPlayerScreenViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SampleAppDI.inject(this)

        setContent {
            WearApp(mediaPlayerScreenViewModelFactory)
        }
    }
}

@Composable
fun WearApp(
    mediaPlayerScreenViewModelFactory: MediaPlayerScreenViewModel.Factory
) {
    val navController = rememberSwipeDismissableNavController()

    var time by remember { mutableStateOf(LocalDateTime.now()) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) {
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = Screen.Menu.route,
        ) {
            composable(route = Screen.Menu.route) {
                MenuScreen(
                    modifier = Modifier.fillMaxSize(),
                    navigateToRoute = { route -> navController.navigate(route) },
                    time = time
                )
            }
            composable(Screen.FillMaxRectangle.route) {
                FillMaxRectangleScreen()
            }
            composable(Screen.Volume.route) {
                val focusRequester = remember { FocusRequester() }

                VolumeScreen(focusRequester = focusRequester)

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }
            composable(Screen.FadeAway.route) {
                FadeAwayScreenLazyColumn()
            }
            composable(Screen.FadeAwaySLC.route) {
                FadeAwayScreenScalingLazyColumn()
            }
            composable(Screen.FadeAwayColumn.route) {
                FadeAwayScreenColumn()
            }
            composable(Screen.DatePicker.route) {
                DatePicker(
                    date = time.toLocalDate(),
                    onDateConfirm = {
                        time = time.toLocalTime().atDate(it)
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.TimePicker.route) {
                TimePickerWith12HourClock(
                    time = time.toLocalTime(),
                    onTimeConfirm = {
                        time = time.toLocalDate().atTime(it)
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.TimeWithSecondsPicker.route) {
                TimePicker(
                    time = time.toLocalTime(),
                    onTimeConfirm = {
                        time = time.toLocalDate().atTime(it)
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.TimeWithoutSecondsPicker.route) {
                TimePicker(
                    time = time.toLocalTime(),
                    onTimeConfirm = {
                        time = time.toLocalDate().atTime(it)
                        navController.popBackStack()
                    },
                    showSeconds = false
                )
            }
            composable(Screen.MediaPlayer.route) {
                val context = LocalContext.current

                MediaPlayerScreen(
                    mediaPlayerScreenViewModel = viewModel(factory = mediaPlayerScreenViewModelFactory),
                    volumeViewModel = viewModel(factory = VolumeViewModel.Factory),
                    onVolumeClick = {
                        navController.navigate(Screen.Volume.route)
                    },
                    onOutputClick = {
                        context.launchBluetoothSettings()
                    }
                )
            }
        }
    }
}
