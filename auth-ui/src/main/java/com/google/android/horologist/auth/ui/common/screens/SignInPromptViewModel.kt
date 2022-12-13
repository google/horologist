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

package com.google.android.horologist.auth.ui.common.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.auth.data.common.repository.AuthRepository
import com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi
import com.google.android.horologist.auth.ui.ext.compareAndSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@ExperimentalHorologistAuthUiApi
public open class SignInPromptViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<SignInPromptScreenState>(SignInPromptScreenState.Idle)
    public val uiState: StateFlow<SignInPromptScreenState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = SignInPromptScreenState.Idle
    )

    public fun startFlow() {
        _uiState.compareAndSet(
            expect = SignInPromptScreenState.Idle,
            update = SignInPromptScreenState.Loading
        ) {
            viewModelScope.launch {
                authRepository.getAuthUser()?.let { authUser ->
                    _uiState.value = SignInPromptScreenState.SignedIn(
                        displayName = authUser.displayName,
                        email = authUser.email,
                        avatar = authUser.avatarUri
                    )
                } ?: run {
                    _uiState.value = SignInPromptScreenState.SignedOut
                }
            }
        }
    }
}

@ExperimentalHorologistAuthUiApi
public sealed class SignInPromptScreenState {

    public object Idle : SignInPromptScreenState()

    public object Loading : SignInPromptScreenState()

    public data class SignedIn(
        val displayName: String? = null,
        val email: String? = null,
        val avatar: Any? = null
    ) : SignInPromptScreenState()

    public object SignedOut : SignInPromptScreenState()
}
