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

package com.google.android.horologist.media.ui.material3.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.MaterialTheme
import com.google.android.horologist.media.ui.material3.components.animated.AnimatedPlayPauseProgressButton
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import ee.schimke.composeai.preview.AnimatedPreview
import kotlin.time.Duration.Companion.seconds

@Preview(
  name = "Animated Play Pause Progress",
  device = "id:wearos_large_round",
  showBackground = true,
  backgroundColor = 0xFF000000,
)
@AnimatedPreview(durationMs = 2000, frameIntervalMs = 100, showCurves = false)
@Composable
fun AnimatedPlayPauseProgressButtonPreview() {
  MaterialTheme {
    Box(modifier = Modifier.background(Color.Black).padding(8.dp)) {
      PlayPauseProgressButton(
        onPlayClick = {},
        onPauseClick = {},
        enabled = true,
        playing = true,
        trackPositionUiModel = TrackPositionUiModel.Actual(0.5f, 50.seconds, 100.seconds),
      )
    }
  }
}

@Preview(
  name = "Animated Play Pause Progress Loading",
  device = "id:wearos_large_round",
  showBackground = true,
  backgroundColor = 0xFF000000,
)
@AnimatedPreview(durationMs = 2000, frameIntervalMs = 100, showCurves = false)
@Composable
fun AnimatedPlayPauseProgressButtonLoadingPreview() {
  MaterialTheme {
    Box(modifier = Modifier.background(Color.Black).padding(8.dp)) {
      PlayPauseProgressButton(
        onPlayClick = {},
        onPauseClick = {},
        enabled = true,
        playing = true,
        trackPositionUiModel = TrackPositionUiModel.Loading(showProgress = true),
      )
    }
  }
}

@Preview(
  name = "Animated Play Pause Morph",
  device = "id:wearos_large_round",
  showBackground = true,
  backgroundColor = 0xFF000000,
)
@AnimatedPreview(durationMs = 2000, frameIntervalMs = 100, showCurves = false)
@Composable
fun AnimatedPlayPauseMorphPreview() {
  MaterialTheme {
    Box(modifier = Modifier.background(Color.Black).padding(8.dp)) {
      AnimatedPlayPauseProgressButton(
        onPlayClick = {},
        onPauseClick = {},
        enabled = true,
        playing = true,
        trackPositionUiModel = TrackPositionUiModel.Actual(0.5f, 50.seconds, 100.seconds),
      )
    }
  }
}
