/*
 * Copyright 2022 The Android Open Source Project
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

@file:OptIn(ExperimentalComposeUiApi::class)

package com.google.android.horologist.compose.navscaffold

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onPreRotaryScrollEvent
import kotlinx.coroutines.launch

/**
 * Adds Rotary support (for devices that have a rotating bezel) to scrollable screens.
 * The screen containing the scrollable item must request focus as appropriate, usually
 *
 * ```
 * LaunchedEffect(Unit) {
 *   focusRequester.requestFocus()
 * }
 * ```
 */
@Deprecated("Use .rotaryWithFling, .rotaryWithScroll or .rotaryWithSnap instead")
public fun Modifier.scrollableColumn(
    focusRequester: FocusRequester,
    scrollableState: ScrollableState
): Modifier = composed {
    val coroutineScope = rememberCoroutineScope()

    onPreRotaryScrollEvent {
        coroutineScope.launch {
            scrollableState.scrollBy(it.verticalScrollPixels)
            scrollableState.animateScrollBy(0f)
        }
        true
    }
        .focusRequester(focusRequester)
        .focusable()
}
