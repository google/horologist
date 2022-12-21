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

package com.google.android.horologist.auth.ui.common.screens.prompt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.auth.data.common.repository.AuthUserRepository
import com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi
import com.google.android.horologist.auth.ui.ext.compareAndSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * A view model for a sign-in prompt screen.
 *
 * It checks if there is a user already signed in, and emits the appropriate
 * [states][SignInPromptScreenState] through the [uiState] property.
 */
@ExperimentalHorologistAuthUiApi
public open class SignInPromptViewModel(
    private val authUserRepository: AuthUserRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<SignInPromptScreenState>(SignInPromptScreenState.Idle)
    public val uiState: StateFlow<SignInPromptScreenState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = SignInPromptScreenState.Idle
    )

    /**
     * Indicate that the screen has observed the [idle][SignInPromptScreenState.Idle] state and that
     * the view model can start its work.
     */
    public fun onIdleStateObserved() {
        _uiState.compareAndSet(
            expect = SignInPromptScreenState.Idle,
            update = SignInPromptScreenState.Loading
        ) {
            viewModelScope.launch {
                if (authUserRepository.getAuthenticated() != null) {
                    _uiState.value = SignInPromptScreenState.SignedIn
                } else {
                    _uiState.value = SignInPromptScreenState.SignedOut
                }
            }
        }
    }
}

/**
 * The states for a sign-in prompt screen.
 */
@ExperimentalHorologistAuthUiApi
public sealed class SignInPromptScreenState {

    public object Idle : SignInPromptScreenState()

    public object Loading : SignInPromptScreenState()

    public object SignedIn : SignInPromptScreenState()

    public object SignedOut : SignInPromptScreenState()
}
