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

package com.google.android.horologist.media.model

import java.lang.Float.max
import java.lang.Float.min
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

public data class LiveMediaPositionPredictor(
    private val eventTimestamp: Long,
    private val durationMs: Long,
    private val currentPositionMs: Long,
    private val positionSpeed: Float
) : PositionPredictor {
    override fun predictPercent(timestamp: Long): Float {
        val predictedDuration = predictDuration(timestamp).inWholeMilliseconds.toFloat()
        val predictedPosition = min(predictedDuration, predictPositionFractional(timestamp))
        return max(0f, predictedPosition / predictedDuration)
    }

    override fun predictDuration(timestamp: Long): Duration {
        val staleness = timestamp - eventTimestamp
        return (durationMs + staleness).milliseconds
    }

    private fun predictPositionFractional(timestamp: Long): Float {
        val staleness = timestamp - eventTimestamp
        return (currentPositionMs + staleness * positionSpeed)
    }

    override fun predictPosition(timestamp: Long): Duration =
        predictPositionFractional(timestamp).roundToLong().milliseconds
}
