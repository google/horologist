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
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.VignettePosition

class NavScaffoldViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private var _scrollType by mutableStateOf<ScrollType?>(null)

    val scrollType: ScrollType
        get() = _scrollType ?: ScrollType.None

    internal var scrollState: ScrollState? = null
    internal var scalingLazyListState: ScalingLazyListState? = null

    val vignettePosition: VignettePosition? = VignettePosition.TopAndBottom

    fun scalingLazyListState(
        creator: () -> ScalingLazyListState = {
            ScalingLazyListState()
        }
    ): ScalingLazyListState {
        check(_scrollType == null || _scrollType == ScrollType.SLC)

        if (scalingLazyListState == null) {
            scalingLazyListState = savedStateHandle.saveable(
                key = "navScaffold.scalingLazyListState",
                saver = ScalingLazyListState.Saver
            ) {
                creator()
            }

            _scrollType = ScrollType.SLC
        }

        return scalingLazyListState!!
    }

    fun scrollState(
        creator: () -> ScrollState = {
            ScrollState(0)
        }
    ): ScrollState {
        check(_scrollType == null || _scrollType == ScrollType.SS)

        if (scrollState == null) {
            scrollState = savedStateHandle.saveable(
                key = "navScaffold.scrollState",
                saver = ScrollState.Saver
            ) {
                creator()
            }

            _scrollType = ScrollType.SS
        }
        return scrollState!!
    }

    fun <T> initialise(scrollStateBuilder: () -> T): T {
        TODO("Not yet implemented")
    }

    enum class ScrollType {
        None, SLC, SS
    }
}
