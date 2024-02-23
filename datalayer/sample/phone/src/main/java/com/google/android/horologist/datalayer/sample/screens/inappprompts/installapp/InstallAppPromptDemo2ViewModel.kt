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
import com.google.android.horologist.data.apphelper.appInstalled
import com.google.android.horologist.datalayer.phone.PhoneDataLayerAppHelper
import com.google.android.horologist.datalayer.phone.ui.PhoneUiDataLayerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InstallAppPromptDemo2ViewModel
    @Inject
    constructor(
        private val phoneDataLayerAppHelper: PhoneDataLayerAppHelper,
        val phoneUiDataLayerHelper: PhoneUiDataLayerHelper,
    ) : ViewModel() {

        private var initializeCalled = false

        private val _uiState =
            MutableStateFlow<InstallAppPromptDemo2ScreenState>(InstallAppPromptDemo2ScreenState.Idle)
        public val uiState: StateFlow<InstallAppPromptDemo2ScreenState> = _uiState

        @MainThread
        fun initialize() {
            if (initializeCalled) return
            initializeCalled = true

            _uiState.value = InstallAppPromptDemo2ScreenState.Loading

            viewModelScope.launch {
                if (!phoneDataLayerAppHelper.isAvailable()) {
                    _uiState.value = InstallAppPromptDemo2ScreenState.ApiNotAvailable
                } else {
                    _uiState.value = InstallAppPromptDemo2ScreenState.Loaded
                }
            }
        }

        fun onRunDemoClick() {
            _uiState.value = InstallAppPromptDemo2ScreenState.Loading

            viewModelScope.launch {
                val node = phoneDataLayerAppHelper.connectedNodes().firstOrNull { !it.appInstalled }

                _uiState.value = if (node != null) {
                    InstallAppPromptDemo2ScreenState.WatchFound
                } else {
                    InstallAppPromptDemo2ScreenState.WatchNotFound
                }
            }
        }

        fun onInstallPromptLaunched() {
            _uiState.value = InstallAppPromptDemo2ScreenState.Idle
        }

        fun onInstallPromptInstallClick() {
            _uiState.value = InstallAppPromptDemo2ScreenState.InstallPromptInstallClicked
        }

        fun onInstallPromptCancel() {
            _uiState.value = InstallAppPromptDemo2ScreenState.InstallPromptInstallCancelled
        }
    }

sealed class InstallAppPromptDemo2ScreenState {
    data object Idle : InstallAppPromptDemo2ScreenState()
    data object Loading : InstallAppPromptDemo2ScreenState()
    data object Loaded : InstallAppPromptDemo2ScreenState()
    data object WatchFound : InstallAppPromptDemo2ScreenState()
    data object WatchNotFound : InstallAppPromptDemo2ScreenState()
    data object InstallPromptInstallClicked : InstallAppPromptDemo2ScreenState()
    data object InstallPromptInstallCancelled : InstallAppPromptDemo2ScreenState()
    data object ApiNotAvailable : InstallAppPromptDemo2ScreenState()
}
