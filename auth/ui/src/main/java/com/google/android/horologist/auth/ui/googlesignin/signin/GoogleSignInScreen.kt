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

package com.google.android.horologist.auth.ui.googlesignin.signin

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes.SIGN_IN_CANCELLED
import com.google.android.gms.common.api.ApiException
import com.google.android.horologist.auth.composables.dialogs.SignedInConfirmationDialog
import com.google.android.horologist.auth.composables.screens.AuthErrorScreen
import com.google.android.horologist.auth.composables.screens.SignInPlaceholderScreen
import com.google.android.horologist.auth.ui.common.logging.TAG

/**
 * A screen for the Google Sign-In authentication method.
 *
 * [onAuthCancelled] should be used to navigate away from this screen.
 */
@Composable
public fun GoogleSignInScreen(
    onAuthCancelled: () -> Unit,
    failedContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GoogleSignInViewModel = viewModel(),
    content: @Composable (successState: GoogleSignInScreenState.Success) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (state) {
        GoogleSignInScreenState.Idle -> {
            SideEffect {
                viewModel.onIdleStateObserved()
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
                val signInRequestLauncher = rememberLauncherForActivityResult(
                    contract = GoogleSignInContract(
                        viewModel.googleSignInClient,
                    ),
                ) { result ->

                    when (result) {
                        GoogleSignInContract.Result.Cancelled -> {
                            viewModel.onAuthCancelled()
                        }

                        GoogleSignInContract.Result.Failed -> {
                            viewModel.onAccountSelectionFailed()
                        }

                        is GoogleSignInContract.Result.Success -> {
                            googleSignInAccount = result.googleSignInAccount
                            googleSignInAccount?.let {
                                viewModel.onAccountSelected(it)
                            }
                        }
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
            content(state as GoogleSignInScreenState.Success)
        }

        GoogleSignInScreenState.Cancelled -> {
            onAuthCancelled()
        }
    }
}

/**
 * A [GoogleSignInScreen] with:
 *
 * - [SignedInConfirmationDialog] as content for [success][GoogleSignInScreenState.Success] state;
 * - [AuthErrorScreen] as content for [failed][GoogleSignInScreenState.Failed] state;
 *
 * Parameters [onAuthCancelled] and [onAuthSucceed] should be used to navigate away from this screen
 * when these events happen.
 */
@Composable
public fun GoogleSignInScreen(
    onAuthCancelled: () -> Unit,
    onAuthSucceed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GoogleSignInViewModel = viewModel(),
) {
    GoogleSignInScreen(
        onAuthCancelled = {
            onAuthCancelled()
        },
        failedContent = {
            AuthErrorScreen(modifier)
        },
        viewModel = viewModel,
    ) { successState ->
        SignedInConfirmationDialog(
            onDismissOrTimeout = { onAuthSucceed() },
            modifier = modifier,
            accountUiModel = successState.accountUiModel,
        )
    }
}

/**
 * An [ActivityResultContract] for signing in with the given [GoogleSignInClient].
 */
private class GoogleSignInContract(
    private val googleSignInClient: GoogleSignInClient,
) : ActivityResultContract<Unit, GoogleSignInContract.Result>() {

    override fun createIntent(
        context: Context,
        input: Unit,
    ): Intent = googleSignInClient.signInIntent

    override fun parseResult(resultCode: Int, intent: Intent?): Result {
        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)

        return when {
            !task.isComplete -> {
                Log.w(TAG, "Sign in failed with incomplete task")
                Result.Failed
            }

            task.isSuccessful -> Result.Success(task.result)

            else -> {
                val exception = task.exception
                if (exception is ApiException) {
                    val message = GoogleSignInStatusCodes.getStatusCodeString(exception.statusCode)
                    Log.w(TAG, "Sign in failed: code=${exception.statusCode}, message=$message")

                    if (exception.statusCode == SIGN_IN_CANCELLED) {
                        Result.Cancelled
                    } else {
                        Result.Failed
                    }
                } else {
                    Log.w(TAG, "Sign in failed with exception: $exception")
                    Result.Failed
                }
            }
        }
    }

    internal sealed class Result {
        object Cancelled : Result()

        object Failed : Result()

        data class Success(val googleSignInAccount: GoogleSignInAccount?) : Result()
    }
}
