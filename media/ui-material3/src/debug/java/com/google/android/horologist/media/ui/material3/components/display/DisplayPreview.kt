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

package com.google.android.horologist.media.ui.material3.components.display

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import androidx.wear.compose.ui.tooling.preview.WearPreviewSmallRound
import com.google.android.horologist.media.ui.state.model.MediaUiModel

@WearPreviewLargeRound
@WearPreviewSmallRound
@Composable
fun TextMediaDisplayPreview() {
  MaterialTheme {
    Box(modifier = Modifier.background(Color.Black).padding(8.dp)) {
      TextMediaDisplay(title = "Starboy", subtitle = "The Weeknd ft. Daft Punk")
    }
  }
}

@WearPreviewLargeRound
@WearPreviewFontScales
@Composable
fun TextMediaDisplayLongTextPreview() {
  MaterialTheme {
    Box(modifier = Modifier.background(Color.Black).padding(8.dp)) {
      TextMediaDisplay(
        title = "Symphony No. 9 in D minor, Op. 125 'Choral'",
        subtitle = "Ludwig van Beethoven, Berlin Philharmonic",
      )
    }
  }
}

@WearPreviewLargeRound
@WearPreviewSmallRound
@Composable
fun TrackMediaDisplayPreview() {
  MaterialTheme {
    Box(modifier = Modifier.background(Color.Black).padding(8.dp)) {
      TrackMediaDisplay(
        media = MediaUiModel.Ready(id = "1", title = "Midnight City", subtitle = "M83")
      )
    }
  }
}

@WearPreviewLargeRound
@WearPreviewSmallRound
@Composable
fun LoadingMediaDisplayPreview() {
  MaterialTheme {
    Box(modifier = Modifier.background(Color.Black).padding(8.dp)) { LoadingMediaDisplay() }
  }
}

@WearPreviewLargeRound
@WearPreviewSmallRound
@Composable
fun NothingPlayingDisplayPreview() {
  MaterialTheme {
    Box(modifier = Modifier.background(Color.Black).padding(8.dp)) { NothingPlayingDisplay() }
  }
}

@WearPreviewLargeRound
@WearPreviewSmallRound
@Composable
fun MessageMediaDisplayPreview() {
  MaterialTheme {
    Box(modifier = Modifier.background(Color.Black).padding(8.dp)) {
      MessageMediaDisplay(message = "Custom Display Message")
    }
  }
}
