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
import com.google.android.horologist.compose.tools.ThemeValues
import com.google.android.horologist.compose.tools.themeValues
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import com.google.android.horologist.screenshots.ScreenshotTestRule.Companion.screenshotTestRuleParams
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.ParameterizedRobolectricTestRunner.Parameters
import kotlin.time.Duration.Companion.seconds

@RunWith(ParameterizedRobolectricTestRunner::class)
class MediaPlayerScreenTest(
    private val themeValue: ThemeValues
) : ScreenshotBaseTest(
    screenshotTestRuleParams {
        testLabel = themeValue.safeName.lowercase()
    }
) {

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
                percent = 0.1f,
                position = 30.seconds,
                duration = 300.seconds
            ),
            connected = true
        )

        screenshotTestRule.takeScreenshot {
            Box(modifier = Modifier.background(Color.Black)) {
                MediaPlayerTestCase(colors = themeValue.colors, playerUiState = playerUiState)
            }
        }
    }

    companion object {
        @JvmStatic
        @Parameters
        fun colors() = themeValues
    }
}
