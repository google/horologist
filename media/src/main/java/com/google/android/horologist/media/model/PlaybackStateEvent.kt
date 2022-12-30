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

package com.google.android.horologist.media.model

import com.google.android.horologist.media.ExperimentalHorologistMediaApi
import kotlin.time.Duration

@ExperimentalHorologistMediaApi
public data class PlaybackStateEvent(
    public val playbackState: PlaybackState,
    public val cause: Cause,
    public val timestamp: Duration? = null
) {
    public enum class Cause {
        Initial,
        PlayerStateChanged,
        ParametersChanged,
        PositionDiscontinuity,
        Other
    }

    public fun createPositionPredictor(): PositionPredictor? {
        if (timestamp == null || playbackState.duration == null || playbackState.currentPosition == null) {
            return null
        }
        return if (playbackState.isLive) {
            LiveMediaPositionPredictor(
                eventTimestamp = timestamp.inWholeMilliseconds,
                durationMs = playbackState.duration.inWholeMilliseconds,
                currentPositionMs = playbackState.currentPosition.inWholeMilliseconds,
                positionSpeed = playbackState.playbackSpeed
            )
        } else {
            MediaPositionPredictor(
                eventTimestamp = timestamp.inWholeMilliseconds,
                durationMs = playbackState.duration.inWholeMilliseconds,
                currentPositionMs = playbackState.currentPosition.inWholeMilliseconds,
                positionSpeed = playbackState.playbackSpeed
            )
        }
    }

    public companion object {
        public val INITIAL: PlaybackStateEvent = PlaybackStateEvent(
            playbackState = PlaybackState.IDLE,
            cause = Cause.Initial,
            timestamp = null
        )
    }
}
