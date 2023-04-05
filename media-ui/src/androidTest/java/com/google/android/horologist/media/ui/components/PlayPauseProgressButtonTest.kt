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

import android.os.SystemClock
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.filters.FlakyTest
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.model.MediaPositionPredictor
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import com.google.android.horologist.test.toolbox.testdoubles.hasProgressBar
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

@FlakyTest(detail = "https://github.com/google/horologist/issues/407")
class PlayPauseProgressButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenIsPlaying_thenPauseButtonIsDisplayed() {
        // given
        var clicked = false
        composeTestRule.setContent {
            PlayPauseProgressButton(
                onPlayClick = {},
                onPauseClick = { clicked = true },
                enabled = true,
                playing = true,
                trackPositionUiModel = TrackPositionUiModel.Actual(0f, 100.seconds, 0.seconds)
            )
        }

        // then
        composeTestRule.onNode(com.google.android.horologist.test.toolbox.testdoubles.hasProgressBar())
            .assertExists()

        composeTestRule.onNode(hasAnyChild(hasContentDescription("Pause")))
            .assertIsDisplayed()
            .performClick()

        // assert that the click event was assigned to the correct button
        composeTestRule.waitUntil(timeoutMillis = 1_000) { clicked }

        composeTestRule.onNode(hasAnyChild(hasContentDescription("Play")))
            .assertDoesNotExist()
    }

    @Test
    fun givenIsNOTPlaying_thenPlayButtonIsDisplayed() {
        // given
        var clicked = false
        composeTestRule.setContent {
            PlayPauseProgressButton(
                onPlayClick = { clicked = true },
                onPauseClick = {},
                enabled = true,
                playing = false,
                trackPositionUiModel = TrackPositionUiModel.Actual(0f, 100.seconds, 0.seconds)
            )
        }

        // then
        composeTestRule.onNode(com.google.android.horologist.test.toolbox.testdoubles.hasProgressBar())
            .assertExists()

        composeTestRule.onNode(hasAnyChild(hasContentDescription("Play")))
            .assertIsDisplayed()
            .performClick()

        // assert that the click event was assigned to the correct button
        composeTestRule.waitUntil(timeoutMillis = 1_000) { clicked }

        composeTestRule.onNode(hasAnyChild(hasContentDescription("Pause")))
            .assertDoesNotExist()
    }

    @Test
    fun givenMediaProgress_thenProgressIsCorrect() {
        // given
        composeTestRule.setContent {
            PlayPauseProgressButton(
                onPlayClick = {},
                onPauseClick = {},
                enabled = true,
                playing = false,
                trackPositionUiModel = TrackPositionUiModel.Actual(0.5f, 50.seconds, 100.seconds)
            )
        }

        // then
        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo(0.5f, 0.0f..1.0f)))
            .assertIsDisplayed()
    }

    @Test
    fun givenPredictiveMediaProgress_thenProgressIsChanging() {
        // given
        composeTestRule.mainClock.autoAdvance = false
        val predictor = MediaPositionPredictor(
            eventTimestamp = SystemClock.elapsedRealtime(),
            currentPositionMs = 0,
            durationMs = 5_000,
            positionSpeed = 1f
        )
        composeTestRule.setContent {
            PlayPauseProgressButton(
                onPlayClick = {},
                onPauseClick = {},
                enabled = true,
                playing = false,
                trackPositionUiModel = TrackPositionUiModel.Predictive(predictor)
            )
        }

        // then
        composeTestRule.mainClock.advanceTimeBy(10_000)
        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo(1f, 0.0f..1.0f)))
            .assertIsDisplayed()
    }
}
