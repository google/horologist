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

package com.google.android.horologist.media.ui.material3.components.animated

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import androidx.wear.compose.ui.tooling.preview.WearPreviewSmallRound
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import kotlin.time.Duration.Companion.seconds

@WearPreviewLargeRound
@WearPreviewSmallRound
@Composable
fun AnimatedPlayPauseProgressButtonPlayingPreview() {
  MaterialTheme {
    Box(
      modifier = Modifier.background(Color.Black).padding(8.dp).size(60.dp),
      contentAlignment = Alignment.Center,
    ) {
      AnimatedPlayPauseProgressButton(
        onPlayClick = {},
        onPauseClick = {},
        playing = true,
        trackPositionUiModel = TrackPositionUiModel.Actual(0.6f, 60.seconds, 100.seconds),
      )
    }
  }
}

@WearPreviewLargeRound
@WearPreviewSmallRound
@Composable
fun AnimatedPlayPauseProgressButtonPausedPreview() {
  MaterialTheme {
    Box(
      modifier = Modifier.background(Color.Black).padding(8.dp).size(60.dp),
      contentAlignment = Alignment.Center,
    ) {
      AnimatedPlayPauseProgressButton(
        onPlayClick = {},
        onPauseClick = {},
        playing = false,
        trackPositionUiModel = TrackPositionUiModel.Actual(0.6f, 60.seconds, 100.seconds),
      )
    }
  }
}

@WearPreviewLargeRound
@WearPreviewSmallRound
@Composable
fun AnimatedPlayPauseButtonPreview() {
  MaterialTheme {
    Box(
      modifier = Modifier.background(Color.Black).padding(8.dp).size(60.dp),
      contentAlignment = Alignment.Center,
    ) {
      AnimatedPlayPauseButton(onPlayClick = {}, onPauseClick = {}, playing = true)
    }
  }
}
