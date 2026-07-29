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

package com.google.android.horologist.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.CompactChip
import com.google.android.horologist.compose.material.SplitToggleChip
import com.google.android.horologist.compose.material.Title
import com.google.android.horologist.compose.material.ToggleChip
import com.google.android.horologist.compose.material.ToggleChipToggleControl

/**
 * Material — `:compose-material`.
 *
 * Horologist's opinionated wrappers over Wear Material 2. These are list items, so each sticker
 * renders in the column context they ship in rather than floating alone; a chip previewed at its
 * intrinsic width tells you nothing about the thing that actually varies, which is how it wraps.
 */
@Composable
private fun ListContext(content: @Composable () -> Unit) {
  CatalogWearMaterial2Theme {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        content()
      }
    }
  }
}

@MaterialCatalog
@Composable
internal fun MaterialTitle() {
  ListContext { Title(text = "Settings") }
}

@MaterialCatalog
@Composable
internal fun MaterialChip() {
  ListContext { Chip(label = "Downloads", onClick = {}) }
}

@MaterialCatalog
@Composable
internal fun MaterialChipWithSecondaryLabel() {
  ListContext {
    Chip(label = "Weather with You", onClick = {}, secondaryLabel = "Crowded House")
  }
}

/** Long labels are the failure mode worth having a permanent sticker for. */
@MaterialCatalog
@Composable
internal fun MaterialChipTruncated() {
  ListContext {
    Chip(
      label = "A label long enough to wrap onto a third line on a small round watch",
      onClick = {},
      secondaryLabel = "And a secondary label that is also rather long",
    )
  }
}

@MaterialCatalog
@Composable
internal fun MaterialCompactChip() {
  ListContext { CompactChip(label = "Show more", onClick = {}) }
}

@MaterialCatalog
@Composable
internal fun MaterialToggleChip() {
  ListContext {
    ToggleChip(
      checked = true,
      onCheckedChanged = {},
      label = "Download over Wi-Fi",
      toggleControl = ToggleChipToggleControl.Switch,
    )
  }
}

@MaterialCatalog
@Composable
internal fun MaterialSplitToggleChip() {
  ListContext {
    SplitToggleChip(
      checked = true,
      onCheckedChanged = {},
      label = "Podcasts",
      onClick = {},
      toggleControl = ToggleChipToggleControl.Checkbox,
    )
  }
}

@MaterialCatalog
@Composable
internal fun MaterialButton() {
  ListContext {
    Button(imageVector = Icons.Default.Check, contentDescription = "Confirm", onClick = {})
  }
}
