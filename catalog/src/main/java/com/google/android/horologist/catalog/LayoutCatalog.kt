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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.fillMaxRectangle
import com.google.android.horologist.compose.layout.rememberColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.Title
import com.google.android.horologist.compose.pager.PagerScreen

/**
 * Layout — `:compose-layout`.
 *
 * The scaffolding, which is exactly the part a static screenshot is worst at describing and best at
 * showing: how far the responsive `ScalingLazyColumn` insets its first item on a round screen, and
 * where `fillMaxRectangle` puts the inscribed rectangle.
 */
@LayoutCatalog
@Composable
internal fun LayoutScalingLazyColumn() {
  CatalogWearMaterial2Theme {
    ScalingLazyColumn(columnState = rememberColumnState()) {
      item { Title(text = "Library") }
      items(4) { index -> Chip(label = "Playlist ${index + 1}", onClick = {}) }
    }
  }
}

@LayoutCatalog
@Composable
internal fun LayoutPagerScreen() {
  CatalogWearMaterial2Theme {
    PagerScreen(state = rememberPagerState(initialPage = 0) { 3 }) { page ->
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Page ${page + 1}")
      }
    }
  }
}

/** The inscribed rectangle, tinted so the inset it leaves on a round screen is the subject. */
@LayoutCatalog
@Composable
internal fun LayoutFillMaxRectangle() {
  CatalogWearMaterial2Theme {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Box(
        modifier = Modifier.fillMaxRectangle().background(Color(0xFF04409F)),
        contentAlignment = Alignment.Center,
      ) {
        Text("fillMaxRectangle")
      }
    }
  }
}
