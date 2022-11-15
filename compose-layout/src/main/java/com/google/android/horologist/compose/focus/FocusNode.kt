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

package com.google.android.horologist.compose.focus

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
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
public fun FocusControl(requiresFocus: () -> Boolean, content: @Composable () -> Unit) {
    val focusManager = LocalFocusManager.current
    FocusComposableImpl(
        requiresFocus,
        onFocusChanged = { if (it) focusManager.clearFocus() },
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
public fun OnFocusChange(onFocusChanged: CoroutineScope.(Boolean) -> Unit) {
    FocusComposableImpl(
        focusEnabled = { true },
        onFocusChanged = onFocusChanged,
        content = {}
    )
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
public fun RequestFocusWhenActive(focusRequester: FocusRequester) {
    OnFocusChange {
        if (it) focusRequester.requestFocus()
    }
}

/**
 * Implements a node in the Focus control tree (either a [FocusControl] or [OnFocusChange]).
 * Each [FocusComposableImpl] maps to a [FocusNode] in our internal representation, this is used to:
 * 1) Check that our parent is focused (or we have no explicit parent), to see if we can be focused.
 * 2) See if we have children. If not, we are a leaf node and will forward focus status updates to
 * the onFocusChanged callback.
 */
@Composable
internal fun FocusComposableImpl(
    focusEnabled: () -> Boolean,
    onFocusChanged: CoroutineScope.(Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    val parent by rememberUpdatedState(LocalFocusNodeParent.current)
    // Node in our internal tree representation of the FocusComposableImpl
    val node = remember {
        FocusNode(
            focused = derivedStateOf {
                (parent?.focused?.value ?: true) && focusEnabled()
            }
        )
    }
    // Attach our note avatar to our parent's (and remove if we leave the composition).
    parent?.let {
        DisposableEffect(it) {
            it.children.add(node)
            onDispose {
                it.children.remove(node)
            }
        }
    }
    CompositionLocalProvider(LocalFocusNodeParent provides node, content = content)
    // If we are a leaf node, forward events to the onFocusChanged callback
    LaunchedEffect(node.focused.value) {
        if (node.children.isEmpty()) {
            onFocusChanged(node.focused.value)
        }
    }
}

// Internal class used to represent a node in our tree of focus-aware components.
internal class FocusNode(
    val focused: State<Boolean>,
    var children: SnapshotStateList<FocusNode> = mutableStateListOf()
)

// Composition Local used to keep a tree of focus-aware node (either controller nodes or
// focus requesting nodes).
// Nodes will register into their parent (unless they are the top ones) when they enter the
// composition and are removed when they leave it.
internal val LocalFocusNodeParent = compositionLocalOf<FocusNode?> { null }
