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

import com.google.android.horologist.auth.composables.model.AccountUiModel
import com.google.android.horologist.auth.data.common.repository.AuthUserRepository
import com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * A default implementation of [StreamlineSignInViewModel].
 */
@ExperimentalHorologistAuthUiApi
public class StreamlineSignInDefaultViewModel(
    authUserRepository: AuthUserRepository
) : StreamlineSignInViewModel(authUserRepository) {

    private val _uiState =
        MutableStateFlow<StreamlineSignInDefaultScreenState>(StreamlineSignInDefaultScreenState.ParentScreen)
    public val defaultUiState: StateFlow<StreamlineSignInDefaultScreenState> = _uiState

    public fun onSingleAccountAvailable(account: AccountUiModel) {
        _uiState.value = StreamlineSignInDefaultScreenState.SignedIn(account)
    }

    public fun onMultipleAccountsAvailable(accounts: List<AccountUiModel>) {
        _uiState.value = StreamlineSignInDefaultScreenState.MultipleAccountsAvailable(accounts)
    }

    public fun onAccountSelected(account: AccountUiModel) {
        _uiState.value = StreamlineSignInDefaultScreenState.SignedIn(account)
    }

    public fun onNoAccountsAvailable() {
        _uiState.value = StreamlineSignInDefaultScreenState.NoAccountsAvailable
    }
}

@ExperimentalHorologistAuthUiApi
public sealed class StreamlineSignInDefaultScreenState {

    public object ParentScreen : StreamlineSignInDefaultScreenState()

    public data class SignedIn(val account: AccountUiModel) : StreamlineSignInDefaultScreenState()

    public data class MultipleAccountsAvailable(val accounts: List<AccountUiModel>) :
        StreamlineSignInDefaultScreenState()

    public object NoAccountsAvailable : StreamlineSignInDefaultScreenState()
}
