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

package com.google.android.horologist.compose.rotaryinput

import kotlin.math.abs

/** Accumulator to trigger callbacks based on rotary input event. */
internal class RotaryInputAccumulator(
    private val eventAccumulationThresholdMs: Long,
    private val minValueChangeDistancePx: Float,
    private val rateLimitCoolDownMs: Long,
    private val onValueChange: ((change: Float) -> Unit),
    private val isLowRes: Boolean = false
) {
    private var accumulatedDistance = 0f
    private var lastAccumulatedEventTimeMs: Long = 0
    private var lastUpdateTimeMs: Long = 0

    /**
     * Process a rotary input event.
     *
     * @param scrollPixels the amount to scroll in pixels of the event.
     * @param eventTimeMillis the time in milliseconds at which this even occurred
     */
    public fun onRotaryScroll(scrollPixels: Float, eventTimeMillis: Long) {
        val timeSinceLastAccumulatedMs = eventTimeMillis - lastAccumulatedEventTimeMs
        lastAccumulatedEventTimeMs = eventTimeMillis
        android.util.Log.d("Yooohoo", "scrollPixels=$scrollPixels")
        // For low res devices
        val change = if (isLowRes && scrollPixels > 0f) {
            1f
        } else if (isLowRes && scrollPixels < 0f) {
            -1f
        } else if (isLowRes /** && scrollPixels == 0f **/) {
            0f
        } else { // !isLowRes
            scrollPixels
        }

        if (timeSinceLastAccumulatedMs > eventAccumulationThresholdMs) {
            accumulatedDistance = change
        } else {
            accumulatedDistance += change
        }
        android.util.Log.d("Yooohoo", "accumulatedDistance1=$accumulatedDistance")
        onEventAccumulated(eventTimeMillis)
    }

    private fun onEventAccumulated(eventTimeMs: Long) {
        android.util.Log.d("Yooohoo", "accumulatedDistance2=$accumulatedDistance")
        android.util.Log.d("Yooohoo", "timeThing=${eventTimeMs - lastUpdateTimeMs < rateLimitCoolDownMs}")
        if ((!isLowRes && abs(accumulatedDistance) < minValueChangeDistancePx) ||
            eventTimeMs - lastUpdateTimeMs < rateLimitCoolDownMs
        ) {
            return
        }
        android.util.Log.d("Yooohoo", "accumulatedDistance3=$accumulatedDistance")
        onValueChange(accumulatedDistance)
        lastUpdateTimeMs = eventTimeMs
        accumulatedDistance = 0f
    }
}
