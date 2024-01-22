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

package com.google.android.horologist.datalayer.sample.screens.nodes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.data.AppHelperResultCode
import com.google.android.horologist.data.activityConfig
import com.google.android.horologist.data.apphelper.AppHelperNodeStatus
import com.google.android.horologist.datalayer.phone.PhoneDataLayerAppHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val REMOTE_ACTIVITY_SAMPLE_CLASS_FULL_NAME =
    "com.google.android.horologist.datalayer.sample.screens.startremote.StartRemoteSampleActivity"

@HiltViewModel
class NodesViewModel
    @Inject
    constructor(
        private val phoneDataLayerAppHelper: PhoneDataLayerAppHelper,
    ) : ViewModel() {

        private var initializeCalled = false
        private var cachedNodeList: List<AppHelperNodeStatus> = emptyList()

        private val _uiState =
            MutableStateFlow<NodesScreenState>(NodesScreenState.Idle)
        public val uiState: StateFlow<NodesScreenState> = _uiState

        fun initialize() {
            if (initializeCalled) return
            initializeCalled = true

            _uiState.value = NodesScreenState.Loading

            viewModelScope.launch {
                if (!phoneDataLayerAppHelper.isAvailable()) {
                    _uiState.value = NodesScreenState.ApiNotAvailable
                } else {
                    loadNodes()
                }
            }
        }

        fun onRefreshClick() {
            viewModelScope.launch {
                loadNodes()
            }
        }

        fun onStartCompanionClick(nodeId: String) {
            runActionAndHandleAppHelperResult {
                phoneDataLayerAppHelper.startCompanion(nodeId = nodeId)
            }
        }

        fun onInstallOnNodeClick(nodeId: String) {
            _uiState.value = NodesScreenState.ActionRunning
            viewModelScope.launch {
                try {
                    phoneDataLayerAppHelper.installOnNode(nodeId = nodeId)

                    _uiState.value = NodesScreenState.ActionSucceeded
                } catch (e: Exception) {
                    _uiState.value = NodesScreenState.ActionFailed(errorCode = e::class.java.simpleName)
                    e.printStackTrace()
                }
            }
        }

        fun onStartRemoteOwnAppClick(nodeId: String) {
            runActionAndHandleAppHelperResult {
                phoneDataLayerAppHelper.startRemoteOwnApp(nodeId = nodeId)
            }
        }

        fun onStartRemoteActivityClick(nodeId: String) {
            runActionAndHandleAppHelperResult {
                val config = activityConfig {
                    classFullName = REMOTE_ACTIVITY_SAMPLE_CLASS_FULL_NAME
                }
                phoneDataLayerAppHelper.startRemoteActivity(nodeId, config)
            }
        }

        fun onDialogDismiss() {
            _uiState.value = NodesScreenState.Loaded(nodeList = cachedNodeList)
        }

        private suspend fun loadNodes() {
            _uiState.value = NodesScreenState.Loading

            cachedNodeList = phoneDataLayerAppHelper.connectedNodes()
            _uiState.value = NodesScreenState.Loaded(nodeList = cachedNodeList)
        }

        private fun runActionAndHandleAppHelperResult(action: suspend () -> AppHelperResultCode) {
            _uiState.value = NodesScreenState.ActionRunning
            viewModelScope.launch {
                when (val result = action()) {
                    AppHelperResultCode.APP_HELPER_RESULT_SUCCESS -> {
                        _uiState.value = NodesScreenState.ActionSucceeded
                    }

                    else -> {
                        _uiState.value =
                            NodesScreenState.ActionFailed(errorCode = result.name)
                    }
                }
            }
        }
    }

sealed class NodesScreenState {
    data object Idle : NodesScreenState()

    data object Loading : NodesScreenState()

    data class Loaded(val nodeList: List<AppHelperNodeStatus>) : NodesScreenState()

    data object ActionRunning : NodesScreenState()
    data object ActionSucceeded : NodesScreenState()
    data class ActionFailed(val errorCode: String) : NodesScreenState()

    data object ApiNotAvailable : NodesScreenState()
}
