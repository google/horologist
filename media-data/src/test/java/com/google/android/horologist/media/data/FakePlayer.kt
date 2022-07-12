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

import androidx.media3.common.C
import androidx.media3.common.FlagSet
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.test.utils.StubPlayer

class FakePlayer(
    var _currentPosition: Long = 0L,
    var _duration: Long = C.TIME_UNSET,
    var _playbackState: Int = STATE_IDLE,
    var _playWhenReady: Boolean = false
) : StubPlayer() {
    private val listeners = mutableListOf<Listener>()

    override fun addListener(listener: Player.Listener) {
        listeners.add(listener)
        super.addListener(listener)
    }

    override fun getCurrentPosition(): Long {
        return _currentPosition
    }

    override fun getDuration(): Long {
        return _duration
    }

    override fun getPlaybackState(): Int {
        return _playbackState
    }

    override fun setPlayWhenReady(playWhenReady: Boolean) {
        _playWhenReady = playWhenReady
    }

    override fun getPlayWhenReady(): Boolean {
        return _playWhenReady
    }

    fun overridePosition(
        currentPosition: Long = 0L,
        duration: Long = C.TIME_UNSET,
    ) {
        _currentPosition = currentPosition
        _duration = duration

        listeners.forEach {
            it.onEvents(
                this,
                Player.Events(FlagSet.Builder().add(EVENT_MEDIA_ITEM_TRANSITION).build())
            )
        }
    }

    fun overrideState(
        playbackState: Int = STATE_IDLE,
        playWhenReady: Boolean = false,
        reason: @Player.PlayWhenReadyChangeReason Int = PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST
    ) {
        _playbackState = playbackState
        _playWhenReady = playWhenReady

        listeners.forEach {
            it.onEvents(
                this,
                Player.Events(
                    FlagSet.Builder()
                        .addAll(EVENT_PLAYBACK_STATE_CHANGED, EVENT_PLAY_WHEN_READY_CHANGED).build()
                )
            )
            it.onPlayWhenReadyChanged(_playWhenReady, reason)
            it.onPlaybackStateChanged(_playbackState)
        }
    }
}
