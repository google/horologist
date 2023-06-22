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

@file:OptIn(ExperimentalWearFoundationApi::class)

package com.google.android.horologist.compose.rotaryinput

import androidx.compose.foundation.focusable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.RotaryScrollEvent
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import com.google.android.horologist.annotations.ExperimentalHorologistApi

/**
 * A focusable modifier that accumulates the scroll distances from [RotaryScrollEvent] and notifies
 * changes with [onValueChange] once accumulated value is over the thresholds.
 *
 * @param focusRequester requests for focus for the rotary
 * @param onValueChange callback invoked once accumulated value is over the thresholds.
 */
@ExperimentalHorologistApi
public fun Modifier.onRotaryInputAccumulatedWithFocus(
    focusRequester: FocusRequester? = null,
    isLowRes: Boolean = false,
    onValueChange: (Float) -> Unit
): Modifier = composed {
    val localFocusRequester = focusRequester ?: rememberActiveFocusRequester()
    onRotaryInputAccumulated(onValueChange = onValueChange, isLowRes = isLowRes)
        .focusRequester(localFocusRequester)
        .focusable()
}

/**
 * Accumulates the scroll distances from [RotaryScrollEvent] and notifies changes with
 * [onValueChange] once accumulated value is over the thresholds.
 *
 * @param eventAccumulationThresholdMs time threshold below which events are accumulated.
 * @param minValueChangeDistancePx minimum distance for value change in pixels.
 * @param rateLimitCoolDownMs cool down time when rate limiting is enabled, negative value disables.
 * @param isLowRes resolution of the device's rotary control. High resolution and low resolutions have different accumulation mechanism. See [RotaryInputAccumulator.changeByResolution] for more.
 * @param onValueChange callback invoked once accumulated value is over the thresholds.
 */
@ExperimentalHorologistApi
public fun Modifier.onRotaryInputAccumulated(
    eventAccumulationThresholdMs: Long = RotaryInputConfigDefaults.DEFAULT_EVENT_ACCUMULATION_THRESHOLD_MS,
    minValueChangeDistancePx: Float = RotaryInputConfigDefaults.DEFAULT_MIN_VALUE_CHANGE_DISTANCE_PX,
    rateLimitCoolDownMs: Long = RotaryInputConfigDefaults.DEFAULT_RATE_LIMIT_COOL_DOWN_MS,
    isLowRes: Boolean = false,
    onValueChange: (change: Float) -> Unit
): Modifier = composed {
    val updatedOnValueChange by rememberUpdatedState(onValueChange)
    val rotaryInputAccumulator = remember {
        RotaryInputAccumulator(
            eventAccumulationThresholdMs = eventAccumulationThresholdMs,
            minValueChangeDistancePx = minValueChangeDistancePx,
            rateLimitCoolDownMs = rateLimitCoolDownMs,
            isLowRes = isLowRes,
            onValueChange = { updatedOnValueChange(it) }
        )
    }
    return@composed onRotaryScrollEvent(rotaryInputAccumulator::onRotaryScrollEvent)
}

/**
 * Process a [RotaryScrollEvent].
 *
 * @param event the [RotaryScrollEvent] to be processed.
 */
@ExperimentalHorologistApi
internal fun RotaryInputAccumulator.onRotaryScrollEvent(event: RotaryScrollEvent): Boolean {
    onRotaryScroll(event.verticalScrollPixels, event.uptimeMillis)
    return true
}

public object RotaryInputConfigDefaults {
    public const val DEFAULT_EVENT_ACCUMULATION_THRESHOLD_MS: Long = 200L
    public const val DEFAULT_MIN_VALUE_CHANGE_DISTANCE_PX: Float = 48f
    public const val DEFAULT_RATE_LIMIT_COOL_DOWN_MS: Long = 30L
    public const val RATE_LIMITING_DISABLED: Long = -1L
}
