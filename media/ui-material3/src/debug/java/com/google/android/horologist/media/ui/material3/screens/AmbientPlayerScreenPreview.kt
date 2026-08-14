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

package com.google.android.horologist.media.ui.material3.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import androidx.wear.compose.ui.tooling.preview.WearPreviewSmallRound
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.material3.components.actions.SettingsButtonDefaults
import com.google.android.horologist.audio.ui.material3.components.actions.VolumeButton
import com.google.android.horologist.media.ui.material3.components.ambient.AmbientMediaControlButtons
import com.google.android.horologist.media.ui.material3.components.ambient.AmbientMediaInfoDisplay
import com.google.android.horologist.media.ui.material3.components.ambient.AmbientSeekToNextButton
import com.google.android.horologist.media.ui.material3.components.ambient.AmbientSeekToPreviousButton
import com.google.android.horologist.media.ui.material3.screens.player.PlayerScreen
import com.google.android.horologist.media.ui.state.model.MediaUiModel

@WearPreviewLargeRound
@WearPreviewSmallRound
@WearPreviewDevices
@Composable
fun AmbientPlayerScreenPreview() {
  MaterialTheme {
    AppScaffold(
      modifier = Modifier.fillMaxSize().background(Color.Black),
      timeText = { TimeText() },
    ) {
      ScreenScaffold {
        PlayerScreen(
          mediaDisplay = {
            AmbientMediaInfoDisplay(
              media = MediaUiModel.Ready(id = "1", title = "Midnight City", subtitle = "M83"),
              loading = false,
            )
          },
          controlButtons = {
            AmbientMediaControlButtons(
              onPlayButtonClick = {},
              onPauseButtonClick = {},
              playPauseButtonEnabled = true,
              playing = true,
              leftButton = { AmbientSeekToPreviousButton(onClick = {}, enabled = true) },
              rightButton = { AmbientSeekToNextButton(onClick = {}, enabled = true) },
            )
          },
          buttons = {
            VolumeButton(
              onVolumeClick = {},
              volumeUiState = VolumeUiState(5, 10),
              buttonColors = SettingsButtonDefaults.ambientButtonColors(),
              border = SettingsButtonDefaults.ambientButtonBorder(enabled = true),
            )
          },
        )
      }
    }
  }
}

@WearPreviewDevices
@Composable
fun AmbientPlayerScreenPausedPreview() {
  MaterialTheme {
    AppScaffold(
      modifier = Modifier.fillMaxSize().background(Color.Black),
      timeText = { TimeText() },
    ) {
      ScreenScaffold {
        PlayerScreen(
          mediaDisplay = {
            AmbientMediaInfoDisplay(
              media = MediaUiModel.Ready(id = "1", title = "Midnight City", subtitle = "M83"),
              loading = false,
            )
          },
          controlButtons = {
            AmbientMediaControlButtons(
              onPlayButtonClick = {},
              onPauseButtonClick = {},
              playPauseButtonEnabled = true,
              playing = false,
              leftButton = { AmbientSeekToPreviousButton(onClick = {}, enabled = true) },
              rightButton = { AmbientSeekToNextButton(onClick = {}, enabled = true) },
            )
          },
          buttons = {
            VolumeButton(
              onVolumeClick = {},
              volumeUiState = VolumeUiState(5, 10),
              buttonColors = SettingsButtonDefaults.ambientButtonColors(),
              border = SettingsButtonDefaults.ambientButtonBorder(enabled = true),
            )
          },
        )
      }
    }
  }
}

@WearPreviewDevices
@Composable
fun AmbientPlayerScreenNothingPlayingPreview() {
  MaterialTheme {
    AppScaffold(
      modifier = Modifier.fillMaxSize().background(Color.Black),
      timeText = { TimeText() },
    ) {
      ScreenScaffold {
        PlayerScreen(
          mediaDisplay = { AmbientMediaInfoDisplay(media = null, loading = false) },
          controlButtons = {
            AmbientMediaControlButtons(
              onPlayButtonClick = {},
              onPauseButtonClick = {},
              playPauseButtonEnabled = false,
              playing = false,
              leftButton = { AmbientSeekToPreviousButton(onClick = {}, enabled = false) },
              rightButton = { AmbientSeekToNextButton(onClick = {}, enabled = false) },
            )
          },
          buttons = {
            VolumeButton(
              onVolumeClick = {},
              volumeUiState = VolumeUiState(5, 10),
              enabled = false,
              buttonColors = SettingsButtonDefaults.ambientButtonColors(),
              border = SettingsButtonDefaults.ambientButtonBorder(enabled = false),
            )
          },
        )
      }
    }
  }
}
