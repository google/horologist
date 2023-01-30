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
import com.google.android.horologist.media.data.ExperimentalHorologistMediaDataApi
import com.google.android.horologist.media.model.PlayerState

/**
 * Maps a [Media3 player][Player] into a [PlayerState].
 */
@ExperimentalHorologistMediaDataApi
public object PlayerStateMapper {
    public fun map(player: Player): PlayerState = when (player.playbackState) {
        Player.STATE_IDLE -> idleOrEnded(player)
        Player.STATE_ENDED -> idleOrEnded(player)
        Player.STATE_BUFFERING -> buffering(player)
        Player.STATE_READY -> ready(player)
        else -> throw IllegalArgumentException("Invalid media3 player state: ${player.playbackState}")
    }

    private fun idleOrEnded(player: Player) = if (player.currentTimeline.isEmpty) {
        PlayerState.Idle
    } else {
        PlayerState.Stopped
    }

    private fun buffering(player: Player) = if (player.playWhenReady) {
        PlayerState.Loading
    } else {
        PlayerState.Stopped
    }

    private fun ready(player: Player) = if (player.isPlaying) {
        PlayerState.Playing
    } else {
        buffering(player)
    }
}
