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

@file:OptIn(ExperimentalHorologistMediaUiApi::class, ExperimentalHorologistComposeToolsApi::class)

package com.google.android.horologist.media.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.google.android.horologist.compose.tools.ExperimentalHorologistComposeToolsApi
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import com.google.android.horologist.media.ui.uamp.UampColors
import com.google.android.horologist.paparazzi.GALAXY_WATCH4_CLASSIC_LARGE
import com.google.android.horologist.paparazzi.WEAR_OS_SMALL_ROUND
import com.google.android.horologist.paparazzi.WEAR_OS_SQUARE
import com.google.android.horologist.paparazzi.WearSnapshotHandler
import com.google.android.horologist.paparazzi.determineHandler
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class MediaPlayerDeviceScreenTest(
    private val device: DeviceConfig
) {
    private val maxPercentDifference = 0.1

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = device,
        theme = "android:ThemeOverlay.Material.Dark",
        maxPercentDifference = maxPercentDifference,
        snapshotHandler = if (device == WEAR_OS_SQUARE) {
            determineHandler(maxPercentDifference)
        } else {
            WearSnapshotHandler(determineHandler(maxPercentDifference))
        }
    )

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
                artist = "Crowded House"
            ),
            trackPosition = TrackPositionUiModel(current = 30, duration = 225, percent = 0.133f),
            connected = true
        )

        paparazzi.snapshot {
            Box(modifier = Modifier.background(Color.Black)) {
                MediaPlayerTestCase(colors = UampColors, playerUiState = playerUiState)
            }
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun devices() = listOf(GALAXY_WATCH4_CLASSIC_LARGE, WEAR_OS_SMALL_ROUND, WEAR_OS_SQUARE)
    }
}
