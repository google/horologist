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

package com.google.android.horologist.mediasample.ui.settings

import android.os.Process
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.media.ui.snackbar.SnackbarManager
import com.google.android.horologist.media.ui.snackbar.UiMessage
import com.google.android.horologist.mediasample.di.IsEmulator
import com.google.android.horologist.mediasample.domain.SettingsRepository
import com.google.android.horologist.mediasample.domain.proto.SettingsProto.OffloadMode
import com.google.android.horologist.mediasample.domain.proto.copy
import com.google.android.horologist.networks.highbandwidth.HighBandwidthConnectionLease
import com.google.android.horologist.networks.highbandwidth.HighBandwidthNetworkMediator
import com.google.android.horologist.networks.request.HighBandwidthRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeveloperOptionsScreenViewModel
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
        private val snackbarManager: SnackbarManager,
        private val highBandwidthNetworkMediator: HighBandwidthNetworkMediator,
        @IsEmulator private val isEmulator: Boolean,
    ) : ViewModel() {
        private val networkRequest = MutableStateFlow<HighBandwidthConnectionLease?>(null)

        val uiState: StateFlow<UiState> =
            combine(settingsRepository.settingsFlow, networkRequest) { it, networkRequest ->
                UiState(
                    showTimeTextInfo = it.showTimeTextInfo,
                    podcastControls = it.podcastControls,
                    loadItemsAtStartup = it.loadItemsAtStartup,
                    offloadMode = it.offloadMode,
                    animated = it.animated,
                    debugOffload = it.debugOffload,
                    writable = true,
                    networkRequest = networkRequest,
                    streamingMode = it.streamingMode,
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UiState(writable = false),
            )

        data class UiState(
            val showTimeTextInfo: Boolean = false,
            val podcastControls: Boolean = false,
            val loadItemsAtStartup: Boolean = false,
            val animated: Boolean = true,
            val debugOffload: Boolean = false,
            val offloadMode: OffloadMode = OffloadMode.BACKGROUND,
            val writable: Boolean = false,
            val networkRequest: HighBandwidthConnectionLease? = null,
            val streamingMode: Boolean = false,
        )

        fun setShowTimeTextInfo(enabled: Boolean) {
            viewModelScope.launch {
                settingsRepository.edit {
                    it.copy { showTimeTextInfo = enabled }
                }
            }
        }

        fun setPodcastControls(enabled: Boolean) {
            viewModelScope.launch {
                settingsRepository.edit {
                    it.copy { podcastControls = enabled }
                }
            }
        }

        fun setLoadItemsAtStartup(enabled: Boolean) {
            viewModelScope.launch {
                settingsRepository.edit {
                    it.copy { loadItemsAtStartup = enabled }
                }
            }
        }

        fun setAnimated(enabled: Boolean) {
            viewModelScope.launch {
                settingsRepository.edit {
                    it.copy { animated = enabled }
                }
            }
        }

        fun setDebugOffload(enabled: Boolean) {
            viewModelScope.launch {
                settingsRepository.edit {
                    it.copy { debugOffload = enabled }
                }
            }
        }

        fun setOffloadMode(mode: OffloadMode) {
            viewModelScope.launch {
                settingsRepository.edit {
                    it.copy { offloadMode = mode }
                }
            }
        }

        fun setStreamingMode(mode: Boolean) {
            viewModelScope.launch {
                settingsRepository.edit {
                    it.copy { streamingMode = mode }
                }
            }
        }

        fun showDialog(message: String) {
            snackbarManager.showMessage(
                UiMessage(
                    message = message,
                    error = true,
                ),
            )
        }

        fun toggleNetworkRequest() {
            networkRequest.update {
                if (it != null) {
                    it.close()
                    null
                } else {
                    val type = if (isEmulator) HighBandwidthRequest.Cell else HighBandwidthRequest.All
                    highBandwidthNetworkMediator.requestHighBandwidthNetwork(type)
                }
            }
        }

        override fun onCleared() {
            networkRequest.value?.close()
        }

        fun forceStop() {
            Process.killProcess(Process.myPid())
        }
    }
