/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.datalayer.sample.screens.inappprompts.custom.signin

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.datalayer.phone.PhoneDataLayerAppHelper
import com.google.android.horologist.datalayer.phone.ui.prompt.signin.SignInPrompt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInCustomPromptDemoViewModel
    @Inject
    constructor(
        private val phoneDataLayerAppHelper: PhoneDataLayerAppHelper,
        private val signInCustomPrompt: SignInPrompt,
    ) : ViewModel() {

        private var initializeCalled = false

        private val _uiState =
            MutableStateFlow<SignInCustomPromptDemoScreenState>(SignInCustomPromptDemoScreenState.Idle)
        public val uiState: StateFlow<SignInCustomPromptDemoScreenState> = _uiState

        @MainThread
        fun initialize() {
            if (initializeCalled) return
            initializeCalled = true

            _uiState.value = SignInCustomPromptDemoScreenState.Loading

            viewModelScope.launch {
                if (!phoneDataLayerAppHelper.isAvailable()) {
                    _uiState.value = SignInCustomPromptDemoScreenState.ApiNotAvailable
                } else {
                    _uiState.value = SignInCustomPromptDemoScreenState.Loaded
                }
            }
        }

        fun onRunDemoClick() {
            _uiState.value = SignInCustomPromptDemoScreenState.Loading

            viewModelScope.launch {
                val node = signInCustomPrompt.shouldDisplayPrompt()

                _uiState.value = if (node != null) {
                    SignInCustomPromptDemoScreenState.WatchFound(node.id)
                } else {
                    SignInCustomPromptDemoScreenState.WatchNotFound
                }
            }
        }

        fun onPromptSignInClick(nodeId: String) {
            viewModelScope.launch {
                signInCustomPrompt.performAction(nodeId = nodeId)
            }

            _uiState.value = SignInCustomPromptDemoScreenState.PromptSignInClicked
        }

        fun onPromptDismiss() {
            _uiState.value = SignInCustomPromptDemoScreenState.PromptDismissed
        }
    }

sealed class SignInCustomPromptDemoScreenState {
    data object Idle : SignInCustomPromptDemoScreenState()
    data object Loading : SignInCustomPromptDemoScreenState()
    data object Loaded : SignInCustomPromptDemoScreenState()
    data class WatchFound(val nodeId: String) : SignInCustomPromptDemoScreenState()
    data object WatchNotFound : SignInCustomPromptDemoScreenState()
    data object PromptSignInClicked : SignInCustomPromptDemoScreenState()
    data object PromptDismissed : SignInCustomPromptDemoScreenState()
    data object ApiNotAvailable : SignInCustomPromptDemoScreenState()
}
