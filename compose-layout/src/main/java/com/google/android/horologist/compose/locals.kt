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

package com.google.android.horologist.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.horologist.compose.devices.LocalHaptics
import com.google.android.horologist.compose.devices.LocalRotaryInput
import com.google.android.horologist.compose.devices.RotaryInput
import com.google.android.horologist.compose.devices.haptics

@Composable
public fun DeviceLocals(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val haptics = remember { haptics(context) }
    val rotaryInput = remember { RotaryInput.instance() }

    CompositionLocalProvider(
        LocalHaptics provides haptics,
        LocalRotaryInput provides rotaryInput
    ) {
        content()
    }
}