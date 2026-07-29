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

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.horologist.datalayer.phone.ui.prompt.signin.SignInBottomSheet

/**
 * Auth Mobile — the phone half of the sign-in story, from `:datalayer:phone-ui`.
 *
 * This is the section that makes the shared-module question concrete: it is `compose.material3`,
 * not `wear.compose.material3`, laid out on a phone, and it sits in the same module and the same
 * render pass as the Wear auth stickers above it. The only thing that differs is the device on the
 * annotation.
 *
 * These are `ModalBottomSheet`s, so they compose into their own window rather than into the host
 * activity's content view — see `catalog/README.md` for what that means for the render.
 */
@AuthMobileCatalog
@Composable
internal fun AuthMobileSignInBottomSheet() {
  CatalogMobileTheme {
    SignInBottomSheet(
      image = { Icon(Icons.Default.Email, contentDescription = null, Modifier.size(48.dp)) },
      topMessage = "Sign in to Horologist Chat",
      bottomMessage = "Signing in on your phone signs you in on your watch too.",
      onDismissRequest = {},
      onConfirmation = {},
    )
  }
}

@AuthMobileCatalog
@Composable
internal fun AuthMobileSignInBottomSheetCustomLabels() {
  CatalogMobileTheme {
    SignInBottomSheet(
      image = { Icon(Icons.Default.Email, contentDescription = null, Modifier.size(48.dp)) },
      topMessage = "Sign in to Horologist Chat",
      bottomMessage = "Signing in on your phone signs you in on your watch too.",
      onDismissRequest = {},
      onConfirmation = {},
      positiveButtonLabel = "Sign in",
      negativeButtonLabel = "Not now",
    )
  }
}
