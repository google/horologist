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

package com.google.android.horologist.media.ui.material3.components.ambient

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import androidx.wear.compose.ui.tooling.preview.WearPreviewSmallRound

@WearPreviewLargeRound
@WearPreviewSmallRound
@Composable
fun AmbientMediaControlButtonsPlayingPreview() {
  MaterialTheme {
    Row(
      modifier = Modifier.fillMaxWidth().background(Color.Black),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      AmbientMediaControlButtons(
        playing = true,
        onPlayButtonClick = {},
        onPauseButtonClick = {},
        playPauseButtonEnabled = true,
        leftButton = { AmbientSeekToPreviousButton(onClick = {}, enabled = true) },
        rightButton = { AmbientSeekToNextButton(onClick = {}, enabled = true) },
      )
    }
  }
}

@WearPreviewLargeRound
@WearPreviewSmallRound
@Composable
fun AmbientMediaControlButtonsPausedPreview() {
  MaterialTheme {
    Row(
      modifier = Modifier.fillMaxWidth().background(Color.Black),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      AmbientMediaControlButtons(
        playing = false,
        onPlayButtonClick = {},
        onPauseButtonClick = {},
        playPauseButtonEnabled = true,
        leftButton = { AmbientSeekToPreviousButton(onClick = {}, enabled = true) },
        rightButton = { AmbientSeekToNextButton(onClick = {}, enabled = true) },
      )
    }
  }
}

@WearPreviewLargeRound
@WearPreviewSmallRound
@Composable
fun AmbientMediaControlButtonsDisabledPreview() {
  MaterialTheme {
    Row(
      modifier = Modifier.fillMaxWidth().background(Color.Black),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      AmbientMediaControlButtons(
        playing = false,
        onPlayButtonClick = {},
        onPauseButtonClick = {},
        playPauseButtonEnabled = false,
        leftButton = { AmbientSeekToPreviousButton(onClick = {}, enabled = false) },
        rightButton = { AmbientSeekToNextButton(onClick = {}, enabled = false) },
      )
    }
  }
}
