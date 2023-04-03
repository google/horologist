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

@file:OptIn(
    ExperimentalHorologistApi::class
)

package com.google.android.horologist.media.ui

import app.cash.paparazzi.DeviceConfig
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import com.google.android.horologist.media.ui.uamp.UampColors
import com.google.android.horologist.screenshots.ScreenshotTest
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class MediaPlayerA11yScreenshotTest(): ScreenshotTest() {

    @Test
    fun mediaPlayer() {
        val playerUiState = PlayerUiState(
            playEnabled = true,
            pauseEnabled = true,
            seekBackEnabled = true,
            seekForwardEnabled = true,
            seekToPreviousEnabled = false,
            seekToNextEnabled = true,
            shuffleEnabled = false,
            shuffleOn = false,
            playPauseEnabled = true,
            playing = true,
            media = MediaUiModel(
                id = "",
                title = "Weather with You",
                subtitle = "Crowded House"
            ),
            trackPositionUiModel = TrackPositionUiModel.Actual(percent = 0.133f, position = 30.seconds, duration = 225.seconds),
            connected = true
        )

        takeScreenshot {
            MediaPlayerTestCase(
                colors = UampColors,
                playerUiState = playerUiState,
                round = device != DeviceConfig.WEAR_OS_SQUARE
            )
        }
    }
}
