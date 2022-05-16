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

/**
 * Represents the current [media item][MediaItem] position, duration and percent progress.
 * Current position and duration are measured in milliseconds.
 */
public sealed class MediaItemPosition(
    public open val current: Long,
) {
    public class KnownDuration internal constructor(
        override val current: Long,
        public val duration: Long,
        public val percent: Float,
    ) : MediaItemPosition(current)

    public data class UnknownDuration(override val current: Long) : MediaItemPosition(current)

    public companion object {

        public fun create(
            current: Long,
            duration: Long
        ): KnownDuration {
            check(current >= 0) {
                "Current position can't be a negative value [current: $current] [duration: $duration]."
            }
            check(duration > 0) {
                "Duration has to be greater than zero [current: $current] [duration: $duration]."
            }
            check(current <= duration) {
                "Current position has to be greater than duration [current: $current] [duration: $duration]."
            }

            val percent = current.toFloat() / duration.toFloat()

            return KnownDuration(current, duration, percent)
        }
    }
}
