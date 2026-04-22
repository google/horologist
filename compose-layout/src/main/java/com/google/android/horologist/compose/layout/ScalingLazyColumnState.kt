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

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults.snapFlingBehavior
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.ScalingParams
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults.behavior
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults.snapBehavior
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.responsiveScalingParams
import com.google.android.horologist.compose.layout.ScalingLazyColumnState.RotaryMode
import com.google.android.horologist.compose.layout.ScalingLazyColumnState.ScrollPosition
import androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults as WearScalingLazyColumnDefaults

/**
 * A Config and State object wrapping up all configuration for a [ScalingLazyColumn].
 * This allows defaults such as [ScalingLazyColumnDefaults.responsive].
 */
@ExperimentalHorologistApi
public class ScalingLazyColumnState(
    public val initialScrollPosition: ScrollPosition = ScrollPosition(1, 0),
    public val timeTextHomeOffset: ScrollPosition = initialScrollPosition,
    public val autoCentering: AutoCenteringParams? = AutoCenteringParams(
        initialScrollPosition.index,
        initialScrollPosition.offsetPx,
    ),
    public val anchorType: ScalingLazyListAnchorType = ScalingLazyListAnchorType.ItemCenter,
    public val contentPadding: PaddingValues = PaddingValues(horizontal = 10.dp),
    public val rotaryMode: RotaryMode? = RotaryMode.Scroll,
    public val reverseLayout: Boolean = false,
    public val verticalArrangement: Arrangement.Vertical =
        Arrangement.spacedBy(
            space = 4.dp,
            alignment = if (!reverseLayout) Alignment.Top else Alignment.Bottom,
        ),
    public val horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    public val userScrollEnabled: Boolean = true,
    public val scalingParams: ScalingParams = WearScalingLazyColumnDefaults.scalingParams(),
    public val hapticsEnabled: Boolean = true,
) : ScrollableState {
    private var _state: ScalingLazyListState? = null
    public var state: ScalingLazyListState
        get() {
            if (_state == null) {
                _state = ScalingLazyListState(
                    initialScrollPosition.index,
                    initialScrollPosition.offsetPx,
                )
            }
            return _state!!
        }
        set(value) {
            _state = value
        }

    override val canScrollBackward: Boolean
        get() = state.canScrollBackward
    override val canScrollForward: Boolean
        get() = state.canScrollForward
    override val isScrollInProgress: Boolean
        get() = state.isScrollInProgress

    override fun dispatchRawDelta(delta: Float): Float = state.dispatchRawDelta(delta)

    override suspend fun scroll(
        scrollPriority: MutatePriority,
        block: suspend ScrollScope.() -> Unit,
    ) {
        state.scroll(scrollPriority, block)
    }

    public sealed interface RotaryMode {
        public object Snap : RotaryMode
        public object Scroll : RotaryMode
    }

    public data class ScrollPosition(
        val index: Int,
        val offsetPx: Int,
    )

    public fun interface Factory {
        @Composable
        public fun create(): ScalingLazyColumnState
    }
}

@Composable
@Deprecated("Use rememberResponsiveColumnState")
public fun rememberColumnState(
    @Suppress("DEPRECATION") factory: ScalingLazyColumnState.Factory = ScalingLazyColumnDefaults.responsive(),
): ScalingLazyColumnState {
    val columnState = factory.create()

    columnState.state = rememberSaveable(saver = ScalingLazyListState.Saver) {
        columnState.state
    }

    return columnState
}

@Composable
public fun rememberResponsiveColumnState(
    first: ItemType,
    last: ItemType,
    verticalArrangement: Arrangement.Vertical =
        Arrangement.spacedBy(
            space = 4.dp,
            alignment = Alignment.Top,
        ),
    rotaryMode: RotaryMode? = RotaryMode.Scroll,
    hapticsEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    userScrollEnabled: Boolean = true,
    initialItemIndex: Int = 0,
): ScalingLazyColumnState {
    return rememberResponsiveColumnState(
        ScalingLazyColumnDefaults.padding(
            first = first,
            last = last,
        ),
        verticalArrangement,
        rotaryMode,
        hapticsEnabled,
        reverseLayout,
        userScrollEnabled,
        initialItemIndex,
    )
}

@Composable
public fun rememberResponsiveColumnState(
    contentPadding: @Composable () -> PaddingValues = {
        rememberResponsiveColumnPadding(
            first = ItemType.Unspecified,
            last = ItemType.Unspecified,
        )
    },
    verticalArrangement: Arrangement.Vertical =
        Arrangement.spacedBy(
            space = 4.dp,
            alignment = Alignment.Top,
        ),
    rotaryMode: RotaryMode? = RotaryMode.Scroll,
    hapticsEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    userScrollEnabled: Boolean = true,
    initialItemIndex: Int = 0,
): ScalingLazyColumnState {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.toFloat()
    val screenHeightDp = configuration.screenHeightDp.toFloat()

    val scalingParams = responsiveScalingParams(screenWidthDp)

    val contentPaddingCalculated = contentPadding()

    val screenHeightPx =
        with(density) { screenHeightDp.dp.roundToPx() }
    val topPaddingPx = with(density) { contentPaddingCalculated.calculateTopPadding().roundToPx() }
    val topScreenOffsetPx = screenHeightPx / 2 - topPaddingPx

    // Calculate the offset from which TimeText scrollAway should move
    val timeTextHomeOffset = ScrollPosition(
        index = 0,
        offsetPx = topScreenOffsetPx,
    )
    val initialScrollPosition = ScrollPosition(
        index = if (initialItemIndex > 0) initialItemIndex else 0,
        offsetPx = if (initialItemIndex > 0) 0 else topScreenOffsetPx,
    )

    // Try to put the top of the first item in the centre, but rely on
    // ScalingLazyColumn to stick to the ContentPadding.

    val columnState = ScalingLazyColumnState(
        initialScrollPosition = initialScrollPosition,
        timeTextHomeOffset = timeTextHomeOffset,
        autoCentering = null,
        anchorType = ScalingLazyListAnchorType.ItemStart,
        rotaryMode = rotaryMode,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = contentPaddingCalculated,
        scalingParams = scalingParams,
        hapticsEnabled = hapticsEnabled,
        reverseLayout = reverseLayout,
        userScrollEnabled = userScrollEnabled,
    )

    columnState.state = rememberSaveable(saver = ScalingLazyListState.Saver) {
        columnState.state
    }

    return columnState
}

@ExperimentalHorologistApi
@Composable
public fun ScalingLazyColumn(
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
    content: ScalingLazyListScope.() -> Unit,
) {
    val (rotaryBehavior, flingBehavior) = when (columnState.rotaryMode) {
        RotaryMode.Snap -> Pair(
            snapBehavior(
                scrollableState = columnState.state,
                hapticFeedbackEnabled = columnState.hapticsEnabled,
            ),
            snapFlingBehavior(state = columnState.state),
        )

        else -> Pair(
            behavior(
                scrollableState = columnState.state,
                hapticFeedbackEnabled = columnState.hapticsEnabled,
            ),
            ScrollableDefaults.flingBehavior(),
        )
    }

    ScalingLazyColumn(
        modifier = modifier.fillMaxSize(),
        state = columnState.state,
        contentPadding = columnState.contentPadding,
        reverseLayout = columnState.reverseLayout,
        verticalArrangement = columnState.verticalArrangement,
        horizontalAlignment = columnState.horizontalAlignment,
        flingBehavior = flingBehavior,
        userScrollEnabled = columnState.userScrollEnabled,
        scalingParams = columnState.scalingParams,
        anchorType = columnState.anchorType,
        autoCentering = columnState.autoCentering,
        rotaryScrollableBehavior = rotaryBehavior,
        content = content,
    )
}
