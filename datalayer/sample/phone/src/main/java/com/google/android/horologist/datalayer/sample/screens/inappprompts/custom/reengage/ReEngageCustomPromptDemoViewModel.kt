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

package com.google.android.horologist.datalayer.sample.screens.inappprompts.custom.reengage

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.datalayer.phone.PhoneDataLayerAppHelper
import com.google.android.horologist.datalayer.phone.ui.prompt.reengage.ReEngagePrompt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReEngageCustomPromptDemoViewModel
    @Inject
    constructor(
        private val phoneDataLayerAppHelper: PhoneDataLayerAppHelper,
        private val reEngagePrompt: ReEngagePrompt,
    ) : ViewModel() {

        private var initializeCalled = false

        private val _uiState =
            MutableStateFlow<ReEngageCustomPromptDemoScreenState>(ReEngageCustomPromptDemoScreenState.Idle)
        public val uiState: StateFlow<ReEngageCustomPromptDemoScreenState> = _uiState

        @MainThread
        fun initialize() {
            if (initializeCalled) return
            initializeCalled = true

            _uiState.value = ReEngageCustomPromptDemoScreenState.Loading

            viewModelScope.launch {
                if (!phoneDataLayerAppHelper.isAvailable()) {
                    _uiState.value = ReEngageCustomPromptDemoScreenState.ApiNotAvailable
                } else {
                    _uiState.value = ReEngageCustomPromptDemoScreenState.Loaded
                }
            }
        }

        fun onRunDemoClick() {
            _uiState.value = ReEngageCustomPromptDemoScreenState.Loading

            viewModelScope.launch {
                val node = reEngagePrompt.shouldDisplayPrompt()

                _uiState.value = if (node != null) {
                    ReEngageCustomPromptDemoScreenState.WatchFound(node.id)
                } else {
                    ReEngageCustomPromptDemoScreenState.WatchNotFound
                }
            }
        }

        fun onPromptPositiveButtonClick(nodeId: String) {
            viewModelScope.launch {
                reEngagePrompt.performAction(nodeId = nodeId)
            }

            _uiState.value = ReEngageCustomPromptDemoScreenState.PromptPositiveButtonClicked
        }

        fun onPromptDismiss() {
            _uiState.value = ReEngageCustomPromptDemoScreenState.PromptDismissed
        }
    }

sealed class ReEngageCustomPromptDemoScreenState {
    data object Idle : ReEngageCustomPromptDemoScreenState()
    data object Loading : ReEngageCustomPromptDemoScreenState()
    data object Loaded : ReEngageCustomPromptDemoScreenState()
    data class WatchFound(val nodeId: String) : ReEngageCustomPromptDemoScreenState()
    data object WatchNotFound : ReEngageCustomPromptDemoScreenState()
    data object PromptPositiveButtonClicked : ReEngageCustomPromptDemoScreenState()
    data object PromptDismissed : ReEngageCustomPromptDemoScreenState()
    data object ApiNotAvailable : ReEngageCustomPromptDemoScreenState()
}
