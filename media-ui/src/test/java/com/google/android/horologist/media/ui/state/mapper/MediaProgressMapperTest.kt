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

package com.google.android.horologist.media.ui.state.mapper

import com.google.android.horologist.media.model.PlaybackState
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.state.model.MediaProgress
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class MediaProgressMapperTest {

    @Test
    fun givenMediaPosition_thenMapsCorrectly() {
        // given
        val current = 1.seconds
        val duration = 2.seconds
        val playbackState = PlaybackState(
            isPlaying = true,
            isLive = false,
            currentPosition = current,
            duration = duration,
            playbackSpeed = 1f,
            elapsedRealtimeWhenCreated = 0.toDuration(DurationUnit.SECONDS)
        )

        // when
        val result = MediaProgressMapper.map(playbackState)

        // then
        assertThat(result).isInstanceOf(MediaProgress.Predictive::class.java)
        result as MediaProgress.Predictive
        assertThat(result.predictPercent(0)).isEqualTo(0.5f)
        assertThat(result.predictPercent(duration.inWholeMilliseconds)).isEqualTo(1f)
    }

    @Test
    fun givenMediaPositionNotPlaying_thenMapsCorrectly() {
        // given
        val current = 1.seconds
        val duration = 2.seconds
        val playbackState = PlaybackState(
            isPlaying = false,
            isLive = false,
            currentPosition = current,
            duration = duration,
            playbackSpeed = 1f,
            elapsedRealtimeWhenCreated = 0.toDuration(DurationUnit.SECONDS)
        )

        // when
        val result = MediaProgressMapper.map(playbackState)

        // then
        assertThat(result).isInstanceOf(MediaProgress.Actual::class.java)
        result as MediaProgress.Actual
        assertThat(result.percent).isEqualTo(0.5f)
    }

    @Test
    fun givenUnknownMediaPosition_thenMapsCorrectly() {
        // given
        val playbackState = PlaybackState.IDLE

        // when
        val result = MediaProgressMapper.map(playbackState)

        // then
        assertThat(result).isInstanceOf(MediaProgress.Hidden::class.java)
        assertThat(result.showProgress).isEqualTo(false)
    }
}
