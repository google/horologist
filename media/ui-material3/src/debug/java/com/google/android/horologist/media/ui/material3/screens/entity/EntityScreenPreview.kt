/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.media.ui.material3.screens.entity

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.FilledTonalButton
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.media.ui.material3.composables.PlaceholderButton

@WearPreviewDevices
@Composable
fun EntityScreenPreviewLoaded() {
  MaterialTheme {
    AppScaffold {
      EntityScreen(
        entityScreenState =
          EntityScreenState.Loaded(
            mediaList = listOf("Track 1 - Intro", "Track 2 - Main Theme", "Track 3 - Outro")
          ),
        headerContent = { DefaultEntityScreenHeader(title = "Album Title") },
        loadingContent = {},
        mediaContent = { media ->
          FilledTonalButton(
            label = { Text(media) },
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
          )
        },
      )
    }
  }
}

@WearPreviewDevices
@Composable
fun EntityScreenPreviewLoading() {
  MaterialTheme {
    AppScaffold {
      EntityScreen(
        entityScreenState = EntityScreenState.Loading,
        headerContent = { DefaultEntityScreenHeader(title = "Album Title") },
        loadingContent = {
          items(3) {
            PlaceholderButton(
              modifier = Modifier.fillMaxWidth(),
              colors = ButtonDefaults.filledTonalButtonColors(),
            )
          }
        },
        mediaContent = {},
      )
    }
  }
}
