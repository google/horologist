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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import com.google.android.horologist.composables.DatePicker
import com.google.android.horologist.composables.MarqueeText
import com.google.android.horologist.composables.PlaceholderChip
import com.google.android.horologist.composables.ProgressIndicatorSegment
import com.google.android.horologist.composables.SegmentedProgressIndicator
import com.google.android.horologist.composables.SquareSegmentedProgressIndicator
import com.google.android.horologist.composables.TimePicker
import com.google.android.horologist.composables.TimePickerWith12HourClock
import java.time.LocalDate
import java.time.LocalTime

/**
 * Composables — `:composables`.
 *
 * The widgets with no Wear Material equivalent. The two segmented indicators are the ones that most
 * need a render: their geometry is drawn rather than laid out, so nothing but pixels tells you
 * whether the segment weights and padding angles came out right.
 *
 * The pickers take an explicit date/time rather than their `LocalDate.now()` / `LocalTime.now()`
 * defaults — a preview whose content depends on the wall clock produces a different PNG on every
 * run and shows up as a spurious diff on every PR.
 */
private val ExerciseSegments =
  listOf(
    ProgressIndicatorSegment(weight = 1f, indicatorColor = Color(0xFF4C8DF6)),
    ProgressIndicatorSegment(weight = 1f, indicatorColor = Color(0xFF7BC86C)),
    ProgressIndicatorSegment(weight = 2f, indicatorColor = Color(0xFFF6C04C)),
    ProgressIndicatorSegment(weight = 1f, indicatorColor = Color(0xFFE8654E)),
  )

@Composable
private fun Centred(content: @Composable () -> Unit) {
  CatalogWearMaterial2Theme {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
  }
}

// --- Progress ---------------------------------------------------------------------------------

@ComposablesCatalog
@Composable
internal fun ComposablesSegmentedProgressIndicator() {
  Centred {
    SegmentedProgressIndicator(
      trackSegments = ExerciseSegments,
      progress = 0.55f,
      modifier = Modifier.fillMaxSize().padding(4.dp),
      paddingAngle = 2f,
      strokeWidth = 8.dp,
    )
  }
}

@ComposablesCatalog
@Composable
internal fun ComposablesSegmentedProgressIndicatorEmpty() {
  Centred {
    SegmentedProgressIndicator(
      trackSegments = ExerciseSegments,
      progress = 0f,
      modifier = Modifier.fillMaxSize().padding(4.dp),
      paddingAngle = 2f,
      strokeWidth = 8.dp,
    )
  }
}

@ComposablesCatalog
@Composable
internal fun ComposablesSegmentedProgressIndicatorComplete() {
  Centred {
    SegmentedProgressIndicator(
      trackSegments = ExerciseSegments,
      progress = 1f,
      modifier = Modifier.fillMaxSize().padding(4.dp),
      paddingAngle = 2f,
      strokeWidth = 8.dp,
    )
  }
}

@ComposablesCatalog
@Composable
internal fun ComposablesSquareSegmentedProgressIndicator() {
  Centred {
    SquareSegmentedProgressIndicator(
      progress = 0.55f,
      modifier = Modifier.fillMaxSize().padding(12.dp),
      trackSegments = ExerciseSegments,
      strokeWidth = 8.dp,
    )
  }
}

// --- Pickers ----------------------------------------------------------------------------------

@ComposablesCatalog
@Composable
internal fun ComposablesDatePicker() {
  Centred { DatePicker(onDateConfirm = {}, date = LocalDate.of(2026, 7, 29)) }
}

@ComposablesCatalog
@Composable
internal fun ComposablesTimePicker() {
  Centred { TimePicker(onTimeConfirm = {}, time = LocalTime.of(9, 41, 0)) }
}

@ComposablesCatalog
@Composable
internal fun ComposablesTimePickerNoSeconds() {
  Centred {
    TimePicker(onTimeConfirm = {}, time = LocalTime.of(9, 41, 0), showSeconds = false)
  }
}

@ComposablesCatalog
@Composable
internal fun ComposablesTimePickerWith12HourClock() {
  Centred { TimePickerWith12HourClock(onTimeConfirm = {}, time = LocalTime.of(21, 41, 0)) }
}

// --- Text and placeholders ----------------------------------------------------------------------

@ComposablesCatalog
@Composable
internal fun ComposablesMarqueeText() {
  Centred {
    MarqueeText(
      text = "A title long enough that it has to scroll to be read on a watch",
      modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
    )
  }
}

@ComposablesCatalog
@Composable
internal fun ComposablesPlaceholderChip() {
  Centred {
    PlaceholderChip(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
  }
}
