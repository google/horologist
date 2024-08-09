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

package com.google.android.horologist.mediasample.ui.player

import androidx.activity.compose.ReportDrawnAfter
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.audio.ui.components.toAudioOutputUi
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.audio.ui.components.toAudioOutputUi
import com.google.android.horologist.images.coil.CoilPaintable
import com.google.android.horologist.media.ui.components.PodcastControlButtons
import com.google.android.horologist.media.ui.components.animated.AnimatedMediaControlButtons
import com.google.android.horologist.media.ui.components.animated.AnimatedMediaInfoDisplay
import com.google.android.horologist.media.ui.components.background.ArtworkColorBackground
import com.google.android.horologist.media.ui.components.background.ColorBackground
import com.google.android.horologist.media.ui.screens.player.DefaultMediaInfoDisplay
import com.google.android.horologist.media.ui.screens.player.DefaultPlayerScreenControlButtons
import com.google.android.horologist.media.ui.screens.player.PlayerScreen
import com.google.android.horologist.media.ui.state.PlayerUiController
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

@Composable
fun UampMediaPlayerScreen(
    mediaPlayerScreenViewModel: MediaPlayerScreenViewModel,
    volumeViewModel: VolumeViewModel,
    onVolumeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val volumeUiState by volumeViewModel.volumeUiState.collectAsStateWithLifecycle()
    val audioOutput by volumeViewModel.audioOutput.collectAsStateWithLifecycle()
    val settingsState by mediaPlayerScreenViewModel.settingsState.collectAsStateWithLifecycle()

    PlayerScreen(
        modifier = modifier,
        background = {
            val artworkColor = (it.media as? MediaUiModel.Ready)?.artworkColor
            if (artworkColor != null) {
                ColorBackground(
                    color = artworkColor,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                ArtworkColorBackground(
                    paintable = (it.media as? MediaUiModel.Ready)?.artwork as? CoilPaintable,
                    defaultColor = MaterialTheme.colors.primary,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        },
        playerViewModel = mediaPlayerScreenViewModel,
        volumeViewModel = volumeViewModel,
        mediaDisplay = { playerUiState ->
            if (settingsState.animated) {
                AnimatedMediaInfoDisplay(
                    media = playerUiState.media,
                    loading = !playerUiState.connected || playerUiState.media is MediaUiModel.Loading,
                )
            } else {
                DefaultMediaInfoDisplay(playerUiState)
            }
        },
        buttons = { state ->
            UampSettingsButtons(
                volumeUiState = volumeUiState,
                audioOutputUi = audioOutput.toAudioOutputUi(),
                onVolumeClick = onVolumeClick,
                enabled = state.connected && state.media != null,
            )
        },
        controlButtons = { playerUiController, playerUiState ->
            if (settingsState.podcastControls) {
                PlayerScreenPodcastControlButtons(playerUiController, playerUiState)
            } else {
                if (settingsState.animated) {
                    AnimatedMediaControlButtons(
                        onPlayButtonClick = { playerUiController.play() },
                        onPauseButtonClick = { playerUiController.pause() },
                        playPauseButtonEnabled = playerUiState.playPauseEnabled,
                        playing = playerUiState.playing,
                        onSeekToPreviousButtonClick = { playerUiController.skipToPreviousMedia() },
                        onSeekToPreviousLongRepeatableClick = { playerUiController.seekBack() },
                        seekToPreviousButtonEnabled = playerUiState.seekToPreviousEnabled,
                        onSeekToNextButtonClick = { playerUiController.skipToNextMedia() },
                        onSeekToNextLongRepeatableClick = { playerUiController.seekForward() },
                        seekToNextButtonEnabled = playerUiState.seekToNextEnabled,
                        trackPositionUiModel = playerUiState.trackPositionUiModel,
                    )
                } else {
                    DefaultPlayerScreenControlButtons(playerUiController, playerUiState)
                }
            }
        },
    )

    ReportDrawnAfter {
        mediaPlayerScreenViewModel.playerState.filterNotNull().first()
    }
}

@Composable
public fun PlayerScreenPodcastControlButtons(
    playerUiController: PlayerUiController,
    playerUiState: PlayerUiState,
    modifier: Modifier = Modifier,
) {
    PodcastControlButtons(
        modifier = modifier,
        playerController = playerUiController,
        playerUiState = playerUiState,
    )
}
