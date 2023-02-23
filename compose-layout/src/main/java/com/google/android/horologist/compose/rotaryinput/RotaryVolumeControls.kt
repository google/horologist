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

package com.google.android.horologist.compose.rotaryinput

import androidx.compose.foundation.focusable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.wear.compose.foundation.RequestFocusWhenActive

internal fun Modifier.rotaryVolumeControls(
    focusRequester: FocusRequester,
    onVolumeUp: () -> Unit,
    onVolumeDown: () -> Unit
) = composed {
    RequestFocusWhenActive(focusRequester)
    onRotaryInputAccumulated { pixels ->
        when {
            pixels > 0 -> onVolumeUp()
            pixels < 0 -> onVolumeDown()
        }
        }
        .focusRequester(focusRequester)
        .focusable()
}
