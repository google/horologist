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

package com.google.android.horologist.sample.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import com.google.android.horologist.compose.layout.Percent
import com.google.android.horologist.compose.layout.MarginsPercent
import com.google.android.horologist.compose.layout.WearMaterialTheme
import com.google.android.horologist.compose.layout.rectangleDimensions
import com.google.android.horologist.compose.layout.roundDimensions

private val RoundDimensions = roundDimensions(
    minMargins = MarginsPercent(
        start = Percent(0.052f),
        top = Percent(0.0938f),
        end = Percent(0.052f),
        bottom = Percent(0.278f),
    ),
)

private val RectangleDimensions = rectangleDimensions(
    minMargins = MarginsPercent(
        start = Percent(0.025f),
        top = Percent(0.1111f),
        end = Percent(0.025f),
        bottom = Percent(0.3556f),
    ),
)

/**
 * Custom Theme for Wear App
 */
@Composable
fun WearAppTheme(
    content: @Composable () -> Unit
) {
    val isScreenRound = LocalConfiguration.current.isScreenRound
    WearMaterialTheme(
        colors = wearColorPalette,
        typography = WearTypography,
        // For shapes, we generally recommend using the default Material Wear shapes which are
        // optimized for round and non-round devices.
        dimensions = if (isScreenRound) RoundDimensions else RectangleDimensions,
        content = content
    )
}
