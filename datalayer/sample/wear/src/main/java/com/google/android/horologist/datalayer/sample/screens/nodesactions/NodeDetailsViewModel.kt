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
import com.google.android.horologist.datalayer.watch.WearDataLayerAppHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NodeDetailsViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val wearDataLayerAppHelper: WearDataLayerAppHelper,
    ) : ViewModel() {
        private val nodeDetailsScreenArgs: NodeDetailsScreenArgs =
            NodeDetailsScreenArgs(savedStateHandle)

        val nodeId: String
            get() = nodeDetailsScreenArgs.nodeId

        fun onStartCompanionClick() {
            viewModelScope.launch {
                wearDataLayerAppHelper.startCompanion(node = nodeId)
            }
        }

        fun onInstallOnNodeClick() {
            viewModelScope.launch {
                wearDataLayerAppHelper.installOnNode(node = nodeId)
            }
        }

        fun onStartRemoteOwnAppClick() {
            viewModelScope.launch {
                wearDataLayerAppHelper.startRemoteOwnApp(node = nodeId)
            }
        }
    }
