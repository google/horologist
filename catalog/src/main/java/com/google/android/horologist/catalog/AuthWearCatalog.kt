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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Text
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
 * The sign-in flow in the order a user meets it: the prompt asking them to sign in, the account
 * picker, the placeholder while an account loads, and the confirmation that closes it out. Each
 * screen gets its states, not just its happy path — the account picker in particular lays out
 * differently for one account, several, and a long address that has to wrap.
 */
private val JohnDoe = AccountUiModel(email = "john@example.com", name = "John Doe")

private val TimAndrews = AccountUiModel(email = "timandrews123@example.com", name = "Tim Andrews")

private val NoName = AccountUiModel(email = "maggie@example.com")

@Composable
private fun Centred(content: @Composable () -> Unit) {
  CatalogWearTheme {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
  }
}

// --- Buttons ----------------------------------------------------------------------------------

@AuthWearCatalog
@Composable
internal fun AuthWearSignInButton() {
  Centred { SignInButton(onClick = {}) }
}

@AuthWearCatalog
@Composable
internal fun AuthWearSignInButtonDisabled() {
  Centred { SignInButton(onClick = {}, enabled = false) }
}

@AuthWearCatalog
@Composable
internal fun AuthWearGuestModeButton() {
  Centred { GuestModeButton(onClick = {}) }
}

// --- Account picker -----------------------------------------------------------------------------

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
internal fun AuthWearSelectAccountScreenSingle() {
  CatalogWearTheme {
    SelectAccountScreen(
      accounts = listOf(JohnDoe),
      onAccountClicked = { _, _ -> },
      title = "Select Account",
    )
  }
}

/** No display name: the row collapses to a single line, which changes the whole list rhythm. */
@AuthWearCatalog
@Composable
internal fun AuthWearSelectAccountScreenEmailOnly() {
  CatalogWearTheme {
    SelectAccountScreen(
      accounts = listOf(NoName, JohnDoe),
      onAccountClicked = { _, _ -> },
      title = "Select Account",
    )
  }
}

@AuthWearCatalog
@Composable
internal fun AuthWearSelectAccountScreenLongEmail() {
  CatalogWearTheme {
    SelectAccountScreen(
      accounts =
        listOf(
          AccountUiModel(
            email = "wolfeschlegelsteinhausenbergerdorff@example.com",
            name = "Wolfeschlegelsteinhausenbergerdorff",
          )
        ),
      onAccountClicked = { _, _ -> },
      title = "Select Account",
    )
  }
}

// --- Loading and confirmation ---------------------------------------------------------------------

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

@AuthWearCatalog
@Composable
internal fun AuthWearSignedInConfirmationDialogEmailOnly() {
  CatalogWearTheme {
    SignedInConfirmationDialog(onDismissOrTimeout = {}, email = "maggie@example.com")
  }
}

/** The long-name case. Truncation is what actually breaks on a 227dp round screen. */
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

// --- Prompt -------------------------------------------------------------------------------------

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

@AuthWearCatalog
@Composable
internal fun AuthWearSignInPromptScreenLoading() {
  CatalogWearTheme {
    SignInPromptScreen(
      state = SignInPromptScreenState.Loading,
      title = "Sign in",
      message = "Send messages and create chat groups with your friends",
      onIdleStateObserved = {},
      onAlreadySignedIn = {},
      loadingContent = {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Text("Loading…")
        }
      },
    ) {}
  }
}
