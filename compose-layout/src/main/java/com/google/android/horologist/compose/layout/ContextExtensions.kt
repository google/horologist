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

import android.content.Context
import android.util.TypedValue
import androidx.annotation.DimenRes
import kotlin.math.roundToInt

fun Context.floatDimensionResource(@DimenRes id: Int): Float {
    val typedValue = TypedValue()
    resources.getValue(id, typedValue, true)
    return typedValue.float
}

/** Loads a float dimension resource and returns its pixel-converted value. */
fun Context.pixelFromFloatDimension(@DimenRes id: Int, isVertical: Boolean = true): Float {
    val floatValue = floatDimensionResource(id)
    val screenSize = if (isVertical)
        resources.displayMetrics.heightPixels
    else
        resources.displayMetrics.widthPixels
    return floatValue * screenSize
}