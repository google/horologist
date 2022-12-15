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
 * Represents a [Media] position, duration and playback speed captured at a given time. It can be
 */
@ExperimentalHorologistMediaApi
public data class PlaybackState(
    public val isPlaying: Boolean,
    public val isLive: Boolean,
    public val currentPosition: Duration?,
    public val duration: Duration?,
    public val playbackSpeed: Float,
    public val elapsedRealtimeWhenCreated: Duration? = null
) {
    public companion object {
        public val IDLE: PlaybackState = PlaybackState(
            isPlaying = false,
            currentPosition = Duration.ZERO,
            duration = Duration.ZERO,
            playbackSpeed = 1f,
            isLive = false,
            elapsedRealtimeWhenCreated = null
        )
    }
}
