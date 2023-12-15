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

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.PageIndicatorState
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText

@Composable
fun AppScaffold(
    timeText: @Composable () -> Unit = { TimeText() },
    snackbar: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    Scaffold(timeText = timeText, content = {
        Box(modifier = Modifier.fillMaxSize()) {
            content()

            snackbar()
        }
    })
}

@Composable
fun ScreenScaffold(
    timeText: (@Composable () -> Unit)? = null,
    scrollState: ScrollableState? = null,
    pageIndicatorState: PageIndicatorState? = null,
    positionIndicator: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Scaffold(
        timeText = timeText,
        content = content
    )
}

@Composable
fun PageScaffold(
    timeText: (@Composable () -> Unit)? = null,
    scrollState: (() -> ScrollableState?)? = { null },
    positionIndicator: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Scaffold(timeText = timeText, content = content)
}