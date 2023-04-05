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

@file:OptIn(ExperimentalWearFoundationApi::class)

package com.google.android.horologist.compose.focus

import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusRequester
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.HierarchicalFocusCoordinator
import kotlinx.coroutines.CoroutineScope

/**
 * Coordinates focus for any composables in [content] and determines which composable will get
 * focus.
 * [FocusControl]s can be nested, and form a tree, with an implicit root.
 * Focus-requiring components (i.e. components using [OnFocusChange] or [RequestFocusWhenActive])
 * should only be in the leaf [FocusControl]s, and there should be at most one per [FocusControl].
 * For [FocusControl] elements sharing a parent (or at the top level, sharing the implicit root
 * parent), only one should have focus enabled.
 * The selected [FocusControl] is the one that has focus enabled for itself and all ancestors, it
 * will pass focus to its focus-requiring component if it has one, or call
 * FocusManager#clearFocus() otherwise.
 * If no [FocusControl] is selected, there will be no change on the focus state.
 *
 * From https://android.googlesource.com/platform/frameworks/support/+/refs/changes/24/2171324/6/wear/compose/compose-foundation/src/commonMain/kotlin/androidx/wear/compose/foundation/Focus.kt
 *
 * Example usage:
 * @sample androidx.wear.compose.foundation.samples.FocusSample
 *
 * @param requiresFocus a function that returns true when the [content] is active in the
 * composition and requires the focus
 */
@Composable
@Deprecated(
    "Replaced with Wear Compose",
    replaceWith = ReplaceWith(
        "HierarchicalFocusCoordinator(requiresFocus, content)",
        imports = ["androidx.wear.compose.foundation.HierarchicalFocusCoordinator"]
    )
)
public fun FocusControl(requiresFocus: () -> Boolean, content: @Composable () -> Unit) {
    HierarchicalFocusCoordinator(
        requiresFocus = requiresFocus,
        content = content
    )
}

/**
 * Use as part of a focus-requiring component to register a callback to be notified when the
 * focus state changes.
 *
 * @param onFocusChanged callback to be invoked when the focus state changes, the parameter is the
 * new state (if true, we are becoming active and should request focus).
 */
@Composable
@Deprecated(
    "Replaced with Wear Compose",
    replaceWith = ReplaceWith(
        "OnFocusChange(onFocusChanged)",
        imports = ["androidx.wear.compose.foundation.OnFocusChange"]
    )
)
public fun OnFocusChange(onFocusChanged: CoroutineScope.(Boolean) -> Unit) {
    androidx.wear.compose.foundation.OnFocusChange(onFocusChanged = onFocusChanged)
}

/**
 * Use as part of a focus-requiring component to register a callback to automatically request
 * focus when this component is active.
 * Note that this will may call requestFocus in the provided FocusRequester, so that focusRequester
 * should be used in a .focusRequester modifier.
 *
 * @param focusRequester The associated [FocusRequester] to request focus on.
 */
@Composable
@Deprecated(
    "Replaced with Wear Compose",
    replaceWith = ReplaceWith(
        "RequestFocusWhenActive(focusRequester)",
        imports = ["androidx.wear.compose.foundation.RequestFocusWhenActive"]
    )
)
public fun RequestFocusWhenActive(focusRequester: FocusRequester) {
    androidx.wear.compose.foundation.RequestFocusWhenActive(focusRequester)
}

/**
 * Creates, remembers and returns a new [FocusRequester], that will have .requestFocus called
 * when the enclosing [HierarchicalFocusCoordinator] becomes active.
 * Note that the location you call this is important, in particular, which
 * [HierarchicalFocusCoordinator] is enclosing it. Also, this may call requestFocus in the returned
 * FocusRequester, so that focusRequester should be used in a .focusRequester modifier on a
 * Composable that is part of the composition.
 */
@Composable
@Deprecated(
    "Replaced with Wear Compose",
    replaceWith = ReplaceWith(
        "rememberActiveFocusRequester()",
        imports = ["androidx.wear.compose.foundation.rememberActiveFocusRequester"]
    )
)
public fun rememberActiveFocusRequester(): FocusRequester =
    androidx.wear.compose.foundation.rememberActiveFocusRequester()
