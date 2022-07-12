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

package com.google.android.horologist.media.data

import androidx.media3.common.Player
import com.google.android.horologist.media.model.PlayerState
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PlayerStateMapperTest {
    val fakeStatePlayer = FakeStatePlayer()

    @Test
    fun `check playback state while playing`() {
        val state = mapPlayerState(
            playbackState = Player.STATE_READY,
            playWhenReady = true
        )

        assertThat(state).isEqualTo(PlayerState.Playing)
    }

    @Test
    fun `check playback state while buffering`() {
        val state = mapPlayerState(
            playbackState = Player.STATE_BUFFERING,
            playWhenReady = true
        )

        assertThat(state).isEqualTo(PlayerState.Playing)
    }

    private fun mapPlayerState(playbackState: Int, playWhenReady: Boolean): PlayerState {
        fakeStatePlayer.overrideState(
            playbackState = playbackState,
            playWhenReady = playWhenReady
        )
        return PlayerStateMapper.map(fakeStatePlayer)
    }
}
