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

@file:OptIn(ExperimentalHorologistMediaUiApi::class)

package com.google.android.horologist.media.ui.components

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.filters.FlakyTest
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.components.controls.SeekButtonIncrement
import org.junit.Rule
import org.junit.Test

@FlakyTest(detail = "https://github.com/google/horologist/issues/407")
class PodcastControlButtonsWithProgressTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenIsPlaying_thenPauseButtonIsDisplayed() {
        // given
        val playing = true

        composeTestRule.setContent {
            PodcastControlButtons(
                onPlayButtonClick = {},
                onPauseButtonClick = {},
                playPauseButtonEnabled = true,
                playing = playing,
                percent = 0.25f,
                onSeekBackButtonClick = {},
                seekBackButtonEnabled = true,
                onSeekForwardButtonClick = {},
                seekForwardButtonEnabled = true
            )
        }

        // then
        composeTestRule.onNodeWithContentDescription("Pause")
            .assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Play")
            .assertDoesNotExist()
    }

    @Test
    fun givenIsPlaying_whenPauseIsClicked_thenCorrectEventIsTriggered() {
        // given
        val playing = true
        var clicked = false

        composeTestRule.setContent {
            PodcastControlButtons(
                onPlayButtonClick = {},
                onPauseButtonClick = { clicked = true },
                playPauseButtonEnabled = true,
                playing = playing,
                percent = 0.25f,
                onSeekBackButtonClick = {},
                seekBackButtonEnabled = true,
                onSeekForwardButtonClick = {},
                seekForwardButtonEnabled = true
            )
        }

        // when
        composeTestRule.onNodeWithContentDescription("Pause")
            .performClick()

        // then
        // assert that the click event was assigned to the correct button
        composeTestRule.waitUntil(timeoutMillis = 1_000) { clicked }
    }

    @Test
    fun givenIsNOTPlaying_thenPlayButtonIsDisplayed() {
        // given
        val playing = false

        composeTestRule.setContent {
            PodcastControlButtons(
                onPlayButtonClick = {},
                onPauseButtonClick = {},
                playPauseButtonEnabled = true,
                playing = playing,
                percent = 0.25f,
                onSeekBackButtonClick = {},
                seekBackButtonEnabled = true,
                onSeekForwardButtonClick = {},
                seekForwardButtonEnabled = true
            )
        }

        // then
        composeTestRule.onNodeWithContentDescription("Play")
            .assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Pause")
            .assertDoesNotExist()
    }

    @Test
    fun givenIsNOTPlaying_whenPlayIsClicked_thenCorrectEventIsTriggered() {
        // given
        val playing = false
        var clicked = false

        composeTestRule.setContent {
            PodcastControlButtons(
                onPlayButtonClick = { clicked = true },
                onPauseButtonClick = {},
                playPauseButtonEnabled = true,
                playing = playing,
                percent = 0.25f,
                onSeekBackButtonClick = {},
                seekBackButtonEnabled = true,
                onSeekForwardButtonClick = {},
                seekForwardButtonEnabled = true
            )
        }

        // when
        composeTestRule.onNodeWithContentDescription("Play")
            .performClick()

        // then
        // assert that the click event was assigned to the correct button
        composeTestRule.waitUntil(timeoutMillis = 1_000) { clicked }
    }

    @Test
    fun whenSeekBackIsClicked_thenCorrectEventIsTriggered() {
        // given
        var clicked = false

        composeTestRule.setContent {
            PodcastControlButtons(
                onPlayButtonClick = {},
                onPauseButtonClick = {},
                playPauseButtonEnabled = true,
                playing = false,
                percent = 0.25f,
                onSeekBackButtonClick = { clicked = true },
                seekBackButtonEnabled = true,
                onSeekForwardButtonClick = {},
                seekForwardButtonEnabled = true
            )
        }

        // when
        composeTestRule.onNodeWithContentDescription("Rewind")
            .performClick()

        // then
        // assert that the click event was assigned to the correct button
        composeTestRule.waitUntil(timeoutMillis = 1_000) { clicked }
    }

    @Test
    fun whenSeekForwardIsClicked_thenCorrectEventIsTriggered() {
        // given
        var clicked = false

        composeTestRule.setContent {
            PodcastControlButtons(
                onPlayButtonClick = {},
                onPauseButtonClick = {},
                playPauseButtonEnabled = true,
                playing = false,
                percent = 0.25f,
                onSeekBackButtonClick = {},
                seekBackButtonEnabled = true,
                onSeekForwardButtonClick = { clicked = true },
                seekForwardButtonEnabled = true
            )
        }

        // when
        composeTestRule.onNodeWithContentDescription("Forward")
            .performClick()

        // then
        // assert that the click event was assigned to the correct button
        composeTestRule.waitUntil(timeoutMillis = 1_000) { clicked }
    }

    @Test
    fun givenPercentParam_thenProgressBarIsDisplayed() {
        // given
        val percent = 0.25f

        composeTestRule.setContent {
            PodcastControlButtons(
                onPlayButtonClick = {},
                onPauseButtonClick = {},
                playPauseButtonEnabled = true,
                playing = false,
                percent = percent,
                onSeekBackButtonClick = {},
                seekBackButtonEnabled = true,
                onSeekForwardButtonClick = {},
                seekForwardButtonEnabled = true
            )
        }

        // then
        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo(percent, 0.0f..1.0f)))
            .assertIsDisplayed()
    }

    @Test
    fun givenIsPlayingAndPlayPauseEnabledIsTrue_thenPauseButtonIsEnabled() {
        // given
        val playing = true
        val playPauseButtonEnabled = true

        composeTestRule.setContent {
            PodcastControlButtons(
                onPlayButtonClick = {},
                onPauseButtonClick = {},
                playPauseButtonEnabled = playPauseButtonEnabled,
                playing = playing,
                percent = 0.25f,
                onSeekBackButtonClick = {},
                seekBackButtonEnabled = false,
                onSeekForwardButtonClick = {},
                seekForwardButtonEnabled = false
            )
        }

        // then
        composeTestRule.onNodeWithContentDescription("Pause")
            .assertIsEnabled()

        composeTestRule.onNodeWithContentDescription("Rewind")
            .assertIsNotEnabled()
        composeTestRule.onNodeWithContentDescription("Forward")
            .assertIsNotEnabled()
    }

    @Test
    fun givenIsNOTPlayingAndPlayPauseEnabledIsTrue_thenPlayButtonIsEnabled() {
        // given
        val playing = false
        val playPauseButtonEnabled = true

        composeTestRule.setContent {
            PodcastControlButtons(
                onPlayButtonClick = {},
                onPauseButtonClick = {},
                playPauseButtonEnabled = playPauseButtonEnabled,
                playing = playing,
                percent = 0.25f,
                onSeekBackButtonClick = {},
                seekBackButtonEnabled = false,
                onSeekForwardButtonClick = {},
                seekForwardButtonEnabled = false
            )
        }

        // then
        composeTestRule.onNodeWithContentDescription("Play")
            .assertIsEnabled()

        composeTestRule.onNodeWithContentDescription("Rewind")
            .assertIsNotEnabled()
        composeTestRule.onNodeWithContentDescription("Forward")
            .assertIsNotEnabled()
    }

    @Test
    fun givenSeekBackButtonEnabledIsTrue_thenSeekBackButtonIsEnabled() {
        // given
        val seekBackButtonEnabled = true

        composeTestRule.setContent {
            PodcastControlButtons(
                onPlayButtonClick = {},
                onPauseButtonClick = {},
                playPauseButtonEnabled = false,
                playing = false,
                percent = 0.25f,
                onSeekBackButtonClick = {},
                seekBackButtonEnabled = seekBackButtonEnabled,
                onSeekForwardButtonClick = {},
                seekForwardButtonEnabled = false,
                seekBackButtonIncrement = SeekButtonIncrement.Unknown
            )
        }

        // then
        composeTestRule.onNodeWithContentDescription("Rewind")
            .assertIsEnabled()

        composeTestRule.onNodeWithContentDescription("Play")
            .assertIsNotEnabled()
        composeTestRule.onNodeWithContentDescription("Forward")
            .assertIsNotEnabled()
    }

    @Test
    fun givenSeekForwardButtonEnabledIsTrue_thenSeekForwardButtonIsEnabled() {
        // given
        val seekForwardButtonEnabled = true

        composeTestRule.setContent {
            PodcastControlButtons(
                onPlayButtonClick = {},
                onPauseButtonClick = {},
                playPauseButtonEnabled = false,
                playing = false,
                percent = 0.25f,
                onSeekBackButtonClick = {},
                seekBackButtonEnabled = false,
                onSeekForwardButtonClick = {},
                seekForwardButtonEnabled = seekForwardButtonEnabled
            )
        }

        // then
        composeTestRule.onNodeWithContentDescription("Forward")
            .assertIsEnabled()

        composeTestRule.onNodeWithContentDescription("Play")
            .assertIsNotEnabled()
        composeTestRule.onNodeWithContentDescription("Rewind")
            .assertIsNotEnabled()
    }

    @Test
    fun givenSeekBackIncrementIsFive_thenSeekBackDescriptionIsFive() {
        // given
        val seekBackButtonIncrement = SeekButtonIncrement.Five

        composeTestRule.setContent {
            PodcastControlButtons(
                onPlayButtonClick = {},
                onPauseButtonClick = {},
                playPauseButtonEnabled = false,
                playing = false,
                percent = 0.25f,
                onSeekBackButtonClick = {},
                seekBackButtonEnabled = false,
                onSeekForwardButtonClick = {},
                seekForwardButtonEnabled = false,
                seekBackButtonIncrement = seekBackButtonIncrement
            )
        }

        // then
        composeTestRule.onNodeWithContentDescription("Rewind 5 seconds")
            .assertExists()

        composeTestRule.onNodeWithContentDescription("Forward")
            .assertExists()
    }

    @Test
    fun givenSeekForwardIncrementIsFive_thenSeekForwardDescriptionIsFive() {
        // given
        val seekForwardButtonIncrement = SeekButtonIncrement.Five

        composeTestRule.setContent {
            PodcastControlButtons(
                onPlayButtonClick = {},
                onPauseButtonClick = {},
                playPauseButtonEnabled = false,
                playing = false,
                percent = 0.25f,
                onSeekBackButtonClick = {},
                seekBackButtonEnabled = false,
                onSeekForwardButtonClick = {},
                seekForwardButtonEnabled = false,
                seekForwardButtonIncrement = seekForwardButtonIncrement
            )
        }

        // then
        composeTestRule.onNodeWithContentDescription("Forward 5 seconds")
            .assertExists()

        composeTestRule.onNodeWithContentDescription("Rewind")
            .assertExists()
    }
}
