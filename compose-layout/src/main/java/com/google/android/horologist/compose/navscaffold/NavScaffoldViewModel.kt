/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.horologist.compose.navscaffold

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.VignettePosition

class NavScaffoldViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    internal var scrollType by mutableStateOf<ScrollType?>(null)

    private lateinit var _scrollState: ScrollState
    private lateinit var _scalingLazyListState: ScalingLazyListState

    val scrollableState: ScrollableState?
        get() = when (scrollType) {
            ScrollType.ScalingLazyColumn -> scalingLazyListState
            ScrollType.ScrollState -> scrollState
            else -> null
        }

    internal val scrollState: ScrollState
        get() {
            check(scrollType == ScrollType.ScrollState)
            return _scrollState
        }

    internal val scalingLazyListState: ScalingLazyListState
        get() {
            check(scrollType == ScrollType.ScalingLazyColumn)
            return _scalingLazyListState
        }

    var vignettePosition: VignetteMode by mutableStateOf(VignetteMode.Off)

    var timeTextMode by mutableStateOf(TimeTextMode.FadeAway)

    var positionIndicatorMode by mutableStateOf(PositionIndicatorMode.WhileScrolling)

    internal var focusRequested: Boolean = false
    val focusRequester: FocusRequester by lazy {
        focusRequested = true
        FocusRequester()
    }

    internal fun initialiseScrollState(scrollStateBuilder: () -> ScrollState): ScrollState {
        check(scrollType == null || scrollType == ScrollType.ScrollState)

        if (scrollType == null) {
            scrollType = ScrollType.ScrollState

            _scrollState = savedStateHandle.saveable(
                key = "navScaffold.ScrollState",
                saver = ScrollState.Saver
            ) {
                scrollStateBuilder()
            }
        }

        return _scrollState
    }

    internal fun initialiseScalingLazyListState(scrollableStateBuilder: () -> ScalingLazyListState): ScalingLazyListState {
        check(scrollType == null || scrollType == ScrollType.ScalingLazyColumn)

        if (scrollType == null) {
            scrollType = ScrollType.ScalingLazyColumn

            _scalingLazyListState = savedStateHandle.saveable(
                key = "navScaffold.ScalingLazyListState",
                saver = ScalingLazyListState.Saver
            ) {
                scrollableStateBuilder()
            }
        }

        return _scalingLazyListState
    }

    internal enum class ScrollType {
        None, ScalingLazyColumn, ScrollState
    }

    enum class TimeTextMode {
        On, Off, FadeAway
    }

    enum class PositionIndicatorMode {
        On, Off, WhileScrolling
    }

    sealed interface VignetteMode {
        object Off : VignetteMode
        data class On(val position: VignettePosition) : VignetteMode
        // TODO add smart, scroll aware?
    }
}
