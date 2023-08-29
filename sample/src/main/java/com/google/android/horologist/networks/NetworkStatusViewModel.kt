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

public class NetworkStatusViewModel(
    private val networkRepository: NetworkRepository,
    private val dataRequestRepository: DataRequestRepository,
) : ViewModel() {
    val state =
        combine(
            networkRepository.networkStatus,
            dataRequestRepository.currentPeriodUsage(),
        ) { networkStatus, currentPeriodUsage ->
            NetworkStatusAppState(networks = networkStatus, dataUsage = currentPeriodUsage)
        }
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = NetworkStatusAppState(
                    networks = networkRepository.networkStatus.value,
                    dataUsage = null,
                ),
            )

    data class NetworkStatusAppState(
        val networks: Networks,
        val dataUsage: DataUsageReport? = null,
    )

    public object Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            check(modelClass == NetworkStatusViewModel::class.java)

            val application = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SampleApplication
            val networkRepository = application.networkRepository
            val dataRequestRepository = application.dataRequestRepository

            return NetworkStatusViewModel(
                networkRepository = networkRepository,
                dataRequestRepository = dataRequestRepository,
            ) as T
        }
    }
}
