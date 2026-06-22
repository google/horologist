/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.auth.composables.material3.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.MaterialTheme

internal val horologist_primary: Color = Color(0xFFD3E3FD)
internal val horologist_on_primary: Color = Color(0xFF001944)
internal val horologist_primary_container: Color = Color(0xFF04409F)
internal val horologist_on_primary_container: Color = Color(0xFFD3E3FD)
internal val horologist_on_background: Color = Color(0xFFFFFFFF)
internal val horologist_surface_container: Color = Color(0xFF29303D)
internal val horologist_on_surface: Color = Color(0xFFEBF1FF)

@Composable
internal fun HorologistMaterialTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = horologist_primary,
            onPrimary = horologist_on_primary,
            primaryContainer = horologist_primary_container,
            onPrimaryContainer = horologist_on_primary_container,
            onBackground = horologist_on_background,
            surfaceContainer = horologist_surface_container,
            onSurface = horologist_on_surface,
        ),
        content = content,
    )
}
