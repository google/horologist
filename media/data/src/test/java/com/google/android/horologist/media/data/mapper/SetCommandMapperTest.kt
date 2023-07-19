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

import androidx.media3.common.Player
import com.google.android.horologist.media.model.Command
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SetCommandMapperTest {

    @Test
    fun `given empty commands then return empty`() {
        // given
        val commands = Player.Commands.EMPTY

        // when
        val result = SetCommandMapper.map(commands)

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `given valid commands then maps correctly`() {
        // given
        val commands = Player.Commands.Builder()
            .addAll(
                Player.COMMAND_PLAY_PAUSE,
                Player.COMMAND_SEEK_BACK,
                Player.COMMAND_SEEK_FORWARD,
                Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM,
                Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM,
                Player.COMMAND_SET_SHUFFLE_MODE,
            )
            .build()

        // when
        val result = SetCommandMapper.map(commands)

        // then
        assertThat(result).hasSize(6)
        assertThat(result).containsExactlyElementsIn(
            arrayOf(
                Command.PlayPause,
                Command.SeekBack,
                Command.SeekForward,
                Command.SkipToPreviousMedia,
                Command.SkipToNextMedia,
                Command.SetShuffle,
            ),
        )
    }

    @Test
    fun `given all commands including invalid ones then maps correctly`() {
        // given
        val commands = Player.Commands.Builder()
            .addAllCommands()
            .build()

        // when
        val result = SetCommandMapper.map(commands)

        // then
        assertThat(result).hasSize(6)
        assertThat(result).containsExactlyElementsIn(
            arrayOf(
                Command.PlayPause,
                Command.SeekBack,
                Command.SeekForward,
                Command.SkipToPreviousMedia,
                Command.SkipToNextMedia,
                Command.SetShuffle,
            ),
        )
    }
}
