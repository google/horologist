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

package com.google.android.horologist.auth.sample.screens.common.streamline

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.google.android.horologist.auth.composables.dialogs.SignedInConfirmationDialog
import com.google.android.horologist.auth.composables.screens.SelectAccountScreen
import com.google.android.horologist.auth.sample.R
import com.google.android.horologist.auth.ui.common.screens.streamline.StreamlineSignInScreen
import com.google.android.horologist.base.ui.components.ConfirmationDialog
import com.google.android.horologist.base.ui.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION
import com.google.android.horologist.compose.layout.ScalingLazyColumnState

@Composable
fun StreamlineSignInSampleScreen(
    navController: NavHostController,
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
    viewModel: StreamlineSignInSampleViewModel = viewModel(factory = StreamlineSignInSampleViewModelFactory)
) {
    val state by viewModel.sampleUiState.collectAsStateWithLifecycle()

    when (state) {
        StreamlineSignInSampleScreenState.ParentScreen -> {
            StreamlineSignInScreen(
                onSingleAccountAvailable = viewModel::onSingleAccountAvailable,
                onMultipleAccountsAvailable = viewModel::onMultipleAccountsAvailable,
                onNoAccountsAvailable = viewModel::onNoAccountsAvailable,
                viewModel = viewModel
            ) {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        imageVector = Icons.Default.Android,
                        contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION
                    )
                }
            }
        }

        is StreamlineSignInSampleScreenState.SignedIn -> {
            val account = (state as StreamlineSignInSampleScreenState.SignedIn).account
            SignedInConfirmationDialog(
                onDismissOrTimeout = { navController.popBackStack() },
                name = account.name,
                email = account.email
            )
        }

        is StreamlineSignInSampleScreenState.MultipleAccountsAvailable -> {
            val accounts =
                (state as StreamlineSignInSampleScreenState.MultipleAccountsAvailable).accounts
            SelectAccountScreen(
                accounts = accounts,
                onAccountClicked = { _, account ->
                    viewModel.onAccountSelected(account)
                },
                columnState = columnState
            )
        }

        StreamlineSignInSampleScreenState.NoAccountsAvailable -> {
            ConfirmationDialog(
                onTimeout = {
                    navController.popBackStack()
                }
            ) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    text = stringResource(id = R.string.common_screens_streamline_no_accounts_message)
                )
            }
        }
    }
}
