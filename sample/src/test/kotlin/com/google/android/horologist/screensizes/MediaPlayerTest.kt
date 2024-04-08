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

@file:OptIn(ExperimentalFoundationApi::class)

package com.google.android.horologist.screensizes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.VolumePositionIndicator
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.components.SettingsButtons
import com.google.android.horologist.audio.ui.components.SettingsButtonsDefaults
import com.google.android.horologist.audio.ui.mapper.VolumeUiStateMapper
import com.google.android.horologist.compose.pager.PagerScreen
import com.google.android.horologist.compose.tools.Device
import com.google.android.horologist.logo.R
import com.google.android.horologist.media.ui.components.animated.AnimatedMediaControlButtons
import com.google.android.horologist.media.ui.components.animated.AnimatedMediaInfoDisplay
import com.google.android.horologist.media.ui.components.background.RadialBackground
import com.google.android.horologist.media.ui.screens.player.PlayerScreen
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import kotlinx.coroutines.flow.flowOf
import kotlin.time.Duration.Companion.seconds

class MediaPlayerTest(device: Device) : WearLegacyScreenSizeTest(
    device = device,
    showTimeText = true,
) {

    @Composable
    override fun Content() {
        MediaPlayerTestCase()
    }
}

@Composable
fun MediaPlayerTestCase() {
    val playerUiState = PlayerUiState(
        playEnabled = true,
        pauseEnabled = true,
        seekBackEnabled = true,
        seekForwardEnabled = true,
        seekInCurrentMediaItemEnabled = true,
        seekToPreviousEnabled = false,
        seekToNextEnabled = true,
        shuffleEnabled = false,
        shuffleOn = false,
        playPauseEnabled = true,
        playing = true,
        media = MediaUiModel(
            id = "",
            title = "Four Seasons In One Day",
            subtitle = "Crowded House",
        ),
        trackPositionUiModel = TrackPositionUiModel.Actual(
            percent = 0.1f,
            position = 30.seconds,
            duration = 300.seconds,
        ),
        connected = true,
    )
    Scaffold(
        positionIndicator = {
            VolumePositionIndicator(
                volumeUiState = {
                    VolumeUiStateMapper.map(volumeState = VolumeState(6, 10))
                },
                displayIndicatorEvents = flowOf(),
            )
        },
    ) {
        val colors = MaterialTheme.colors
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
            PagerScreen(
                state = rememberPagerState {
                    2
                },
            ) {
                if (it == 0) {
                    PlayerScreen(
                        modifier = Modifier.fillMaxSize(),
                        mediaDisplay = {
                            AnimatedMediaInfoDisplay(
                                playerUiState.media,
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
                                trackPositionUiModel = playerUiState.trackPositionUiModel,
                            )
                        },
                        buttons = {
                            SettingsButtons(
                                volumeUiState = VolumeUiState(5, 10),
                                onVolumeClick = { /*TODO*/ },
                                onOutputClick = { },
                                brandIcon = {
                                    SettingsButtonsDefaults.BrandIcon(
                                        R.drawable.ic_stat_horologist,
                                        enabled = playerUiState.connected,
                                    )
                                },
                                enabled = playerUiState.connected,
                            )
                        },
                        background = { RadialBackground(color = colors.primary) },
                    )
                }
            }
        }
    }
}
