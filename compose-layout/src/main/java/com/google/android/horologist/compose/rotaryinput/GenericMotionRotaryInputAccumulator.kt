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

import android.content.Context
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.core.view.InputDeviceCompat
import androidx.core.view.MotionEventCompat
import androidx.core.view.ViewConfigurationCompat

/**
 * Accumulator to trigger callbacks based on [MotionEvent] from rotary input.
 *
 * This should normally be passed events received from [android.view.View.onGenericMotionEvent].
 */
public class GenericMotionRotaryInputAccumulator(
    context: Context,
    onValueChange: (change: Float) -> Unit,
    eventAccumulationThresholdMs: Long = RotaryInputConfigDefaults.DEFAULT_EVENT_ACCUMULATION_THRESHOLD_MS,
    minValueChangeDistancePx: Float = RotaryInputConfigDefaults.DEFAULT_MIN_VALUE_CHANGE_DISTANCE_PX,
    rateLimitCoolDownMs: Long = RotaryInputConfigDefaults.DEFAULT_RATE_LIMIT_COOL_DOWN_MS
) {

    private val rotaryInputEventReader: RotaryInputEventReader = RotaryInputEventReader(context)
    private val rotaryInputAccumulator: RotaryInputAccumulator =
        RotaryInputAccumulator(
            eventAccumulationThresholdMs = eventAccumulationThresholdMs,
            minValueChangeDistancePx = minValueChangeDistancePx,
            rateLimitCoolDownMs = rateLimitCoolDownMs,
            onValueChange = onValueChange
        )

    /**
     * Process a [MotionEvent].
     *
     * @param event the [MotionEvent] to be processed.
     * @return `true` when the event has produced change in the accumulator.
     */
    public fun onGenericMotionEvent(event: MotionEvent): Boolean {
        if (!rotaryInputEventReader.isRotaryScrollEvent(event)) {
            return false
        }
        rotaryInputAccumulator.onRotaryScroll(
            rotaryInputEventReader.getScrollDistance(event),
            event.eventTime
        )
        return true
    }

    /**
     * Reads [android.view.MotionEvent] to determine whether they are rotary input events and to
     * obtain axis value.
     */
    private class RotaryInputEventReader(context: Context) {
        private val scaledScrollFactor =
            ViewConfigurationCompat.getScaledVerticalScrollFactor(
                ViewConfiguration.get(context),
                context
            )

        fun isRotaryScrollEvent(ev: MotionEvent): Boolean =
            ev.source == InputDeviceCompat.SOURCE_ROTARY_ENCODER &&
                ev.action == MotionEvent.ACTION_SCROLL

        fun getScrollDistance(ev: MotionEvent): Float =
            -ev.getAxisValue(MotionEventCompat.AXIS_SCROLL) * scaledScrollFactor
    }
}
