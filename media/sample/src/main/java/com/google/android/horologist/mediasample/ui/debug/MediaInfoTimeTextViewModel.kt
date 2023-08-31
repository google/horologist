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

package com.google.android.horologist.mediasample.ui.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.media3.offload.AudioOffloadManager
import com.google.android.horologist.media3.offload.AudioOffloadStatus
import com.google.android.horologist.mediasample.domain.SettingsRepository
import com.google.android.horologist.networks.data.DataRequestRepository
import com.google.android.horologist.networks.data.DataUsageReport
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.data.Networks
import com.google.android.horologist.networks.highbandwidth.HighBandwidthNetworkMediator
import com.google.android.horologist.networks.status.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MediaInfoTimeTextViewModel
    @Inject
    constructor(
        networkRepository: NetworkRepository,
        dataRequestRepository: DataRequestRepository,
        audioOffloadManager: AudioOffloadManager,
        highBandwidthNetworkMediator: HighBandwidthNetworkMediator,
        settingsRepository: SettingsRepository,
    ) : ViewModel() {
        val enabledFlow: Flow<Boolean> =
            settingsRepository.settingsFlow.map { it.showTimeTextInfo }

        val uiState = enabledFlow.flatMapLatest { enabled ->
            if (enabled) {
                combine(
                    networkRepository.networkStatus,
                    audioOffloadManager.offloadStatus,
                    dataRequestRepository.currentPeriodUsage(),
                    highBandwidthNetworkMediator.pinned,
                ) { networkStatus, offloadStatus, currentPeriodUsage, pinnedNetworks ->
                    UiState(
                        enabled = enabled,
                        networks = networkStatus,
                        audioOffloadStatus = offloadStatus,
                        dataUsageReport = currentPeriodUsage,
                        pinnedNetworks = pinnedNetworks,
                    )
                }
            } else {
                flowOf(UiState())
            }
        }
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
                initialValue = UiState(),
            )

        data class UiState(
            val enabled: Boolean = false,
            val networks: Networks = Networks(null, listOf()),
            val audioOffloadStatus: AudioOffloadStatus? = null,
            val dataUsageReport: DataUsageReport? = null,
            val pinnedNetworks: Set<NetworkType> = setOf(),
        )
    }
