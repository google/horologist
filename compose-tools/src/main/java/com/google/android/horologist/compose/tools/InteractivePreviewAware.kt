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

package com.google.android.horologist.compose.tools

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalInspectionMode

public val LocalInteractivePreview: ProvidableCompositionLocal<Boolean> = compositionLocalOf { false }

public val LocalStaticPreview: ProvidableCompositionLocal<Boolean> = compositionLocalOf { false }

/**
 * Utility to determine the preview mode and set LocalInteractivePreview or
 * LocalStaticPreview.  All previews will start out as static, since they are identical
 * to the runtime until the second frame (when LaunchedEffect has run) when it will be set
 * correctly.
 */
@Composable
@ExperimentalHorologistComposeToolsApi
public fun InteractivePreviewAware(block: @Composable () -> Unit) {
    if (LocalInspectionMode.current) {
        var interactive by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            interactive = true
        }

        CompositionLocalProvider(
            LocalInteractivePreview.provides(interactive),
            LocalStaticPreview.provides(!interactive),
        ) {
            block()
        }
    } else {
        block()
    }
}
