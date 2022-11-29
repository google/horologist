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

import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ScalingLazyListAnchorType
import com.google.android.horologist.compose.layout.ScalingLazyColumnConfig.RotaryMode

public object ScalingLazyColumnConfigDefaults {
    @Composable
    public fun rememberTopAlignedConfig(rotaryMode: RotaryMode = RotaryMode.Snap): ScalingLazyColumnConfig {
        val density = LocalDensity.current
        val configuration = LocalConfiguration.current

        val flingBehavior = ScrollableDefaults.flingBehavior()

        return remember {
            val screenHeightPx = with(density) { configuration.screenHeightDp.dp.roundToPx() }
            val topPaddingPx = with(density) { 32.dp.roundToPx() }
            val topScreenOffsetPx = screenHeightPx / 2 - topPaddingPx

            ScalingLazyColumnConfig(
                initialScrollPosition = ScalingLazyColumnConfig.ScrollPosition(
                    index = 0,
                    offsetPx = topScreenOffsetPx
                ),
                anchorType = ScalingLazyListAnchorType.ItemStart,
                flingBehavior = flingBehavior,
                rotaryMode = rotaryMode
            )
        }
    }
}
