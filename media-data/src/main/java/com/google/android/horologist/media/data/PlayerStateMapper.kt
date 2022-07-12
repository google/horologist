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

/**
 * Maps a [Media3 player][Player] into a [PlayerState].
 */
public object PlayerStateMapper {
    public fun map(player: Player): PlayerState {
        return if ((
            player.playbackState == Player.STATE_BUFFERING ||
                player.playbackState == Player.STATE_READY
            ) &&
            player.playWhenReady
        ) {
            PlayerState.Playing
        } else if (player.isLoading) {
            PlayerState.Loading
        } else {
            map(player.playbackState)
        }
    }

    private fun map(@Player.State media3PlayerState: Int): PlayerState = when (media3PlayerState) {
        Player.STATE_IDLE -> PlayerState.Idle
        Player.STATE_BUFFERING -> PlayerState.Loading
        Player.STATE_READY -> PlayerState.Ready
        Player.STATE_ENDED -> PlayerState.Ended
        else -> throw IllegalArgumentException("Invalid media3 player state: $media3PlayerState")
    }

    public fun affectsState(events: Player.Events): Boolean {
        return events.containsAny(
            Player.EVENT_IS_LOADING_CHANGED,
            Player.EVENT_IS_PLAYING_CHANGED,
            Player.EVENT_PLAYBACK_STATE_CHANGED,
            Player.EVENT_PLAY_WHEN_READY_CHANGED,
        )
    }
}
