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
import com.android.resources.ScreenRound
import com.google.android.horologist.compose.tools.ExperimentalHorologistComposeToolsApi
import com.google.android.horologist.compose.tools.a11y.ComposeA11yExtension
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import com.google.android.horologist.media.ui.uamp.UampColors
import com.google.android.horologist.paparazzi.ExperimentalHorologistPaparazziApi
import com.google.android.horologist.paparazzi.WearPaparazzi
import com.google.android.horologist.paparazzi.WearSnapshotHandler
import com.google.android.horologist.paparazzi.a11y.A11ySnapshotHandler
import com.google.android.horologist.paparazzi.determineHandler
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class MediaPlayerA11yScreenshotTest(
    private val device: DeviceConfig
) {
    private val maxPercentDifference = 1.0

    val composeA11yExtension = ComposeA11yExtension()

    @get:Rule
    val paparazzi = WearPaparazzi(
        deviceConfig = device.copy(screenRound = ScreenRound.NOTROUND),
        maxPercentDifference = maxPercentDifference,
        renderExtensions = setOf(composeA11yExtension),
        snapshotHandler = WearSnapshotHandler(
            A11ySnapshotHandler(
                delegate = determineHandler(
                    maxPercentDifference = maxPercentDifference
                ),
                accessibilityStateFn = { composeA11yExtension.accessibilityState }
            ),
            round = device != DeviceConfig.WEAR_OS_SQUARE
        )
    )

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
            trackPosition = TrackPositionUiModel(current = 30, duration = 225, percent = 0.133f, showProgress = true),
            connected = true
        )

        paparazzi.snapshot {
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
