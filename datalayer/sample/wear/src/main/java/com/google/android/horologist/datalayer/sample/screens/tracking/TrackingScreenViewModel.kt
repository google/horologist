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

package com.google.android.horologist.datalayer.sample.screens.tracking

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.wear.watchface.complications.data.ComplicationType
import com.google.android.horologist.data.UsageStatus
import com.google.android.horologist.datalayer.watch.WearDataLayerAppHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackingScreenViewModel
    @Inject
    constructor(
        private val wearDataLayerAppHelper: WearDataLayerAppHelper,
    ) : ViewModel() {

        private val fakeTileList = listOf("Tile1", "Tile2")
        private val fakeComplicationList = listOf("Comp1", "Comp2")

        private var initializeCalled = false

        private val _uiState = MutableStateFlow<TrackingScreenUiState>(TrackingScreenUiState.Idle)
        public val uiState: StateFlow<TrackingScreenUiState> = _uiState

        @MainThread
        fun initialize() {
            if (initializeCalled) return
            initializeCalled = true

            _uiState.value = TrackingScreenUiState.Loading

            viewModelScope.launch {
                wearDataLayerAppHelper.surfacesInfo.collectLatest { surfacesInfo ->
                    when (_uiState.value) {
                        TrackingScreenUiState.Loading,
                        is TrackingScreenUiState.Loaded,
                        -> {
                            val tilesMap = fakeTileList.associateWith { tile ->
                                surfacesInfo.tilesList.any { it.name == tile }
                            }

                            val complicationsMap = fakeComplicationList.associateWith { complication ->
                                surfacesInfo.complicationsList.any { it.name == complication }
                            }

                            _uiState.value = TrackingScreenUiState.Loaded(
                                activityLaunchedOnce = surfacesInfo.activityLaunched.activityLaunchedOnce,
                                setupCompleted = surfacesInfo.usageInfo.usageStatus == UsageStatus.USAGE_STATUS_SETUP_COMPLETE,
                                tilesInstalled = tilesMap,
                                complicationsInstalled = complicationsMap,
                            )
                        }

                        else -> {
                            /* noop */
                        }
                    }
                }
            }
        }

        fun onActivityLaunchedOnceCheckedChanged(checked: Boolean) {
            if (checked) {
                viewModelScope.launch {
                    wearDataLayerAppHelper.markActivityLaunchedOnce()
                }
            }
        }

        fun onSetupCompletedCheckedChanged(checked: Boolean) {
            viewModelScope.launch {
                if (checked) {
                    wearDataLayerAppHelper.markSetupComplete()
                } else {
                    wearDataLayerAppHelper.markSetupNoLongerComplete()
                }
            }
        }

        fun onTileCheckedChanged(tile: String, checked: Boolean) {
            viewModelScope.launch {
                if (checked) {
                    wearDataLayerAppHelper.markTileAsInstalled(tile)
                } else {
                    wearDataLayerAppHelper.markTileAsRemoved(tile)
                }
            }
        }

        fun onComplicationCheckedChanged(complication: String, checked: Boolean) {
            viewModelScope.launch {
                if (checked) {
                    wearDataLayerAppHelper.markComplicationAsActivated(
                        complicationName = complication,
                        complicationInstanceId = 1234,
                        complicationType = ComplicationType.SHORT_TEXT,
                    )
                } else {
                    wearDataLayerAppHelper.markComplicationAsDeactivated(
                        complicationInstanceId = 1234,
                    )
                }
            }
        }
    }

sealed class TrackingScreenUiState {
    data object Idle : TrackingScreenUiState()
    data object Loading : TrackingScreenUiState()
    data class Loaded(
        val activityLaunchedOnce: Boolean,
        val setupCompleted: Boolean,
        val tilesInstalled: Map<String, Boolean>,
        val complicationsInstalled: Map<String, Boolean>,
    ) : TrackingScreenUiState()
}
