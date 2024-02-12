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

package com.google.android.horologist.datalayer.sample.screens.counter

import androidx.annotation.MainThread
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.data.ProtoDataStoreHelper.protoDataStore
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.datalayer.phone.PhoneDataLayerAppHelper
import com.google.android.horologist.datalayer.sample.shared.grpc.GrpcDemoProto
import com.google.android.horologist.datalayer.sample.shared.grpc.copy
import com.google.android.horologist.datalayer.sample.util.toProtoTimestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CounterScreenViewModel
    @Inject
    constructor(
        private val phoneDataLayerAppHelper: PhoneDataLayerAppHelper,
        private val registry: WearDataLayerRegistry,
    ) : ViewModel() {

        private var initializeCalled = false
        private val apiAvailable = MutableStateFlow(false)

        private lateinit var counterDataStore: DataStore<GrpcDemoProto.CounterValue>

        @OptIn(ExperimentalCoroutinesApi::class)
        private val counterState: Flow<GrpcDemoProto.CounterValue?> =
            apiAvailable.flatMapLatest { apiAvailable ->
                if (!apiAvailable) {
                    flowOf(null)
                } else {
                    counterDataStore.data
                }
            }

        private val _uiState = MutableStateFlow<CounterScreenUiState>(CounterScreenUiState.Idle)
        public val uiState: StateFlow<CounterScreenUiState> = _uiState

        @MainThread
        fun initialize() {
            if (initializeCalled) return
            initializeCalled = true

            viewModelScope.launch {
                _uiState.value = CounterScreenUiState.CheckingApiAvailability
                if (!phoneDataLayerAppHelper.isAvailable()) {
                    _uiState.value = CounterScreenUiState.ApiNotAvailable
                } else {
                    apiAvailable.value = true
                    counterDataStore =
                        registry.protoDataStore<GrpcDemoProto.CounterValue>(viewModelScope)
                    _uiState.value = CounterScreenUiState.Loading
                }

                counterState.collect { counter ->
                    if (counter != null) {
                        when (_uiState.value) {
                            CounterScreenUiState.Loading,
                            is CounterScreenUiState.Loaded,
                            -> {
                                _uiState.value = CounterScreenUiState.Loaded(counter = counter.value)
                            }
                            else -> { /* noop */ }
                        }
                    }
                }
            }
        }

        fun updateCounter() {
            viewModelScope.launch {
                counterDataStore.updateData {
                    it.copy {
                        value += 1
                        updated = System.currentTimeMillis().toProtoTimestamp()
                    }
                }
            }
        }
    }

public sealed class CounterScreenUiState {
    public data object Idle : CounterScreenUiState()
    public data object Loading : CounterScreenUiState()
    public data class Loaded(val counter: Long) : CounterScreenUiState()
    public data object CheckingApiAvailability : CounterScreenUiState()
    public data object ApiNotAvailable : CounterScreenUiState()
}
