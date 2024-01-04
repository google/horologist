/*
 * Copyright 2024 The Android Open Source Project
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

@file:OptIn(ExperimentalFoundationApi::class)

package com.google.android.horologist.compose.rotaryinput

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.google.android.horologist.compose.rotaryinput.RotaryInputConfigDefaults.DEFAULT_MIN_VALUE_CHANGE_DISTANCE_PX
import kotlinx.coroutines.launch

public fun Modifier.rotaryWithPager(
    state: PagerState,
    focusRequester: FocusRequester,
): Modifier = composed {
    val coroutineScope = rememberCoroutineScope()
    val haptics = rememberDefaultRotaryHapticFeedback()

    onRotaryInputAccumulated(minValueChangeDistancePx = DEFAULT_MIN_VALUE_CHANGE_DISTANCE_PX * 3) {
        val pageChange = if (it > 0f) 1 else -1

        if ((pageChange == 1 && state.currentPage >= state.pageCount - 1) || (pageChange == -1 && state.currentPage == 0)) {
            haptics.performHapticFeedback(RotaryHapticsType.ScrollLimit)
        } else {
            haptics.performHapticFeedback(RotaryHapticsType.ScrollItemFocus)

            coroutineScope.launch {
                state.animateScrollToPage(state.currentPage + pageChange)
            }
        }
    }
        .focusRequester(focusRequester)
        .focusable()
}
