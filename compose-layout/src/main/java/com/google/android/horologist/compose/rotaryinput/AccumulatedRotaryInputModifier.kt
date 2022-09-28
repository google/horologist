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

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.rotary.RotaryScrollEvent
import androidx.compose.ui.input.rotary.onRotaryScrollEvent

/**
 * Accumulates the scroll distances from [RotaryScrollEvent] and notifies changes with
 * [onValueChange] once accumulated value is over the thresholds.
 */
@OptIn(ExperimentalComposeUiApi::class)
public fun Modifier.onRotaryInputAccumulated(
    onValueChange: (change: Float) -> Unit
): Modifier {
    val rotaryInputAccumulator = RotaryInputAccumulator(onValueChange = onValueChange)
    return onRotaryScrollEvent(rotaryInputAccumulator::onRotaryScrollEvent)
}

/**
 * Process a [RotaryScrollEvent].
 *
 * @param event the [RotaryScrollEvent] to be processed.
 */
@OptIn(ExperimentalComposeUiApi::class)
internal fun RotaryInputAccumulator.onRotaryScrollEvent(event: RotaryScrollEvent): Boolean {
    onRotaryScroll(event.verticalScrollPixels, event.uptimeMillis)
    return true
}
