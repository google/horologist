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

package com.google.android.horologist.datalayer.sample.screens.inappprompts.custom.installtile

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.datalayer.phone.PhoneDataLayerAppHelper
import com.google.android.horologist.datalayer.phone.ui.prompt.installtile.InstallTilePrompt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InstallTilePromptDemoViewModel
    @Inject
    constructor(
        private val phoneDataLayerAppHelper: PhoneDataLayerAppHelper,
        val installTilePrompt: InstallTilePrompt,
    ) : ViewModel() {

        private var initializeCalled = false

        private val _uiState =
            MutableStateFlow<InstallTileCustomPromptDemoScreenState>(InstallTileCustomPromptDemoScreenState.Idle)
        private val tileName = "com.google.android.horologist.datalayer.sample.SampleTileService"
        public val uiState: StateFlow<InstallTileCustomPromptDemoScreenState> = _uiState

        @MainThread
        fun initialize() {
            if (initializeCalled) return
            initializeCalled = true

            _uiState.value = InstallTileCustomPromptDemoScreenState.Loading

            viewModelScope.launch {
                if (!phoneDataLayerAppHelper.isAvailable()) {
                    _uiState.value = InstallTileCustomPromptDemoScreenState.ApiNotAvailable
                } else {
                    _uiState.value = InstallTileCustomPromptDemoScreenState.Loaded
                }
            }
        }

        fun onRunDemoClick() {
            _uiState.value = InstallTileCustomPromptDemoScreenState.Loading

            viewModelScope.launch {
                val node = installTilePrompt.shouldDisplayPrompt(tileName)

                _uiState.value = if (node != null) {
                    InstallTileCustomPromptDemoScreenState.WatchFound
                } else {
                    InstallTileCustomPromptDemoScreenState.WatchNotFound
                }
            }
        }

        fun onInstallPromptInstallClick() {
            _uiState.value = InstallTileCustomPromptDemoScreenState.InstallPromptInstallClicked
        }

        fun onInstallPromptCancel() {
            _uiState.value = InstallTileCustomPromptDemoScreenState.InstallPromptInstallCancelled
        }
    }

sealed class InstallTileCustomPromptDemoScreenState {
    data object Idle : InstallTileCustomPromptDemoScreenState()
    data object Loading : InstallTileCustomPromptDemoScreenState()
    data object Loaded : InstallTileCustomPromptDemoScreenState()
    data object WatchFound : InstallTileCustomPromptDemoScreenState()
    data object WatchNotFound : InstallTileCustomPromptDemoScreenState()
    data object InstallPromptInstallClicked : InstallTileCustomPromptDemoScreenState()
    data object InstallPromptInstallCancelled : InstallTileCustomPromptDemoScreenState()
    data object ApiNotAvailable : InstallTileCustomPromptDemoScreenState()
}
