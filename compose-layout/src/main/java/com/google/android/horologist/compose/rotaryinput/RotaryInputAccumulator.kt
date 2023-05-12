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
    private val isLowRes: Boolean = false,
    private val onValueChange: ((change: Float) -> Unit)
) {
    private var accumulatedDistance = 0f
    private var lastAccumulatedEventTimeMs: Long = 0
    private var lastUpdateTimeMs: Long = 0

    /**
     * Process a rotary input event.
     *
     * @param scrollPixels the amount of scrolled in pixels of the event
     * @param eventTimeMillis the time in milliseconds at which this even occurred
     */
    public fun onRotaryScroll(scrollPixels: Float, eventTimeMillis: Long) {
        val timeSinceLastAccumulatedMs = eventTimeMillis - lastAccumulatedEventTimeMs
        lastAccumulatedEventTimeMs = eventTimeMillis

        // If still within the eventAccumulationThresholdMs time range, accumulate the changes,
        // otherwise restart accumulation.
        accumulatedDistance = if (timeSinceLastAccumulatedMs <= eventAccumulationThresholdMs) {
            accumulatedDistance + changeByResolution(scrollPixels)
        } else {
            changeByResolution(scrollPixels)
        }

        onEventAccumulated(eventTimeMillis)
    }

    private fun onEventAccumulated(eventTimeMs: Long) {
        if (shouldIgnoreAccumulatedInput(eventTimeMs)) return

        onValueChange(accumulatedDistance)
        lastUpdateTimeMs = eventTimeMs
        accumulatedDistance = 0f
    }

    /**
     * Converts a change by scrolled pixels to the actual accumulated values. For a low resolution
     * rotary device, a positive scroll will be 1f and a negative scroll will be -1. For a high
     * resolution rotary device, take the scrolled pixels as is.
     */
    private fun changeByResolution(scrollPixels: Float): Float {
        return if (isLowRes && scrollPixels > 0f) { // For positive tick in low res devices
            1f
        } else if (isLowRes && scrollPixels < 0f) { // For negative tick in low res devices
            -1f
        } else if (isLowRes /** && scrollPixels == 0f **/) { // For no tick in low res devices
            0f
        } else { // Take it as is for high res devices
            scrollPixels
        }
    }

    private fun shouldIgnoreAccumulatedInput(eventTimeMs: Long): Boolean {
        return (
            !isLowRes &&
                abs(accumulatedDistance) < minValueChangeDistancePx
            ) ||
            eventTimeMs - lastUpdateTimeMs < rateLimitCoolDownMs
    }
}
