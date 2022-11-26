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

@file:OptIn(ExperimentalHorologistComposeLayoutApi::class)

package com.google.android.horologist.compose.layout

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyColumnDefaults
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.ScalingLazyListScope
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.ScalingParams
import com.google.android.horologist.compose.focus.RequestFocusWhenActive
import com.google.android.horologist.compose.layout.ScalingLazyColumnConfig.RotaryMode
import com.google.android.horologist.compose.navscaffold.ExperimentalHorologistComposeLayoutApi
import com.google.android.horologist.compose.rotaryinput.rotaryWithSnap
import com.google.android.horologist.compose.rotaryinput.toRotaryScrollAdapter

object TopAlignedDefaults {
    @Composable
    fun rememberTopAlignedConfig(rotaryMode: RotaryMode = RotaryMode.Snap): ScalingLazyColumnConfig {
        val density = LocalDensity.current
        val configuration = LocalConfiguration.current

        val focusRequester = FocusRequester()

        RequestFocusWhenActive(focusRequester = focusRequester)

        val flingBehavior = ScrollableDefaults.flingBehavior()

        return remember {
            val screenHeightPx = with(density) { configuration.screenHeightDp.dp.roundToPx() }
            val topPaddingPx = with(density) { 32.dp.roundToPx() }
            val topScreenOffsetPx = screenHeightPx / 2 - topPaddingPx

            ScalingLazyColumnConfig(
                state = ScalingLazyListState(
                    initialCenterItemIndex = 0,
                    initialCenterItemScrollOffset = topScreenOffsetPx
                ),
                anchorType = ScalingLazyListAnchorType.ItemStart,
                focusRequester = focusRequester,
                flingBehavior = flingBehavior,
                rotaryMode = rotaryMode
            )
        }
    }
}

@Stable
class ScalingLazyColumnConfig(
    val state: ScalingLazyListState = ScalingLazyListState(),
    val focusRequester: FocusRequester,
    val autoCentering: AutoCenteringParams? = AutoCenteringParams(),
    val anchorType: ScalingLazyListAnchorType = ScalingLazyListAnchorType.ItemCenter,
    val contentPadding: PaddingValues = PaddingValues(horizontal = 10.dp),
    val rotaryMode: RotaryMode? = RotaryMode.Fling,
    val reverseLayout: Boolean = false,
    val verticalArrangement: Arrangement.Vertical =
        Arrangement.spacedBy(
            space = 4.dp,
            alignment = if (!reverseLayout) Alignment.Top else Alignment.Bottom
        ),
    val horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    val flingBehavior: FlingBehavior,
    val userScrollEnabled: Boolean = true,
    val scalingParams: ScalingParams = ScalingLazyColumnDefaults.scalingParams(),
) {
    val modifier: Modifier
        @Composable
        get() = when (rotaryMode) {
            else -> Modifier.rotaryWithSnap(focusRequester, state.toRotaryScrollAdapter())
        }

    sealed interface RotaryMode {
        object Fling : RotaryMode
        object Snap : RotaryMode
    }
}

@Composable
fun ScalingLazyColumnWithConfig(
    modifier: Modifier = Modifier,
    config: ScalingLazyColumnConfig = TopAlignedDefaults.rememberTopAlignedConfig(
        RotaryMode.Fling
    ),
    content: ScalingLazyListScope.() -> Unit
) {
    ScalingLazyColumn(
        modifier,
        config.state,
        config.contentPadding,
        config.reverseLayout,
        config.verticalArrangement,
        config.horizontalAlignment,
        config.flingBehavior,
        config.userScrollEnabled,
        config.scalingParams,
        config.anchorType,
        config.autoCentering,
        content
    )
}