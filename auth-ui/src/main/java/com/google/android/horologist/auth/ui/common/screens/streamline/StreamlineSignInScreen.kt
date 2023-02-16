/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.auth.ui.common.screens.streamline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.horologist.auth.composables.dialogs.SignedInConfirmationDialog
import com.google.android.horologist.auth.composables.model.AccountUiModel
import com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi

/**
 * A composable to streamline the sign in process.
 *
 * The [content] of this composable would be displayed when the screen is in "loading" state. This
 * is an optional param, in case of no other layout is expected to be displayed while this screen is
 * loading, e.g. in the scenario where the app is already displaying the splash screen.
 *
 * The [viewModel] will take care of
 * [streamlining](https://developer.android.com/training/wearables/design/sign-in#streamline) the
 * process when the user is already signed in. [onSingleAccountAvailable] should be used to navigate away
 * from this screen in that scenario.
 *
 * Suggested usage for the screen:
 * - [onSingleAccountAvailable] should display a
 * [signed in confirmation dialog][SignedInConfirmationDialog] or navigate the user to the main
 * screen of your app.
 *
 * - [onMultipleAccountsAvailable] should navigate the user to the account selection screen.
 *
 * - [onNoAccountsAvailable] should navigate the user to the sign in screen.
 */
@ExperimentalHorologistAuthUiApi
@Composable
public fun StreamlineSignInScreen(
    onSingleAccountAvailable: (account: AccountUiModel) -> Unit,
    onMultipleAccountsAvailable: (accounts: List<AccountUiModel>) -> Unit,
    onNoAccountsAvailable: () -> Unit,
    viewModel: StreamlineSignInViewModel = viewModel(),
    content: @Composable () -> Unit = { }
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    StreamlineSignInScreen(
        state = state,
        onIdleStateObserved = { viewModel.onIdleStateObserved() },
        onSingleAccountAvailable = onSingleAccountAvailable,
        onMultipleAccountsAvailable = onMultipleAccountsAvailable,
        onNoAccountsAvailable = onNoAccountsAvailable,
        content = content
    )
}

@OptIn(ExperimentalHorologistAuthUiApi::class)
@Composable
internal fun StreamlineSignInScreen(
    state: StreamlineSignInScreenState,
    onIdleStateObserved: () -> Unit,
    onSingleAccountAvailable: (account: AccountUiModel) -> Unit,
    onMultipleAccountsAvailable: (accounts: List<AccountUiModel>) -> Unit,
    onNoAccountsAvailable: () -> Unit,
    content: @Composable () -> Unit = { }
) {
    when (state) {
        StreamlineSignInScreenState.Idle -> {
            SideEffect {
                onIdleStateObserved()
            }
        }

        StreamlineSignInScreenState.Loading -> {
            content()
        }

        is StreamlineSignInScreenState.SingleAccountAvailable -> {
            onSingleAccountAvailable(state.account)
        }

        is StreamlineSignInScreenState.MultipleAccountsAvailable -> {
            onMultipleAccountsAvailable(state.accounts)
        }

        StreamlineSignInScreenState.NoAccountsAvailable -> {
            onNoAccountsAvailable()
        }
    }
}
