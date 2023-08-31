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

package com.google.android.horologist.media.data.mapper

import androidx.media3.common.C
import androidx.media3.common.Player
import com.google.android.horologist.media.model.PlaybackState
import com.google.android.horologist.media.model.PlaybackStateEvent
import com.google.android.horologist.test.toolbox.testdoubles.FakeStatePlayer
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

class PlaybackStateMapperTest {
    private val fakeStatePlayer = FakeStatePlayer()
    private var elapsedRealtime = 123L
    private val playbackStateMapper = PlaybackStateMapper { elapsedRealtime }

    @Test
    fun `check position calculations idle`() {
        val playbackState = playbackStateMapper.map(null)
        assertThat(playbackState).isEqualTo(PlaybackState.IDLE)
    }

    @Test
    fun `check position calculation unset results in null position and duration`() {
        fakeStatePlayer.overridePosition(
            currentPosition = 10L,
            duration = C.TIME_UNSET,
        )
        val playbackState = playbackStateMapper.map(fakeStatePlayer)
        assertThat(playbackState.currentPosition).isNull()
        assertThat(playbackState.duration).isNull()
    }

    @Test
    fun `check position calculation invalid results in null position and duration`() {
        fakeStatePlayer.overridePosition(
            currentPosition = 10L,
            duration = -500L,
        )
        val playbackState = playbackStateMapper.map(fakeStatePlayer)
        assertThat(playbackState.currentPosition).isNull()
        assertThat(playbackState.duration).isNull()
    }

    @Test
    fun `check position calculations past end`() {
        fakeStatePlayer.overrideState(
            Player.STATE_READY,
        )
        fakeStatePlayer.overridePosition(
            currentPosition = 100L,
            duration = 99L,
        )
        val playbackState = playbackStateMapper.map(fakeStatePlayer)
        assertThat(playbackState.currentPosition).isEqualTo(100.milliseconds)
        assertThat(playbackState.duration).isEqualTo(100.milliseconds)
    }

    @Test
    fun `check position calculations during`() {
        fakeStatePlayer.overrideState(
            Player.STATE_READY,
        )
        fakeStatePlayer.overridePosition(
            currentPosition = 100L,
            duration = 1000L,
        )
        fakeStatePlayer.overridePlaybackSpeed(2f)
        val playbackState = playbackStateMapper.map(fakeStatePlayer)
        assertThat(playbackState.currentPosition).isEqualTo(100.milliseconds)
        assertThat(playbackState.duration).isEqualTo(1000.milliseconds)
        assertThat(playbackState.playbackSpeed).isEqualTo(2f)
        assertThat(playbackState.isLive).isEqualTo(false)
    }

    @Test
    fun `check event timestamp`() {
        val event = playbackStateMapper.createEvent(fakeStatePlayer, PlaybackStateEvent.Cause.Other)
        assertThat(event.timestamp).isEqualTo(elapsedRealtime.milliseconds)
        assertThat(event.cause).isEqualTo(PlaybackStateEvent.Cause.Other)
        assertThat(event.playbackState).isEqualTo(playbackStateMapper.map(fakeStatePlayer))
    }

    @Test
    fun `check null Player results in idle state`() {
        val playbackState = playbackStateMapper.map(null)
        assertThat(playbackState).isEqualTo(PlaybackState.IDLE)
    }
}
