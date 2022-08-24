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

package com.google.android.horologist.networks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.android.horologist.components.SampleApplication
import com.google.android.horologist.networks.data.DataRequestRepository
import com.google.android.horologist.networks.data.DataUsageReport
import com.google.android.horologist.networks.data.Networks
import com.google.android.horologist.networks.status.NetworkRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

public class NetworkScreenViewModel(
    private val networkRepository: NetworkRepository,
    private val dataRequestRepository: DataRequestRepository,
    private val inMemory: InMemoryStatusLogger,
    private val callFactory: Call.Factory
) : ViewModel() {
    fun makeRequest() {
        viewModelScope.launch {
            callFactory.newCall(
                Request.Builder()
                    .url("https://github.com/google/horologist/raw/main/media-sample/backend/images/album_art.jpg")
                    .build()
            ).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    inMemory.logNetworkEvent("response: $e")
                }

                override fun onResponse(call: Call, response: Response) {
                    response.body?.bytes()
                }
            })
        }
    }

    val state =
        combine(
            networkRepository.networkStatus,
            dataRequestRepository.currentPeriodUsage(),
            inMemory.events
        ) { networkStatus, currentPeriodUsage, requests ->
            NetworkScreenUiState(
                networks = networkStatus,
                dataUsage = currentPeriodUsage,
                requests = requests
            )
        }
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = NetworkScreenUiState(
                    networks = networkRepository.networkStatus.value,
                    dataUsage = DataUsageReport.Empty,
                    requests = listOf()
                )
            )

    data class NetworkScreenUiState(
        val networks: Networks,
        val dataUsage: DataUsageReport,
        val requests: List<InMemoryStatusLogger.Event>
    )

    public object Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            check(modelClass == NetworkScreenViewModel::class.java)

            val application =
                extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SampleApplication

            return NetworkScreenViewModel(
                networkRepository = application.networkRepository,
                dataRequestRepository = application.dataRequestRepository,
                inMemory = application.networkLogger,
                callFactory = application.networkAwareCallFactory
            ) as T
        }
    }
}
