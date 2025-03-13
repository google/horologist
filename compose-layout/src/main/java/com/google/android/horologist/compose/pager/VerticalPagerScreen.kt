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

package com.google.android.horologist.compose.pager

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.pager.PagerState
import androidx.wear.compose.foundation.pager.VerticalPager
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScreenScaffold

/**
 * A Wear Material Compliant Vertical Pager screen.
 *
 * Combines the Compose Foundation Pager, with a VerticalPageIndicator.
 */
@Composable
@ExperimentalHorologistApi
public fun VerticalPagerScreen(
    state: PagerState,
    modifier: Modifier = Modifier,
    beyondViewportPageCount: Int = 0,
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    key: ((index: Int) -> Any)? = null,
    content: @Composable (Int) -> Unit,
) {
    ScreenScaffold(
        modifier = modifier.fillMaxSize(),
        positionIndicator = {
            VerticalPageIndicator(
                pageIndicatorState = PageScreenIndicatorState(state),
                modifier = Modifier.padding(6.dp),
            )
        },
    ) {
        VerticalPager(
            modifier = Modifier
                .fillMaxSize(),
            state = state,
            beyondViewportPageCount = beyondViewportPageCount,
            userScrollEnabled = userScrollEnabled,
            reverseLayout = reverseLayout,
            key = key,
        ) { page ->
            ClippedBox(state) {
                content(page)
            }
        }
    }
}
