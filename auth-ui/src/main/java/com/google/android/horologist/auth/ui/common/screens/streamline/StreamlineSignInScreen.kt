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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.horologist.auth.composables.screens.SignInPlaceholderScreen
import com.google.android.horologist.auth.data.common.model.AuthUser
import com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi

/**
 * A screen to streamline the sign in process.
 *
 * The [viewModel] will take care of
 * [streamlining](https://developer.android.com/training/wearables/design/sign-in#streamline) the
 * process when the user is already signed in. [onAlreadySignedIn] should be used to navigate away
 * from this screen in that scenario.
 *
 * [onSignedOut] should navigate the user to the sign in screen.
 *
 */
@ExperimentalHorologistAuthUiApi
@Composable
public fun StreamlineSignInScreen(
    onAlreadySignedIn: (authUser: AuthUser) -> Unit,
    onSignedOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StreamlineSignInViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    StreamlineSignInScreen(
        state = state,
        onIdleStateObserved = { viewModel.onIdleStateObserved() },
        onAlreadySignedIn = onAlreadySignedIn,
        onSignedOut = onSignedOut,
        modifier = modifier
    )
}

@OptIn(ExperimentalHorologistAuthUiApi::class)
@Composable
internal fun StreamlineSignInScreen(
    state: StreamlineSignInScreenState,
    onIdleStateObserved: () -> Unit,
    onAlreadySignedIn: (authUser: AuthUser) -> Unit,
    onSignedOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    SignInPlaceholderScreen(modifier = modifier)

    when (state) {
        StreamlineSignInScreenState.Idle -> {
            SideEffect {
                onIdleStateObserved()
            }
        }

        StreamlineSignInScreenState.Loading -> {
            /* do nothing */
        }

        is StreamlineSignInScreenState.SignedIn -> {
            onAlreadySignedIn(state.authUser)
        }

        StreamlineSignInScreenState.SignedOut -> {
            onSignedOut()
        }
    }
}
