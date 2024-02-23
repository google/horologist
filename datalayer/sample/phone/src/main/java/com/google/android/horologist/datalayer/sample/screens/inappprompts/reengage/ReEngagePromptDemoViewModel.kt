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

package com.google.android.horologist.datalayer.sample.screens.inappprompts.reengage

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.data.apphelper.appInstalled
import com.google.android.horologist.datalayer.phone.PhoneDataLayerAppHelper
import com.google.android.horologist.datalayer.phone.ui.prompt.reengage.ReEngagePrompt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReEngagePromptDemoViewModel
    @Inject
    constructor(
        private val phoneDataLayerAppHelper: PhoneDataLayerAppHelper,
        val reEngagePrompt: ReEngagePrompt,
    ) : ViewModel() {

        private var initializeCalled = false

        private val _uiState =
            MutableStateFlow<ReEngagePromptDemoScreenState>(ReEngagePromptDemoScreenState.Idle)
        public val uiState: StateFlow<ReEngagePromptDemoScreenState> = _uiState

        @MainThread
        fun initialize() {
            if (initializeCalled) return
            initializeCalled = true

            _uiState.value = ReEngagePromptDemoScreenState.Loading

            viewModelScope.launch {
                if (!phoneDataLayerAppHelper.isAvailable()) {
                    _uiState.value = ReEngagePromptDemoScreenState.ApiNotAvailable
                } else {
                    _uiState.value = ReEngagePromptDemoScreenState.Loaded
                }
            }
        }

        fun onRunDemoClick() {
            _uiState.value = ReEngagePromptDemoScreenState.Loading

            viewModelScope.launch {
                val node = phoneDataLayerAppHelper.connectedNodes().firstOrNull { it.appInstalled }

                _uiState.value = if (node != null) {
                    ReEngagePromptDemoScreenState.WatchFound(node.id)
                } else {
                    ReEngagePromptDemoScreenState.WatchNotFound
                }
            }
        }

        fun onPromptLaunched() {
            _uiState.value = ReEngagePromptDemoScreenState.Idle
        }

        fun onPromptPositiveButtonClick() {
            _uiState.value = ReEngagePromptDemoScreenState.PromptPositiveButtonClicked
        }

        fun onPromptDismiss() {
            _uiState.value = ReEngagePromptDemoScreenState.PromptDismissed
        }
    }

sealed class ReEngagePromptDemoScreenState {
    data object Idle : ReEngagePromptDemoScreenState()
    data object Loading : ReEngagePromptDemoScreenState()
    data object Loaded : ReEngagePromptDemoScreenState()
    data class WatchFound(val nodeId: String) : ReEngagePromptDemoScreenState()
    data object WatchNotFound : ReEngagePromptDemoScreenState()
    data object PromptPositiveButtonClicked : ReEngagePromptDemoScreenState()
    data object PromptDismissed : ReEngagePromptDemoScreenState()
    data object ApiNotAvailable : ReEngagePromptDemoScreenState()
}
