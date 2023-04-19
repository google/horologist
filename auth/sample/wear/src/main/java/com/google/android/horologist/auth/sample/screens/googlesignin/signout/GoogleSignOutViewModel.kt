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

package com.google.android.horologist.auth.sample.screens.googlesignin.signout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class GoogleSignOutViewModel(
    private val googleSignInClient: GoogleSignInClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoogleSignOutScreenState.Idle)
    public val uiState: StateFlow<GoogleSignOutScreenState> = _uiState

    fun onIdleStateObserved() {
        if (_uiState.compareAndSet(
                expect = GoogleSignOutScreenState.Idle,
                update = GoogleSignOutScreenState.Loading
            )
        ) {
            viewModelScope.launch {
                try {
                    googleSignInClient.signOut().await()
                    _uiState.value = GoogleSignOutScreenState.Success
                } catch (apiException: ApiException) {
                    Log.w(TAG, "Sign out failed: $apiException")
                    _uiState.value = GoogleSignOutScreenState.Failed
                }
            }
        }
    }

    companion object {

        private val TAG = GoogleSignOutViewModel::class.java.simpleName

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY]!!

                val googleSignInClient = GoogleSignIn.getClient(
                    application,
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build()
                )

                GoogleSignOutViewModel(googleSignInClient)
            }
        }
    }
}

enum class GoogleSignOutScreenState {
    Idle,
    Loading,
    Success,
    Failed
}
