/*
 * Copyright 2022 The Android Open Source Project
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

package com.google.android.horologist.datalayer

import android.os.Build
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.android.gms.wearable.Node
import com.google.android.horologist.components.SampleApplication
import com.google.android.horologist.data.ProtoDataStoreHelper.protoDataStore
import com.google.android.horologist.data.ProtoDataStoreHelper.protoFlow
import com.google.android.horologist.data.TargetNodeId
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.data.proto.SampleProto.Data
import com.google.android.horologist.data.proto.copy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

public class DataLayerNodesViewModel(
    private val registry: WearDataLayerRegistry
) : ViewModel() {
    val nodes = flow {
        val self = registry.nodeClient.localNode.await()
        emit(registry.nodeClient.connectedNodes.await() + self)
    }.shareIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        replay = 1
    )

    val protoState = flow {
        val ids = nodes.first().map { it.id }

        val flow: Flow<Map<String, Data>> = combine(
            ids.map { registry.protoFlow<Data>(TargetNodeId.SpecificNodeId(it)) }
        ) {
            it.zip(ids) { data, id -> id to data }.toMap()
        }

        emitAll(flow)
    }

    val thisDataStore: DataStore<Data> = registry.protoDataStore(viewModelScope)

    val state =
        combine(
            nodes,
            protoState,
            thisDataStore.data
        ) { nodes, protoMap, thisData ->
            UIState(nodes, protoMap, thisData)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UIState()
        )

    fun increment() {
        viewModelScope.launch {
            thisDataStore.updateData { current ->
                current.copy {
                    value += +1
                    name = Build.DEVICE
                }
            }
        }
    }

    data class UIState(
        val nodes: List<Node> = listOf(),
        val protoMap: Map<String, Data> = mapOf(),
        val thisData: Data? = null
    )

    public object Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            check(modelClass == DataLayerNodesViewModel::class.java)

            val application =
                extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SampleApplication

            return DataLayerNodesViewModel(registry = application.registry) as T
        }
    }
}
