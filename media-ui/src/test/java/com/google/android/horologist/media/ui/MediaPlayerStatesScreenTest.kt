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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.cash.paparazzi.Paparazzi
import com.google.android.horologist.compose.tools.ExperimentalHorologistComposeToolsApi
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import com.google.android.horologist.paparazzi.ExperimentalHorologistPaparazziApi
import com.google.android.horologist.paparazzi.GALAXY_WATCH4_CLASSIC_LARGE
import com.google.android.horologist.paparazzi.WearSnapshotHandler
import com.google.android.horologist.paparazzi.determineHandler
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class MediaPlayerStatesScreenTest(
    private val state: State
) {
    private val maxPercentDifference = 0.1

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = GALAXY_WATCH4_CLASSIC_LARGE,
        theme = "android:ThemeOverlay.Material.Dark",
        maxPercentDifference = maxPercentDifference,
        snapshotHandler = WearSnapshotHandler(determineHandler(maxPercentDifference))
    )

    @Test
    fun mediaPlayerScreen() {
        val playerUiState = PlayerUiState(
            playEnabled = state.connected,
            pauseEnabled = state.connected,
            seekBackEnabled = state.connected,
            seekForwardEnabled = state.connected,
            seekToPreviousEnabled = false,
            seekToNextEnabled = state.connected,
            shuffleEnabled = false,
            shuffleOn = false,
            playPauseEnabled = state.connected,
            playing = state.connected,
            media = if (state.media) {
                MediaUiModel(
                    id = "",
                    title = "Weather with You",
                    artist = "Crowded House"
                )
            } else {
                null
            },
            trackPosition = if (state.media) {
                TrackPositionUiModel(
                    current = 30,
                    duration = 225,
                    percent = 0.133f
                )
            } else {
                null
            },
            connected = state.connected
        )

        paparazzi.snapshot(name = state.name) {
            Box(modifier = Modifier.background(Color.Black)) {
                MediaPlayerTestCase(playerUiState = playerUiState)
            }
        }
    }

    data class State(
        val connected: Boolean,
        val media: Boolean,
        val name: String
    )

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun states() = listOf(
            State(connected = true, media = false, name = "NoMedia"),
            State(connected = false, media = false, name = "NotConnected")
        )
    }
}
