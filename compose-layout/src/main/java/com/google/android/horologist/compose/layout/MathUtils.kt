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

package com.google.android.horologist.compose.layout

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Return vertical padding for rectangular content in a round viewport.
 * Ref. https://www.google.com/search?q=pythagorean+theorem
 */
public fun calculateVerticalOffsetForRect(
    viewportDiameter: Int,
    childViewWidth: Int
): Float {
    val radius = viewportDiameter / 2.0
    val result = radius - sqrt(radius.pow(2) - (childViewWidth / 2.0).pow(2))
    return result.toFloat()
}

/**
 * Return vertical padding for Chip content, a round rect which has half-round edges,
 * in a round viewport.
 */
public fun calculateVerticalOffsetForChip(
    viewportDiameter: Double,
    childViewHeight: Double,
    childViewWidth: Double
): Double {
    val radius = viewportDiameter / 2.0
    return radius -
        sqrt(
            (radius - childViewHeight + childViewWidth * 0.5) * (radius - childViewWidth * 0.5)
        ) -
        childViewHeight * 0.5
}
