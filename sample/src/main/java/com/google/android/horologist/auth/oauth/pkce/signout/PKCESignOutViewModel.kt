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

package com.google.android.horologist.auth.oauth.pkce.signout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.horologist.auth.oauth.pkce.data.PKCEAuthUserRepositorySample
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PKCESignOutViewModel(
    private val authUserRepository: PKCEAuthUserRepositorySample
) : ViewModel() {

    private val _uiState = MutableStateFlow(PKCESignOutScreenState.Idle)
    public val uiState: StateFlow<PKCESignOutScreenState> = _uiState

    fun onIdleStateObserved() {
        if (_uiState.compareAndSet(
                expect = PKCESignOutScreenState.Idle,
                update = PKCESignOutScreenState.Loading
            )
        ) {
            viewModelScope.launch {
                try {
                    authUserRepository.clearAuthenticated()
                    _uiState.value = PKCESignOutScreenState.Success
                } catch (exception: Exception) {
                    Log.w(TAG, "Sign out failed: $exception")
                    _uiState.value = PKCESignOutScreenState.Failed
                }
            }
        }
    }

    companion object {

        private val TAG = PKCESignOutViewModel::class.java.simpleName

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PKCESignOutViewModel(PKCEAuthUserRepositorySample)
            }
        }
    }
}

enum class PKCESignOutScreenState {
    Idle,
    Loading,
    Success,
    Failed
}
