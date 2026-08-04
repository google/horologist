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

// A single preview catalog covering both form factors.
//
// The per-library `composeAiPreview` applications render whatever previews happen to live in that
// library's `src/debug`. This module is the curated counterpart: one place where every Horologist
// surface worth looking at is previewed with realistic data, on the device the surface actually
// ships to, and grouped so a reader can navigate by area.
//
// Grouping is carried by the multipreview annotations in `CatalogPreviews.kt` — one annotation per
// (area, form factor) pair, e.g. `@AuthWearCatalog` / `@AuthMobileCatalog`. The form factor is only
// spelled out when an area has both; a Wear-only area is just `@MediaCatalog`.
//
// Wear and mobile deliberately share one module rather than splitting into `:catalog:wear` and
// `:catalog:mobile`. The device is a per-preview property (`@Preview(device = …)`), not a
// per-module one, so a split would buy nothing and cost a second Gradle module, a second render
// invocation, and a duplicated dependency block.
plugins {
  id("com.android.library")
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.composeAiPreview)
}

composePreview {
  // Robolectric SDK 36 requires the render JVM to be JDK 21+; the project toolchain is 17.
  sdkVersion.set(35)
}

android {
  compileSdk = 36

  defaultConfig {
    // Raised above the library floor by wear-compose-material3 / media ui-material3.
    minSdk = 30
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  buildFeatures {
    compose = true
    buildConfig = false
  }

  testOptions { unitTests { isIncludeAndroidResources = true } }

  lint { checkReleaseBuilds = false }

  namespace = "com.google.android.horologist.catalog"
}

dependencies {
  implementation(platform(libs.compose.bom))

  // Wear areas.
  implementation(projects.ai.ui)
  implementation(projects.auth.composablesMaterial3)
  implementation(projects.auth.uiMaterial3)
  implementation(projects.composables)
  implementation(projects.composeLayout)
  implementation(projects.composeMaterial)
  implementation(projects.composeTools)
  implementation(projects.health.composables)
  implementation(projects.images.base)
  // `CoilPaintable`, for the Media section's artwork-bearing previews.
  implementation(projects.images.coil)
  implementation(projects.media.audioUiMaterial3)
  implementation(projects.media.audioUiModel)
  implementation(projects.media.uiMaterial3)
  implementation(projects.media.uiModel)

  // Mobile areas.
  implementation(projects.datalayer.phoneUi)

  implementation(libs.compose.foundation.foundation)
  implementation(libs.compose.material3)
  implementation(libs.compose.material.iconscore)
  implementation(libs.compose.runtime)
  implementation(libs.compose.ui)
  implementation(libs.compose.ui.graphics)
  implementation(libs.compose.ui.text)
  implementation(libs.compose.ui.unit)
  implementation(libs.composeAiPreviewAnnotations)
  implementation(libs.wearcompose.foundation)
  implementation(libs.wearcompose.material)
  implementation(libs.androidx.wear.compose.material3)

  // `@Preview` itself, plus the Wear device ids the catalog annotations reference. These are
  // `implementation`, not `debugImplementation`, because the previews are in `src/main` — the
  // catalog's whole content is previews, so there is no non-debug variant to keep them out of.
  implementation(libs.compose.ui.toolingpreview)
  implementation(libs.wearcompose.tooling)

  debugImplementation(libs.compose.ui.tooling)
}
