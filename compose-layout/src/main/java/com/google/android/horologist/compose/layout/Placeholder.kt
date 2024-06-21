/*
 * Copyright 2024 The Android Open Source Project
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

@file:OptIn(ExperimentalWearMaterialApi::class, ExperimentalWearFoundationApi::class)

package com.google.android.horologist.compose.layout

import androidx.compose.runtime.Composable
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.OnFocusChange
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.PlaceholderState
import androidx.wear.compose.material.rememberPlaceholderState
import kotlinx.coroutines.launch

@Composable
fun rememberActivePlaceholderState(isContentReady: () -> Boolean): PlaceholderState {
    val placeholderState = rememberPlaceholderState {
        isContentReady()
    }

    OnFocusChange { focused ->
        if (focused) {
            if (!placeholderState.isShowContent) {
                launch {
                    placeholderState.startPlaceholderAnimation()
                }
            }
        }
    }

    return placeholderState
}
