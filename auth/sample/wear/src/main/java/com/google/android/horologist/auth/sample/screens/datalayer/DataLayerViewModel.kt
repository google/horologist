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

package com.google.android.horologist.auth.sample.screens.datalayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.horologist.auth.sample.SampleApplication
import com.google.android.horologist.auth.sample.shared.grpc.CounterServiceGrpcKt.CounterServiceCoroutineStub
import com.google.android.horologist.auth.sample.shared.grpc.GrpcDemoProto.CounterValue
import com.google.android.horologist.auth.sample.shared.grpc.counterDelta
import com.google.android.horologist.auth.sample.shared.grpc.updatedOrNull
import com.google.protobuf.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DataLayerViewModel(
    val counterService: CounterServiceCoroutineStub,
    val counterFlow: Flow<CounterValue>
) : ViewModel() {
    init {
        viewModelScope.launch {
            counterFlow.collectLatest { newValue ->
                uiState.update { state ->
                    updateIfNewer(state, newValue)
                }
            }
        }
    }

    fun addDelta(i: Int) {
        viewModelScope.launch {
            try {
                val newValue: CounterValue =
                    counterService.increment(counterDelta { delta = i.toLong() })
                uiState.update {
                    updateIfNewer(it, newValue)
                }
            } catch (e: Exception) {
                uiState.update {
                    it.copy(error = e.message)
                }
            }
        }
    }

    private fun updateIfNewer(
        it: DataLayerScreenState,
        newValue: CounterValue
    ): DataLayerScreenState {
        val currentUpdated = it.counterValue?.updatedOrNull
        return if (currentUpdated == null || currentUpdated.isBefore(newValue)) {
            it.copy(counterValue = newValue)
        } else {
            it
        }
    }

    val uiState: MutableStateFlow<DataLayerScreenState> = MutableStateFlow(DataLayerScreenState())

    companion object {
        public val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as SampleApplication)

                DataLayerViewModel(application.counterService, application.counterFlow)
            }
        }
    }
}

private fun Timestamp.isBefore(other: CounterValue): Boolean =
    seconds < other.updated.seconds || (seconds == other.updated.seconds && nanos < other.updated.nanos)

data class DataLayerScreenState(
    val counterValue: CounterValue? = null,
    val error: String? = null
)
