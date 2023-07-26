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

package com.google.android.horologist.media.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.VolumePositionIndicator
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.components.SettingsButtons
import com.google.android.horologist.audio.ui.components.SettingsButtonsDefaults
import com.google.android.horologist.audio.ui.mapper.VolumeUiStateMapper
import com.google.android.horologist.compose.pager.PagerScreen
import com.google.android.horologist.media.ui.components.animated.AnimatedMediaControlButtons
import com.google.android.horologist.media.ui.components.background.radialBackgroundBrush
import com.google.android.horologist.media.ui.screens.player.DefaultMediaInfoDisplay
import com.google.android.horologist.media.ui.screens.player.PlayerScreen
import com.google.android.horologist.media.ui.state.PlayerUiState
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaPlayerTestCase(
    playerUiState: PlayerUiState,
    mediaDisplay: @Composable ColumnScope.() -> Unit = {
        DefaultMediaInfoDisplay(playerUiState)
    },
    controlButtons: @Composable RowScope.() -> Unit = {
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
    buttons: @Composable RowScope.() -> Unit = {
        SettingsButtons(
            volumeUiState = VolumeUiState(5, 10),
            onVolumeClick = { /*TODO*/ },
            onOutputClick = { },
            brandIcon = {
                SettingsButtonsDefaults.BrandIcon(
                    com.google.android.horologist.logo.R.drawable.ic_stat_horologist,
                    enabled = playerUiState.connected
                )
            },
            enabled = playerUiState.connected
        )
    },
    colors: Colors = MaterialTheme.colors
) {
    MaterialTheme(colors = colors) {
        Scaffold(
            positionIndicator = {
                VolumePositionIndicator(
                    volumeUiState = {
                        VolumeUiStateMapper.map(volumeState = VolumeState(6, 10))
                    },
                    displayIndicatorEvents = flowOf()
                )
            }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                PagerScreen(
                    state = rememberPagerState {
                        2
                    }
                ) {
                    if (it == 0) {
                        PlayerScreen(
                            modifier = Modifier
                                .fillMaxSize()
                                .drawWithCache {
                                    val background = if (playerUiState.media != null) {
                                        radialBackgroundBrush(
                                            color = colors.primary,
                                            background = Color.Black
                                        )
                                    } else {
                                        null
                                    }
                                    onDrawWithContent {
                                        if (background != null) {
                                            drawRect(
                                                color = Color.Black,
                                                blendMode = BlendMode.Clear
                                            )
                                        }
                                        drawContent()
                                        if (background != null) {
                                            drawRect(background, blendMode = BlendMode.DstOver)
                                        }
                                    }
                                },
                            mediaDisplay = { mediaDisplay() },
                            controlButtons = { controlButtons() },
                            buttons = { buttons() }
                        )
                    }
                }
            }
        }
    }
}
