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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.horologist.health.composables.FormattedDurationText
import com.google.android.horologist.health.composables.components.MetricDisplay
import com.google.android.horologist.health.composables.model.MetricUiModel
import com.google.android.horologist.health.composables.screens.MetricsScreen
import java.time.Duration

/**
 * Health — `:health:composables`.
 *
 * The mid-exercise screen and its parts. Four metrics is the dense case and the one that decides
 * whether the baseline alignment in [MetricsScreen] holds, so it leads; the one- and two-metric
 * screens are here because the layout re-centres rather than just dropping rows.
 *
 * Durations are fixed values, not `Duration.between(start, Instant.now())` — a preview that reads
 * the wall clock renders differently on every run and reports as a diff on every PR.
 */
private val Pace = MetricUiModel(text = "12:34", bottomRightText = "min/km")

private val HeartRate =
  MetricUiModel(text = "142", bottomRightText = "bpm", color = Color(0xFFE8654E))

private val Distance = MetricUiModel(text = "5.21", bottomRightText = "km")

private val Calories = MetricUiModel(text = "412", bottomRightText = "kcal")

@Composable
private fun Centred(content: @Composable () -> Unit) {
  CatalogWearMaterial2Theme {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
  }
}

@HealthCatalog
@Composable
internal fun HealthMetricsScreenFour() {
  CatalogWearMaterial2Theme {
    MetricsScreen(
      firstMetric = Pace,
      secondMetric = HeartRate,
      thirdMetric = Distance,
      fourthMetric = Calories,
    )
  }
}

@HealthCatalog
@Composable
internal fun HealthMetricsScreenTwo() {
  CatalogWearMaterial2Theme { MetricsScreen(firstMetric = Pace, secondMetric = HeartRate) }
}

@HealthCatalog
@Composable
internal fun HealthMetricsScreenSingle() {
  CatalogWearMaterial2Theme { MetricsScreen(firstMetric = HeartRate) }
}

/** The wide case — a five-digit metric is what pushes the unit label off the screen. */
@HealthCatalog
@Composable
internal fun HealthMetricsScreenWideValues() {
  CatalogWearMaterial2Theme {
    MetricsScreen(
      firstMetric = MetricUiModel(text = "12345", bottomRightText = "steps"),
      secondMetric = MetricUiModel(text = "1:23:45", bottomRightText = "elapsed"),
    )
  }
}

@HealthCatalog
@Composable
internal fun HealthMetricDisplay() {
  Centred { MetricDisplay(metric = HeartRate) }
}

@HealthCatalog
@Composable
internal fun HealthFormattedDurationText() {
  Centred { FormattedDurationText(duration = Duration.ofMinutes(93)) }
}

@HealthCatalog
@Composable
internal fun HealthFormattedDurationTextSubMinute() {
  Centred { FormattedDurationText(duration = Duration.ofSeconds(42)) }
}
