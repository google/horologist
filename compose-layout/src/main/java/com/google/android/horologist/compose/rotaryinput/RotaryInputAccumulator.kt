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
internal class RotaryInputAccumulator constructor(
    private val eventAccumulationThresholdMs: Long = DEFAULT_EVENT_ACCUMULATION_THRESHOLD_MS,
    private val minValueChangeDistancePx: Float = DEFAULT_MIN_VALUE_CHANGE_DISTANCE_PX,
    private val rateLimitCoolDownMs: Long = DEFAULT_RATE_LIMIT_COOL_DOWN_MS,
    private val onValueChange: ((change: Float) -> Unit)
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
        if (timeSinceLastAccumulatedMs > eventAccumulationThresholdMs) {
            accumulatedDistance = scrollPixels
        } else {
            accumulatedDistance += scrollPixels
        }
        onEventAccumulated(eventTimeMillis)
    }

    private fun onEventAccumulated(eventTimeMs: Long) {
        if (abs(accumulatedDistance) < minValueChangeDistancePx ||
            eventTimeMs - lastUpdateTimeMs < rateLimitCoolDownMs
        ) {
            return
        }
        onValueChange(accumulatedDistance)
        lastUpdateTimeMs = eventTimeMs
        accumulatedDistance = 0f
    }

    internal companion object {
        const val DEFAULT_EVENT_ACCUMULATION_THRESHOLD_MS = 200L
        const val DEFAULT_MIN_VALUE_CHANGE_DISTANCE_PX = 48f
        const val DEFAULT_RATE_LIMIT_COOL_DOWN_MS = 300L
    }
}
