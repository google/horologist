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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.OnFocusChange
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold

/**
 * Navigation Route (Screen) Scaffold to place *inside*
 * [androidx.wear.compose.navigation.composable]. The TimeText if set will override the
 * [AppScaffold] timeText.
 *
 * @param modifier the Scaffold modifier.
 * @param timeText the page specific time text.
 * @param scrollState the ScrollableState to show in a default PositionIndicator.
 * @param positionIndicator set a non default PositionIndicator or disable with an no-op lambda.
 * @param content the content block.
 */
@Composable
fun ScreenScaffold(
    modifier: Modifier = Modifier,
    timeText: (@Composable () -> Unit)? = null,
    scrollState: ScrollableState? = null,
    positionIndicator: (@Composable () -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val scaffoldState = LocalScaffoldState.current

    val key = remember { Any() }

    // We need to update the scaffoldState with the proper scrollState
    key(scrollState) {
        DisposableEffect(key) {
            onDispose {
                scaffoldState.removeScreen(key)
            }
        }

        OnFocusChange { focused ->
            if (focused) {
                scaffoldState.addScreen(key, timeText, scrollState)
            } else {
                scaffoldState.removeScreen(key)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        positionIndicator = remember(scrollState, positionIndicator) {
            {
                if (positionIndicator != null) {
                    positionIndicator()
                } else if (scrollState is ScalingLazyColumnState) {
                    PositionIndicator(scalingLazyListState = scrollState.state)
                } else if (scrollState is ScalingLazyListState) {
                    PositionIndicator(scalingLazyListState = scrollState)
                } else if (scrollState is LazyListState) {
                    PositionIndicator(scrollState)
                } else if (scrollState is ScrollState) {
                    PositionIndicator(scrollState)
                }
            }
        },
        content = { Box { content() } },
    )
}
