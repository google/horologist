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

package com.google.android.horologist.datalayer.sample.screens.inappprompts.installtile

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
            MutableStateFlow<InstallTilePromptDemoScreenState>(InstallTilePromptDemoScreenState.Idle)
        public val uiState: StateFlow<InstallTilePromptDemoScreenState> = _uiState

        @MainThread
        fun initialize() {
            if (initializeCalled) return
            initializeCalled = true

            _uiState.value = InstallTilePromptDemoScreenState.Loading

            viewModelScope.launch {
                if (!phoneDataLayerAppHelper.isAvailable()) {
                    _uiState.value = InstallTilePromptDemoScreenState.ApiNotAvailable
                } else {
                    _uiState.value = InstallTilePromptDemoScreenState.Loaded
                }
            }
        }

        fun onRunDemoClick() {
            _uiState.value = InstallTilePromptDemoScreenState.Loading

            viewModelScope.launch {
                val node = installTilePrompt.shouldDisplayPrompt()

                _uiState.value = if (node != null) {
                    InstallTilePromptDemoScreenState.WatchFound(node.id)
                } else {
                    InstallTilePromptDemoScreenState.WatchNotFound
                }
            }
        }

        fun onPromptLaunched() {
            _uiState.value = InstallTilePromptDemoScreenState.Idle
        }

        fun onPromptPositiveButtonClick() {
            _uiState.value = InstallTilePromptDemoScreenState.PromptPositiveButtonClicked
        }

        fun onPromptDismiss() {
            _uiState.value = InstallTilePromptDemoScreenState.PromptDismissed
        }
    }

sealed class InstallTilePromptDemoScreenState {
    data object Idle : InstallTilePromptDemoScreenState()
    data object Loading : InstallTilePromptDemoScreenState()
    data object Loaded : InstallTilePromptDemoScreenState()
    data class WatchFound(val nodeId: String) : InstallTilePromptDemoScreenState()
    data object WatchNotFound : InstallTilePromptDemoScreenState()
    data object PromptPositiveButtonClicked : InstallTilePromptDemoScreenState()
    data object PromptDismissed : InstallTilePromptDemoScreenState()
    data object ApiNotAvailable : InstallTilePromptDemoScreenState()
}
