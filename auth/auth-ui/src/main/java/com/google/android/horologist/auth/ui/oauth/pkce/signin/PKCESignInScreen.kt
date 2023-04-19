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

package com.google.android.horologist.auth.ui.oauth.pkce.signin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.auth.composables.dialogs.SignedInConfirmationDialog
import com.google.android.horologist.auth.composables.screens.AuthErrorScreen
import com.google.android.horologist.auth.composables.screens.CheckYourPhoneScreen

@ExperimentalHorologistApi
@Composable
public fun <PKCEConfig, OAuthCodePayload, TokenPayload> PKCESignInScreen(
    failedContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PKCESignInViewModel<PKCEConfig, OAuthCodePayload, TokenPayload>,
    content: @Composable (successState: PKCEScreenState.Success) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (state) {
        PKCEScreenState.Idle -> {
            SideEffect {
                viewModel.startAuthFlow()
            }
        }

        PKCEScreenState.Loading,
        PKCEScreenState.CheckPhone -> {
            CheckYourPhoneScreen(modifier = modifier)
        }

        PKCEScreenState.Failed -> {
            failedContent()
        }

        PKCEScreenState.Success -> {
            content(state as PKCEScreenState.Success)
        }
    }
}

@ExperimentalHorologistApi
@Composable
public fun <PKCEConfig, OAuthCodePayload, TokenPayload> PKCESignInScreen(
    onAuthSucceed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PKCESignInViewModel<PKCEConfig, OAuthCodePayload, TokenPayload>
) {
    PKCESignInScreen(
        failedContent = {
            AuthErrorScreen(modifier = modifier)
        },
        modifier = modifier,
        viewModel = viewModel
    ) {
        SignedInConfirmationDialog(
            onDismissOrTimeout = { onAuthSucceed() },
            modifier = modifier
        )
    }
}
