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
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider
import com.google.android.horologist.compose.tools.ThemeColors
import com.google.android.horologist.compose.tools.themeValues
import ee.schimke.composeai.preview.WearThemeCatalog

/**
 * Dark custom themes shared by both Wear Material generations in the catalog.
 *
 * Horologist already carries these palettes in `:compose-tools` for its preview and screenshot
 * coverage. Each provider installs the same palette into Wear Material 3 and Wear Material 2.
 */
private val BlueTheme = themeValues.single { it.index == 0 }.themeColors

private val LilacTheme = themeValues.single { it.index == 2 }.themeColors

private val GreenTheme = themeValues.single { it.index == 3 }.themeColors

@WearThemeCatalog(name = "Blue", group = "Horologist · Dark")
public class HorologistBlueThemeCatalog : PreviewWrapperProvider {
  @Composable
  override fun Wrap(content: @Composable () -> Unit) {
    HorologistCatalogTheme(colors = BlueTheme, content = content)
  }
}

@WearThemeCatalog(name = "Lilac", group = "Horologist · Dark")
public class HorologistLilacThemeCatalog : PreviewWrapperProvider {
  @Composable
  override fun Wrap(content: @Composable () -> Unit) {
    HorologistCatalogTheme(colors = LilacTheme, content = content)
  }
}

@WearThemeCatalog(name = "Green", group = "Horologist · Dark")
public class HorologistGreenThemeCatalog : PreviewWrapperProvider {
  @Composable
  override fun Wrap(content: @Composable () -> Unit) {
    HorologistCatalogTheme(colors = GreenTheme, content = content)
  }
}

@Composable
private fun HorologistCatalogTheme(colors: ThemeColors, content: @Composable () -> Unit) {
  androidx.wear.compose.material.MaterialTheme(colors = colors.toColors()) {
    val base = androidx.wear.compose.material3.MaterialTheme.colorScheme
    androidx.wear.compose.material3.MaterialTheme(
      colorScheme =
        base.copy(
          primary = colors.primary,
          onPrimary = colors.onPrimary,
          primaryContainer = colors.primaryVariant,
          onPrimaryContainer = colors.onPrimary,
          secondary = colors.secondary,
          onSecondary = colors.onSecondary,
          secondaryContainer = colors.secondaryVariant,
          onSecondaryContainer = colors.onSecondary,
          background = colors.background,
          onBackground = colors.onBackground,
          surfaceContainer = colors.surface,
          onSurface = colors.onSurface,
          error = colors.error,
          onError = colors.onError,
        ),
      content = content,
    )
  }
}

/**
 * The default theme is installed by the catalog annotation's preview wrapper. Keeping these small
 * pass-through seams avoids touching every preview body while allowing a selected custom provider
 * to remain the outermost and only theme installer.
 */
@Composable
internal fun CatalogWearTheme(content: @Composable () -> Unit) {
  content()
}

/** Wear Material 2 theme, for the `:compose-material` and `:composables` sections. */
@Composable
internal fun CatalogWearMaterial2Theme(content: @Composable () -> Unit) {
  content()
}

/** Mobile theme, for the `:datalayer:phone-ui` sections. */
@Composable
internal fun CatalogMobileTheme(content: @Composable () -> Unit) {
  androidx.compose.material3.MaterialTheme(content = content)
}
