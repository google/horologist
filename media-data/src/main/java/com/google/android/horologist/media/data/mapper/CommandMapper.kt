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
import androidx.media3.common.Player.COMMAND_PLAY_PAUSE
import androidx.media3.common.Player.COMMAND_SEEK_BACK
import androidx.media3.common.Player.COMMAND_SEEK_FORWARD
import androidx.media3.common.Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM
import androidx.media3.common.Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM
import androidx.media3.common.Player.COMMAND_SET_SHUFFLE_MODE
import com.google.android.horologist.media.model.Command

/**
 * Maps [Player.Command] into a [Command].
 */
public object CommandMapper {

    public fun map(@Player.Command command: Int): Command =
        when (command) {
            COMMAND_PLAY_PAUSE -> Command.PlayPause
            COMMAND_SEEK_BACK -> Command.SeekBack
            COMMAND_SEEK_FORWARD -> Command.SeekForward
            COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM -> Command.SkipToPreviousMediaItem
            COMMAND_SEEK_TO_NEXT_MEDIA_ITEM -> Command.SkipToNextMediaItem
            COMMAND_SET_SHUFFLE_MODE -> Command.SetShuffle
            else -> throw IllegalArgumentException("Invalid command: $command")
        }
}
