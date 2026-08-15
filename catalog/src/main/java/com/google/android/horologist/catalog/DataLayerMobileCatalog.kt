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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.google.android.horologist.catalog

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.horologist.datalayer.phone.ui.prompt.installapp.InstallAppBottomSheet
import com.google.android.horologist.datalayer.phone.ui.prompt.installtile.InstallTileBottomSheet
import com.google.android.horologist.datalayer.phone.ui.prompt.reengage.ReEngageBottomSheet

/**
 * DataLayer Mobile — `:datalayer:phone-ui`.
 *
 * The three prompts a phone app shows to push its user towards the watch app. Separated from "Auth
 * Mobile" because they are a different job, even though they come from the same Gradle module:
 * grouping follows the area a reader is looking for, not the module boundary.
 */
@Composable
private fun WatchIcon() {
  Icon(Icons.Default.Notifications, contentDescription = null, Modifier.size(48.dp))
}

@DataLayerMobileCatalog
@Composable
internal fun DataLayerMobileInstallAppBottomSheet() {
  CatalogMobileTheme {
    InstallAppBottomSheet(
      image = { WatchIcon() },
      topMessage = "Get Horologist Chat on your watch",
      bottomMessage = "Reply to messages without reaching for your phone.",
      onDismissRequest = {},
      onConfirmation = {},
    )
  }
}

@DataLayerMobileCatalog
@Composable
internal fun DataLayerMobileInstallTileBottomSheet() {
  CatalogMobileTheme {
    InstallTileBottomSheet(
      image = { WatchIcon() },
      topMessage = "Add the Horologist tile",
      bottomMessage = "Swipe to your latest conversation straight from the watch face.",
      onDismissRequest = {},
      onConfirmation = {},
    )
  }
}

@DataLayerMobileCatalog
@Composable
internal fun DataLayerMobileReEngageBottomSheet() {
  CatalogMobileTheme {
    ReEngageBottomSheet(
      image = { WatchIcon() },
      topMessage = "You have unread messages on your watch",
      bottomMessage = "Open Horologist Chat to catch up.",
      onDismissRequest = {},
      onConfirmation = {},
    )
  }
}
