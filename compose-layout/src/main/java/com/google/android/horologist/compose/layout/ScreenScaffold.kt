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

@file:OptIn(ExperimentalWearFoundationApi::class)

package com.google.android.horologist.compose.layout

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.material3.ScreenScaffold as Material3ScreenScaffold

/**
 * Navigation Route (Screen) Scaffold to place *inside*
 * [androidx.wear.compose.navigation.composable]. The TimeText if set will override the
 * [AppScaffold] timeText.
 *
 * @param modifier the Scaffold modifier.
 * @param timeText the page specific time text.
 * @param scrollState the ScrollableState to show in a default PositionIndicator.
 * @param content the content block.
 */
@Composable
fun ScreenScaffold(
    modifier: Modifier = Modifier,
    timeText: (@Composable () -> Unit)? = null,
    scrollState: ScrollableState? = null,
    positionIndicator: (@Composable BoxScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    if (positionIndicator != null) {
        Material3ScreenScaffold(
            modifier = modifier,
            content = content,
            timeText = timeText,
            scrollIndicator = positionIndicator,
        )
    } else {
        when (scrollState) {
            is ScalingLazyColumnState -> {
                Material3ScreenScaffold(
                    modifier = modifier,
                    scrollState = scrollState.state,
                    content = content,
                    timeText = timeText
                )
            }

            is ScalingLazyListState -> {
                Material3ScreenScaffold(
                    modifier = modifier,
                    scrollState = scrollState,
                    content = content,
                    timeText = timeText
                )
            }

            is LazyListState -> {
                Material3ScreenScaffold(
                    modifier = modifier,
                    scrollState = scrollState,
                    content = content,
                    timeText = timeText
                )
            }

            is ScrollState -> {
                Material3ScreenScaffold(
                    modifier = modifier,
                    scrollState = scrollState,
                    content = content,
                    timeText = timeText
                )
            }

            else -> {
                Material3ScreenScaffold(modifier = modifier, content = content, timeText = timeText)
            }
        }
    }
}
