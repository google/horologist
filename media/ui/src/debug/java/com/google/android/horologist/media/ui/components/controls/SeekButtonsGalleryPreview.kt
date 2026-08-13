/*
 * Copyright 2022 The Android Open Source Project
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

package com.google.android.horologist.media.ui.components.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(name = "Seek Buttons Gallery", showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun SeekButtonsGalleryPreview() {
  Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      SeekBackButton(onClick = {}, seekButtonIncrement = SeekButtonIncrement.Five)
      SeekBackButton(onClick = {}, seekButtonIncrement = SeekButtonIncrement.Ten)
      SeekBackButton(onClick = {}, seekButtonIncrement = SeekButtonIncrement.Thirty)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      SeekForwardButton(onClick = {}, seekButtonIncrement = SeekButtonIncrement.Five)
      SeekForwardButton(onClick = {}, seekButtonIncrement = SeekButtonIncrement.Ten)
      SeekForwardButton(onClick = {}, seekButtonIncrement = SeekButtonIncrement.Thirty)
    }
  }
}
