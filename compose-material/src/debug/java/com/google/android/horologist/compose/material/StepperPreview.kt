/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.compose.material

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound

@WearPreviewLargeRound
@Composable
fun StepperPreview() {
  MaterialTheme {
    Box(
      modifier = Modifier.fillMaxSize().background(Color.Black),
      contentAlignment = Alignment.Center,
    ) {
      Stepper(value = 5, onValueChange = {}, valueProgression = 0..10) { Text("Value: 5") }
    }
  }
}

@WearPreviewLargeRound
@Composable
fun StepperFloatPreview() {
  MaterialTheme {
    Box(
      modifier = Modifier.fillMaxSize().background(Color.Black),
      contentAlignment = Alignment.Center,
    ) {
      Stepper(value = 2.5f, onValueChange = {}, steps = 5, valueRange = 0f..5f) {
        Text("Value: 2.5")
      }
    }
  }
}
