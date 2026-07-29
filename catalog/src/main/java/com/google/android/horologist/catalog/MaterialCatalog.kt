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
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.ButtonSize
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.CompactChip
import com.google.android.horologist.compose.material.OutlinedChip
import com.google.android.horologist.compose.material.OutlinedCompactChip
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.google.android.horologist.compose.material.SecondaryTitle
import com.google.android.horologist.compose.material.SplitToggleChip
import com.google.android.horologist.compose.material.Title
import com.google.android.horologist.compose.material.ToggleButton
import com.google.android.horologist.compose.material.ToggleChip
import com.google.android.horologist.compose.material.ToggleChipToggleControl

/**
 * Material — `:compose-material`.
 *
 * Horologist's opinionated wrappers over Wear Material 2. These are list items, so each sticker
 * renders inside the column context they ship in rather than floating alone: a chip previewed at
 * its intrinsic width tells you nothing about the thing that actually varies, which is how it
 * wraps on a round screen.
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

// --- Headers ----------------------------------------------------------------------------------

@MaterialCatalog
@Composable
internal fun MaterialTitle() {
  ListContext { Title(text = "Settings") }
}

@MaterialCatalog
@Composable
internal fun MaterialSecondaryTitle() {
  ListContext { SecondaryTitle(text = "Playback") }
}

@MaterialCatalog
@Composable
internal fun MaterialResponsiveListHeader() {
  ListContext { ResponsiveListHeader { Title(text = "Downloads") } }
}

// --- Chips ------------------------------------------------------------------------------------

@MaterialCatalog
@Composable
internal fun MaterialChip() {
  ListContext { Chip(label = "Downloads", onClick = {}) }
}

@MaterialCatalog
@Composable
internal fun MaterialChipWithSecondaryLabel() {
  ListContext { Chip(label = "Weather with You", onClick = {}, secondaryLabel = "Crowded House") }
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
internal fun MaterialChipDisabled() {
  ListContext { Chip(label = "Not available offline", onClick = {}, enabled = false) }
}

@MaterialCatalog
@Composable
internal fun MaterialOutlinedChip() {
  ListContext { OutlinedChip(label = "Manage storage", onClick = {}) }
}

@MaterialCatalog
@Composable
internal fun MaterialCompactChip() {
  ListContext { CompactChip(label = "Show more", onClick = {}) }
}

@MaterialCatalog
@Composable
internal fun MaterialOutlinedCompactChip() {
  ListContext { OutlinedCompactChip(label = "Show more", onClick = {}) }
}

// --- Toggles ----------------------------------------------------------------------------------

@MaterialCatalog
@Composable
internal fun MaterialToggleChipSwitch() {
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
internal fun MaterialToggleChipRadio() {
  ListContext {
    ToggleChip(
      checked = true,
      onCheckedChanged = {},
      label = "High quality",
      secondaryLabel = "Uses more data",
      toggleControl = ToggleChipToggleControl.Radio,
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
internal fun MaterialToggleButton() {
  ListContext {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
      ToggleButton(text = "Aa", onCheckedChanged = {}, checked = true)
    }
  }
}

// --- Buttons ----------------------------------------------------------------------------------

@MaterialCatalog
@Composable
internal fun MaterialButton() {
  ListContext {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
      Button(imageVector = Icons.Default.Check, contentDescription = "Confirm", onClick = {})
    }
  }
}

@MaterialCatalog
@Composable
internal fun MaterialButtonLarge() {
  ListContext {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
      Button(
        imageVector = Icons.Default.Close,
        contentDescription = "Dismiss",
        onClick = {},
        buttonSize = ButtonSize.Large,
      )
    }
  }
}
