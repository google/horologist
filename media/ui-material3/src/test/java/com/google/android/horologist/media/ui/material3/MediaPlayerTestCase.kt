/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.media.ui.material3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.ScreenScaffold
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.mapper.VolumeUiStateMapper
import com.google.android.horologist.audio.ui.material3.VolumeLevelIndicator
import com.google.android.horologist.audio.ui.material3.components.actions.SettingsButton
import com.google.android.horologist.audio.ui.material3.components.actions.SettingsButtonDefaults
import com.google.android.horologist.audio.ui.material3.components.actions.VolumeButtonWithBadge
import com.google.android.horologist.audio.ui.material3.components.toAudioOutputUi
import com.google.android.horologist.media.ui.material3.components.ambient.AmbientMediaControlButtons
import com.google.android.horologist.media.ui.material3.components.ambient.AmbientMediaInfoDisplay
import com.google.android.horologist.media.ui.material3.components.animated.AnimatedMediaControlButtons
import com.google.android.horologist.media.ui.material3.components.animated.AnimatedMediaInfoDisplay
import com.google.android.horologist.media.ui.material3.components.background.ArtworkImageBackground
import com.google.android.horologist.media.ui.material3.screens.player.PlayerScreen
import com.google.android.horologist.media.ui.state.PlayerUiState
import kotlinx.coroutines.flow.flowOf

@Composable
fun MediaPlayerTestCase(
    playerUiState: PlayerUiState,
    isAmbientModeEnabled: Boolean = false,
    mediaDisplay: @Composable () -> Unit = {
        if (isAmbientModeEnabled) {
            AmbientMediaInfoDisplay(playerUiState.media, loading = false)
        } else {
            AnimatedMediaInfoDisplay(playerUiState.media, loading = false)
        }
    },
    controlButtons: @Composable () -> Unit = {
        if (isAmbientModeEnabled) {
            AmbientMediaControlButtons(
                playerUiState = playerUiState,
                onPlayButtonClick = { },
                onPauseButtonClick = { },
                onSeekToPreviousButtonClick = { },
                onSeekToNextButtonClick = { },
            )
        } else {
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
        }
    },
    buttons: @Composable () -> Unit = {
        val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        top = screenWidthDp * 0.012f,
                        start = screenWidthDp * 0.145f,
                        end = screenWidthDp * 0.145f,
                    ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                VolumeButtonWithBadge(
                    volumeUiState = VolumeUiState(5, 10),
                    audioOutputUi = if (playerUiState.connected) {
                        AudioOutput.BluetoothHeadset(id = "id", name = "name")
                    } else {
                        AudioOutput.None
                    }.toAudioOutputUi(),
                    onOutputClick = { },
                    enabled = playerUiState.connected,
                    alignment = Alignment.TopCenter,
                    buttonColors = if (isAmbientModeEnabled) {
                        SettingsButtonDefaults.ambientButtonColors()
                    } else {
                        SettingsButtonDefaults.buttonColors()
                    },
                    border = if (isAmbientModeEnabled) {
                        SettingsButtonDefaults.ambientButtonBorder(playerUiState.connected)
                    } else {
                        null
                    },
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            ) {
                SettingsButton(
                    onClick = { },
                    enabled = playerUiState.connected,
                    imageVector = Icons.Rounded.MoreVert,
                    alignment = Alignment.TopCenter,
                    contentDescription = "More Actions",
                    buttonColors = if (isAmbientModeEnabled) {
                        SettingsButtonDefaults.ambientButtonColors()
                    } else {
                        SettingsButtonDefaults.buttonColors()
                    },
                    border = if (isAmbientModeEnabled) {
                        SettingsButtonDefaults.ambientButtonBorder(playerUiState.connected)
                    } else {
                        null
                    },
                )
            }
        }
    },
) {
    ScreenScaffold(
        scrollIndicator = {
            VolumeLevelIndicator(
                volumeUiState = {
                    VolumeUiStateMapper.map(volumeState = VolumeState(6, 10))
                },
                displayIndicatorEvents = flowOf(),
            )
        },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            PlayerScreen(
                modifier = Modifier.fillMaxSize(),
                mediaDisplay = mediaDisplay,
                controlButtons = controlButtons,
                buttons = buttons,
                background = { ArtworkImageBackground(null) },
            )
        }
    }
}
