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

package com.google.android.horologist.auth.ui.oauth.devicegrant.signin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.horologist.auth.composables.dialogs.SignedInConfirmationDialog
import com.google.android.horologist.auth.composables.screens.AuthErrorScreen
import com.google.android.horologist.auth.composables.screens.CheckYourPhoneScreen
import com.google.android.horologist.auth.composables.screens.SignInPlaceholderScreen
import com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi

@ExperimentalHorologistAuthUiApi
@Composable
public fun <DeviceGrantConfig, VerificationInfoPayload, TokenPayload> DeviceGrantSignInScreen(
    failedContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DeviceGrantViewModel<DeviceGrantConfig, VerificationInfoPayload, TokenPayload>,
    content: @Composable (successState: DeviceGrantScreenState.Success) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (state) {
        DeviceGrantScreenState.Idle -> {
            SideEffect {
                viewModel.startAuthFlow()
            }
        }

        DeviceGrantScreenState.Loading -> {
            SignInPlaceholderScreen(modifier = modifier)
        }

        is DeviceGrantScreenState.CheckPhone -> {
            val code = (state as DeviceGrantScreenState.CheckPhone).code
            CheckYourPhoneScreen(modifier = modifier, message = code)
        }

        DeviceGrantScreenState.Failed -> {
            failedContent()
        }

        DeviceGrantScreenState.Success -> {
            content(state as DeviceGrantScreenState.Success)
        }
    }
}

@ExperimentalHorologistAuthUiApi
@Composable
public fun <DeviceGrantConfig, VerificationInfoPayload, TokenPayload> DeviceGrantSignInScreen(
    onAuthSucceed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DeviceGrantViewModel<DeviceGrantConfig, VerificationInfoPayload, TokenPayload>
) {
    DeviceGrantSignInScreen(
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
