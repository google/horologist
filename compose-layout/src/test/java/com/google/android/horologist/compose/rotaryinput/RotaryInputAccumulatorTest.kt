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

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class RotaryInputAccumulatorTest {

    private val latestValue = AtomicReference<Float>()
    private val valueChangedTimes = AtomicInteger(0)
    private val accumulationThreshold = 200L
    private val minChangePx = 48f

    private val rotaryInputAccumulator =
        RotaryInputAccumulator(
            eventAccumulationThresholdMs = accumulationThreshold,
            minValueChangeDistancePx = minChangePx,
            rateLimitCoolDownMs = RotaryInputAccumulator.RATE_LIMITING_DISABLED
        ) {
            latestValue.set(it)
            valueChangedTimes.incrementAndGet()
        }

    @Test
    fun onRotaryScroll_whenAccumulatedValueBelowMinimum_doNotNotifyChange() {
        rotaryInputAccumulator.onRotaryScroll(scrollPixels = 1f, eventTimeMillis = 0L)
        verifyOnValueChange(timesCalled = 0, expectedLatestValue = null)
    }

    @Test
    fun onRotaryScroll_whenAccumulatedValueAboveMinimum_notifyChange() {
        rotaryInputAccumulator.onRotaryScroll(
            minChangePx,
            eventTimeMillis = 0L
        )
        verifyOnValueChange(timesCalled = 1, minChangePx)
    }

    @Test
    fun onRotaryScroll_whenWithinAccumulationThreshold_notifyChange() {
        val scrollPixels = minChangePx / 2

        rotaryInputAccumulator.onRotaryScroll(scrollPixels, 0L)
        rotaryInputAccumulator.onRotaryScroll(scrollPixels, 1L)
        verifyOnValueChange(timesCalled = 1, minChangePx)

        rotaryInputAccumulator.onRotaryScroll(scrollPixels, 2L)
        rotaryInputAccumulator.onRotaryScroll(scrollPixels, 3L)
        verifyOnValueChange(timesCalled = 2, minChangePx)
    }

    @Test
    fun onRotaryScroll_whenOutsideAccumulationThreshold_resetAccumulation() {
        val scrollPixels = minChangePx / 2

        rotaryInputAccumulator.onRotaryScroll(
            scrollPixels,
            0L
        )
        rotaryInputAccumulator.onRotaryScroll(
            scrollPixels,
            accumulationThreshold + 1
        )
        rotaryInputAccumulator.onRotaryScroll(
            scrollPixels,
            accumulationThreshold + 2
        )

        verifyOnValueChange(timesCalled = 1, minChangePx)
    }

    @Test
    fun onRotaryScroll_whenRateLimitedAndEventTooFrequent_notifyOnce() {
        val scrollPixels = 10f
        val rateLimitCoolDownMs = 100L
        val firstEventTime = 12345L
        val coolDownRotaryInputAccumulator =
            RotaryInputAccumulator(
                eventAccumulationThresholdMs = 200L,
                minValueChangeDistancePx = scrollPixels,
                rateLimitCoolDownMs = rateLimitCoolDownMs
            ) {
                latestValue.set(it)
                valueChangedTimes.incrementAndGet()
            }

        coolDownRotaryInputAccumulator.onRotaryScroll(scrollPixels, firstEventTime)
        coolDownRotaryInputAccumulator.onRotaryScroll(
            scrollPixels,
            firstEventTime + rateLimitCoolDownMs - 1
        )

        verifyOnValueChange(timesCalled = 1, 10f)
    }

    @Test
    fun onRotaryScroll_whenRateLimitedAndEventEmittedAfterCoolDown_notifyTwice() {
        val scrollPixels = 48f
        val rateLimitCoolDownMs = 100L
        val firstEventTime = 12345L
        val coolDownRotaryInputAccumulator =
            RotaryInputAccumulator(
                eventAccumulationThresholdMs = 200L,
                minValueChangeDistancePx = scrollPixels,
                rateLimitCoolDownMs = rateLimitCoolDownMs
            ) {
                latestValue.set(it)
                valueChangedTimes.incrementAndGet()
            }

        coolDownRotaryInputAccumulator.onRotaryScroll(scrollPixels, firstEventTime)
        coolDownRotaryInputAccumulator.onRotaryScroll(
            scrollPixels,
            firstEventTime + rateLimitCoolDownMs - 1
        )
        verifyOnValueChange(timesCalled = 1, scrollPixels)

        coolDownRotaryInputAccumulator.onRotaryScroll(
            scrollPixels,
            firstEventTime + rateLimitCoolDownMs
        )
        verifyOnValueChange(timesCalled = 2, scrollPixels * 2)
    }

    private fun verifyOnValueChange(timesCalled: Int, expectedLatestValue: Float?) {
        assertThat(valueChangedTimes.get()).isEqualTo(timesCalled)
        assertThat(latestValue.get()).isEqualTo(expectedLatestValue)
    }
}
