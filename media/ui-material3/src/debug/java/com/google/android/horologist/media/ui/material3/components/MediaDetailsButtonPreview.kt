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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material3.MaterialTheme
import com.google.android.horologist.compose.tools.WearPreview
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable.Companion.asPaintable

@WearPreview
@Composable
fun MediaDetailsButtonPreview() {
  MaterialTheme {
    MediaDetailsButton(
      title = "Very Long Song Title That Might Truncate",
      artworkPaintable = Icons.Default.MusicNote.asPaintable(),
      onClick = {},
      modifier = Modifier.fillMaxWidth(),
    )
  }
}
