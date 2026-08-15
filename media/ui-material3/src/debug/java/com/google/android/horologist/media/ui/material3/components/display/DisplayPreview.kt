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

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.google.android.horologist.compose.tools.WearPreview
import com.google.android.horologist.media.ui.state.model.MediaUiModel

@WearPreview
@Composable
fun TextMediaDisplayPreview() {
  MaterialTheme { TextMediaDisplay(title = "Starboy", subtitle = "The Weeknd ft. Daft Punk") }
}

@WearPreviewFontScales
@Composable
fun TextMediaDisplayLongTextPreview() {
  MaterialTheme {
    TextMediaDisplay(
      title = "Symphony No. 9 in D minor, Op. 125 'Choral'",
      subtitle = "Ludwig van Beethoven, Berlin Philharmonic",
    )
  }
}

@WearPreview
@Composable
fun TrackMediaDisplayPreview() {
  MaterialTheme {
    TrackMediaDisplay(
      media = MediaUiModel.Ready(id = "1", title = "Midnight City", subtitle = "M83")
    )
  }
}

@WearPreview
@Composable
fun LoadingMediaDisplayPreview() {
  MaterialTheme { LoadingMediaDisplay() }
}

@WearPreview
@Composable
fun NothingPlayingDisplayPreview() {
  MaterialTheme { NothingPlayingDisplay() }
}

@WearPreview
@Composable
fun MessageMediaDisplayPreview() {
  MaterialTheme { MessageMediaDisplay(message = "Custom Display Message") }
}
