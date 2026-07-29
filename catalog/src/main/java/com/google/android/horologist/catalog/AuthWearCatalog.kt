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

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.ButtonDefaults
import com.google.android.horologist.auth.composables.material3.buttons.GuestModeButton
import com.google.android.horologist.auth.composables.material3.buttons.SignInButton
import com.google.android.horologist.auth.composables.material3.models.AccountUiModel
import com.google.android.horologist.auth.composables.material3.screens.SelectAccountScreen
import com.google.android.horologist.auth.composables.material3.screens.SignInPlaceholderScreen
import com.google.android.horologist.auth.composables.material3.screens.SignedInConfirmationDialog
import com.google.android.horologist.auth.ui.material3.common.screens.prompt.SignInPromptScreen
import com.google.android.horologist.auth.ui.material3.common.screens.prompt.SignInPromptScreenState

/**
 * Auth Wear — `:auth:composables-material3` and `:auth:ui-material3`.
 *
 * The sign-in flow in the order a user meets it: the prompt that asks them to sign in, the account
 * picker, the placeholder shown while the account loads, and the confirmation that closes it out.
 */
private val JohnDoe = AccountUiModel(email = "john@example.com", name = "John Doe")

private val TimAndrews =
  AccountUiModel(email = "timandrews123@example.com", name = "Tim Andrews")

@AuthWearCatalog
@Composable
internal fun AuthWearSignInButton() {
  CatalogWearTheme { SignInButton(onClick = {}) }
}

@AuthWearCatalog
@Composable
internal fun AuthWearGuestModeButton() {
  CatalogWearTheme { GuestModeButton(onClick = {}) }
}

@AuthWearCatalog
@Composable
internal fun AuthWearSelectAccountScreen() {
  CatalogWearTheme {
    SelectAccountScreen(
      accounts = listOf(JohnDoe, TimAndrews),
      onAccountClicked = { _, _ -> },
      title = "Select Account",
    )
  }
}

@AuthWearCatalog
@Composable
internal fun AuthWearSignInPlaceholderScreen() {
  CatalogWearTheme { SignInPlaceholderScreen() }
}

@AuthWearCatalog
@Composable
internal fun AuthWearSignedInConfirmationDialog() {
  CatalogWearTheme {
    SignedInConfirmationDialog(onDismissOrTimeout = {}, name = "John", email = "john@example.com")
  }
}

/**
 * The long-name case. Truncation is the thing that actually breaks on a 227dp round screen, so it
 * gets its own sticker rather than being left to whoever remembers to try it.
 */
@AuthWearCatalog
@Composable
internal fun AuthWearSignedInConfirmationDialogTruncated() {
  CatalogWearTheme {
    SignedInConfirmationDialog(
      onDismissOrTimeout = {},
      name = "Wolfeschlegelsteinhausenbergerdorff",
      email = "wolfeschlegelsteinhausenbergerdorff@example.com",
    )
  }
}

@AuthWearCatalog
@Composable
internal fun AuthWearSignInPromptScreen() {
  CatalogWearTheme {
    SignInPromptScreen(
      state = SignInPromptScreenState.SignedOut,
      title = "Sign in",
      message = "Send messages and create chat groups with your friends",
      onIdleStateObserved = {},
      onAlreadySignedIn = {},
    ) {
      item { SignInButton(onClick = {}, colors = ButtonDefaults.filledTonalButtonColors()) }
      item { GuestModeButton(onClick = {}, colors = ButtonDefaults.filledTonalButtonColors()) }
    }
  }
}
