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

@file:OptIn(ExperimentalHorologistApi::class)

package com.google.android.horologist.media.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import com.google.android.horologist.test.toolbox.testdoubles.hasProgressBar
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class MediaControlButtonsWithProgressTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenIsPlaying_thenPauseButtonIsDisplayed() {
        // given
        val playing = true

        composeTestRule.setContent {
            MediaControlButtons(
                onPlayButtonClick = {},
                onPauseButtonClick = {},
                playPauseButtonEnabled = true,
                playing = playing,
                onSeekToPreviousButtonClick = {},
                seekToPreviousButtonEnabled = true,
                onSeekToNextButtonClick = {},
                seekToNextButtonEnabled = true,
                trackPositionUiModel = TrackPositionUiModel.Actual(0.25f, 100.seconds, 25.seconds)
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
            MediaControlButtons(
                onPlayButtonClick = {},
                onPauseButtonClick = { clicked = true },
                playPauseButtonEnabled = true,
                playing = playing,
                onSeekToPreviousButtonClick = {},
                seekToPreviousButtonEnabled = true,
                onSeekToNextButtonClick = {},
                seekToNextButtonEnabled = true,
                trackPositionUiModel = TrackPositionUiModel.Actual(0.25f, 100.seconds, 25.seconds)
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
            MediaControlButtons(
                onPlayButtonClick = {},
                onPauseButtonClick = {},
                playPauseButtonEnabled = true,
                playing = playing,
                onSeekToPreviousButtonClick = {},
                seekToPreviousButtonEnabled = true,
                onSeekToNextButtonClick = {},
                seekToNextButtonEnabled = true,
                trackPositionUiModel = TrackPositionUiModel.Actual(0.25f, 100.seconds, 25.seconds)
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
            MediaControlButtons(
                onPlayButtonClick = { clicked = true },
                onPauseButtonClick = {},
                playPauseButtonEnabled = true,
                playing = playing,
                onSeekToPreviousButtonClick = {},
                seekToPreviousButtonEnabled = true,
                onSeekToNextButtonClick = {},
                seekToNextButtonEnabled = true,
                trackPositionUiModel = TrackPositionUiModel.Actual(0.25f, 100.seconds, 25.seconds)
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
    fun whenSeekToPreviousIsClicked_thenCorrectEventIsTriggered() {
        // given
        var clicked = false

        composeTestRule.setContent {
            MediaControlButtons(
                onPlayButtonClick = {},
                onPauseButtonClick = {},
                playPauseButtonEnabled = true,
                playing = false,
                onSeekToPreviousButtonClick = { clicked = true },
                seekToPreviousButtonEnabled = true,
                onSeekToNextButtonClick = {},
                seekToNextButtonEnabled = true,
                trackPositionUiModel = TrackPositionUiModel.Actual(0.25f, 100.seconds, 25.seconds)
            )
        }

        // when
        composeTestRule.onNodeWithContentDescription("Previous")
            .performClick()

        // then
        // assert that the click event was assigned to the correct button
        composeTestRule.waitUntil(timeoutMillis = 1_000) { clicked }
    }

    @Test
    fun whenSeekToNextIsClicked_thenCorrectEventIsTriggered() {
        // given
        var clicked = false

        composeTestRule.setContent {
            MediaControlButtons(
                onPlayButtonClick = {},
                onPauseButtonClick = {},
                playPauseButtonEnabled = true,
                playing = false,
                onSeekToPreviousButtonClick = {},
                seekToPreviousButtonEnabled = true,
                onSeekToNextButtonClick = { clicked = true },
                seekToNextButtonEnabled = true,
                trackPositionUiModel = TrackPositionUiModel.Actual(0.25f, 100.seconds, 25.seconds)
            )
        }

        // when
        composeTestRule.onNodeWithContentDescription("Next")
            .performClick()

        // then
        // assert that the click event was assigned to the correct button
        composeTestRule.waitUntil(timeoutMillis = 1_000) { clicked }
    }

    @Test
    fun givenPercentParam_thenProgressBarIsDisplayed() {
        // given
        composeTestRule.setContent {
            MediaControlButtons(
                onPlayButtonClick = {},
                onPauseButtonClick = {},
                playPauseButtonEnabled = true,
                playing = false,
                onSeekToPreviousButtonClick = {},
                seekToPreviousButtonEnabled = true,
                onSeekToNextButtonClick = {},
                seekToNextButtonEnabled = true,
                trackPositionUiModel = TrackPositionUiModel.Actual(0.25f, 100.seconds, 25.seconds)
            )
        }

        // then
        composeTestRule.onNode(com.google.android.horologist.test.toolbox.testdoubles.hasProgressBar())
            .assertIsDisplayed()
    }

    @Test
    fun givenIsPlayingAndPlayPauseEnabledIsTrue_thenPauseButtonIsEnabled() {
        // given
        val playing = true
        val playPauseButtonEnabled = true

        composeTestRule.setContent {
            MediaControlButtons(
                onPlayButtonClick = {},
                onPauseButtonClick = {},
                playPauseButtonEnabled = playPauseButtonEnabled,
                playing = playing,
                onSeekToPreviousButtonClick = {},
                seekToPreviousButtonEnabled = false,
                onSeekToNextButtonClick = {},
                seekToNextButtonEnabled = false,
                trackPositionUiModel = TrackPositionUiModel.Actual(0.25f, 100.seconds, 25.seconds)
            )
        }

        // then
        composeTestRule.onNodeWithContentDescription("Pause")
            .assertIsEnabled()

        composeTestRule.onNodeWithContentDescription("Previous")
            .assertIsNotEnabled()
        composeTestRule.onNodeWithContentDescription("Next")
            .assertIsNotEnabled()
    }

    @Test
    fun givenIsNOTPlayingAndPlayPauseEnabledIsTrue_thenPlayButtonIsEnabled() {
        // given
        val playing = false
        val playPauseButtonEnabled = true

        composeTestRule.setContent {
            MediaControlButtons(
                onPlayButtonClick = {},
                onPauseButtonClick = {},
                playPauseButtonEnabled = playPauseButtonEnabled,
                playing = playing,
                onSeekToPreviousButtonClick = {},
                seekToPreviousButtonEnabled = false,
                onSeekToNextButtonClick = {},
                seekToNextButtonEnabled = false,
                trackPositionUiModel = TrackPositionUiModel.Actual(0.25f, 100.seconds, 25.seconds)
            )
        }

        // then
        composeTestRule.onNodeWithContentDescription("Play")
            .assertIsEnabled()

        composeTestRule.onNodeWithContentDescription("Previous")
            .assertIsNotEnabled()
        composeTestRule.onNodeWithContentDescription("Next")
            .assertIsNotEnabled()
    }

    @Test
    fun givenSeekToPreviousButtonEnabledIsTrue_thenSeekToPreviousButtonIsEnabled() {
        // given
        val seekToPreviousButtonEnabled = true

        composeTestRule.setContent {
            MediaControlButtons(
                onPlayButtonClick = {},
                onPauseButtonClick = {},
                playPauseButtonEnabled = false,
                playing = false,
                onSeekToPreviousButtonClick = {},
                seekToPreviousButtonEnabled = seekToPreviousButtonEnabled,
                onSeekToNextButtonClick = {},
                seekToNextButtonEnabled = false,
                trackPositionUiModel = TrackPositionUiModel.Actual(0.25f, 100.seconds, 25.seconds)
            )
        }

        // then
        composeTestRule.onNodeWithContentDescription("Previous")
            .assertIsEnabled()

        composeTestRule.onNodeWithContentDescription("Play")
            .assertIsNotEnabled()
        composeTestRule.onNodeWithContentDescription("Next")
            .assertIsNotEnabled()
    }

    @Test
    fun givenSeekToNextButtonEnabledIsTrue_thenSeekToNextButtonIsEnabled() {
        // given
        val seekToNextButtonEnabled = true

        composeTestRule.setContent {
            MediaControlButtons(
                onPlayButtonClick = {},
                onPauseButtonClick = {},
                playPauseButtonEnabled = false,
                playing = false,
                onSeekToPreviousButtonClick = {},
                seekToPreviousButtonEnabled = false,
                onSeekToNextButtonClick = {},
                seekToNextButtonEnabled = seekToNextButtonEnabled,
                trackPositionUiModel = TrackPositionUiModel.Actual(0.25f, 100.seconds, 25.seconds)
            )
        }

        // then
        composeTestRule.onNodeWithContentDescription("Next")
            .assertIsEnabled()

        composeTestRule.onNodeWithContentDescription("Previous")
            .assertIsNotEnabled()
        composeTestRule.onNodeWithContentDescription("Play")
            .assertIsNotEnabled()
    }
}
