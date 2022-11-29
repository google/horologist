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

package com.google.android.horologist.auth.googlesignin

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
import com.google.android.horologist.auth.ui.googlesignin.AuthGoogleSignInScreenState
import com.google.android.horologist.auth.ui.googlesignin.GoogleSignInScreen
import com.google.android.horologist.auth.ui.googlesignin.GoogleSignInViewModel

@Composable
fun GoogleSignInSampleScreen(
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GoogleSignInViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (state) {
        AuthGoogleSignInScreenState.Idle,
        AuthGoogleSignInScreenState.SelectAccount -> {
            GoogleSignInScreen(viewModel)
        }

        AuthGoogleSignInScreenState.Failed -> {
            AuthErrorScreen(modifier)
        }

        is AuthGoogleSignInScreenState.Success -> {
            var showDialog by rememberSaveable { mutableStateOf(true) }
            val successState = state as AuthGoogleSignInScreenState.Success

            if (successState.displayName != null && successState.email != null) {
                SignedInConfirmationDialog(
                    displayName = successState.displayName!!,
                    email = successState.email!!,
                    showDialog = showDialog,
                    onDismissOrTimeout = {
                        showDialog = false

                        onAuthSuccess()
                    },
                    modifier = modifier
                )
            } else {
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
}
