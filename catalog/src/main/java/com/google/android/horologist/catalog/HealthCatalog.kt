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
import com.google.android.horologist.health.composables.FormattedDurationText
import com.google.android.horologist.health.composables.model.MetricUiModel
import com.google.android.horologist.health.composables.screens.MetricsScreen
import java.time.Duration

/**
 * Health — `:health:composables`.
 *
 * The mid-exercise screen. Four metrics is the dense case and the one that decides whether the
 * baseline alignment in [MetricsScreen] holds, so it is the one previewed.
 */
@HealthCatalog
@Composable
internal fun HealthMetricsScreen() {
  CatalogWearMaterial2Theme {
    MetricsScreen(
      firstMetric = MetricUiModel(text = "12:34", bottomRightText = "min/km"),
      secondMetric = MetricUiModel(text = "142", bottomRightText = "bpm", color = Color(0xFFE8654E)),
      thirdMetric = MetricUiModel(text = "5.21", bottomRightText = "km"),
      fourthMetric = MetricUiModel(text = "412", bottomRightText = "kcal"),
    )
  }
}

@HealthCatalog
@Composable
internal fun HealthMetricsScreenSingle() {
  CatalogWearMaterial2Theme {
    MetricsScreen(firstMetric = MetricUiModel(text = "142", bottomRightText = "bpm"))
  }
}

@HealthCatalog
@Composable
internal fun HealthFormattedDurationText() {
  CatalogWearMaterial2Theme { FormattedDurationText(duration = Duration.ofMinutes(93)) }
}
