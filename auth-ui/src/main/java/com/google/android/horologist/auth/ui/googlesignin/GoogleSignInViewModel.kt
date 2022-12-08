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

package com.google.android.horologist.auth.ui.googlesignin

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.horologist.auth.data.googlesignin.GoogleSignInAccountListener
import com.google.android.horologist.auth.data.googlesignin.GoogleSignInAccountListenerNoOpImpl
import com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@ExperimentalHorologistAuthUiApi
public open class GoogleSignInViewModel(
    private val googleSignInAccountListener: GoogleSignInAccountListener = GoogleSignInAccountListenerNoOpImpl()
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<GoogleSignInScreenState>(GoogleSignInScreenState.Idle)
    public val uiState: StateFlow<GoogleSignInScreenState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = GoogleSignInScreenState.Idle
    )

    public fun startAuthFlow() {
        _uiState.compareAndSet(
            expect = GoogleSignInScreenState.Idle,
            update = GoogleSignInScreenState.SelectAccount
        )
    }

    public fun onAccountSelected(account: GoogleSignInAccount) {
        viewModelScope.launch {
            googleSignInAccountListener.onAccountReceived(account)
        }

        _uiState.value = GoogleSignInScreenState.Success(
            displayName = account.displayName,
            email = account.email,
            photoUrl = account.photoUrl
        )
    }

    public fun onAccountSelectionFailed() {
        _uiState.value = GoogleSignInScreenState.Failed
    }
}

@ExperimentalHorologistAuthUiApi
public sealed class GoogleSignInScreenState {

    public object Idle : GoogleSignInScreenState()

    public object SelectAccount : GoogleSignInScreenState()
    public data class Success(
        val displayName: String?,
        val email: String?,
        val photoUrl: Uri?
    ) : GoogleSignInScreenState()

    public object Failed : GoogleSignInScreenState()
}
