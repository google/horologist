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

import com.google.android.horologist.auth.composables.model.AccountUiModel
import com.google.android.horologist.auth.data.common.repository.AuthUserRepository
import com.google.android.horologist.auth.ui.common.screens.streamline.StreamlineSignInViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StreamlineSignInSampleViewModel(
    authUserRepository: AuthUserRepository
) : StreamlineSignInViewModel(authUserRepository) {

    private val _uiState =
        MutableStateFlow<StreamlineSignInSampleScreenState>(StreamlineSignInSampleScreenState.ParentScreen)
    public val sampleUiState: StateFlow<StreamlineSignInSampleScreenState> = _uiState

    fun onSingleAccountAvailable(account: AccountUiModel) {
        _uiState.value = StreamlineSignInSampleScreenState.SignedIn(account)
    }

    fun onMultipleAccountsAvailable(accounts: List<AccountUiModel>) {
        _uiState.value = StreamlineSignInSampleScreenState.MultipleAccountsAvailable(accounts)
    }

    fun onAccountSelected(account: AccountUiModel) {
        _uiState.value = StreamlineSignInSampleScreenState.SignedIn(account)
    }

    fun onNoAccountsAvailable() {
        _uiState.value = StreamlineSignInSampleScreenState.NoAccountsAvailable
    }
}

sealed class StreamlineSignInSampleScreenState {

    object ParentScreen : StreamlineSignInSampleScreenState()

    data class SignedIn(val account: AccountUiModel) : StreamlineSignInSampleScreenState()

    data class MultipleAccountsAvailable(val accounts: List<AccountUiModel>) :
        StreamlineSignInSampleScreenState()

    object NoAccountsAvailable : StreamlineSignInSampleScreenState()
}
