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

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.wear.compose.material.ScalingLazyColumn

@Stable
public class Dimensions(
    minMargins: MarginsPercent = MarginsPercent(
        start = Percent(0.052f),
        top = Percent(0.0938f),
        end = Percent(0.052f),
        bottom = Percent(0.104f),
    ),
    columnMargins: MarginsPercent = MarginsPercent(
        start = Percent(0.052f),
        top = Percent(0.0938f),
        end = Percent(0.052f),
        bottom = Percent(0.278f),
    ),
) {
    /**
     * Margin
     */
    public var minMargins: MarginsPercent by mutableStateOf(minMargins)
        internal set

    /**
     * Values in percent to set outer margins of list UI via [Column] or [ScalingLazyColumn]
     */
    public var columnMargins: MarginsPercent by mutableStateOf(columnMargins)
        internal set

    /**
     * Returns a copy of this Dimensions, optionally overriding some of the values.
     */
    fun copy(
        minMargins: MarginsPercent = this.minMargins,
        columnMargins: MarginsPercent = this.columnMargins,
    ): Dimensions = Dimensions(
        minMargins = minMargins.copy(),
        columnMargins = columnMargins.copy(),
    )
}

public fun roundDimensions(
    minMargins: MarginsPercent = MarginsPercent(
        start = Percent(0.052f),
        top = Percent(0.0938f),
        end = Percent(0.052f),
        bottom = Percent(0.104f),
    ),
    columnMargins: MarginsPercent = MarginsPercent(
        start = Percent(0.052f),
        top = Percent(0.0938f),
        end = Percent(0.052f),
        bottom = Percent(0.278f),
    ),
): Dimensions =
    Dimensions(
        minMargins = minMargins,
        columnMargins = columnMargins,
    )

public fun rectangleDimensions(
    minMargins: MarginsPercent = MarginsPercent(
        start = Percent(0.025f),
        top = Percent(0.1111f),
        end = Percent(0.025f),
        bottom = Percent(0.050f),
    ),
    columnMargins: MarginsPercent = MarginsPercent(
        start = Percent(0.025f),
        top = Percent(0.1111f),
        end = Percent(0.025f),
        bottom = Percent(0.3556f),
    ),
): Dimensions =
    Dimensions(
        minMargins = minMargins,
        columnMargins = columnMargins,
    )

internal val LocalDimensions = staticCompositionLocalOf { Dimensions() }
