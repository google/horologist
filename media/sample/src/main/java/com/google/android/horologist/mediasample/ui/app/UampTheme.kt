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
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme

public val UampColors = ColorScheme(
    primary = Color(0xFFFFB0C9),
    onPrimary = Color(0xFF5F1142),
    primaryContainer = Color(0xFF7A2A59),
    onPrimaryContainer = Color(0xFFFFD9E5),
    secondary = Color(0xFFE0BDC9),
    onSecondary = Color(0xFF422C35),
    secondaryContainer = Color(0xFF5A424C),
    onSecondaryContainer = Color(0xFFFCD9E5),
    tertiary = Color(0xFFF5B993),
    onTertiary = Color(0xFF4E250A),
    tertiaryContainer = Color(0xFF693C1F),
    onTertiaryContainer = Color(0xFFFFDCC3),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF1F1A1C),
    onBackground = Color(0xFFEBE0E2),
    onSurface = Color(0xFFEBE0E2),
    onSurfaceVariant = Color(0xFFD3C2C6),
    outline = Color(0xFF9A8D91),
    outlineVariant = Color(0xFF4F4347),
)

@Composable
public fun UampTheme(block: @Composable () -> Unit) {
    MaterialTheme(colorScheme = UampColors) {
        block()
    }
}