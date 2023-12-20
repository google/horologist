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

package com.google.android.horologist.datalayer.sample.screens.nodesactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.data.apphelper.AppInstallationStatus
import com.google.android.horologist.data.apphelper.AppInstallationStatusNodeType
import com.google.android.horologist.datalayer.watch.WearDataLayerAppHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NodesActionViewModel
    @Inject
    constructor(
        private val wearDataLayerAppHelper: WearDataLayerAppHelper,
    ) : ViewModel() {

        private var initializeCalled = false

        private val _uiState =
            MutableStateFlow<NodesActionScreenState>(NodesActionScreenState.Idle)
        public val uiState: StateFlow<NodesActionScreenState> = _uiState

        fun initialize() {
            if (initializeCalled) return
            initializeCalled = true

            _uiState.value = NodesActionScreenState.Loading

            viewModelScope.launch {
                if (!wearDataLayerAppHelper.isAvailable()) {
                    _uiState.value = NodesActionScreenState.ApiNotAvailable
                } else {
                    loadNodes()
                }
            }
        }

        fun onRefreshClick() {
            _uiState.value = NodesActionScreenState.Loading

            viewModelScope.launch {
                loadNodes()
            }
        }

        private suspend fun loadNodes() {
            _uiState.value = NodesActionScreenState.Loaded(
                nodeList = wearDataLayerAppHelper.connectedNodes().map { node ->
                    val type = when (node.appInstallationStatus) {
                        is AppInstallationStatus.Installed -> {
                            val status = node.appInstallationStatus as AppInstallationStatus.Installed
                            when (status.nodeType) {
                                AppInstallationStatusNodeType.WATCH -> NodeTypeUiModel.WATCH
                                AppInstallationStatusNodeType.PHONE -> NodeTypeUiModel.PHONE
                                AppInstallationStatusNodeType.UNKNOWN -> NodeTypeUiModel.UNKNOWN
                            }
                        }
                        AppInstallationStatus.NotInstalled -> NodeTypeUiModel.UNKNOWN
                    }

                    NodeUiModel(
                        id = node.id,
                        name = node.displayName,
                        appInstalled = node.appInstallationStatus is AppInstallationStatus.Installed,
                        type = type,
                    )
                },
            )
        }
    }

sealed class NodesActionScreenState {
    data object Idle : NodesActionScreenState()

    data object Loading : NodesActionScreenState()

    data class Loaded(val nodeList: List<NodeUiModel>) : NodesActionScreenState()

    data object ApiNotAvailable : NodesActionScreenState()
}
