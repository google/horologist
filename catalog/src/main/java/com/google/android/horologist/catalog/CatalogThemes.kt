/*
 * Copyright 2026 The Android Open Source Project
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

package com.google.android.horologist.catalog

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * The two themes the catalog renders under.
 *
 * Both form factors need one, and they are genuinely different types — `wear.compose.material3`'s
 * `MaterialTheme` is not `compose.material3`'s — which is the one place the shared module shows the
 * seam. Everything else about the catalog is form-factor agnostic.
 */
private val CatalogPrimary = Color(0xFFD3E3FD)

private val CatalogOnPrimary = Color(0xFF001944)

private val CatalogPrimaryContainer = Color(0xFF04409F)

private val CatalogOnPrimaryContainer = Color(0xFFD3E3FD)

private val CatalogSurfaceContainer = Color(0xFF29303D)

private val CatalogOnSurface = Color(0xFFEBF1FF)

/**
 * Wear theme, matching the palette the auth and media samples ship so the catalog and the samples
 * are comparable side by side.
 */
@Composable
internal fun CatalogWearTheme(content: @Composable () -> Unit) {
  androidx.wear.compose.material3.MaterialTheme(
    colorScheme =
      androidx.wear.compose.material3.MaterialTheme.colorScheme.copy(
        primary = CatalogPrimary,
        onPrimary = CatalogOnPrimary,
        primaryContainer = CatalogPrimaryContainer,
        onPrimaryContainer = CatalogOnPrimaryContainer,
        onBackground = Color.White,
        surfaceContainer = CatalogSurfaceContainer,
        onSurface = CatalogOnSurface,
      ),
    content = content,
  )
}

/** Wear Material 2 theme, for the `:compose-material` and `:composables` sections. */
@Composable
internal fun CatalogWearMaterial2Theme(content: @Composable () -> Unit) {
  androidx.wear.compose.material.MaterialTheme(content = content)
}

/** Mobile theme, for the `:datalayer:phone-ui` sections. */
@Composable
internal fun CatalogMobileTheme(content: @Composable () -> Unit) {
  androidx.compose.material3.MaterialTheme(content = content)
}
