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

@file:OptIn(ExperimentalHorologistMediaDataApi::class)

package com.google.android.horologist.media.data.mapper

import androidx.media3.common.C
import com.google.android.horologist.media.data.ExperimentalHorologistMediaDataApi
import com.google.android.horologist.media.model.PlaybackState
import com.google.android.horologist.test.toolbox.testdoubles.FakeStatePlayer
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

class PlaybackStateMapperTest {
    private val fakeStatePlayer = FakeStatePlayer()
    private var elapsedRealtime = 123L
    private val playbackStateMapper = PlaybackStateMapper { elapsedRealtime }

    @Test
    fun `check position calculations null`() {
        val position = playbackStateMapper.map(null)
        assertThat(position).isNull()
    }

    @Test
    fun `check position calculation unset results in null position and duration`() {
        fakeStatePlayer.overridePosition(
            currentPosition = 10L,
            duration = C.TIME_UNSET
        )
        val position = playbackStateMapper.map(fakeStatePlayer)
        assertThat(position.currentPosition).isNull()
        assertThat(position.duration).isNull()
    }

    @Test
    fun `check position calculation invalid results in null position and duration`() {
        fakeStatePlayer.overridePosition(
            currentPosition = 10L,
            duration = -500L
        )
        val position = playbackStateMapper.map(fakeStatePlayer)
        assertThat(position.currentPosition).isNull()
        assertThat(position.duration).isNull()
    }

    @Test
    fun `check position calculations past end`() {
        fakeStatePlayer.overridePosition(
            currentPosition = 100L,
            duration = 99L
        )
        val position = playbackStateMapper.map(fakeStatePlayer)
        assertThat(position.currentPosition).isEqualTo(100.milliseconds)
        assertThat(position.duration).isEqualTo(100.milliseconds)
    }

    @Test
    fun `check position calculations during`() {
        fakeStatePlayer.overridePosition(
            currentPosition = 100L,
            duration = 1000L
        )
        fakeStatePlayer.overridePlaybackSpeed(2f)
        val position = playbackStateMapper.map(fakeStatePlayer)
        assertThat(position.currentPosition).isEqualTo(100.milliseconds)
        assertThat(position.duration).isEqualTo(1000.milliseconds)
        assertThat(position.elapsedRealtimeWhenCreated).isEqualTo(elapsedRealtime.milliseconds)
        assertThat(position.playbackSpeed).isEqualTo(2f)
        assertThat(position.isLive).isEqualTo(false)
    }

    @Test
    fun `check null Player results in idle state`() {
        val position = playbackStateMapper.map(null)
        assertThat(position).isEqualTo(PlaybackState.IDLE)
    }
}
