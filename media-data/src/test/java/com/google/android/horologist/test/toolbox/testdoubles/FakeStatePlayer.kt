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

package com.google.android.horologist.test.toolbox.testdoubles

import androidx.media3.common.AdPlaybackState
import androidx.media3.common.C
import androidx.media3.common.FlagSet
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.common.Timeline
import androidx.media3.test.utils.FakeTimeline
import androidx.media3.test.utils.StubPlayer
import com.google.common.collect.ImmutableList

class FakeStatePlayer(
    var _currentPosition: Long = 0L,
    var _duration: Long = C.TIME_UNSET,
    var _playbackState: Int = STATE_IDLE,
    var _playWhenReady: Boolean = false,
    var _currentMediaItem: MediaItem? = null,
    var _playbackSpeed: Float = 1f
) : StubPlayer() {
    private val listeners = mutableListOf<Listener>()

    override fun addListener(listener: Listener) {
        listeners.add(listener)
        super.addListener(listener)
    }

    override fun getCurrentPosition(): Long = _currentPosition

    override fun getDuration(): Long {
        return _duration
    }

    override fun getPlaybackState(): Int = _playbackState

    override fun setPlayWhenReady(playWhenReady: Boolean) {
        _playWhenReady = playWhenReady
    }

    override fun getPlayWhenReady(): Boolean = _playWhenReady

    override fun isLoading(): Boolean = false

    override fun getCurrentTimeline(): Timeline {
        val currentMediaItem = _currentMediaItem
        if (currentMediaItem == null) {
            return FakeTimeline(0)
        } else {
            return FakeTimeline(
                FakeTimeline.TimelineWindowDefinition(
                    /* periodCount = */ 1,
                    /* id = */ 1,
                    /* isSeekable = */ true,
                    /* isDynamic = */ false,
                    /* isLive = */ false,
                    /* isPlaceholder = */ false,
                    /* durationUs = */ 1000 * C.MICROS_PER_SECOND,
                    /* defaultPositionUs = */ 2 * C.MICROS_PER_SECOND,
                    /* windowOffsetInFirstPeriodUs = */ 123456789,
                    ImmutableList.of(AdPlaybackState.NONE),
                    currentMediaItem
                )
            )
        }
    }

    override fun getCurrentMediaItemIndex(): Int = 0

    override fun getPlaybackSuppressionReason(): Int = PLAYBACK_SUPPRESSION_REASON_NONE

    override fun getPlaybackParameters(): PlaybackParameters = PlaybackParameters(_playbackSpeed)

    fun overridePosition(
        currentPosition: Long = 0L,
        duration: Long = C.TIME_UNSET,
        currentMediaItem: MediaItem? = MediaItem.EMPTY
    ) {
        _currentPosition = currentPosition
        _duration = duration
        _currentMediaItem = currentMediaItem

        for (it in listeners) {
            it.onEvents(
                this,
                Player.Events(
                    FlagSet.Builder().addAll(
                        EVENT_MEDIA_ITEM_TRANSITION,
                        EVENT_TIMELINE_CHANGED,
                        EVENT_MEDIA_METADATA_CHANGED
                    ).build()
                )
            )
        }
    }

    fun overridePlaybackSpeed(
        playbackSpeed: Float
    ) {
        _playbackSpeed = playbackSpeed
        for (it in listeners) {
            it.onEvents(
                this,
                Player.Events(FlagSet.Builder().add(EVENT_PLAYBACK_PARAMETERS_CHANGED).build())
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

        for (it in listeners) {
            it.onEvents(
                this,
                Player.Events(
                    FlagSet.Builder().addAll(
                        EVENT_PLAYBACK_STATE_CHANGED,
                        EVENT_PLAY_WHEN_READY_CHANGED,
                        EVENT_IS_PLAYING_CHANGED,
                        EVENT_MEDIA_ITEM_TRANSITION
                    ).build()
                )
            )
            it.onPlayWhenReadyChanged(_playWhenReady, reason)
            it.onPlaybackStateChanged(_playbackState)
            it.onIsPlayingChanged(isPlaying)
        }
    }
}
