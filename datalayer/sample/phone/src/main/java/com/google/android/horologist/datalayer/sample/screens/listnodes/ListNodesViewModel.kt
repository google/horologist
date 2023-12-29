/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.datalayer.sample.screens.listnodes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.data.apphelper.AppHelperNodeStatus
import com.google.android.horologist.datalayer.phone.PhoneDataLayerAppHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListNodesViewModel
    @Inject
    constructor(
        private val phoneDataLayerAppHelper: PhoneDataLayerAppHelper,
    ) : ViewModel() {

        private var initializeCalled = false

        private val _uiState = MutableStateFlow<ListNodesScreenUiState>(ListNodesScreenUiState.Idle)
        public val uiState: StateFlow<ListNodesScreenUiState> = _uiState

        fun initialize() {
            if (initializeCalled) return
            initializeCalled = true

            viewModelScope.launch {
                _uiState.value = ListNodesScreenUiState.Loading
                if (!phoneDataLayerAppHelper.isAvailable()) {
                    _uiState.value = ListNodesScreenUiState.ApiNotAvailable
                } else {
                    _uiState.value = ListNodesScreenUiState.Loaded(nodeList = emptyList())
                }
            }
        }

        fun onListNodesClick() {
            viewModelScope.launch {
                _uiState.value = ListNodesScreenUiState.Loading
                val nodeList = phoneDataLayerAppHelper.connectedNodes()
                _uiState.value = ListNodesScreenUiState.Loaded(nodeList = nodeList)
            }
        }

        fun onInstallClick(nodeId: String) {
            viewModelScope.launch {
                phoneDataLayerAppHelper.installOnNode(nodeId)
            }
        }

        fun onLaunchClick(nodeId: String) {
            viewModelScope.launch {
                phoneDataLayerAppHelper.startRemoteOwnApp(nodeId)
            }
        }

        fun onCompanionClick(nodeId: String) {
            viewModelScope.launch {
                phoneDataLayerAppHelper.startCompanion(nodeId)
            }
        }
    }

public sealed class ListNodesScreenUiState {
    public data object Idle : ListNodesScreenUiState()
    public data object Loading : ListNodesScreenUiState()
    public data class Loaded(val nodeList: List<AppHelperNodeStatus>) : ListNodesScreenUiState()
    public data object ApiNotAvailable : ListNodesScreenUiState()
}
