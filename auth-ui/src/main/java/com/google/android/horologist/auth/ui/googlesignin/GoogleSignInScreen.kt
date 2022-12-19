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

@file:OptIn(ExperimentalLifecycleComposeApi::class)

package com.google.android.horologist.auth.ui.googlesignin

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.horologist.auth.composables.dialogs.SignedInConfirmationDialog
import com.google.android.horologist.auth.composables.screens.AuthErrorScreen
import com.google.android.horologist.auth.composables.screens.SignInPlaceholderScreen
import com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi

@ExperimentalHorologistAuthUiApi
@Composable
public fun GoogleSignInScreen(
    modifier: Modifier = Modifier,
    viewModel: GoogleSignInViewModel = viewModel(),
    successContent: @Composable (successState: GoogleSignInScreenState.Success) -> Unit,
    failedContent: @Composable () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (state) {
        GoogleSignInScreenState.Idle -> {
            SideEffect {
                viewModel.startAuthFlow()
            }

            SignInPlaceholderScreen(modifier = modifier)
        }

        GoogleSignInScreenState.SelectAccount -> {
            SignInPlaceholderScreen(modifier = modifier)

            val context = LocalContext.current

            var googleSignInAccount by remember {
                mutableStateOf(GoogleSignIn.getLastSignedInAccount(context))
            }

            googleSignInAccount?.let { account ->
                SideEffect {
                    viewModel.onAccountSelected(account)
                }
            } ?: run {
                val googleSignInClient = GoogleSignIn.getClient(
                    context,
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build()
                )

                val signInRequestLauncher = rememberLauncherForActivityResult(
                    contract = GoogleSignInContract(
                        googleSignInClient
                    ) { viewModel.onAccountSelectionFailed() }
                ) { account ->
                    googleSignInAccount = account
                    googleSignInAccount?.let {
                        viewModel.onAccountSelected(it)
                    }
                }

                SideEffect {
                    signInRequestLauncher.launch(Unit)
                }
            }
        }

        GoogleSignInScreenState.Failed -> {
            failedContent()
        }

        is GoogleSignInScreenState.Success -> {
            successContent(state as GoogleSignInScreenState.Success)
        }
    }
}

@ExperimentalHorologistAuthUiApi
@Composable
public fun GoogleSignInScreen(
    modifier: Modifier = Modifier,
    viewModel: GoogleSignInViewModel = viewModel(),
    onAuthSucceed: () -> Unit
) {
    GoogleSignInScreen(
        viewModel = viewModel,
        successContent = { successState ->
            SignedInConfirmationDialog(
                modifier = modifier,
                name = successState.displayName,
                email = successState.email,
                avatar = successState.photoUrl
            ) {
                onAuthSucceed()
            }
        },
        failedContent = {
            AuthErrorScreen(modifier)
        }
    )
}

/**
 * An [ActivityResultContract] for signing in with the given [GoogleSignInClient].
 */
private class GoogleSignInContract(
    private val googleSignInClient: GoogleSignInClient,
    private val onTaskFailed: () -> Unit
) : ActivityResultContract<Unit, GoogleSignInAccount?>() {

    override fun createIntent(
        context: Context,
        input: Unit
    ): Intent = googleSignInClient.signInIntent

    override fun parseResult(resultCode: Int, intent: Intent?): GoogleSignInAccount? {
        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
        // As documented, this task must be complete
        check(task.isComplete)

        return if (task.isSuccessful) {
            task.result
        } else {
            val exception = task.exception
            check(exception is ApiException)
            val message = GoogleSignInStatusCodes.getStatusCodeString(exception.statusCode)
            Log.w(TAG, "Sign in failed: code=${exception.statusCode}, message=$message")

            onTaskFailed()

            null
        }
    }

    private companion object {
        private val TAG = GoogleSignInContract::class.java.simpleName
    }
}
