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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.horologist.media.ui.material3.components.animated.MarqueeTextMediaDisplay
import com.google.android.horologist.media.ui.material3.components.display.LoadingMediaDisplay
import com.google.android.horologist.media.ui.material3.components.display.MessageMediaDisplay
import com.google.android.horologist.media.ui.material3.components.display.NothingPlayingDisplay
import com.google.android.horologist.media.ui.material3.composables.PlaceholderButton

/**
 * Media — `:media:ui-material3`.
 *
 * The player's four display states (playing, loading, nothing playing, message) plus the
 * placeholder button the browse screens use while their content resolves. Wear-only, so the
 * section is just "Media".
 */
@MediaCatalog
@Composable
internal fun MediaMarqueeTextMediaDisplay() {
  CatalogWearTheme {
    MarqueeTextMediaDisplay(
      modifier = Modifier.fillMaxSize(),
      title = "Weather with You",
      artist = "Crowded House",
    )
  }
}

@MediaCatalog
@Composable
internal fun MediaLoadingMediaDisplay() {
  CatalogWearTheme { LoadingMediaDisplay(modifier = Modifier.fillMaxSize()) }
}

@MediaCatalog
@Composable
internal fun MediaNothingPlayingDisplay() {
  CatalogWearTheme { NothingPlayingDisplay(modifier = Modifier.fillMaxSize()) }
}

@MediaCatalog
@Composable
internal fun MediaMessageMediaDisplay() {
  CatalogWearTheme {
    MessageMediaDisplay(modifier = Modifier.fillMaxSize(), message = "Connect to Wi-Fi to download")
  }
}

@MediaCatalog
@Composable
internal fun MediaPlaceholderButton() {
  CatalogWearTheme { PlaceholderButton() }
}
