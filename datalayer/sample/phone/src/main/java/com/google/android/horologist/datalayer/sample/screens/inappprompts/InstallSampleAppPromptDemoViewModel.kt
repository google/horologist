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

package com.google.android.horologist.datalayer.sample.screens.inappprompts

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
class InstallSampleAppPromptDemoViewModel
    @Inject
    constructor(
        private val phoneDataLayerAppHelper: PhoneDataLayerAppHelper,
        val phoneUiDataLayerHelper: PhoneUiDataLayerHelper,
    ) : ViewModel() {

        private val _uiState =
            MutableStateFlow<InstallSampleAppPromptDemoScreenState>(InstallSampleAppPromptDemoScreenState.Idle)
        public val uiState: StateFlow<InstallSampleAppPromptDemoScreenState> = _uiState

        fun onRunDemoClick() {
            _uiState.value = InstallSampleAppPromptDemoScreenState.Loading

            viewModelScope.launch {
                val node = phoneDataLayerAppHelper.connectedNodes().firstOrNull { !it.appInstalled }

                _uiState.value = if (node != null) {
                    InstallSampleAppPromptDemoScreenState.WatchFound(watchName = node.displayName)
                } else {
                    InstallSampleAppPromptDemoScreenState.WatchNotFound
                }
            }
        }

        fun onInstallPromptLaunched() {
            _uiState.value = InstallSampleAppPromptDemoScreenState.Idle
        }

        fun onInstallPromptInstallClick() {
            _uiState.value = InstallSampleAppPromptDemoScreenState.InstallPromptInstallClicked
        }

        fun onInstallPromptCancel() {
            _uiState.value = InstallSampleAppPromptDemoScreenState.InstallPromptInstallCancelled
        }
    }

sealed class InstallSampleAppPromptDemoScreenState {
    data object Idle : InstallSampleAppPromptDemoScreenState()
    data object Loading : InstallSampleAppPromptDemoScreenState()
    data class WatchFound(val watchName: String) : InstallSampleAppPromptDemoScreenState()
    data object WatchNotFound : InstallSampleAppPromptDemoScreenState()
    data object InstallPromptInstallClicked : InstallSampleAppPromptDemoScreenState()
    data object InstallPromptInstallCancelled : InstallSampleAppPromptDemoScreenState()
}
