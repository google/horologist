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

@file:OptIn(ExperimentalWearMaterialApi::class)

package com.google.android.horologist.catalog

import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.horologist.composables.PlaceholderChip
import com.google.android.horologist.composables.ProgressIndicatorSegment
import com.google.android.horologist.composables.SegmentedProgressIndicator

/**
 * Composables — `:composables`.
 *
 * The widgets with no Wear Material equivalent. The segmented indicator is the one that most needs
 * a render: its geometry is drawn rather than laid out, so nothing but pixels tells you whether the
 * segment weights and padding angles came out right.
 */
private val ExerciseSegments =
  listOf(
    ProgressIndicatorSegment(weight = 1f, indicatorColor = Color(0xFF4C8DF6)),
    ProgressIndicatorSegment(weight = 1f, indicatorColor = Color(0xFF7BC86C)),
    ProgressIndicatorSegment(weight = 2f, indicatorColor = Color(0xFFF6C04C)),
    ProgressIndicatorSegment(weight = 1f, indicatorColor = Color(0xFFE8654E)),
  )

@ComposablesCatalog
@Composable
internal fun ComposablesSegmentedProgressIndicator() {
  CatalogWearMaterial2Theme {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      SegmentedProgressIndicator(
        trackSegments = ExerciseSegments,
        progress = 0.55f,
        modifier = Modifier.fillMaxSize().padding(4.dp),
        paddingAngle = 2f,
        strokeWidth = 8.dp,
      )
    }
  }
}

@ComposablesCatalog
@Composable
internal fun ComposablesSegmentedProgressIndicatorEmpty() {
  CatalogWearMaterial2Theme {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      SegmentedProgressIndicator(
        trackSegments = ExerciseSegments,
        progress = 0f,
        modifier = Modifier.fillMaxSize().padding(4.dp),
        paddingAngle = 2f,
        strokeWidth = 8.dp,
      )
    }
  }
}

@ComposablesCatalog
@Composable
internal fun ComposablesPlaceholderChip() {
  CatalogWearMaterial2Theme {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      PlaceholderChip(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
    }
  }
}
