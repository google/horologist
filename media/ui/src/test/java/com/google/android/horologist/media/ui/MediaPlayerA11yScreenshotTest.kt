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

import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers.WearOSSmallRound
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers.WearOSSquare
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import com.google.android.horologist.media.ui.uamp.UampColors
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import com.google.android.horologist.screenshots.ScreenshotTestRule.Companion.screenshotTestRuleParams
import org.junit.Test
import org.robolectric.annotation.Config
import kotlin.time.Duration.Companion.seconds

class MediaPlayerA11yScreenshotTest : ScreenshotBaseTest(
    screenshotTestRuleParams {
        enableA11y = true
    }
) {

    @Test
    fun mediaPlayerLargeRound() {
        mediaPlayerScreen()
    }

    @Config(
        sdk = [30],
        qualifiers = WearOSSmallRound
    )
    @Test
    fun mediaPlayerSmallRound() {
        mediaPlayerScreen()
    }

    @Config(
        sdk = [30],
        qualifiers = WearOSSquare
    )
    @Test
    fun mediaPlayerSquare() {
        mediaPlayerScreen()
    }

    private fun mediaPlayerScreen() {
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
                percent = 0.133f,
                position = 30.seconds,
                duration = 225.seconds
            ),
            connected = true
        )

        screenshotTestRule.setContent(takeScreenshot = true) {
            MediaPlayerTestCase(
                colors = UampColors,
                playerUiState = playerUiState
            )
        }
    }
}
