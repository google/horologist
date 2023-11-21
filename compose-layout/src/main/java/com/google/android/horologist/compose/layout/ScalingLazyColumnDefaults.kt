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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.material.ChipDefaults
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumnState.RotaryMode
import kotlin.math.sqrt

/**
 * Default layouts for ScalingLazyColumnState, based on UX guidance.
 */
public object ScalingLazyColumnDefaults {
    /**
     * Layout the first item, directly under the time text.
     * This is positioned from the top of the screen instead of the
     * center.
     */
    @ExperimentalHorologistApi
    public fun belowTimeText(
        rotaryMode: RotaryMode = RotaryMode.Scroll,
        firstItemIsFullWidth: Boolean = false,
        verticalArrangement: Arrangement.Vertical =
            Arrangement.spacedBy(
                space = 4.dp,
                alignment = Alignment.Top,
            ),
        horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
        contentPadding: PaddingValues = PaddingValues(horizontal = 10.dp),
        topPaddingDp: Dp = 32.dp + (if (firstItemIsFullWidth) 20.dp else 0.dp),
    ): ScalingLazyColumnState.Factory {
        return object : ScalingLazyColumnState.Factory {
            @Composable
            override fun create(): ScalingLazyColumnState {
                val density = LocalDensity.current
                val configuration = LocalConfiguration.current

                return remember {
                    val screenHeightPx =
                        with(density) { configuration.screenHeightDp.dp.roundToPx() }
                    val topPaddingPx = with(density) { topPaddingDp.roundToPx() }
                    val topScreenOffsetPx = screenHeightPx / 2 - topPaddingPx

                    ScalingLazyColumnState(
                        initialScrollPosition = ScalingLazyColumnState.ScrollPosition(
                            index = 0,
                            offsetPx = topScreenOffsetPx,
                        ),
                        anchorType = ScalingLazyListAnchorType.ItemStart,
                        rotaryMode = rotaryMode,
                        verticalArrangement = verticalArrangement,
                        horizontalAlignment = horizontalAlignment,
                        contentPadding = contentPadding,
                    )
                }
            }
        }
    }

    /**
     * Layout the item [initialCenterIndex] at [initialCenterOffset] from the
     * center of the screen.
     */
    @ExperimentalHorologistApi
    public fun scalingLazyColumnDefaults(
        rotaryMode: RotaryMode = RotaryMode.Scroll,
        initialCenterIndex: Int = 1,
        initialCenterOffset: Int = 0,
        verticalArrangement: Arrangement.Vertical =
            Arrangement.spacedBy(
                space = 4.dp,
                alignment = Alignment.Top,
            ),
        horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
        contentPadding: PaddingValues = PaddingValues(horizontal = 10.dp),
        autoCentering: AutoCenteringParams? = AutoCenteringParams(
            initialCenterIndex,
            initialCenterOffset,
        ),
        anchorType: ScalingLazyListAnchorType = ScalingLazyListAnchorType.ItemCenter,
        hapticsEnabled: Boolean = true,
        reverseLayout: Boolean = false,
    ): ScalingLazyColumnState.Factory {
        return object : ScalingLazyColumnState.Factory {
            @Composable
            override fun create(): ScalingLazyColumnState {
                return remember {
                    ScalingLazyColumnState(
                        initialScrollPosition = ScalingLazyColumnState.ScrollPosition(
                            index = initialCenterIndex,
                            offsetPx = initialCenterOffset,
                        ),
                        rotaryMode = rotaryMode,
                        verticalArrangement = verticalArrangement,
                        horizontalAlignment = horizontalAlignment,
                        contentPadding = contentPadding,
                        autoCentering = autoCentering,
                        anchorType = anchorType,
                        hapticsEnabled = hapticsEnabled,
                        reverseLayout = reverseLayout,
                    )
                }
            }
        }
    }

    @ExperimentalHorologistApi
    public fun responsive(
        firstItemIsFullWidth: Boolean = true,
        verticalArrangement: Arrangement.Vertical =
            Arrangement.spacedBy(
                space = 4.dp,
                alignment = Alignment.Top,
            ),
        horizontalPaddingPercent: Float = 0.052f,
    ): ScalingLazyColumnState.Factory {
        fun calculateVerticalOffsetForChip(
            viewportDiameter: Float,
            horizontalPaddingPercent: Float,
        ): Dp {
            val childViewHeight: Float = ChipDefaults.Height.value
            val childViewWidth: Float = viewportDiameter * (1.0f - (2f * horizontalPaddingPercent))
            val radius = viewportDiameter / 2f
            return (
                radius -
                    sqrt(
                        (radius - childViewHeight + childViewWidth * 0.5f) * (radius - childViewWidth * 0.5f),
                    ) -
                    childViewHeight * 0.5f
                ).dp
        }

        return object : ScalingLazyColumnState.Factory {
            @Composable
            override fun create(): ScalingLazyColumnState {
                val density = LocalDensity.current
                val configuration = LocalConfiguration.current
                val screenWidthDp = configuration.screenWidthDp.toFloat()
                val screenHeightDp = configuration.screenHeightDp.toFloat()
                val padding = screenWidthDp * horizontalPaddingPercent
                val topPaddingDp: Dp = if (firstItemIsFullWidth && configuration.isScreenRound) {
                    calculateVerticalOffsetForChip(screenWidthDp, horizontalPaddingPercent)
                } else {
                    32.dp
                }
                val bottomPaddingDp: Dp = if (configuration.isScreenRound) {
                    calculateVerticalOffsetForChip(screenWidthDp, horizontalPaddingPercent)
                } else {
                    0.dp
                }

                val sizeRatio = ((screenWidthDp - 192) / (233 - 192).toFloat()).coerceIn(0f, 1.5f)
                val presetRatio = 0f

                val minElementHeight = lerp(0.2f, 0.157f, sizeRatio)
                val maxElementHeight = lerp(0.6f, 0.216f, sizeRatio).coerceAtLeast(minElementHeight)
                val minTransitionArea = lerp(0.35f, lerp(0.35f, 0.393f, presetRatio), sizeRatio)
                val maxTransitionArea = lerp(0.55f, lerp(0.55f, 0.593f, presetRatio), sizeRatio)

                val scalingParams = ScalingLazyColumnDefaults.scalingParams(
                    minElementHeight = minElementHeight,
                    maxElementHeight = maxElementHeight,
                    minTransitionArea = minTransitionArea,
                    maxTransitionArea = maxTransitionArea,
                )

                return remember {
                    val screenHeightPx =
                        with(density) { screenHeightDp.dp.roundToPx() }
                    val topPaddingPx = with(density) { topPaddingDp.roundToPx() }
                    val topScreenOffsetPx = screenHeightPx / 2 - topPaddingPx

                    ScalingLazyColumnState(
                        initialScrollPosition = ScalingLazyColumnState.ScrollPosition(
                            index = 0,
                            offsetPx = topScreenOffsetPx,
                        ),
                        autoCentering = null,
                        anchorType = ScalingLazyListAnchorType.ItemStart,
                        rotaryMode = RotaryMode.Scroll,
                        verticalArrangement = verticalArrangement,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = PaddingValues(start = padding.dp, end = padding.dp, top = topPaddingDp, bottom = bottomPaddingDp),
                        scalingParams = scalingParams,
                    )
                }
            }
        }
    }
}
