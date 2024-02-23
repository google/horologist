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

package com.google.android.horologist.datalayer.sample.screens.inappprompts.signin

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.data.UsageStatus
import com.google.android.horologist.datalayer.phone.PhoneDataLayerAppHelper
import com.google.android.horologist.datalayer.phone.ui.prompt.signin.SignInPrompt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInPromptDemoViewModel
    @Inject
    constructor(
        private val phoneDataLayerAppHelper: PhoneDataLayerAppHelper,
        val signInPrompt: SignInPrompt,
    ) : ViewModel() {

        private var initializeCalled = false

        private val _uiState =
            MutableStateFlow<SignInPromptDemoScreenState>(SignInPromptDemoScreenState.Idle)
        public val uiState: StateFlow<SignInPromptDemoScreenState> = _uiState

        @MainThread
        fun initialize() {
            if (initializeCalled) return
            initializeCalled = true

            _uiState.value = SignInPromptDemoScreenState.Loading

            viewModelScope.launch {
                if (!phoneDataLayerAppHelper.isAvailable()) {
                    _uiState.value = SignInPromptDemoScreenState.ApiNotAvailable
                } else {
                    _uiState.value = SignInPromptDemoScreenState.Loaded
                }
            }
        }

        fun onRunDemoClick() {
            _uiState.value = SignInPromptDemoScreenState.Loading

            viewModelScope.launch {
                val node = phoneDataLayerAppHelper.connectedNodes().firstOrNull {
                    it.surfacesInfo.usageInfo.usageStatus == UsageStatus.USAGE_STATUS_SETUP_COMPLETE
                }

                _uiState.value = if (node != null) {
                    SignInPromptDemoScreenState.WatchFound(node.id)
                } else {
                    SignInPromptDemoScreenState.WatchNotFound
                }
            }
        }

        fun onPromptLaunched() {
            _uiState.value = SignInPromptDemoScreenState.Idle
        }

        fun onPromptSignInClick() {
            _uiState.value = SignInPromptDemoScreenState.PromptPositiveButtonClicked
        }

        fun onPromptDismiss() {
            _uiState.value = SignInPromptDemoScreenState.PromptDismissed
        }
    }

sealed class SignInPromptDemoScreenState {
    data object Idle : SignInPromptDemoScreenState()
    data object Loading : SignInPromptDemoScreenState()
    data object Loaded : SignInPromptDemoScreenState()
    data class WatchFound(val nodeId: String) : SignInPromptDemoScreenState()
    data object WatchNotFound : SignInPromptDemoScreenState()
    data object PromptPositiveButtonClicked : SignInPromptDemoScreenState()
    data object PromptDismissed : SignInPromptDemoScreenState()
    data object ApiNotAvailable : SignInPromptDemoScreenState()
}
