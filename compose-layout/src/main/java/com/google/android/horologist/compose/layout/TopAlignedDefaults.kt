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
import com.google.android.horologist.compose.focus.rememberActiveFocusRequester
import com.google.android.horologist.compose.layout.ScalingLazyColumnConfig.RotaryMode
import com.google.android.horologist.compose.navscaffold.ExperimentalHorologistComposeLayoutApi
import com.google.android.horologist.compose.rotaryinput.rotaryWithFling
import com.google.android.horologist.compose.rotaryinput.rotaryWithScroll
import com.google.android.horologist.compose.rotaryinput.rotaryWithSnap
import com.google.android.horologist.compose.rotaryinput.toRotaryScrollAdapter

public object TopAlignedDefaults {
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

@Stable
public class ScalingLazyColumnConfig internal constructor(
    public val initialScrollPosition: ScrollPosition = ScrollPosition(1, 0),
    public val autoCentering: AutoCenteringParams? = AutoCenteringParams(
        initialScrollPosition.index,
        initialScrollPosition.offsetPx
    ),
    public val anchorType: ScalingLazyListAnchorType = ScalingLazyListAnchorType.ItemCenter,
    public val contentPadding: PaddingValues = PaddingValues(horizontal = 10.dp),
    public val rotaryMode: RotaryMode = RotaryMode.Fling,
    public val reverseLayout: Boolean = false,
    public val verticalArrangement: Arrangement.Vertical =
        Arrangement.spacedBy(
            space = 4.dp,
            alignment = if (!reverseLayout) Alignment.Top else Alignment.Bottom
        ),
    public val horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    public val flingBehavior: FlingBehavior,
    public val userScrollEnabled: Boolean = true,
    public val scalingParams: ScalingParams = ScalingLazyColumnDefaults.scalingParams()
) {
    private var _state: ScalingLazyListState? = null
    public var state: ScalingLazyListState
        get() {
            if (_state == null) {
                _state = initialScrollPosition.toState()
            }
            return _state!!
        }
        set(value) {
            _state = value
        }

    @Composable
    public fun modifier(focusRequester: FocusRequester): Modifier = when (rotaryMode) {
        RotaryMode.Snap -> Modifier.rotaryWithSnap(focusRequester, state.toRotaryScrollAdapter())
        RotaryMode.Fling -> Modifier.rotaryWithFling(focusRequester, state)
        RotaryMode.Scroll -> Modifier.rotaryWithScroll(focusRequester, state)
    }

    public sealed interface RotaryMode {
        public object Fling : RotaryMode
        public object Snap : RotaryMode
        public object Scroll : RotaryMode
    }

    public data class ScrollPosition(
        val index: Int,
        val offsetPx: Int
    ) {
        public fun toState(): ScalingLazyListState {
            return ScalingLazyListState(
                index,
                offsetPx
            )
        }
    }
}

@Composable
public fun ScalingLazyColumnWithConfig(
    modifier: Modifier = Modifier,
    config: ScalingLazyColumnConfig = TopAlignedDefaults.rememberTopAlignedConfig(
        RotaryMode.Fling
    ),
    content: ScalingLazyListScope.() -> Unit
) {
    val focusRequester = rememberActiveFocusRequester()
    ScalingLazyColumn(
        modifier.then(config.modifier(focusRequester = focusRequester)),
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
