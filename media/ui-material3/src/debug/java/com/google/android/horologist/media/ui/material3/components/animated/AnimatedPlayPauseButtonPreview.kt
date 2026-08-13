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

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.MaterialTheme
import com.google.android.horologist.compose.tools.WearPreview
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import kotlin.time.Duration.Companion.seconds

@WearPreview
@Composable
fun AnimatedPlayPauseProgressButtonPlayingPreview() {
  MaterialTheme {
    AnimatedPlayPauseProgressButton(
      onPlayClick = {},
      onPauseClick = {},
      playing = true,
      trackPositionUiModel = TrackPositionUiModel.Actual(0.6f, 60.seconds, 100.seconds),
      modifier = Modifier.size(60.dp),
    )
  }
}

@WearPreview
@Composable
fun AnimatedPlayPauseProgressButtonPausedPreview() {
  MaterialTheme {
    AnimatedPlayPauseProgressButton(
      onPlayClick = {},
      onPauseClick = {},
      playing = false,
      trackPositionUiModel = TrackPositionUiModel.Actual(0.6f, 60.seconds, 100.seconds),
      modifier = Modifier.size(60.dp),
    )
  }
}

@WearPreview
@Composable
fun AnimatedPlayPauseButtonPreview() {
  MaterialTheme {
    AnimatedPlayPauseButton(
      onPlayClick = {},
      onPauseClick = {},
      playing = true,
      modifier = Modifier.size(60.dp),
    )
  }
}
