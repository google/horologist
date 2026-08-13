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

package com.google.android.horologist.media.ui.material3.components

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
import com.google.android.horologist.media.ui.components.controls.SeekButtonIncrement
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import kotlin.time.Duration.Companion.seconds

@WearPreviewLargeRound
@WearPreviewSmallRound
@Composable
fun PodcastControlButtonsPlayingPreview() {
  MaterialTheme {
    Row(
      modifier = Modifier.fillMaxWidth().background(Color.Black),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      PodcastControlButtons(
        onPlayButtonClick = {},
        onPauseButtonClick = {},
        playPauseButtonEnabled = true,
        playing = true,
        onSeekBackButtonClick = {},
        seekBackButtonEnabled = true,
        seekBackButtonIncrement = SeekButtonIncrement.Ten,
        onSeekForwardButtonClick = {},
        seekForwardButtonEnabled = true,
        seekForwardButtonIncrement = SeekButtonIncrement.Thirty,
        trackPositionUiModel = TrackPositionUiModel.Actual(0.5f, 50.seconds, 100.seconds),
      )
    }
  }
}

@WearPreviewLargeRound
@WearPreviewSmallRound
@Composable
fun PodcastControlButtonsPausedPreview() {
  MaterialTheme {
    Row(
      modifier = Modifier.fillMaxWidth().background(Color.Black),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      PodcastControlButtons(
        onPlayButtonClick = {},
        onPauseButtonClick = {},
        playPauseButtonEnabled = true,
        playing = false,
        onSeekBackButtonClick = {},
        seekBackButtonEnabled = true,
        seekBackButtonIncrement = SeekButtonIncrement.Five,
        onSeekForwardButtonClick = {},
        seekForwardButtonEnabled = true,
        seekForwardButtonIncrement = SeekButtonIncrement.Five,
        trackPositionUiModel = TrackPositionUiModel.Actual(0.2f, 20.seconds, 100.seconds),
      )
    }
  }
}
