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

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier

internal class ScaffoldState {
    fun removeScreen(key: Any) {
        screenContent.removeIf { it.key === key }
    }

    fun addScreen(
        key: Any,
        timeText: @Composable (() -> Unit)?,
        scrollState: ScrollableState?,
    ) {
        screenContent.add(ScreenContent(key, scrollState, timeText))
    }

    fun updateIfNeeded(
        key: Any,
        timeText: @Composable (() -> Unit)?,
        scrollState: ScrollableState?,
    ) {
        screenContent.find { it.key == key }?.let {
            it.timeText.value = timeText
            it.scrollState.value = scrollState
        }
    }

    internal val appTimeText: MutableState<(@Composable (() -> Unit))> =
        mutableStateOf({ ResponsiveTimeText() })
    internal val screenContent = mutableStateListOf<ScreenContent>()

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
            if (it.timeText.value != null) {
                resultTimeText = it.timeText.value
            }
            if (it.scrollState.value != null) {
                resultState = it.scrollState.value
            }
        }
        return Pair(resultState, resultTimeText ?: appTimeText.value)
    }

    internal data class ScreenContent(
        val key: Any,
        val scrollState: MutableState<ScrollableState?> = mutableStateOf(null),
        val timeText: MutableState<(@Composable () -> Unit)?> = mutableStateOf(null),
    ) {
        constructor(
            key: Any,
            scrollState: ScrollableState?,
            timeText: @Composable (() -> Unit)?
        ) : this(key, mutableStateOf(scrollState), mutableStateOf(timeText))
    }
}
