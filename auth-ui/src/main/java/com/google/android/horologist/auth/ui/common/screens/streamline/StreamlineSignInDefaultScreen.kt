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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.horologist.auth.composables.dialogs.SignedInConfirmationDialog
import com.google.android.horologist.auth.composables.screens.SelectAccountScreen
import com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi
import com.google.android.horologist.compose.layout.ScalingLazyColumnState

/**
 * An opinionated implementation of [StreamlineSignInScreen] that:
 * - displays the [SignedInConfirmationDialog] when there is a single account available;
 * - displays the [SelectAccountScreen] when there are multiple accounts available, then displays
 * the [SignedInConfirmationDialog] after the account is selected;
 */
@ExperimentalHorologistAuthUiApi
@Composable
public fun StreamlineSignInDefaultScreen(
    onSignedInConfirmationDialogDismissOrTimeout: () -> Unit,
    onNoAccountsAvailable: () -> Unit,
    columnState: ScalingLazyColumnState,
    viewModel: StreamlineSignInDefaultViewModel,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = { }
) {
    val state by viewModel.defaultUiState.collectAsStateWithLifecycle()

    when (state) {
        StreamlineSignInDefaultScreenState.ParentScreen -> {
            StreamlineSignInScreen(
                onSingleAccountAvailable = viewModel::onSingleAccountAvailable,
                onMultipleAccountsAvailable = viewModel::onMultipleAccountsAvailable,
                onNoAccountsAvailable = viewModel::onNoAccountsAvailable,
                viewModel = viewModel,
                content = content
            )
        }

        is StreamlineSignInDefaultScreenState.SignedIn -> {
            val account = (state as StreamlineSignInDefaultScreenState.SignedIn).account
            SignedInConfirmationDialog(
                onDismissOrTimeout = onSignedInConfirmationDialogDismissOrTimeout,
                modifier = modifier,
                name = account.name,
                email = account.email
            )
        }

        is StreamlineSignInDefaultScreenState.MultipleAccountsAvailable -> {
            val accounts =
                (state as StreamlineSignInDefaultScreenState.MultipleAccountsAvailable).accounts
            SelectAccountScreen(
                accounts = accounts,
                onAccountClicked = { _, account ->
                    viewModel.onAccountSelected(account)
                },
                columnState = columnState,
                modifier = modifier
            )
        }

        StreamlineSignInDefaultScreenState.NoAccountsAvailable -> {
            onNoAccountsAvailable()
        }
    }
}
