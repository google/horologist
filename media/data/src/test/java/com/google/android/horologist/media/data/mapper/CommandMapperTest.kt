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
import org.junit.Assert
import org.junit.Test

class CommandMapperTest {

    @Test
    fun `given COMMAND_PLAY_PAUSE then maps correctly`() {
        // given
        val command = Player.COMMAND_PLAY_PAUSE

        // when
        val result = CommandMapper.map(command)

        // then
        assertThat(result).isEqualTo(Command.PlayPause)
    }

    @Test
    fun `given COMMAND_SEEK_BACK then maps correctly`() {
        // given
        val command = Player.COMMAND_SEEK_BACK

        // when
        val result = CommandMapper.map(command)

        // then
        assertThat(result).isEqualTo(Command.SeekBack)
    }

    @Test
    fun `given COMMAND_SEEK_FORWARD then maps correctly`() {
        // given
        val command = Player.COMMAND_SEEK_FORWARD

        // when
        val result = CommandMapper.map(command)

        // then
        assertThat(result).isEqualTo(Command.SeekForward)
    }

    @Test
    fun `given COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM then maps correctly`() {
        // given
        val command = Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM

        // when
        val result = CommandMapper.map(command)

        // then
        assertThat(result).isEqualTo(Command.SkipToPreviousMedia)
    }

    @Test
    fun `given COMMAND_SEEK_TO_NEXT_MEDIA_ITEM then maps correctly`() {
        // given
        val command = Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM

        // when
        val result = CommandMapper.map(command)

        // then
        assertThat(result).isEqualTo(Command.SkipToNextMedia)
    }

    @Test
    fun `given COMMAND_SET_SHUFFLE_MODE then maps correctly`() {
        // given
        val command = Player.COMMAND_SET_SHUFFLE_MODE

        // when
        val result = CommandMapper.map(command)

        // then
        assertThat(result).isEqualTo(Command.SetShuffle)
    }

    @Test
    fun `given an invalid command then exception is thrown`() {
        // given
        val command = Player.COMMAND_INVALID

        // when
        val whenBlock = { CommandMapper.map(command) }

        // then
        Assert.assertThrows(IllegalArgumentException::class.java) { whenBlock() }
    }

    @Test
    fun `given an unmapped command then exception is thrown`() {
        // given
        val command = Player.COMMAND_ADJUST_DEVICE_VOLUME_WITH_FLAGS

        // when
        val whenBlock = { CommandMapper.map(command) }

        // then
        Assert.assertThrows(IllegalArgumentException::class.java) { whenBlock() }
    }
}
