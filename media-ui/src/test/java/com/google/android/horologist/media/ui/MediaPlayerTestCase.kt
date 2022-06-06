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

@file:OptIn(ExperimentalHorologistMediaUiApi::class, ExperimentalHorologistComposeToolsApi::class)

package com.google.android.horologist.media.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeSource
import androidx.wear.compose.material.TimeText
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.components.SettingsButtons
import com.google.android.horologist.compose.tools.ExperimentalHorologistComposeToolsApi
import com.google.android.horologist.compose.tools.RoundPreview
import com.google.android.horologist.media.ui.components.MediaControlButtons
import com.google.android.horologist.media.ui.screens.DefaultPlayerScreenMediaDisplay
import com.google.android.horologist.media.ui.screens.PlayerScreen
import com.google.android.horologist.media.ui.state.PlayerUiState

@Composable
fun MediaPlayerTestCase(
    playerUiState: PlayerUiState,
    mediaDisplay: @Composable ColumnScope.() -> Unit = {
        DefaultPlayerScreenMediaDisplay(playerUiState)
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
            showProgress = true,
            percent = playerUiState.trackPosition?.percent ?: 0f,
        )
    },
    buttons: @Composable RowScope.() -> Unit = {
        SettingsButtons(
            volumeState = VolumeState(5, 10),
            onVolumeClick = { /*TODO*/ },
            onOutputClick = { }
        )
    },
    background: @Composable BoxScope.() -> Unit = {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background))
    },
    colors: Colors = MaterialTheme.colors,
) {
    RoundPreview {
        MaterialTheme(colors = colors) {
            Scaffold(
                timeText = { TimeText(timeSource = object : TimeSource {
                    override val currentTime: String
                        @Composable get() = "10:10"
                }) }
            ) {
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
