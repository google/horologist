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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.MaterialTheme
import com.google.android.horologist.compose.tools.WearPreview
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable.Companion.asPaintable
import com.google.android.horologist.media.ui.components.controls.SeekButtonIncrement

@WearPreview
@Composable
fun SeekButtonsPreview() {
  MaterialTheme {
    Row {
      SeekToPreviousButton(onClick = {})
      SeekBackButton(onClick = {}, seekButtonIncrement = SeekButtonIncrement.Ten)
      SeekForwardButton(onClick = {}, seekButtonIncrement = SeekButtonIncrement.Thirty)
      SeekToNextButton(onClick = {})
    }
  }
}

@WearPreview
@Composable
fun MediaButtonPreview() {
  MaterialTheme {
    MediaButton(onClick = {}, icon = Icons.Default.Album, contentDescription = "Album")
  }
}

@WearPreview
@Composable
fun MediaTitleIconPreview() {
  MaterialTheme {
    Box(modifier = Modifier.size(24.dp)) {
      MediaTitleIcon(paintableRes = Icons.Default.Album.asPaintable())
    }
  }
}
