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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import com.google.android.horologist.audio.ui.VolumePositionIndicator
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.compose.layout.StateUtils.rememberStateWithLifecycle
import com.google.android.horologist.compose.navscaffold.scrollableColumn
import com.google.android.horologist.media.ui.components.PodcastControlButtons
import com.google.android.horologist.media.ui.components.animated.AnimatedMediaControlButtons
import com.google.android.horologist.media.ui.components.background.ArtworkColorBackground
import com.google.android.horologist.media.ui.components.background.ColorBackground
import com.google.android.horologist.media.ui.screens.DefaultPlayerScreenControlButtons
import com.google.android.horologist.media.ui.screens.DefaultPlayerScreenMediaDisplay
import com.google.android.horologist.media.ui.screens.PlayerScreen
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.media.ui.state.PlayerViewModel
import com.google.android.horologist.mediasample.domain.model.Settings
import com.google.android.horologist.mediasample.ui.components.AnimatedPlayerScreenMediaDisplay

@Composable
fun UampMediaPlayerScreen(
    mediaPlayerScreenViewModel: MediaPlayerScreenViewModel,
    volumeViewModel: VolumeViewModel,
    onVolumeClick: () -> Unit,
    playerFocusRequester: FocusRequester,
    modifier: Modifier = Modifier,
    settingsState: Settings?,
) {
    val volumeState by rememberStateWithLifecycle(flow = volumeViewModel.volumeState)

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .scrollableColumn(
                focusRequester = playerFocusRequester,
                volumeViewModel.volumeScrollableState
            ),
        positionIndicator = { VolumePositionIndicator(volumeState = { volumeState }) },
    ) {
        PlayerScreen(
            playerViewModel = mediaPlayerScreenViewModel,
            mediaDisplay = { playerUiState ->
                if (settingsState?.animated == true) {
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
            controlButtons = {
                if (settingsState?.podcastControls == true) {
                    PlayerScreenPodcastControlButtons(mediaPlayerScreenViewModel, it)
                } else {
                    if (settingsState?.animated == true) {
                        AnimatedMediaControlButtons(
                            onPlayButtonClick = { mediaPlayerScreenViewModel.play() },
                            onPauseButtonClick = { mediaPlayerScreenViewModel.pause() },
                            playPauseButtonEnabled = it.playPauseEnabled,
                            playing = it.playing,
                            onSeekToPreviousButtonClick = { mediaPlayerScreenViewModel.skipToPreviousMedia() },
                            seekToPreviousButtonEnabled = it.seekToPreviousEnabled,
                            onSeekToNextButtonClick = { mediaPlayerScreenViewModel.skipToNextMedia() },
                            seekToNextButtonEnabled = it.seekToNextEnabled,
                            percent = it.trackPosition?.percent ?: 0f,
                        )
                    } else {
                        DefaultPlayerScreenControlButtons(mediaPlayerScreenViewModel, it)
                    }
                }
            },
            background = {
                if (settingsState?.artworkGradient == true) {
                    val artworkUri = it.media?.artworkUri
                    ArtworkColorBackground(
                        artworkUri = artworkUri,
                        defaultColor = MaterialTheme.colors.primary
                    )
                } else {
                    ColorBackground(color = MaterialTheme.colors.primary)
                }
            }
        )
    }
}

@Composable
public fun PlayerScreenPodcastControlButtons(
    playerViewModel: PlayerViewModel,
    playerUiState: PlayerUiState,
) {
    PodcastControlButtons(
        onPlayButtonClick = { playerViewModel.play() },
        onPauseButtonClick = { playerViewModel.pause() },
        playPauseButtonEnabled = playerUiState.playPauseEnabled,
        playing = playerUiState.playing,
        percent = playerUiState.trackPosition?.percent ?: 0f,
        onSeekBackButtonClick = { playerViewModel.seekBack() },
        seekBackButtonEnabled = playerUiState.seekBackEnabled,
        onSeekForwardButtonClick = { playerViewModel.seekForward() },
        seekForwardButtonEnabled = playerUiState.seekForwardEnabled,
        seekBackButtonIncrement = playerUiState.seekBackButtonIncrement,
        seekForwardButtonIncrement = playerUiState.seekForwardButtonIncrement
    )
}
