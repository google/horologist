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

package com.google.android.horologist.mediasample.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

public val UampColors = Colors(
    primary = Color(0xFF981F68),
    primaryVariant = Color(0xFF66003d),
    secondary = Color(0xFF981F68),
    error = Color(0xFFE24444),
    onPrimary = Color.White,
    onSurfaceVariant = Color(0xFFDADCE0),
    surface = Color(0xFF303133),
    onError = Color.Black
)

@Composable
public fun UampTheme(block: @Composable () -> Unit) {
    MaterialTheme(colors = UampColors) {
        block()
    }
}
