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

package com.google.android.horologist.datalayer.sample.screens.inappprompts.custom.installapp

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.datalayer.phone.PhoneDataLayerAppHelper
import com.google.android.horologist.datalayer.phone.ui.prompt.installapp.InstallAppPrompt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InstallAppPromptDemoViewModel
    @Inject
    constructor(
        private val phoneDataLayerAppHelper: PhoneDataLayerAppHelper,
        val installAppPrompt: InstallAppPrompt,
    ) : ViewModel() {

        private var initializeCalled = false

        private val _uiState =
            MutableStateFlow<InstallAppCustomPromptDemoScreenState>(InstallAppCustomPromptDemoScreenState.Idle)
        public val uiState: StateFlow<InstallAppCustomPromptDemoScreenState> = _uiState

        @MainThread
        fun initialize() {
            if (initializeCalled) return
            initializeCalled = true

            _uiState.value = InstallAppCustomPromptDemoScreenState.Loading

            viewModelScope.launch {
                if (!phoneDataLayerAppHelper.isAvailable()) {
                    _uiState.value = InstallAppCustomPromptDemoScreenState.ApiNotAvailable
                } else {
                    _uiState.value = InstallAppCustomPromptDemoScreenState.Loaded
                }
            }
        }

        fun onRunDemoClick() {
            _uiState.value = InstallAppCustomPromptDemoScreenState.Loading

            viewModelScope.launch {
                val node = installAppPrompt.shouldDisplayPrompt()

                _uiState.value = if (node != null) {
                    InstallAppCustomPromptDemoScreenState.WatchFound
                } else {
                    InstallAppCustomPromptDemoScreenState.WatchNotFound
                }
            }
        }

        fun onInstallPromptInstallClick() {
            _uiState.value = InstallAppCustomPromptDemoScreenState.InstallPromptInstallClicked
        }

        fun onInstallPromptCancel() {
            _uiState.value = InstallAppCustomPromptDemoScreenState.InstallPromptInstallCancelled
        }
    }

sealed class InstallAppCustomPromptDemoScreenState {
    data object Idle : InstallAppCustomPromptDemoScreenState()
    data object Loading : InstallAppCustomPromptDemoScreenState()
    data object Loaded : InstallAppCustomPromptDemoScreenState()
    data object WatchFound : InstallAppCustomPromptDemoScreenState()
    data object WatchNotFound : InstallAppCustomPromptDemoScreenState()
    data object InstallPromptInstallClicked : InstallAppCustomPromptDemoScreenState()
    data object InstallPromptInstallCancelled : InstallAppCustomPromptDemoScreenState()
    data object ApiNotAvailable : InstallAppCustomPromptDemoScreenState()
}
