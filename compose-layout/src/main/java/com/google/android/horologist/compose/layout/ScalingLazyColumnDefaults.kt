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
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.util.lerp
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.foundation.lazy.ScalingParams
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
    @Deprecated("Replaced by rememberResponsiveColumnState")
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
    @Deprecated("Replaced by rememberResponsiveColumnState")
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

    /**
     * Creates a Responsive layout for ScalingLazyColumn. The first and last items will scroll
     * just onto screen at full size, assuming rounded corners of a Chip.
     *
     * @param firstItemIsFullWidth set to false if the first item is small enough to fit at the top,
     * however it may be scaled.
     * @param additionalPaddingAtBottom additional padding at end of content to avoid problem items
     * clipping
     * @param verticalArrangement the ScalingLazyColumn verticalArrangement.
     * @param horizontalPaddingPercent the amount of horizontal padding as a percent.
     * @param rotaryMode the rotary handling, such as Fling or Snap.
     * @param hapticsEnabled whether haptics are enabled.
     * @param reverseLayout whether to start at the bottom.
     * @param userScrollEnabled whether to allow user to scroll.
     */
    @Deprecated("Replaced by rememberResponsiveColumnState")
    @ExperimentalHorologistApi
    public fun responsive(
        firstItemIsFullWidth: Boolean = true,
        additionalPaddingAtBottom: Dp = 10.dp,
        verticalArrangement: Arrangement.Vertical =
            Arrangement.spacedBy(
                space = 4.dp,
                alignment = Alignment.Top,
            ),
        horizontalPaddingPercent: Float = 0.052f,
        rotaryMode: RotaryMode? = RotaryMode.Scroll,
        hapticsEnabled: Boolean = true,
        reverseLayout: Boolean = false,
        userScrollEnabled: Boolean = true,
    ): ScalingLazyColumnState.Factory {
        return object : ScalingLazyColumnState.Factory {
            @Composable
            override fun create(): ScalingLazyColumnState {
                val density = LocalDensity.current
                val configuration = LocalConfiguration.current
                val screenWidthDp = configuration.screenWidthDp.toFloat()
                val screenHeightDp = configuration.screenHeightDp.toFloat()

                return remember {
                    val padding = screenWidthDp * horizontalPaddingPercent
                    val topPaddingDp: Dp =
                        if (firstItemIsFullWidth && configuration.isScreenRound) {
                            calculateVerticalOffsetForChip(screenWidthDp, horizontalPaddingPercent)
                        } else {
                            32.dp
                        }
                    val bottomPaddingDp: Dp = if (configuration.isScreenRound) {
                        calculateVerticalOffsetForChip(
                            screenWidthDp,
                            horizontalPaddingPercent,
                        ) + additionalPaddingAtBottom
                    } else {
                        0.dp
                    }
                    val contentPadding = PaddingValues(
                        start = padding.dp,
                        end = padding.dp,
                        top = topPaddingDp,
                        bottom = bottomPaddingDp,
                    )

                    val scalingParams = responsiveScalingParams(screenWidthDp)

                    val screenHeightPx =
                        with(density) { screenHeightDp.dp.roundToPx() }
                    val topPaddingPx = with(density) { topPaddingDp.roundToPx() }
                    val topScreenOffsetPx = screenHeightPx / 2 - topPaddingPx

                    val initialScrollPosition = ScalingLazyColumnState.ScrollPosition(
                        index = 0,
                        offsetPx = topScreenOffsetPx,
                    )
                    ScalingLazyColumnState(
                        initialScrollPosition = initialScrollPosition,
                        autoCentering = null,
                        anchorType = ScalingLazyListAnchorType.ItemStart,
                        rotaryMode = rotaryMode,
                        verticalArrangement = verticalArrangement,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = contentPadding,
                        scalingParams = scalingParams,
                        hapticsEnabled = hapticsEnabled,
                        reverseLayout = reverseLayout,
                        userScrollEnabled = userScrollEnabled,
                    )
                }
            }
        }
    }

    internal fun calculateVerticalOffsetForChip(
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

    fun responsiveScalingParams(screenWidthDp: Float): ScalingParams {
        val sizeRatio =
            ((screenWidthDp - 192) / (233 - 192).toFloat()).coerceIn(0f, 1.5f)
        val presetRatio = 0f

        val minElementHeight = lerp(0.2f, 0.157f, sizeRatio)
        val maxElementHeight =
            lerp(0.6f, 0.472f, sizeRatio).coerceAtLeast(minElementHeight)
        val minTransitionArea = lerp(0.35f, lerp(0.35f, 0.393f, presetRatio), sizeRatio)
        val maxTransitionArea = lerp(0.55f, lerp(0.55f, 0.593f, presetRatio), sizeRatio)

        val scalingParams = ScalingLazyColumnDefaults.scalingParams(
            minElementHeight = minElementHeight,
            maxElementHeight = maxElementHeight,
            minTransitionArea = minTransitionArea,
            maxTransitionArea = maxTransitionArea,
        )
        return scalingParams
    }

    internal val Padding12Pct = 0.1248f
    internal val Padding16Pct = 0.1664f
    internal val Padding20Pct = 0.2083f
    internal val Padding21Pct = 0.2188f
    internal val Padding31Pct = 0.3646f

    enum class ItemType(
        val topPaddingDp: Float,
        val bottomPaddingDp: Float,
        val paddingCorrection: Dp = 0.dp,
    ) {
        Card(Padding21Pct, Padding31Pct),
        Chip(Padding21Pct, Padding31Pct),
        CompactChip(
            topPaddingDp = Padding12Pct,
            bottomPaddingDp = Padding20Pct,
            paddingCorrection = (-8).dp,
        ),
        Icon(Padding12Pct, Padding21Pct),
        MultiButton(Padding21Pct, Padding20Pct),
        SingleButton(Padding12Pct, Padding20Pct),
        Text(Padding21Pct, Padding31Pct),
        Unspecified(0f, 0f),
    }

    @Composable
    public fun padding(
        first: ItemType = ItemType.Unspecified,
        last: ItemType = ItemType.Unspecified,
        horizontalPercent: Float = 0.052f,
    ): @Composable () -> PaddingValues {
        val configuration = LocalConfiguration.current
        val screenWidthDp = configuration.screenWidthDp.toFloat()
        val screenHeightDp = configuration.screenHeightDp.toFloat()

        return {
            val height = screenHeightDp.dp
            val horizontalPadding = screenWidthDp.dp * horizontalPercent

            val topPadding = if (first != ItemType.Unspecified) {
                first.topPaddingDp * height + first.paddingCorrection
            } else {
                if (configuration.isScreenRound) {
                    calculateVerticalOffsetForChip(screenWidthDp, horizontalPercent)
                } else {
                    32.dp
                }
            }

            val bottomPadding = if (last != ItemType.Unspecified) {
                last.bottomPaddingDp * height + first.paddingCorrection
            } else {
                if (configuration.isScreenRound) {
                    calculateVerticalOffsetForChip(
                        screenWidthDp,
                        horizontalPercent,
                    ) + 10.dp
                } else {
                    0.dp
                }
            }

            PaddingValues(
                top = topPadding,
                bottom = bottomPadding,
                start = horizontalPadding,
                end = horizontalPadding,
            )
        }
    }

    @Composable
    fun Modifier.listTextPadding() = this.padding(horizontal = 0.052f * LocalConfiguration.current.screenWidthDp.dp)
}
