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
@file:Suppress("DEPRECATION")

package com.google.android.horologist.compose.rotaryinput

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.rotary.RotaryScrollableBehavior
import com.google.android.horologist.compose.rotaryinput.RotaryDefaults.isLowResInput

@Composable
fun accumulatedBehavior(
    eventAccumulationThresholdMs: Long = RotaryInputConfigDefaults.DEFAULT_EVENT_ACCUMULATION_THRESHOLD_MS,
    minValueChangeDistancePx: Float = RotaryInputConfigDefaults.DEFAULT_MIN_VALUE_CHANGE_DISTANCE_PX,
    rateLimitCoolDownMs: Long = RotaryInputConfigDefaults.DEFAULT_RATE_LIMIT_COOL_DOWN_MS,
    onValueChange: (change: Float) -> Unit,
): RotaryScrollableBehavior {
    val isLowRes = isLowResInput()
    val onValueChangeState = rememberUpdatedState(newValue = onValueChange)

    return remember {
        RotaryInputAccumulator(
            eventAccumulationThresholdMs,
            minValueChangeDistancePx,
            rateLimitCoolDownMs,
            isLowRes,
            onValueChangeState,
        )
    }
}

public object RotaryInputConfigDefaults {
    public const val DEFAULT_EVENT_ACCUMULATION_THRESHOLD_MS: Long = 200L
    public const val DEFAULT_MIN_VALUE_CHANGE_DISTANCE_PX: Float = 48f
    public const val DEFAULT_RATE_LIMIT_COOL_DOWN_MS: Long = 30L
    public const val RATE_LIMITING_DISABLED: Long = -1L
}
