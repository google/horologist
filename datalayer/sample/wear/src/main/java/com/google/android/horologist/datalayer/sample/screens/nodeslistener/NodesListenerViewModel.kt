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

package com.google.android.horologist.datalayer.sample.screens.nodeslistener

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.datalayer.watch.WearDataLayerAppHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NodesListenerViewModel
    @Inject
    constructor(
        private val wearDataLayerAppHelper: WearDataLayerAppHelper,
    ) : ViewModel() {

        private var initializeCalled = false

        private val _uiState =
            MutableStateFlow<NodesListenerScreenState>(NodesListenerScreenState.Idle)
        public val uiState: StateFlow<NodesListenerScreenState> = _uiState

        @MainThread
        fun initialize() {
            if (initializeCalled) return
            initializeCalled = true

            _uiState.value = NodesListenerScreenState.Loading

            viewModelScope.launch {
                if (!wearDataLayerAppHelper.isAvailable()) {
                    _uiState.value = NodesListenerScreenState.ApiNotAvailable
                } else {
                    loadNodes()
                }
            }
        }

        private suspend fun loadNodes() {
            _uiState.value = NodesListenerScreenState.Loading

            wearDataLayerAppHelper.connectedAndInstalledNodes.collect { nodes ->
                _uiState.value = NodesListenerScreenState.Loaded(
                    nodeList = nodes.mapTo(mutableSetOf()) { it.toNodesUIMapper() },
                )
            }
        }
    }

sealed class NodesListenerScreenState {
    data object Idle : NodesListenerScreenState()

    data object Loading : NodesListenerScreenState()

    data class Loaded(val nodeList: Set<NodeUiModel>) : NodesListenerScreenState()

    data object ApiNotAvailable : NodesListenerScreenState()
}
