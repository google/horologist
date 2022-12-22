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
    ExperimentalHorologistMediaUiApi::class,
    ExperimentalHorologistComposeToolsApi::class,
    ExperimentalHorologistPaparazziApi::class
)

package com.google.android.horologist.media.ui

import app.cash.paparazzi.DeviceConfig
import com.google.android.horologist.compose.tools.ExperimentalHorologistComposeToolsApi
import com.google.android.horologist.compose.tools.snapshotInABox
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import com.google.android.horologist.media.ui.uamp.UampColors
import com.google.android.horologist.paparazzi.ExperimentalHorologistPaparazziApi
import com.google.android.horologist.paparazzi.WearPaparazzi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.time.Duration.Companion.seconds

@RunWith(Parameterized::class)
class MediaPlayerDeviceScreenTest(
    private val device: DeviceConfig
) {
    @get:Rule
    val paparazzi = WearPaparazzi(deviceConfig = device)

    @Test
    fun mediaPlayerScreen() {
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
            trackPositionUiModel = TrackPositionUiModel.Actual(
                position = 30.seconds,
                duration = 225.seconds,
                percent = 0.133f
            ),
            connected = true
        )

        paparazzi.snapshotInABox {
            MediaPlayerTestCase(
                colors = UampColors,
                playerUiState = playerUiState,
                round = device != DeviceConfig.WEAR_OS_SQUARE
            )
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun devices() = listOf(
            DeviceConfig.GALAXY_WATCH4_CLASSIC_LARGE,
            DeviceConfig.WEAR_OS_SMALL_ROUND,
            DeviceConfig.WEAR_OS_SQUARE
        )
    }
}
