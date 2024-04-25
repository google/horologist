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

import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.screenshots.rng.WearDevice
import com.google.android.horologist.screenshots.rng.WearDeviceScreenshotTest
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import com.google.android.horologist.media.ui.uamp.UampColors
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.ParameterizedRobolectricTestRunner.Parameters
import kotlin.time.Duration.Companion.seconds

@RunWith(ParameterizedRobolectricTestRunner::class)
class MediaPlayerFontScaleTest(device: WearDevice) : WearDeviceScreenshotTest(device) {

    @Test
    fun mediaPlayerLargeRound() {
        mediaPlayerScreen()
    }

    @Test
    fun mediaPlayerScreen() {
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
                position = 30.seconds,
                duration = 225.seconds,
                percent = 0.133f,
            ),
            connected = true,
        )

        runTest {
            MediaPlayerTestCase(
                colors = UampColors,
                playerUiState = playerUiState,
            )
        }
    }

    companion object {
        @JvmStatic
        @Parameters
        fun devices() = listOf(

    WearDevice(
        id = "small_round",
        modelName = "Generic Small Round",
        screenSizePx = 384,
        density = 2.0f,
    ),
    WearDevice(
        id = "large_round",
        modelName = "Generic Large Round",
        screenSizePx = 454,
        density = 2.0f,
    ),
        )

    }
}
