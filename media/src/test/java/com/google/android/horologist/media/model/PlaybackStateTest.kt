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

package com.google.android.horologist.media.model

import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class PlaybackStateTest {
    private val elapsedMilliseconds = 123L

    @Test
    fun givenValidValues_returnsPositionPredictor_thatPredictsPosition() {
        // given
        val current = 1.seconds
        val duration = 2.seconds

        // when
        val result = eventWith(position = current, duration = duration)
        val predictor = result.createPositionPredictor()
        // then
        assertThat(predictor).isNotNull()
        assertThat(predictor!!.predictDuration(elapsedMilliseconds)).isEqualTo(duration)
        assertThat(predictor.predictPosition(elapsedMilliseconds)).isEqualTo(current)
        assertThat(predictor.predictPercent(elapsedMilliseconds)).isEqualTo(0.5f)
        assertThat(predictor.predictDuration(elapsedMilliseconds + 100)).isEqualTo(duration)
        assertThat(predictor.predictPosition(elapsedMilliseconds + 100)).isEqualTo(1100.milliseconds)
    }

    @Test
    fun givenLive_returnsPositionPredictor_thatPredictsPositionAndDuration() {
        // given
        val current = 1.seconds
        val duration = 2.seconds

        // when
        val result = eventWith(position = current, duration = duration, live = true)
        val predictor = result.createPositionPredictor()
        // then
        assertThat(predictor).isNotNull()
        assertThat(predictor!!.predictDuration(elapsedMilliseconds)).isEqualTo(duration)
        assertThat(predictor.predictPosition(elapsedMilliseconds)).isEqualTo(current)
        assertThat(predictor.predictPercent(elapsedMilliseconds)).isEqualTo(0.5f)
        assertThat(predictor.predictDuration(elapsedMilliseconds + 100)).isEqualTo(2100.milliseconds)
        assertThat(predictor.predictPosition(elapsedMilliseconds + 100)).isEqualTo(1100.milliseconds)
    }

    @Test
    fun given2x_returnsPositionPredictor_thatPredictsPosition() {
        // given
        val current = 1.seconds
        val duration = 2.seconds

        // when
        val result = eventWith(position = current, duration = duration, speed = 2f)
        val predictor = result.createPositionPredictor()
        // then
        assertThat(predictor).isNotNull()
        assertThat(predictor!!.predictDuration(elapsedMilliseconds + 100)).isEqualTo(duration)
        assertThat(predictor.predictPosition(elapsedMilliseconds + 100)).isEqualTo(1200.milliseconds)
    }

    @Test
    fun givenNoDuration_createPositionPredictor_returnsNull() {
        // when
        val result = eventWith(position = 1.milliseconds, duration = null)
        val predictor = result.createPositionPredictor()
        // then
        assertThat(predictor).isNull()
    }

    @Test
    fun givenNoPosition_createPositionPredictor_returnsNull() {
        // when
        val result = eventWith(position = null, duration = 1.milliseconds)
        val predictor = result.createPositionPredictor()
        // then
        assertThat(predictor).isNull()
    }

    private fun eventWith(position: Duration?, duration: Duration?, speed: Float = 1f, live: Boolean = false) =
        PlaybackStateEvent(playbackState = PlaybackState(playerState = PlayerState.Playing, isLive = live, currentPosition = position, duration = duration, playbackSpeed = speed), cause = PlaybackStateEvent.Cause.Other, timestamp = elapsedMilliseconds.milliseconds)
}
