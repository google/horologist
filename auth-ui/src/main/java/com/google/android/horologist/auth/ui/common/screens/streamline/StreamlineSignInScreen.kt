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

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.horologist.auth.composables.dialogs.SignedInConfirmationDialog
import com.google.android.horologist.auth.composables.screens.SelectAccountScreen
import com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi
import com.google.android.horologist.compose.layout.ScalingLazyColumnState

/**
 * A composable to streamline the sign in process, that:
 * - displays the [SignedInConfirmationDialog] when there is a single account available;
 * - displays the [SelectAccountScreen] when there are multiple accounts available, then displays
 * the [SignedInConfirmationDialog] after the account is selected;
 *
 * The [content] of this composable would be displayed when the screen is in "loading" state. This
 * is an optional param, in case of no other layout is expected to be displayed while this screen is
 * loading, e.g. in the scenario where the app is already displaying the splash screen.
 */
@ExperimentalHorologistAuthUiApi
@Composable
public fun <DomainModel, UiModel> StreamlineSignInScreen(
    accountName: (account: UiModel) -> String,
    accountEmail: (account: UiModel) -> String,
    selectAccountAvatarContent: @Composable (BoxScope.(account: UiModel) -> Unit),
    signedInConfirmationAvatarContent: @Composable (ColumnScope.(account: UiModel) -> Unit),
    onSignedInConfirmationDialogDismissOrTimeout: (account: UiModel) -> Unit,
    onNoAccountsAvailable: () -> Unit,
    viewModel: StreamlineSignInViewModel<DomainModel, UiModel>,
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = { }
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (state) {
        StreamlineSignInScreenState.Idle -> {
            SideEffect {
                viewModel.onIdleStateObserved()
            }
        }

        StreamlineSignInScreenState.Loading -> {
            content()
        }

        is StreamlineSignInScreenState.SignedIn -> {
            val account = (state as StreamlineSignInScreenState.SignedIn).account
            SignedInConfirmationDialog(
                onDismissOrTimeout = { onSignedInConfirmationDialogDismissOrTimeout(account) },
                modifier = modifier,
                name = accountName(account),
                email = accountEmail(account),
                avatarContent = { signedInConfirmationAvatarContent(account) }
            )
        }

        is StreamlineSignInScreenState.MultipleAccountsAvailable -> {
            val accounts =
                (state as StreamlineSignInScreenState.MultipleAccountsAvailable).accounts
            SelectAccountScreen(
                accounts = accounts,
                label = accountEmail,
                avatarContent = selectAccountAvatarContent,
                onAccountClicked = { _, account ->
                    viewModel.onAccountSelected(account)
                },
                columnState = columnState,
                modifier = modifier
            )
        }

        StreamlineSignInScreenState.NoAccountsAvailable -> {
            onNoAccountsAvailable()
        }
    }
}

/**
 * A [StreamlineSignInScreen] that uses a single model for domain and UI.
 */
@ExperimentalHorologistAuthUiApi
@Composable
public fun <T> StreamlineSignInScreen(
    accountName: (account: T) -> String,
    accountEmail: (account: T) -> String,
    selectAccountAvatarContent: @Composable (BoxScope.(account: T) -> Unit),
    signedInConfirmationAvatarContent: @Composable (ColumnScope.(account: T) -> Unit),
    onSignedInConfirmationDialogDismissOrTimeout: (account: T) -> Unit,
    onNoAccountsAvailable: () -> Unit,
    columnState: ScalingLazyColumnState,
    singleModelViewModel: StreamlineSignInViewModel<T, T>,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = { }
) {
    StreamlineSignInScreen(
        accountName = accountName,
        accountEmail = accountEmail,
        selectAccountAvatarContent = selectAccountAvatarContent,
        signedInConfirmationAvatarContent = signedInConfirmationAvatarContent,
        onSignedInConfirmationDialogDismissOrTimeout = onSignedInConfirmationDialogDismissOrTimeout,
        onNoAccountsAvailable = onNoAccountsAvailable,
        viewModel = singleModelViewModel,
        columnState = columnState,
        modifier = modifier,
        content = content
    )
}
