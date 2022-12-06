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

@file:Suppress("ObjectLiteralToLambda")

package com.google.android.horologist.compose.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ScalingLazyListAnchorType
import com.google.android.horologist.compose.layout.ScalingLazyColumnState.RotaryMode
import com.google.android.horologist.compose.navscaffold.ExperimentalHorologistComposeLayoutApi

public object ScalingLazyColumnDefaults {
    @ExperimentalHorologistComposeLayoutApi
    public fun belowTimeText(
        rotaryMode: RotaryMode = RotaryMode.Snap,
        firstItemIsFullWidth: Boolean = false
    ): ScalingLazyColumnState.Factory {
        return object : ScalingLazyColumnState.Factory {
            @Composable
            override fun create(): ScalingLazyColumnState {
                val density = LocalDensity.current
                val configuration = LocalConfiguration.current

                return remember {
                    val screenHeightPx =
                        with(density) { configuration.screenHeightDp.dp.roundToPx() }
                    val topPaddingDp = 32.dp + (if (firstItemIsFullWidth) 20.dp else 0.dp)
                    val topPaddingPx = with(density) { topPaddingDp.roundToPx() }
                    val topScreenOffsetPx = screenHeightPx / 2 - topPaddingPx

                    ScalingLazyColumnState(
                        initialScrollPosition = ScalingLazyColumnState.ScrollPosition(
                            index = 0,
                            offsetPx = topScreenOffsetPx
                        ),
                        anchorType = ScalingLazyListAnchorType.ItemStart,
                        rotaryMode = rotaryMode
                    )
                }
            }
        }
    }

    @ExperimentalHorologistComposeLayoutApi
    public fun scalingLazyColumnDefaults(
        rotaryMode: RotaryMode = RotaryMode.Snap,
        initialCenterIndex: Int = 1,
        initialCenterOffset: Int = 0
    ): ScalingLazyColumnState.Factory {
        return object : ScalingLazyColumnState.Factory {
            @Composable
            override fun create(): ScalingLazyColumnState {
                return remember {
                    ScalingLazyColumnState(
                        initialScrollPosition = ScalingLazyColumnState.ScrollPosition(
                            index = initialCenterIndex,
                            offsetPx = initialCenterOffset
                        ),
                        rotaryMode = rotaryMode
                    )
                }
            }
        }
    }
}
