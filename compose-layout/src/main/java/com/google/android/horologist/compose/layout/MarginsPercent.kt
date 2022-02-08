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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp

@JvmInline
public value class Percent(public val value: Float) {
    init {
        check(value in 0f..1f)
    }
}

public data class MarginsPercent(
    val start: Percent,
    val top: Percent,
    val end: Percent,
    val bottom: Percent,
) {
    @Composable
    public fun paddingValues(): PaddingValues {
        val configuration = LocalConfiguration.current
        return PaddingValues(
            start = Dp(configuration.screenWidthDp * start.value),
            top = Dp(configuration.screenHeightDp * top.value),
            end = Dp(configuration.screenWidthDp * end.value),
            bottom = Dp(configuration.screenHeightDp * bottom.value),
        )
    }
}
