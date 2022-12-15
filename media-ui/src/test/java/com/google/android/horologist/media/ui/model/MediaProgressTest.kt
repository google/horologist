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

package com.google.android.horologist.media.ui.model

import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.state.model.MediaProgress
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MediaProgressTest {
    @Test
    fun `Predictive predicts percent`() {
        val underTest = MediaProgress.Predictive(
            currentPositionMs = 500,
            durationMs = 1000,
            playbackSpeed = 1f,
            isLive = false,
            elapsedRealtimeMs = 0
        )

        val predicted = underTest.predictPercent(250)

        assertThat(predicted).isEqualTo(0.75f)
    }

    @Test
    fun `Predictive takes speed into account`() {
        val underTest = MediaProgress.Predictive(
            currentPositionMs = 500,
            durationMs = 1000,
            playbackSpeed = 2f,
            isLive = false,
            elapsedRealtimeMs = 0
        )

        val predicted = underTest.predictPercent(200)

        assertThat(predicted).isEqualTo(0.9f)
    }

    @Test
    fun `Predictive takes live into account`() {
        val underTest = MediaProgress.Predictive(
            currentPositionMs = 500,
            durationMs = 1000,
            playbackSpeed = 2f,
            isLive = true,
            elapsedRealtimeMs = 0
        )

        val predicted = underTest.predictPercent(200)

        assertThat(predicted).isEqualTo(0.75f)
    }
}
