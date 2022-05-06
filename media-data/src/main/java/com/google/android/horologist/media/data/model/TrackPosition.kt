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

package com.google.android.horologist.media.data.model

/**
 * Data class for representing the current track position, track length and
 * percent progress.  Track position and duration are measure in milliseconds.
 */
public data class TrackPosition(val current: Long, val duration: Long) {
    val percent: Float
        get() = current.toFloat() / duration.toFloat()

    public companion object {
        public val Unknown: TrackPosition = TrackPosition(0, 0)
    }
}
