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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.horologist.ai.ui.components.FailedResponseChip
import com.google.android.horologist.ai.ui.components.ResponseInProgressCard
import com.google.android.horologist.ai.ui.components.TextPromptDisplay
import com.google.android.horologist.ai.ui.components.TextResponseCard
import com.google.android.horologist.ai.ui.model.FailedResponseUiModel
import com.google.android.horologist.ai.ui.model.InProgressResponseUiModel
import com.google.android.horologist.ai.ui.model.TextPromptUiModel
import com.google.android.horologist.ai.ui.model.TextResponseUiModel
import java.time.Instant

/**
 * AI — `:ai:ui`.
 *
 * One sticker per state of a prompt/response turn: the prompt as the user sees it echoed back, the
 * spinner while the model thinks, the answer, and the failure. `submitted` is a fixed [Instant]
 * rather than the `Instant.now()` default so the render is reproducible.
 */
private val Submitted: Instant = Instant.parse("2026-07-29T09:41:00Z")

@Composable
private fun Card(content: @Composable () -> Unit) {
  CatalogWearTheme {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)) { content() }
    }
  }
}

@AiCatalog
@Composable
internal fun AiTextPromptDisplay() {
  Card {
    TextPromptDisplay(
      prompt = TextPromptUiModel(prompt = "How long is my next run?", submitted = Submitted)
    )
  }
}

@AiCatalog
@Composable
internal fun AiResponseInProgressCard() {
  Card { ResponseInProgressCard(inProgress = InProgressResponseUiModel) }
}

@AiCatalog
@Composable
internal fun AiTextResponseCard() {
  Card {
    TextResponseCard(
      textResponseUiModel = TextResponseUiModel(text = "Your next run is 8 km, starting at 18:00.")
    )
  }
}

/** The long-answer case — the reason the response card scrolls rather than clips. */
@AiCatalog
@Composable
internal fun AiTextResponseCardLong() {
  Card {
    TextResponseCard(
      textResponseUiModel =
        TextResponseUiModel(
          text =
            "Your next run is 8 km, starting at 18:00. The forecast is 14°C and light rain, so " +
              "a shell layer is worth taking. Based on last week you should hold about 5:30/km."
        )
    )
  }
}

@AiCatalog
@Composable
internal fun AiFailedResponseChip() {
  Card {
    FailedResponseChip(answer = FailedResponseUiModel(message = "No connection to the model"))
  }
}
