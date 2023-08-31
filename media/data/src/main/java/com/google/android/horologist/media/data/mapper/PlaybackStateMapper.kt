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

import android.os.SystemClock
import androidx.media3.common.C
import androidx.media3.common.Player
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.model.PlaybackState
import com.google.android.horologist.media.model.PlaybackStateEvent
import com.google.android.horologist.media.model.PlayerState
import com.google.android.horologist.media.model.TimestampProvider
import kotlin.time.Duration.Companion.milliseconds

/**
 * Maps a [Media3 player][Player] position into a [PlaybackState].
 */
@ExperimentalHorologistApi
public class PlaybackStateMapper(private val timestampProvider: TimestampProvider = TimestampProvider { SystemClock.elapsedRealtime() }) {
    public fun createEvent(player: Player?, cause: PlaybackStateEvent.Cause): PlaybackStateEvent =
        PlaybackStateEvent(
            playbackState = map(player),
            cause = cause,
            timestamp = timestampProvider.getTimestamp().milliseconds,
        )

    // should only be mapped as an event
    internal fun map(player: Player?): PlaybackState {
        if (player == null) {
            return PlaybackState.IDLE
        }
        val playbackSpeed = player.playbackParameters.speed
        val isLive = player.isCurrentMediaItemLive && player.isCurrentMediaItemDynamic
        val playerState = PlayerStateMapper.map(player)
        return if (
            player.currentMediaItem == null ||
            player.duration == C.TIME_UNSET ||
            player.duration <= 0L ||
            player.currentPosition < 0L ||
            playerState == PlayerState.Idle
        ) {
            PlaybackState(
                playerState = playerState,
                currentPosition = null,
                duration = null,
                playbackSpeed = playbackSpeed,
                isLive = isLive,
            )
        } else {
            PlaybackState(
                playerState = playerState,
                currentPosition = player.currentPosition.milliseconds,
                duration = (player.duration.coerceAtLeast(player.currentPosition)).milliseconds,
                playbackSpeed = playbackSpeed,
                isLive = isLive,
            )
        }
    }
}
