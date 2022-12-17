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

package com.google.android.horologist.auth.ui.oauth.devicegrant

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.horologist.auth.composables.dialogs.SignedInConfirmationDialog
import com.google.android.horologist.auth.composables.screens.AuthErrorScreen
import com.google.android.horologist.auth.composables.screens.CheckYourPhoneScreen
import com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi
import com.google.android.horologist.auth.ui.common.screens.LoadingView

@ExperimentalHorologistAuthUiApi
@Composable
public fun <AuthDeviceGrantConfig, VerificationInfoPayload, TokenPayload> AuthDeviceGrantScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthDeviceGrantViewModel<AuthDeviceGrantConfig, VerificationInfoPayload, TokenPayload>,
    successContent: @Composable (successState: AuthDeviceGrantScreenState.Success) -> Unit,
    failedContent: @Composable () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (state) {
        AuthDeviceGrantScreenState.Idle -> {
            SideEffect {
                viewModel.startAuthFlow()
            }
        }

        AuthDeviceGrantScreenState.Loading -> {
            LoadingView(modifier = modifier)
        }

        is AuthDeviceGrantScreenState.CheckPhone -> {
            val code = (state as AuthDeviceGrantScreenState.CheckPhone).code
            CheckYourPhoneScreen(modifier = modifier, message = code)
        }

        AuthDeviceGrantScreenState.Failed -> {
            failedContent()
        }

        AuthDeviceGrantScreenState.Success -> {
            successContent(state as AuthDeviceGrantScreenState.Success)
        }
    }
}

@ExperimentalHorologistAuthUiApi
@Composable
public fun <AuthDeviceGrantConfig, VerificationInfoPayload, TokenPayload> AuthDeviceGrantScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthDeviceGrantViewModel<AuthDeviceGrantConfig, VerificationInfoPayload, TokenPayload>,
    onAuthSucceed: () -> Unit
) {
    AuthDeviceGrantScreen(
        modifier = modifier,
        viewModel = viewModel,
        successContent = {
            SignedInConfirmationDialog(modifier = modifier) {
                onAuthSucceed()
            }
        },
        failedContent = {
            AuthErrorScreen(modifier = modifier)
        }
    )
}
