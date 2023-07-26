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

import com.google.android.horologist.media.ui.components.PodcastControlButtons
import com.google.android.horologist.media.ui.components.controls.SeekButtonIncrement
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import com.google.android.horologist.screenshots.ScreenshotTestRule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.ParameterizedRobolectricTestRunner.Parameters
import kotlin.time.Duration.Companion.seconds

@RunWith(ParameterizedRobolectricTestRunner::class)
class PodcastPlayerScreenTest(
    private val options: PodcastOptions
) : ScreenshotBaseTest(
    ScreenshotTestRule.screenshotTestRuleParams {
        screenTimeText = {}
        testLabel = options.toString().lowercase()
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
                title = "The power of types",
                subtitle = "Kotlinconf"
            ),
            trackPositionUiModel = TrackPositionUiModel.Actual(
                percent = 0.1f,
                position = 30.seconds,
                duration = 300.seconds
            ),
            connected = true
        )

        screenshotTestRule.setContent(takeScreenshot = true) {
            MediaPlayerTestCase(playerUiState = playerUiState, controlButtons = {
                PodcastControlButtons(
                    onPlayButtonClick = { },
                    onPauseButtonClick = { },
                    playPauseButtonEnabled = playerUiState.playPauseEnabled,
                    playing = playerUiState.playing,
                    trackPositionUiModel = playerUiState.trackPositionUiModel,
                    onSeekBackButtonClick = { },
                    seekBackButtonEnabled = playerUiState.seekBackEnabled,
                    onSeekForwardButtonClick = { },
                    seekForwardButtonEnabled = playerUiState.seekForwardEnabled,
                    seekBackButtonIncrement = options.seekBackButtonIncrement,
                    seekForwardButtonIncrement = options.seekForwardButtonIncrement
                )
            })
        }
    }

    companion object {
        @JvmStatic
        @Parameters
        fun options(): List<PodcastOptions> = listOf(
            PodcastOptions(SeekButtonIncrement.Unknown, SeekButtonIncrement.Unknown),
            PodcastOptions(SeekButtonIncrement.Ten, SeekButtonIncrement.Ten),
            PodcastOptions(SeekButtonIncrement.Five, SeekButtonIncrement.Thirty)
        )
    }

    data class PodcastOptions(
        val seekBackButtonIncrement: SeekButtonIncrement,
        val seekForwardButtonIncrement: SeekButtonIncrement
    ) {
        override fun toString(): String {
            return "${seekBackButtonIncrement}_$seekForwardButtonIncrement"
        }
    }
}
