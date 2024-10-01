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
import com.google.android.horologist.images.base.paintable.DrawableResPaintable
import com.google.android.horologist.logo.R
import com.google.android.horologist.media.ui.components.animated.AnimatedMediaControlButtons
import com.google.android.horologist.media.ui.components.animated.AnimatedMediaInfoDisplay
import com.google.android.horologist.media.ui.components.background.ArtworkColorBackground
import com.google.android.horologist.media.ui.screens.player.PlayerScreen
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import com.google.android.horologist.mediasample.ui.app.UampTheme
import com.google.android.horologist.screenshots.rng.WearDevice
import com.google.android.horologist.screenshots.rng.WearDeviceScreenshotTest
import org.junit.Test

class UampNothingPlayerScreenshotTest(device: WearDevice) :
    WearDeviceScreenshotTest(device = device) {

    @Test
    fun nothingPlayerScreen() = runTest {
        val volumeUiState = VolumeUiState(current = 1)

        val audioOutput = AudioOutput.BluetoothHeadset(
            id = "bt0",
            name = "BT_Headphone",
        )

        UampTheme {
            PlayerScreen(
                mediaDisplay = {
                    AnimatedMediaInfoDisplay(
                        media = null,
                        loading = false,
                        appIcon = DrawableResPaintable(R.drawable.ic_horologist_monochrome),
                    )
                },
                controlButtons = {
                    AnimatedMediaControlButtons(
                        onPlayButtonClick = { },
                        onPauseButtonClick = { },
                        playPauseButtonEnabled = false,
                        playing = false,
                        onSeekToPreviousButtonClick = { },
                        seekToPreviousButtonEnabled = false,
                        onSeekToNextButtonClick = { },
                        seekToNextButtonEnabled = false,
                        trackPositionUiModel = TrackPositionUiModel.Actual.ZERO,
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
