/*
 * Copyright 2022 The Android Open Source Project
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

package com.google.android.horologist.auth.oauth.pkce

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.horologist.auth.composables.dialogs.SignedInConfirmationDialog
import com.google.android.horologist.auth.composables.screens.AuthErrorScreen
import com.google.android.horologist.auth.ui.oauth.pkce.AuthPKCEScreen
import com.google.android.horologist.auth.ui.oauth.pkce.AuthPKCEScreenState

@Composable
fun AuthPKCESampleScreen(
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthPKCESampleViewModel = viewModel(factory = AuthPKCESampleViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (state) {
        AuthPKCEScreenState.Idle,
        AuthPKCEScreenState.Loading,
        AuthPKCEScreenState.CheckPhone -> {
            AuthPKCEScreen(viewModel = viewModel)
        }

        AuthPKCEScreenState.Failed -> {
            AuthErrorScreen(modifier)
        }

        AuthPKCEScreenState.Success -> {
            var showDialog by rememberSaveable { mutableStateOf(true) }

            SignedInConfirmationDialog(
                showDialog = showDialog,
                onDismissOrTimeout = {
                    showDialog = false

                    onAuthSuccess()
                },
                modifier = modifier
            )
        }
    }
}
