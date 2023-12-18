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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.OnFocusChange
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.PageIndicatorState
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText

@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    timeText: @Composable () -> Unit = { TimeText() },
    snackbar: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val scaffoldState = LocalScaffoldState.current.apply {
        appTimeText.value = timeText
    }

    Scaffold(
        modifier = modifier,
        timeText = scaffoldState.timeText,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            content()

            snackbar()
        }
    }
}

@Composable
fun ScreenScaffold(
    modifier: Modifier = Modifier,
    timeText: (@Composable () -> Unit)? = null,
    scrollState: ScrollableState? = null,
    pageIndicatorState: PageIndicatorState? = null,
    positionIndicator: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val scaffoldState = LocalScaffoldState.current
    val key = remember { Any() }

    OnFocusChange { focused ->
        if (focused) {
            scaffoldState.addScreenTimeText(key, timeText, scrollState)
        } else {
            scaffoldState.removeScreenTimeText(key)
        }
    }

    Scaffold(
        modifier = modifier,
        timeText = timeText,
        pageIndicator = {
            if (pageIndicatorState != null) {
                HorizontalPageIndicator(pageIndicatorState = pageIndicatorState)
            }
        },
        positionIndicator = {
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
        },
        content = content,
    )
}

@Composable
fun PageScaffold(
    modifier: Modifier = Modifier,
    timeText: (@Composable () -> Unit)? = null,
    scrollState: ScrollableState? = null,
    positionIndicator: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    ScreenScaffold(modifier = modifier, timeText, scrollState, null, positionIndicator, content)
}

internal class ScaffoldState() {
    fun removeScreenTimeText(key: Any) {
        screenContent.removeIf { it.key === key }
    }

    fun addScreenTimeText(
        key: Any,
        timeText: @Composable (() -> Unit)?,
        scrollState: ScrollableState?,
    ) {
        screenContent.add(PageContent(key, scrollState, timeText))
    }

    internal val appTimeText: MutableState<(@Composable (() -> Unit))> =
        mutableStateOf({ TimeText() })
    internal val screenContent = mutableStateListOf<PageContent>()

    val timeText: @Composable (() -> Unit)
        get() = {
            val (scrollState, timeText) = currentContent()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scrollAway {
                        scrollState ?: ScrollState(0)
                    },
            ) {
                timeText()
            }
        }

    private fun currentContent(): Pair<ScrollableState?, @Composable (() -> Unit)> {
        var resultTimeText: @Composable (() -> Unit)? = null
        var resultState: ScrollableState? = null
        screenContent.forEach {
            if (it.timeText != null) {
                resultTimeText = it.timeText
            }
            if (it.scrollState != null) {
                resultState = it.scrollState
            }
        }
        return Pair(resultState, resultTimeText ?: appTimeText.value)
    }

    internal data class PageContent(
        val key: Any,
        val scrollState: ScrollableState? = null,
        val timeText: (@Composable () -> Unit)? = null,
    )
}

internal val LocalScaffoldState = compositionLocalOf { ScaffoldState() }
