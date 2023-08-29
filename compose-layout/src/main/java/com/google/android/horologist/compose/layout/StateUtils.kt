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

package com.google.android.horologist.compose.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow

public object StateUtils {
    @Deprecated(
        message = "Replace with collectAsStateWithLifecycle",
        replaceWith = ReplaceWith(
            "flow.collectAsStateWithLifecycle()",
            "androidx.lifecycle.compose.collectAsStateWithLifecycle",
        ),
    )
    @Composable
    public fun <T> rememberStateWithLifecycle(
        flow: StateFlow<T>,
        lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
        minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    ): State<T> = flow.collectAsStateWithLifecycle(
        lifecycle = lifecycle,
        minActiveState = minActiveState,
    )
}
