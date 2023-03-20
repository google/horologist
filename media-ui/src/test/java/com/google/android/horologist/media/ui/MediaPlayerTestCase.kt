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

@file:OptIn(
    ExperimentalHorologistMediaUiApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalFoundationApi::class, ExperimentalHorologistMediaUiApi::class
)

package com.google.android.horologist.media.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeSource
import androidx.wear.compose.material.TimeText
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.VolumePositionIndicator
import com.google.android.horologist.audio.ui.components.SettingsButtons
import com.google.android.horologist.audio.ui.components.SettingsButtonsDefaults
import com.google.android.horologist.audio.ui.mapper.VolumeUiStateMapper
import com.google.android.horologist.compose.pager.PagerScreen
import com.google.android.horologist.compose.tools.RoundPreview
import com.google.android.horologist.media.ui.components.MediaControlButtons
import com.google.android.horologist.media.ui.components.background.RadialBackground
import com.google.android.horologist.media.ui.screens.player.DefaultMediaInfoDisplay
import com.google.android.horologist.media.ui.screens.player.PlayerScreen
import com.google.android.horologist.media.ui.state.PlayerUiState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaPlayerTestCase(
    playerUiState: PlayerUiState,
    mediaDisplay: @Composable ColumnScope.() -> Unit = {
        DefaultMediaInfoDisplay(playerUiState)
    },
    controlButtons: @Composable RowScope.() -> Unit = {
        MediaControlButtons(
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
    buttons: @Composable RowScope.() -> Unit = {
        SettingsButtons(
            volumeState = VolumeState(5, 10),
            onVolumeClick = { /*TODO*/ },
            onOutputClick = { },
            brandIcon = {
                SettingsButtonsDefaults.BrandIcon(
                    R.drawable.ic_uamp,
                    enabled = playerUiState.connected
                )
            },
            enabled = playerUiState.connected
        )
    },
    colors: Colors = MaterialTheme.colors,
    background: @Composable BoxScope.() -> Unit = {
        if (playerUiState.media != null) {
            RadialBackground(color = colors.primary)
        }
    },
    time: String = "10:10",
    round: Boolean = true
) {
    RoundPreview(round = round) {
        Box(modifier = Modifier.background(Color.Black)) {
            MaterialTheme(colors = colors) {
                Scaffold(
                    timeText = {
                        TimeText(
                            timeSource = object : TimeSource {
                                override val currentTime: String
                                    @Composable get() = time
                            }
                        )
                    },
                    positionIndicator = {
                        VolumePositionIndicator(
                            volumeUiState = {
                                VolumeUiStateMapper.map(volumeState = VolumeState(6, 10))
                            }
                        )
                    }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        PagerScreen(count = 2) {
                            if (it == 0) {
                                PlayerScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    mediaDisplay = { mediaDisplay() },
                                    controlButtons = { controlButtons() },
                                    buttons = { buttons() },
                                    background = background
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
