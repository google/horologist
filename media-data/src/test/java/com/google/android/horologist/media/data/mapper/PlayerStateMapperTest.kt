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

import androidx.media3.common.Player
import com.google.android.horologist.media.data.ExperimentalHorologistMediaDataApi
import com.google.android.horologist.media.model.PlayerState
import com.google.android.horologist.test.toolbox.testdoubles.FakeStatePlayer
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PlayerStateMapperTest {
    val fakeStatePlayer = FakeStatePlayer()

    @Test
    fun `check playback state while playing`() {
        fakeStatePlayer.overrideState(
            playbackState = Player.STATE_READY,
            playWhenReady = true
        )
        val state = PlayerStateMapper.map(fakeStatePlayer)

        assertThat(state).isEqualTo(PlayerState.Playing)
    }

    @Test
    fun `check playback state while buffering`() {
        fakeStatePlayer.overrideState(
            playbackState = Player.STATE_BUFFERING,
            playWhenReady = true
        )
        val state = PlayerStateMapper.map(fakeStatePlayer)

        assertThat(state).isEqualTo(PlayerState.Playing)
    }
}
