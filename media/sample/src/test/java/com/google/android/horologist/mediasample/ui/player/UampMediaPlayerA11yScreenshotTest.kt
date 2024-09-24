/*
 * Copyright 2024 The Android Open Source Project
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
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.components.toAudioOutputUi
import com.google.android.horologist.media.ui.components.animated.AnimatedMediaControlButtons
import com.google.android.horologist.media.ui.components.animated.AnimatedMediaInfoDisplay
import com.google.android.horologist.media.ui.components.background.ArtworkColorBackground
import com.google.android.horologist.media.ui.screens.player.PlayerScreen
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import com.google.android.horologist.media.ui.uamp.UampTheme
import com.google.android.horologist.screenshots.rng.WearLegacyA11yTest
import org.junit.Test
import org.robolectric.annotation.Config
import kotlin.time.Duration.Companion.seconds

class UampMediaPlayerA11yScreenshotTest : WearLegacyA11yTest() {

    @Test
    fun mediaPlayerLargeRound() {
        uampMediaPlayerScreen()
    }

    @Config(
        qualifiers = "+w192dp-h192dp",
    )
    @Test
    fun mediaPlayerSmallRound() {
        uampMediaPlayerScreen()
    }

    private fun uampMediaPlayerScreen() {
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
            media = MediaUiModel.Ready(
                id = "",
                title = "Weather with You",
                subtitle = "Crowded House",
            ),
            trackPositionUiModel = TrackPositionUiModel.Actual(
                percent = 0.133f,
                position = 30.seconds,
                duration = 225.seconds,
            ),
            connected = true,
        )

        val volumeUiState = VolumeUiState(current = 1)

        val audioOutput = AudioOutput.BluetoothHeadset(
            id = "bt0",
            name = "BT_Headphone",
        )

        runScreenTest {
            UampTheme {
                PlayerScreen(
                    mediaDisplay = {
                        AnimatedMediaInfoDisplay(
                            media = playerUiState.media,
                            loading = !playerUiState.connected || playerUiState.media is MediaUiModel.Loading,
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
                        UampSettingsButtons(
                            volumeUiState = volumeUiState,
                            audioOutputUi = audioOutput.toAudioOutputUi(),
                            onVolumeClick = { },
                        )
                    },
                    background = {
                        ArtworkColorBackground(
                            paintable = null,
                            defaultColor = MaterialTheme.colors.primary,
                            modifier = Modifier.fillMaxSize(),
                        )
                    },
                )
            }
        }
    }
}
