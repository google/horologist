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

package com.google.android.horologist.compose.pager

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import com.google.accompanist.pager.PagerState
import com.google.android.horologist.compose.devices.LocalHaptics
import com.google.android.horologist.compose.devices.LocalRotaryInput
import kotlinx.coroutines.launch

@Composable
public fun Modifier.pageScrollable(focusRequester: () -> FocusRequester, state: PagerState): Modifier {
    val coroutineScope = rememberCoroutineScope()

    val haptics = LocalHaptics.current
    val rotaryInput = LocalRotaryInput.current

    if (rotaryInput != null) {
        val pagerScrollHandler = remember {
            PagerScrollHandler(
                pagerState = state,
                coroutineScope = coroutineScope,
                rotaryInput = rotaryInput,
                haptics = haptics
            )
        }

        return this
            .onRotaryScrollEvent {
                coroutineScope.launch {
                    pagerScrollHandler.scrollBy(it.verticalScrollPixels)
                }
                true
            }
            .focusRequester(focusRequester())
            .focusable()
    } else {
        return this
    }
}