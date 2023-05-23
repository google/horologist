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

package com.google.android.horologist.mediasample.ui.player.benchmark

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.media.ui.components.animated.AnimatedMediaControlButtons
import com.google.android.horologist.media.ui.components.background.ArtworkColorBackground
import com.google.android.horologist.media.ui.components.display.TextMediaDisplay
import com.google.android.horologist.media.ui.screens.player.DefaultPlayerScreenControlButtons
import com.google.android.horologist.media.ui.screens.player.PlayerScreen
import com.google.android.horologist.mediasample.ui.player.MediaPlayerScreenViewModel
import com.google.android.horologist.mediasample.ui.player.UampSettingsButtons

@Composable
fun UampBenchmarkPlayerScreen(
    mediaPlayerScreenViewModel: MediaPlayerScreenViewModel,
    volumeViewModel: VolumeViewModel,
    onVolumeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val volumeUiState by volumeViewModel.volumeUiState.collectAsStateWithLifecycle()
    val playerUiState by mediaPlayerScreenViewModel.playerUiState.collectAsStateWithLifecycle()

    val config = PlayerScreenConfig.fromString(playerUiState.media?.subtitle)

    PlayerScreen(
        modifier = modifier,
        playerViewModel = mediaPlayerScreenViewModel,
        volumeViewModel = volumeViewModel,
        mediaDisplay = { _ ->
            when (config.header) {
                HeaderConfig.Marquee -> {
                    MarqueeTextMediaDisplay(
                        title = playerUiState.media?.title,
                        artist = playerUiState.media?.subtitle,
                        modifier = modifier,
                        offscreen = true
                    )
                }

                HeaderConfig.NonOffscreen -> {
                    MarqueeTextMediaDisplay(
                        title = playerUiState.media?.title,
                        artist = playerUiState.media?.subtitle,
                        modifier = modifier,
                        offscreen = false
                    )
                }

                HeaderConfig.NonAnimated -> {
                    TextMediaDisplay(
                        title = playerUiState.media?.title.orEmpty(),
                        subtitle = playerUiState.media?.subtitle.orEmpty(),
                        modifier = modifier
                    )
                }

                HeaderConfig.Hidden -> {}
            }
        },
        buttons = {
            when (config.settings) {
                SettingsConfig.Shown -> UampSettingsButtons(
                    volumeUiState = volumeUiState,
                    onVolumeClick = onVolumeClick,
                    enabled = it.connected && it.media != null
                )

                SettingsConfig.Hidden -> {}
            }
        },
        controlButtons = { playerUiController, _ ->
            when (config.buttons) {
                ButtonsConfig.Animated -> {
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
                }

                ButtonsConfig.NonAnimated -> {
                    DefaultPlayerScreenControlButtons(playerUiController, playerUiState)
                }

                ButtonsConfig.Hidden -> {}
            }
        },
        background = {
            when (config.background) {
                BackgroundConfig.Radial -> RadialBackground(
                    Color.Yellow.copy(alpha = 0.3f),
                    Color.Transparent
                )
                BackgroundConfig.Linear -> LinearBackground(
                    Color.Yellow.copy(alpha = 0.3f),
                    Color.Transparent
                )
                BackgroundConfig.Flat -> RadialBackground(
                    Color.DarkGray,
                    Color.Black
                )
                BackgroundConfig.Hidden -> {}
            }
        }
    )
}

data class PlayerScreenConfig(
    val header: HeaderConfig = HeaderConfig.Marquee,
    val buttons: ButtonsConfig = ButtonsConfig.Animated,
    val background: BackgroundConfig = BackgroundConfig.Radial,
    val settings: SettingsConfig = SettingsConfig.Shown
) {

    override fun toString(): String {
        return listOf(header, buttons, background, settings).joinToString("-")
    }

    companion object {
        fun fromString(s: String?): PlayerScreenConfig {
            return try {
                if (s != null) {
                    val (header, buttons, background) = s.split("-")
                    PlayerScreenConfig(
                        HeaderConfig.valueOf(header),
                        ButtonsConfig.valueOf(buttons),
                        BackgroundConfig.valueOf(background)
                    )
                } else {
                    PlayerScreenConfig()
                }
            } catch (iae: IllegalArgumentException) {
                PlayerScreenConfig()
            }
        }
    }
}

enum class ButtonsConfig {
    Animated, NonAnimated, Hidden
}

enum class BackgroundConfig {
    Radial, Linear, Flat, Hidden
}

enum class HeaderConfig {
    Marquee, NonAnimated, NonOffscreen, Hidden
}

enum class SettingsConfig {
    Shown, Hidden
}
