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

package com.google.android.horologist.datalayer.sample.screens.inappprompts.installapp

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.data.apphelper.AppHelperNodeStatus
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
            MutableStateFlow<InstallAppPromptDemoScreenState>(InstallAppPromptDemoScreenState.Idle)
        public val uiState: StateFlow<InstallAppPromptDemoScreenState> = _uiState

        @MainThread
        fun initialize() {
            if (initializeCalled) return
            initializeCalled = true

            _uiState.value = InstallAppPromptDemoScreenState.Loading

            viewModelScope.launch {
                if (!phoneDataLayerAppHelper.isAvailable()) {
                    _uiState.value = InstallAppPromptDemoScreenState.ApiNotAvailable
                } else {
                    _uiState.value = InstallAppPromptDemoScreenState.Loaded
                }
            }
        }

        fun onRunDemoClick(shouldFilterByNearby: Boolean) {
            _uiState.value = InstallAppPromptDemoScreenState.Loading

            viewModelScope.launch {
                val filter = if (shouldFilterByNearby) {
                    { node: AppHelperNodeStatus -> node.isNearby }
                } else {
                    null
                }
                val node = installAppPrompt.shouldDisplayPrompt(filter)

                _uiState.value = if (node != null) {
                    InstallAppPromptDemoScreenState.WatchFound
                } else {
                    InstallAppPromptDemoScreenState.WatchNotFound
                }
            }
        }

        fun onInstallPromptLaunched() {
            _uiState.value = InstallAppPromptDemoScreenState.Idle
        }

        fun onInstallPromptInstallClick() {
            _uiState.value = InstallAppPromptDemoScreenState.InstallPromptInstallClicked
        }

        fun onInstallPromptCancel() {
            _uiState.value = InstallAppPromptDemoScreenState.InstallPromptInstallCancelled
        }
    }

sealed class InstallAppPromptDemoScreenState {
    data object Idle : InstallAppPromptDemoScreenState()
    data object Loading : InstallAppPromptDemoScreenState()
    data object Loaded : InstallAppPromptDemoScreenState()
    data object WatchFound : InstallAppPromptDemoScreenState()
    data object WatchNotFound : InstallAppPromptDemoScreenState()
    data object InstallPromptInstallClicked : InstallAppPromptDemoScreenState()
    data object InstallPromptInstallCancelled : InstallAppPromptDemoScreenState()
    data object ApiNotAvailable : InstallAppPromptDemoScreenState()
}
