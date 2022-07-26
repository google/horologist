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
 * Represents the current [Media] position, duration and percent progress.
 * Current position and duration are measured in milliseconds.
 */
@ExperimentalHorologistMediaApi
public sealed class MediaPosition(
    public open val current: Duration,
) {
    public class KnownDuration internal constructor(
        override val current: Duration,
        public val duration: Duration,
        public val percent: Float,
    ) : MediaPosition(current)

    public data class UnknownDuration(override val current: Duration) : MediaPosition(current)

    public companion object {

        public fun create(
            current: Duration,
            duration: Duration
        ): KnownDuration {
            check(!current.isNegative()) {
                "Current position can't be a negative value [current: $current] [duration: $duration]."
            }
            check(duration.isPositive()) {
                "Duration has to be greater than zero [current: $current] [duration: $duration]."
            }
            check(current <= duration) {
                "Current position has to be greater than duration [current: $current] [duration: $duration]."
            }

            val percent =
                current.inWholeMilliseconds.toFloat() / duration.inWholeMilliseconds.toFloat()

            return KnownDuration(current, duration, percent)
        }
    }
}
