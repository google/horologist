/*
 * Copyright 2023 The Android Open Source Project
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

@file:OptIn(ExperimentalWearFoundationApi::class)

package com.google.android.horologist.mediasample.benchmark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.rotaryVolumeControlsWithFocus
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.composable
import com.google.android.horologist.compose.pager.PagerScreen
import com.google.android.horologist.compose.rotaryinput.RotaryDefaults
import com.google.android.horologist.media.ui.components.animated.AnimatedMediaControlButtons
import com.google.android.horologist.media.ui.components.animated.AnimatedMediaInfoDisplay
import com.google.android.horologist.media.ui.components.background.ColorBackground
import com.google.android.horologist.media.ui.screens.player.PlayerScreen
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import com.google.android.horologist.mediasample.ui.player.UampSettingsButtons
import kotlin.time.Duration.Companion.seconds

class MediaPlayerBenchmarkActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        val modeString = intent.getStringExtra("Mode")!!
        val mode = Mode.valueOf(modeString)

        setContent {
            MediaPlayerSampleWithScaffold(mode)
        }
    }
}

enum class Mode {
    Full, NavOnly, PagerOnly, ScreenOnly
}

@Composable
fun MediaPlayerSampleWithScaffold(mode: Mode) {
    when (mode) {
        Mode.Full -> {
            MediaPlayerNav {
                MediaPlayerPager {
                    MediaPlayerSample()
                }
            }
        }

        Mode.NavOnly -> {
            MediaPlayerNav {
                MediaPlayerSample()
            }
        }

        Mode.PagerOnly -> {
            MediaPlayerPager {
                MediaPlayerSample()
            }
        }

        Mode.ScreenOnly -> {
            MediaPlayerSample()
        }
    }
}

@Composable
private fun MediaPlayerNav(content: @Composable () -> Unit) {
    val navController = rememberSwipeDismissableNavController()

    WearNavScaffold(startDestination = "/a", navController = navController) {
        composable("/a") {
            content()
        }
    }
}

@Composable
private fun MediaPlayerPager(content: @Composable () -> Unit) {
    val pagerState = rememberPagerState { 2 }
    PagerScreen(state = pagerState) {
        content()
    }
}

@Composable
fun MediaPlayerSample() {
    val focusRequester = rememberActiveFocusRequester()

    val volumeUiState = VolumeUiState(current = 0, max = 10)

    val playerUiState = PlayerUiState(
        playEnabled = true,
        pauseEnabled = true,
        seekBackEnabled = true,
        seekForwardEnabled = true,
        seekToPreviousEnabled = false,
        seekToNextEnabled = true,
        shuffleEnabled = false,
        shuffleOn = false,
        playPauseEnabled = true,
        playing = true,
        media = MediaUiModel(
            id = "",
            title = "The power of types, volume II",
            subtitle = "Kotlinconf"
        ),
        trackPositionUiModel = TrackPositionUiModel.Actual(
            percent = 0.1f,
            position = 30.seconds,
            duration = 300.seconds
        ),
        connected = true
    )

    PlayerScreen(
        mediaDisplay = {
            AnimatedMediaInfoDisplay(
                media = playerUiState.media,
                loading = false,
            )
        },
        controlButtons = {
            AnimatedMediaControlButtons(
                onPlayButtonClick = { },
                onPauseButtonClick = { },
                playPauseButtonEnabled = playerUiState.playPauseEnabled,
                playing = playerUiState.playing,
                onSeekToPreviousButtonClick = { },
                seekToPreviousButtonEnabled = playerUiState.seekToPreviousEnabled,
                onSeekToNextButtonClick = { },
                seekToNextButtonEnabled = playerUiState.seekToNextEnabled,
                trackPositionUiModel = playerUiState.trackPositionUiModel
            )
        },
        buttons = {
            UampSettingsButtons(
                volumeUiState = volumeUiState,
                onVolumeClick = { },
                enabled = true
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .rotaryVolumeControlsWithFocus(
                focusRequester = focusRequester,
                volumeUiStateProvider = { volumeUiState },
                onRotaryVolumeInput = { },
                localView = LocalView.current,
                isLowRes = RotaryDefaults.isLowResInput()
            ),
        background = {
            ColorBackground(color = Color.Yellow)
        }
    )
}
