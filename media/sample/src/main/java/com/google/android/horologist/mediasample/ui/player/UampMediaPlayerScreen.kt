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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.media.ui.components.PodcastControlButtons
import com.google.android.horologist.media.ui.components.animated.AnimatedMediaControlButtons
import com.google.android.horologist.media.ui.components.animated.AnimatedMediaInfoDisplay
import com.google.android.horologist.media.ui.components.background.rememberArtworkColor
import com.google.android.horologist.media.ui.components.background.rememberArtworkColorBrush
import com.google.android.horologist.media.ui.screens.player.DefaultMediaInfoDisplay
import com.google.android.horologist.media.ui.screens.player.DefaultPlayerScreenControlButtons
import com.google.android.horologist.media.ui.screens.player.PlayerScreen
import com.google.android.horologist.media.ui.state.PlayerUiController
import com.google.android.horologist.media.ui.state.PlayerUiState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

@Composable
fun UampMediaPlayerScreen(
    mediaPlayerScreenViewModel: MediaPlayerScreenViewModel,
    volumeViewModel: VolumeViewModel,
    onVolumeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val volumeUiState by volumeViewModel.volumeUiState.collectAsStateWithLifecycle()
    val settingsState by mediaPlayerScreenViewModel.settingsState.collectAsStateWithLifecycle()
    val playerState by mediaPlayerScreenViewModel.playerUiState.collectAsStateWithLifecycle()

    val artworkColor = rememberArtworkColor(playerState.media?.artworkUri)
    val background = rememberArtworkColorBrush(artworkColor = artworkColor.value)

    PlayerScreen(
        modifier = modifier.drawWithContent {
            // Clear the circular region so we have transparent pixels to blend against
            // This enables us to reuse the underlying buffer we are drawing into without
            // having to consume additional overhead of an offscreen compositing layer
            drawRect(color = Color.Black, blendMode = BlendMode.Clear)

            drawContent()

            // Draw background in the gaps, to allow for transparent marquee
            drawRect(background.value, blendMode = BlendMode.DstOver)
        },
        playerViewModel = mediaPlayerScreenViewModel,
        volumeViewModel = volumeViewModel,
        mediaDisplay = { playerUiState ->
            if (settingsState.animated) {
                AnimatedMediaInfoDisplay(
                    media = playerUiState.media,
                    loading = !playerUiState.connected || playerUiState.media?.loading == true,
                    modifier = modifier
                )
            } else {
                DefaultMediaInfoDisplay(playerUiState)
            }
        },
        buttons = {
            UampSettingsButtons(
                volumeUiState = volumeUiState,
                onVolumeClick = onVolumeClick,
                enabled = it.connected && it.media != null
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
                        trackPositionUiModel = playerUiState.trackPositionUiModel
                    )
                } else {
                    DefaultPlayerScreenControlButtons(playerUiController, playerUiState)
                }
            }
        }
    )

    ReportDrawnAfter {
        mediaPlayerScreenViewModel.playerState.filterNotNull().first()
    }
}

@Composable
public fun PlayerScreenPodcastControlButtons(
    playerUiController: PlayerUiController,
    playerUiState: PlayerUiState
) {
    PodcastControlButtons(
        playerController = playerUiController,
        playerUiState = playerUiState
    )
}
