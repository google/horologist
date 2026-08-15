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

import androidx.compose.ui.tooling.preview.Preview
import ee.schimke.composeai.preview.PreviewWrapperClass

/**
 * The catalog's section annotations: one per (area, form factor) pair.
 *
 * Each is a multipreview annotation that fixes three things at once, so an individual preview
 * function never repeats them:
 * - the **device**, which is what makes the form factor real rather than a naming convention;
 * - the **background**, dark for Wear (Wear OS is dark-first) and light for mobile;
 * - the **group**, which is the label this section shows up under in `previews.json` and in every
 *   downstream viewer.
 *
 * The form factor is only named when an area actually spans both — `Auth Wear` / `Auth Mobile`. An
 * area that only exists on the watch is just `Media`, `Material`, `Composables`, and reads as Wear
 * by default, matching how Horologist itself is described.
 *
 * Adding an area means adding an annotation here and a file next to the existing ones. Nothing in
 * the build wiring is per-area.
 */
private const val WEAR_DEVICE = "id:wearos_large_round"

private const val MOBILE_DEVICE = "id:pixel_7"

private const val WEAR_BACKGROUND = 0xFF000000

private const val MOBILE_BACKGROUND = 0xFFFFFBFE

private const val DEFAULT_THEME_PROVIDER =
  "com.google.android.horologist.catalog.HorologistBlueThemeCatalog"

// ---------------------------------------------------------------------------------------------
// Wear areas. No form factor in the name — Wear is the default reading.
// ---------------------------------------------------------------------------------------------

/** `:auth:composables-material3` and `:auth:ui-material3` — sign-in surfaces on the watch. */
@Preview(
  device = WEAR_DEVICE,
  backgroundColor = WEAR_BACKGROUND,
  showBackground = true,
  group = "Auth Wear",
)
@PreviewWrapperClass(DEFAULT_THEME_PROVIDER)
public annotation class AuthWearCatalog

/** `:media:ui-material3` — the player, its displays, and its controls. */
@Preview(
  device = WEAR_DEVICE,
  backgroundColor = WEAR_BACKGROUND,
  showBackground = true,
  group = "Media",
)
@PreviewWrapperClass(DEFAULT_THEME_PROVIDER)
public annotation class MediaCatalog

/** `:compose-material` — the Material 2 building blocks Horologist wraps. */
@Preview(
  device = WEAR_DEVICE,
  backgroundColor = WEAR_BACKGROUND,
  showBackground = true,
  group = "Material",
)
@PreviewWrapperClass(DEFAULT_THEME_PROVIDER)
public annotation class MaterialCatalog

/** `:composables` — the standalone widgets that have no Material equivalent. */
@Preview(
  device = WEAR_DEVICE,
  backgroundColor = WEAR_BACKGROUND,
  showBackground = true,
  group = "Composables",
)
@PreviewWrapperClass(DEFAULT_THEME_PROVIDER)
public annotation class ComposablesCatalog

/** `:health:composables` — exercise metrics and durations. */
@Preview(
  device = WEAR_DEVICE,
  backgroundColor = WEAR_BACKGROUND,
  showBackground = true,
  group = "Health",
)
@PreviewWrapperClass(DEFAULT_THEME_PROVIDER)
public annotation class HealthCatalog

/** `:media:audio-ui-material3` — volume and audio-output surfaces. */
@Preview(
  device = WEAR_DEVICE,
  backgroundColor = WEAR_BACKGROUND,
  showBackground = true,
  group = "Audio",
)
@PreviewWrapperClass(DEFAULT_THEME_PROVIDER)
public annotation class AudioCatalog

/** `:compose-layout` — the scaffolding: scrolling columns, pagers, time text. */
@Preview(
  device = WEAR_DEVICE,
  backgroundColor = WEAR_BACKGROUND,
  showBackground = true,
  group = "Layout",
)
@PreviewWrapperClass(DEFAULT_THEME_PROVIDER)
public annotation class LayoutCatalog

/** `:ai:ui` — the on-watch prompt/response surfaces. */
@Preview(
  device = WEAR_DEVICE,
  backgroundColor = WEAR_BACKGROUND,
  showBackground = true,
  group = "AI",
)
@PreviewWrapperClass(DEFAULT_THEME_PROVIDER)
public annotation class AiCatalog

// ---------------------------------------------------------------------------------------------
// Mobile areas. These carry the form factor explicitly, because the phone is the exception.
// ---------------------------------------------------------------------------------------------

/** `:datalayer:phone-ui` — the phone-side sign-in prompt. */
@Preview(
  device = MOBILE_DEVICE,
  backgroundColor = MOBILE_BACKGROUND,
  showBackground = true,
  group = "Auth Mobile",
)
public annotation class AuthMobileCatalog

/** `:datalayer:phone-ui` — install / re-engage prompts pushed from the phone to the watch. */
@Preview(
  device = MOBILE_DEVICE,
  backgroundColor = MOBILE_BACKGROUND,
  showBackground = true,
  group = "DataLayer Mobile",
)
public annotation class DataLayerMobileCatalog
