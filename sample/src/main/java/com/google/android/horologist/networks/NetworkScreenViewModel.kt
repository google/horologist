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
import com.google.android.horologist.networks.status.NetworkRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

public class NetworkScreenViewModel(
    private val networkRepository: NetworkRepositoryImpl,
    private val dataRequestRepository: DataRequestRepository,
    private val inMemory: InMemoryStatusLogger,
    private val callFactory: Call.Factory,
    private val okHttpClient: OkHttpClient
) : ViewModel() {
    val request = Request.Builder()
        .url("https://github.com/google/horologist/raw/main/media-sample/backend/images/album_art.jpg")
        .build()

    fun makeRequests() {
        viewModelScope.launch {
            val networkSelectingResponse = makeRequest(callFactory)
            delay(50)
            val standardCallResponse = makeRequest(okHttpClient)

            responses.value = mapOf("network aware" to networkSelectingResponse.await(), "standard" to standardCallResponse.await())
        }
    }

    private fun CoroutineScope.makeRequest(callFactory1: Call.Factory) =
        async {
            try {
                callFactory1.newCall(request).executeAsync().use {
                    it.body!!.bytes()
                    it.code.toString()
                }
            } catch (e: IOException) {
                e.toString()
            }
        }

    private val responses = MutableStateFlow(mapOf<String, String>())

    val state =
        combine(
            networkRepository.networkStatus,
            dataRequestRepository.currentPeriodUsage(),
            inMemory.events,
            responses
        ) { networkStatus, currentPeriodUsage, requests, responses ->
            NetworkScreenUiState(
                networks = networkStatus,
                dataUsage = currentPeriodUsage,
                requests = requests,
                responses = responses
            )
        }
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = NetworkScreenUiState(
                    networks = networkRepository.networkStatus.value,
                    dataUsage = DataUsageReport.Empty,
                    requests = listOf(),
                    responses = mapOf()
                )
            )

    data class NetworkScreenUiState(
        val networks: Networks,
        val dataUsage: DataUsageReport,
        val requests: List<InMemoryStatusLogger.Event>,
        val responses: Map<String, String>
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
                callFactory = application.networkAwareCallFactory,
                okHttpClient = application.okHttpClient
            ) as T
        }
    }
}
