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

package com.google.android.horologist.mediasample.ui.auth.signout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.google.android.horologist.mediasample.data.auth.GoogleSignInAuthUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UampGoogleSignOutViewModel @Inject constructor(
    private val googleSignInAuthUserRepository: GoogleSignInAuthUserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoogleSignOutScreenState.Idle)
    public val uiState: StateFlow<GoogleSignOutScreenState> = _uiState

    fun onIdleStateObserved() {
        if (_uiState.compareAndSet(
                expect = GoogleSignOutScreenState.Idle,
                update = GoogleSignOutScreenState.Loading,
            )
        ) {
            viewModelScope.launch {
                try {
                    googleSignInAuthUserRepository.signOut()
                    _uiState.value = GoogleSignOutScreenState.Success
                } catch (apiException: ApiException) {
                    Log.w(TAG, "Sign out failed: $apiException")
                    _uiState.value = GoogleSignOutScreenState.Failed
                }
            }
        }
    }

    companion object {
        private val TAG = UampGoogleSignOutViewModel::class.java.simpleName
    }
}

enum class GoogleSignOutScreenState {
    Idle,
    Loading,
    Success,
    Failed,
}
