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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.horologist.auth.composables.model.AccountUiModel
import com.google.android.horologist.auth.data.googlesignin.GoogleSignInEventListener
import com.google.android.horologist.auth.data.googlesignin.GoogleSignInEventListenerNoOpImpl
import com.google.android.horologist.auth.ui.googlesignin.mapper.AccountUiModelMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * A view model for a Google Sign-In screen.
 */
public open class GoogleSignInViewModel(
    public val googleSignInClient: GoogleSignInClient,
    private val googleSignInEventListener: GoogleSignInEventListener = GoogleSignInEventListenerNoOpImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow<GoogleSignInScreenState>(GoogleSignInScreenState.Idle)
    public val uiState: StateFlow<GoogleSignInScreenState> = _uiState

    /**
     * Indicate that the screen has observed the [idle][GoogleSignInScreenState.Idle] state and that
     * the view model can start its work.
     */
    public fun onIdleStateObserved() {
        _uiState.compareAndSet(
            expect = GoogleSignInScreenState.Idle,
            update = GoogleSignInScreenState.SelectAccount
        )
    }

    /**
     * Indicate that [account] was selected.
     */
    public fun onAccountSelected(account: GoogleSignInAccount) {
        viewModelScope.launch {
            googleSignInEventListener.onSignedIn(account)
        }

        _uiState.value = GoogleSignInScreenState.Success(
            AccountUiModelMapper.map(account)
        )
    }

    /**
     * Indicate that the process to select an account failed.
     *
     * Note that [onAuthCancelled] should be used when the user cancel the account selection.
     */
    public fun onAccountSelectionFailed() {
        _uiState.value = GoogleSignInScreenState.Failed
    }

    /**
     * Indicate that the authentication was cancelled.
     */
    public fun onAuthCancelled() {
        _uiState.value = GoogleSignInScreenState.Cancelled
    }
}

/**
 * The states for a Google Sign-In screen.
 */
public sealed class GoogleSignInScreenState {

    public object Idle : GoogleSignInScreenState()

    public object SelectAccount : GoogleSignInScreenState()

    public data class Success(val accountUiModel: AccountUiModel) : GoogleSignInScreenState()

    public object Failed : GoogleSignInScreenState()

    public object Cancelled : GoogleSignInScreenState()
}
