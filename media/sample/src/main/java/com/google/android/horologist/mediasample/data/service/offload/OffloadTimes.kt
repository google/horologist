/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.mediasample.data.service.offload

import com.google.android.horologist.annotations.ExperimentalHorologistApi
import java.text.NumberFormat

/**
 * A stats object for Audio Offload state.
 */
@ExperimentalHorologistApi
public data class OffloadTimes(
    public val enabled: Long = 0L,
    public val disabled: Long = 0L,
    public val notPlaying: Long = 0L,
    public val isPlaying: Boolean = false,
    public val updated: Long = System.currentTimeMillis(),
) {
    val shortDescription: String
        get() = "$enabled/$disabled/$isPlaying"

    public val percent: String
        get() {
            val value = enabled.toFloat() / (enabled + disabled)
            return if (value.isNaN()) "--%" else PercentFormat.format(value)
        }

    public fun timesToNow(sleepingForOffload: Boolean, updatedIsPlaying: Boolean): OffloadTimes {
        val time = System.currentTimeMillis()
        val extra = time - updated

        return if (isPlaying) {
            copy(
                enabled = enabled + (if (sleepingForOffload) extra else 0),
                disabled = disabled + (if (sleepingForOffload) 0 else extra),
                updated = time,
                isPlaying = updatedIsPlaying,
            )
        } else {
            copy(
                notPlaying = notPlaying + extra,
                updated = time,
                isPlaying = updatedIsPlaying,
            )
        }
    }

    internal companion object {
        internal val PercentFormat: NumberFormat = NumberFormat.getPercentInstance()
    }
}
