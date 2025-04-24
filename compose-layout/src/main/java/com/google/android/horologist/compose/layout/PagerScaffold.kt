/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.compose.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.hierarchicalFocus
import androidx.wear.compose.foundation.pager.PagerState
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import com.google.android.horologist.compose.pager.PageScreenIndicatorState

/**
 * Pager Scaffold to place *above* a HorizontalPager.
 * The [TimeText] if set will override the AppScaffold timeText.
 *
 * @param modifier the Scaffold modifier.
 * @param timeText specific time text for the pages on this pager screen.
 * @param pagerState state for a HorizontalPager.
 * @param content the content block.
 */
@Composable
fun PagerScaffold(
    modifier: Modifier = Modifier,
    timeText: (@Composable () -> Unit)? = null,
    pagerState: PagerState? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val scaffoldState = LocalScaffoldState.current

    val key = remember { Any() }

    // Update the timeText & scrollInfoProvider if there is a change and the screen is already
    // present
    scaffoldState.updateIfNeeded(key, timeText = timeText, null)

    DisposableEffect(key) { onDispose { scaffoldState.removeScreen(key) } }

    Scaffold(
        modifier = modifier.fillMaxSize().hierarchicalFocus(true) { focused ->
            if (focused) {
                scaffoldState.addScreen(key, timeText = timeText, null)
            } else {
                scaffoldState.removeScreen(key)
            }
        },
        timeText = timeText,
        pageIndicator = {
            if (pagerState != null) {
                val pageIndicatorState = remember(pagerState) { PageScreenIndicatorState(pagerState) }

                HorizontalPageIndicator(
                    modifier = Modifier.padding(6.dp),
                    pageIndicatorState = pageIndicatorState,
                )
            }
        },
        content = { Box { content() } },
    )
}
