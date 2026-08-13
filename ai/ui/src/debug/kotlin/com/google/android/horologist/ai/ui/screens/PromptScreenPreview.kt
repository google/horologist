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

package com.google.android.horologist.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.ai.ui.model.FailedResponseUiModel
import com.google.android.horologist.ai.ui.model.ModelInstanceUiModel
import com.google.android.horologist.ai.ui.model.TextPromptUiModel
import com.google.android.horologist.ai.ui.model.TextResponseUiModel

@WearPreviewDevices
@Composable
fun PromptScreenSuccessPreview() {
  MaterialTheme {
    AppScaffold(
      modifier = Modifier.fillMaxSize().background(Color.Black),
      timeText = { TimeText() },
    ) {
      PromptScreen(
        uiState =
          PromptUiState(
            modelInfo = ModelInstanceUiModel("nano", "Gemini Nano"),
            messages =
              listOf(
                TextPromptUiModel("Summarize my daily meetings"),
                TextResponseUiModel(
                  "You have 3 meetings today:\n1. 10:00 AM - Sprint Planning\n2. 2:00 PM - Design Review\n3. 4:30 PM - 1:1 with Manager"
                ),
              ),
          ),
        promptEntry = {},
      )
    }
  }
}

@WearPreviewDevices
@Composable
fun PromptScreenStreamingPreview() {
  MaterialTheme {
    AppScaffold(
      modifier = Modifier.fillMaxSize().background(Color.Black),
      timeText = { TimeText() },
    ) {
      PromptScreen(
        uiState =
          PromptUiState(
            modelInfo = ModelInstanceUiModel("nano", "Gemini Nano"),
            messages = listOf(TextPromptUiModel("Summarize my daily meetings")),
            pending = true,
          ),
        promptEntry = {},
      )
    }
  }
}

@WearPreviewDevices
@Composable
fun PromptScreenErrorPreview() {
  MaterialTheme {
    AppScaffold(
      modifier = Modifier.fillMaxSize().background(Color.Black),
      timeText = { TimeText() },
    ) {
      PromptScreen(
        uiState =
          PromptUiState(
            modelInfo = ModelInstanceUiModel("nano", "Gemini Nano"),
            messages =
              listOf(
                TextPromptUiModel("Summarize my daily meetings"),
                FailedResponseUiModel(
                  message = "Network connection lost. Please check Bluetooth or Wi-Fi."
                ),
              ),
          ),
        promptEntry = {},
      )
    }
  }
}

@WearPreviewDevices
@Composable
fun PromptScreenMultiTurnPreview() {
  MaterialTheme {
    AppScaffold(
      modifier = Modifier.fillMaxSize().background(Color.Black),
      timeText = { TimeText() },
    ) {
      PromptScreen(
        uiState =
          PromptUiState(
            modelInfo = ModelInstanceUiModel("nano", "Gemini Nano"),
            messages =
              listOf(
                TextPromptUiModel("Summarize my daily meetings"),
                TextResponseUiModel(
                  "You have 3 meetings today:\n1. 10:00 AM - Sprint Planning\n2. 2:00 PM - Design Review\n3. 4:30 PM - 1:1 with Manager"
                ),
                TextPromptUiModel("When is the design review?"),
                TextResponseUiModel("The Design Review is at 2:00 PM."),
              ),
          ),
        promptEntry = {},
      )
    }
  }
}
