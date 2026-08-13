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

package com.google.android.horologist.media.ui.material3.components.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import androidx.wear.compose.ui.tooling.preview.WearPreviewSmallRound
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable.Companion.asPaintable
import com.google.android.horologist.media.ui.components.controls.SeekButtonIncrement

@WearPreviewLargeRound
@WearPreviewSmallRound
@Composable
fun SeekButtonsPreview() {
  MaterialTheme {
    Box(modifier = Modifier.background(Color.Black).padding(8.dp)) {
      Row {
        SeekToPreviousButton(onClick = {})
        SeekBackButton(onClick = {}, seekButtonIncrement = SeekButtonIncrement.Ten)
        SeekForwardButton(onClick = {}, seekButtonIncrement = SeekButtonIncrement.Thirty)
        SeekToNextButton(onClick = {})
      }
    }
  }
}

@WearPreviewLargeRound
@WearPreviewSmallRound
@Composable
fun MediaButtonPreview() {
  MaterialTheme {
    Box(modifier = Modifier.background(Color.Black).padding(8.dp)) {
      MediaButton(onClick = {}, icon = Icons.Default.Album, contentDescription = "Album")
    }
  }
}

@WearPreviewLargeRound
@WearPreviewSmallRound
@Composable
fun MediaTitleIconPreview() {
  MaterialTheme {
    Box(modifier = Modifier.background(Color.Black).padding(8.dp).size(24.dp)) {
      MediaTitleIcon(paintableRes = Icons.Default.Album.asPaintable())
    }
  }
}
