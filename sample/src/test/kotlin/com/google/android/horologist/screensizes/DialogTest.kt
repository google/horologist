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

package com.google.android.horologist.screensizes

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.rememberColumnState
import com.google.android.horologist.compose.material.AlertContent
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.ResponsiveDialogContent
import com.google.android.horologist.compose.material.ToggleChip
import com.google.android.horologist.compose.material.ToggleChipToggleControl
import com.google.android.horologist.compose.tools.Device
import com.google.android.horologist.screenshots.ScreenshotTestRule
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.math.sqrt

class DialogTest(device: Device) : ScreenSizeTest(
    device = device,
    showTimeText = false,
    recordMode = ScreenshotTestRule.RecordMode.Record
) {

    @Composable
    override fun Content() {
        // horologist AlertContent using ResponsiveDialogContent
        AlertContent(
            title = "Phone app is required",
            onCancelButtonClick = {},
            onOKButtonClick = {},
            message = "Tap the button below to install it on your phone.",
            columnState = rememberColumnState(responsiveDialog())
        )
    }

    @Test
    fun wearMaterial() {
        // androidx.wear.compose.material.dialog.Alert with no formatting
        runTest {
            Alert(
                title = { Text("Phone app is required") },
                negativeButton = {
                    Button(
                        imageVector = Icons.Default.Close,
                        contentDescription = "",
                        onClick = {},
                    )
                },
                positiveButton = {
                    Button(
                        imageVector = Icons.Default.Check,
                        contentDescription = "",
                        onClick = { },
                    )
                },
            ) {
                Text(
                    text = "Tap the button below to install it on your phone.",
                )
            }
        }
    }

    @Test
    fun longDialogScreen1() {
        lateinit var columnState: ScalingLazyColumnState

        runTest(testFn = {
            screenshotTestRule.interact {
                runBlocking {
                    columnState.state.scrollToItem(999, 0)
                }
            }

            screenshotTestRule.takeScreenshot()
        }) {
            columnState = rememberColumnState(responsiveDialog())

            ResponsiveDialogContent(
                title = {
                    Text(
                        text = "Turn on Bedtime mode?",
                        color = MaterialTheme.colors.onBackground,
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                    )
                },
                onOkButtonClick = {},
                onCancelButtonClick = {},
                okButtonContentDescription = stringResource(R.string.ok),
                cancelButtonContentDescription = stringResource(R.string.cancel),
                state = columnState,
            ) {
                item {
                    Text(
                        text = "Watch screen, tilt-to-wake, and touch are turned off. " +
                            "Only calls from starred contacts, repeat callers, " +
                            "and alarms will notify you.",
                        textAlign = TextAlign.Left,
                    )
                }
                item {
                    ToggleChip(
                        checked = false,
                        onCheckedChanged = {},
                        label = "Don't show again",
                        toggleControl = ToggleChipToggleControl.Checkbox,
                    )
                }
            }
        }
    }

    @ExperimentalHorologistApi
    public fun responsiveDialog(
        verticalArrangement: Arrangement.Vertical =
            Arrangement.spacedBy(
                space = 4.dp,
                alignment = Alignment.Top,
            ),
        horizontalPaddingPercent: Float = 0.0416f,
        rotaryMode: ScalingLazyColumnState.RotaryMode? = ScalingLazyColumnState.RotaryMode.Scroll,
        hapticsEnabled: Boolean = true,
        userScrollEnabled: Boolean = true,
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

                return remember {
                    val padding = screenWidthDp * horizontalPaddingPercent
                    val topPaddingDp: Dp = (screenWidthDp * 0.1456f).dp
                    val bottomPaddingDp: Dp = if (configuration.isScreenRound) {
                        calculateVerticalOffsetForChip(screenWidthDp, horizontalPaddingPercent)
                    } else {
                        0.dp
                    }
                    val contentPadding = PaddingValues(
                        start = padding.dp,
                        end = padding.dp,
                        top = topPaddingDp,
                        bottom = bottomPaddingDp,
                    )

                    val sizeRatio = ((screenWidthDp - 192) / (233 - 192).toFloat()).coerceIn(0f, 1.5f)
                    val presetRatio = 0f

                    val minElementHeight = lerp(0.2f, 0.157f, sizeRatio)
                    val maxElementHeight = lerp(0.6f, 0.472f, sizeRatio).coerceAtLeast(minElementHeight)
                    val minTransitionArea = lerp(0.35f, lerp(0.35f, 0.393f, presetRatio), sizeRatio)
                    val maxTransitionArea = lerp(0.55f, lerp(0.55f, 0.593f, presetRatio), sizeRatio)

                    val scalingParams = ScalingLazyColumnDefaults.scalingParams(
                        minElementHeight = minElementHeight,
                        maxElementHeight = maxElementHeight,
                        minTransitionArea = minTransitionArea,
                        maxTransitionArea = maxTransitionArea,
                    )

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
                        userScrollEnabled = userScrollEnabled,
                    )
                }
            }
        }
    }
}
