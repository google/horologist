/*
 * Copyright 2023 The Android Open Source Project
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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import com.google.android.horologist.media.model.MediaPositionPredictor
import com.google.android.horologist.media.model.TimestampProvider
import com.google.android.horologist.media.ui.state.LocalTimestampProvider
import com.google.android.horologist.media.ui.state.ProgressStateHolder
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@Ignore("XXX")
class ProgressStateHolderTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenPredictiveProgress_usesTimestampForInitialValue() = runTest {
        // given
        val predictor = MediaPositionPredictor(
            eventTimestamp = 500,
            durationMs = 1000,
            currentPositionMs = 100,
            positionSpeed = 1f,
        )
        val trackPositionUiModel = TrackPositionUiModel.Predictive(predictor)
        val timestamp = 600L
        val progressStateHolder = setContentWithResult({ timestamp }) {
            ProgressStateHolder.fromTrackPositionUiModel(trackPositionUiModel = trackPositionUiModel)
        }

        // then
        assertThat(progressStateHolder.value).isEqualTo(0.2f)
    }

    @Test
    fun givenPredictiveProgress_predictsProgress() = runTest {
        // given
        val timestampProvider = TimestampProvider { composeTestRule.mainClock.currentTime }
        val predictor = MediaPositionPredictor(
            eventTimestamp = timestampProvider.getTimestamp(),
            durationMs = 1000,
            currentPositionMs = 100,
            positionSpeed = 1f,
        )
        val trackPositionUiModel = TrackPositionUiModel.Predictive(predictor)
        composeTestRule.mainClock.autoAdvance = false
        val progressStateHolder = setContentWithResult(timestampProvider) {
            ProgressStateHolder.fromTrackPositionUiModel(trackPositionUiModel = trackPositionUiModel)
        }

        // then
        assertThat(progressStateHolder.value).isEqualTo(0.1f)
        composeTestRule.mainClock.advanceTimeBy(200, ignoreFrameDuration = false)
        // check range because clock is not fully precise
        assertThat(progressStateHolder.value).isGreaterThan(0.29f)
        assertThat(progressStateHolder.value).isAtMost(0.3f)
    }

    private suspend fun <T> setContentWithResult(
        timestampProvider: TimestampProvider,
        block: @Composable () -> T,
    ): T {
        val result = CompletableDeferred<T>()
        composeTestRule.setContent {
            CompositionLocalProvider(LocalTimestampProvider provides timestampProvider) {
                result.complete(block())
            }
        }
        return result.await()
    }
}
