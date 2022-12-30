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

/**
 * Represents a state of playback, duration and playback speed. It can be used to determine whether
 * media is playing and to infer current and future position and duration.
 */
@ExperimentalHorologistMediaApi
public data class PlaybackState(
    public val playerState: PlayerState,
    public val isLive: Boolean,
    public val currentPosition: Duration?,
    public val duration: Duration?,
    public val playbackSpeed: Float
) {
    public val isPlaying: Boolean get() = playerState == PlayerState.Playing

    public companion object {
        public val IDLE: PlaybackState = PlaybackState(
            playerState = PlayerState.Idle,
            isLive = false,
            currentPosition = null,
            duration = null,
            playbackSpeed = 1f
        )
    }
}
