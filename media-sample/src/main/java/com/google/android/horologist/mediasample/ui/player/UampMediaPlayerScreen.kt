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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.media.ui.components.PodcastControlButtons
import com.google.android.horologist.media.ui.components.animated.AnimatedMediaControlButtons
import com.google.android.horologist.media.ui.components.animated.AnimatedPlayerScreenMediaDisplay
import com.google.android.horologist.media.ui.components.background.ArtworkColorBackground
import com.google.android.horologist.media.ui.screens.player.DefaultPlayerScreenControlButtons
import com.google.android.horologist.media.ui.screens.player.DefaultPlayerScreenMediaDisplay
import com.google.android.horologist.media.ui.screens.player.PlayerScreen
import com.google.android.horologist.media.ui.state.PlayerUiController
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.mediasample.ui.util.ReportFullyDrawn

@Composable
fun UampMediaPlayerScreen(
    mediaPlayerScreenViewModel: MediaPlayerScreenViewModel,
    volumeViewModel: VolumeViewModel,
    onVolumeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val volumeState by volumeViewModel.volumeState.collectAsStateWithLifecycle()
    val settingsState by mediaPlayerScreenViewModel.settingsState.collectAsStateWithLifecycle()

    PlayerScreen(
        modifier = modifier,
        playerViewModel = mediaPlayerScreenViewModel,
        mediaDisplay = { playerUiState ->
            if (settingsState.animated) {
                AnimatedPlayerScreenMediaDisplay(playerUiState)
            } else {
                DefaultPlayerScreenMediaDisplay(playerUiState)
            }
        },
        buttons = {
            UampSettingsButtons(
                volumeState = volumeState,
                onVolumeClick = onVolumeClick,
                enabled = it.connected
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
                        seekToPreviousButtonEnabled = playerUiState.seekToPreviousEnabled,
                        onSeekToNextButtonClick = { playerUiController.skipToNextMedia() },
                        seekToNextButtonEnabled = playerUiState.seekToNextEnabled,
                        percent = playerUiState.trackPosition?.percent ?: 0f,
                        showProgress = playerUiState.trackPosition?.showProgress ?: false,
                        animateProgress = true
                    )
                } else {
                    DefaultPlayerScreenControlButtons(playerUiController, playerUiState)
                }
            }
        },
        background = {
            val artworkUri = it.media?.artworkUri
            ArtworkColorBackground(
                artworkUri = artworkUri,
                defaultColor = MaterialTheme.colors.primary
            )
        }
    )

    val player by mediaPlayerScreenViewModel.playerState.collectAsStateWithLifecycle()
    if (player != null) {
        ReportFullyDrawn()
    }
}

@Composable
public fun PlayerScreenPodcastControlButtons(
    playerUiController: PlayerUiController,
    playerUiState: PlayerUiState
) {
    PodcastControlButtons(
        onPlayButtonClick = { playerUiController.play() },
        onPauseButtonClick = { playerUiController.pause() },
        playPauseButtonEnabled = playerUiState.playPauseEnabled,
        playing = playerUiState.playing,
        percent = playerUiState.trackPosition?.percent ?: 0f,
        showProgress = playerUiState.trackPosition?.showProgress ?: false,
        onSeekBackButtonClick = { playerUiController.seekBack() },
        seekBackButtonEnabled = playerUiState.seekBackEnabled,
        onSeekForwardButtonClick = { playerUiController.seekForward() },
        seekForwardButtonEnabled = playerUiState.seekForwardEnabled,
        seekBackButtonIncrement = playerUiState.seekBackButtonIncrement,
        seekForwardButtonIncrement = playerUiState.seekForwardButtonIncrement
    )
}
