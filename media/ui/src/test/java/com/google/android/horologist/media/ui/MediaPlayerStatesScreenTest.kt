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

package com.google.android.horologist.media.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import com.google.android.horologist.screenshots.rng.WearLegacyScreenTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.ParameterizedRobolectricTestRunner.Parameters
import kotlin.time.Duration.Companion.seconds

@RunWith(ParameterizedRobolectricTestRunner::class)
class MediaPlayerStatesScreenTest(
    private val state: State,
) : WearLegacyScreenTest() {

    override fun testName(suffix: String): String {
        return "src/test/snapshots/images/" +
            "${javaClass.`package`?.name}_${javaClass.simpleName}_${testInfo.methodName}_" +
            "${state.name.lowercase()}.png"
    }

    @Test
    fun mediaPlayerScreen() {
        val playerUiState = PlayerUiState(
            playEnabled = state.connected,
            pauseEnabled = state.connected,
            seekBackEnabled = state.connected,
            seekForwardEnabled = state.connected,
            seekInCurrentMediaItemEnabled = state.connected,
            seekToPreviousEnabled = false,
            seekToNextEnabled = state.connected,
            shuffleEnabled = false,
            shuffleOn = false,
            playPauseEnabled = state.connected,
            playing = state.connected,
            media = if (state.media) {
                MediaUiModel.Ready(
                    id = "",
                    title = "Weather with You",
                    subtitle = "Crowded House",
                )
            } else {
                null
            },
            trackPositionUiModel = if (state.media) {
                TrackPositionUiModel.Actual(
                    percent = 0.133f,
                    position = 30.seconds,
                    duration = 225.seconds,
                )
            } else {
                TrackPositionUiModel.Actual.ZERO
            },
            connected = state.connected,
        )

        runTest {
            Box(modifier = Modifier.background(Color.Black)) {
                MediaPlayerTestCase(playerUiState = playerUiState)
            }
        }
    }

    data class State(
        val connected: Boolean,
        val media: Boolean,
        val name: String,
    )

    companion object {
        @JvmStatic
        @Parameters
        fun states() = listOf(
            State(connected = true, media = false, name = "NoMedia"),
            State(connected = false, media = false, name = "NotConnected"),
        )
    }
}
