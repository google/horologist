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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.data.AppHelperResultCode
import com.google.android.horologist.data.activityConfig
import com.google.android.horologist.datalayer.watch.WearDataLayerAppHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val REMOTE_ACTIVITY_SAMPLE_PACKAGE_NAME = "com.google.android.gm"
private const val REMOTE_ACTIVITY_SAMPLE_CLASS_FULL_NAME = "com.google.android.gm.ConversationListActivityGmail"

@HiltViewModel
class NodeDetailsViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val wearDataLayerAppHelper: WearDataLayerAppHelper,
    ) : ViewModel() {
        private val nodeDetailsScreenArgs: NodeDetailsScreenArgs = NodeDetailsScreenArgs(savedStateHandle)

        val nodeId: String
            get() = nodeDetailsScreenArgs.nodeId

        private val _uiState = MutableStateFlow<NodeDetailsScreenState>(NodeDetailsScreenState.Idle)
        public val uiState: StateFlow<NodeDetailsScreenState> = _uiState

        fun onStartCompanionClick() {
            runActionAndHandleAppHelperResult {
                wearDataLayerAppHelper.startCompanion(node = nodeId)
            }
        }

        fun onInstallOnNodeClick() {
            _uiState.value = NodeDetailsScreenState.ActionRunning
            viewModelScope.launch {
                try {
                    wearDataLayerAppHelper.installOnNode(node = nodeId)

                    _uiState.value = NodeDetailsScreenState.ActionSucceeded
                } catch (e: Exception) {
                    // This should be handled with AppHelperResultCode if API gets improved:
                    // https://github.com/google/horologist/issues/1902
                    _uiState.value = NodeDetailsScreenState.ActionFailed(errorCode = e::class.java.simpleName)
                    e.printStackTrace()
                }
            }
        }

        fun onStartRemoteOwnAppClick() {
            runActionAndHandleAppHelperResult {
                wearDataLayerAppHelper.startRemoteOwnApp(node = nodeId)
            }
        }

        fun onStartRemoteActivityClick() {
            runActionAndHandleAppHelperResult {
                val config = activityConfig {
                    packageName = REMOTE_ACTIVITY_SAMPLE_PACKAGE_NAME
                    classFullName = REMOTE_ACTIVITY_SAMPLE_CLASS_FULL_NAME
                }
                wearDataLayerAppHelper.startRemoteActivity(nodeId, config)
            }
        }

        fun onDialogDismiss() {
            _uiState.value = NodeDetailsScreenState.Idle
        }

        private fun runActionAndHandleAppHelperResult(action: suspend () -> AppHelperResultCode) {
            _uiState.value = NodeDetailsScreenState.ActionRunning
            viewModelScope.launch {
                when (val result = action()) {
                    AppHelperResultCode.APP_HELPER_RESULT_SUCCESS -> {
                        _uiState.value = NodeDetailsScreenState.ActionSucceeded
                    }

                    else -> {
                        _uiState.value = NodeDetailsScreenState.ActionFailed(errorCode = result.name)
                    }
                }
            }
        }
    }

sealed class NodeDetailsScreenState {
    data object Idle : NodeDetailsScreenState()
    data object ActionRunning : NodeDetailsScreenState()
    data object ActionSucceeded : NodeDetailsScreenState()
    data class ActionFailed(val errorCode: String) : NodeDetailsScreenState()
}
