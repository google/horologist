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

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.util.VelocityTracker

/**
 * Intercepts VelocityTracker to provide support for rotary input.
 */
public class RotaryVelocityTracker {
    // TODO(b/32830165): Current implementation of VelocityTracker resets the speed after 40ms
    // if no motion events received. This threshold can't be changed.
    // The solution will be to use updated impulse-based velocityTracker from Android
    // or write similar in compose.
    private var velocityTracker: VelocityTracker = VelocityTracker()
    private var position: Float = 0f

    /**
     * Retrieve the last computed Y velocity.
     */
    public val velocity: Float
        get() = velocityTracker.calculateVelocity().y

    /**
     * Start tracking motion.
     */
    public fun start(currentTime: Long) {
        velocityTracker.resetTracking()
        position = 0f
        velocityTracker.addPosition(currentTime, Offset(0f, position))
    }

    /**
     * Continue tracking motion as the input rotates.
     */
    public fun move(currentTime: Long, delta: Float) {
        position += delta
        velocityTracker.addPosition(currentTime, Offset(0f, position))
    }

    /**
     * Stop tracking motion.
     */
    public fun end() {
        velocityTracker.resetTracking()
    }
}
