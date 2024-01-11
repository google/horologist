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

package com.google.android.horologist.ai.sample.wear.prompt.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.ai.core.InferenceService
import com.google.android.horologist.ai.core.modelId
import com.google.android.horologist.ai.ui.model.ModelInstanceUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val inferenceService: InferenceService,
    ) : ViewModel() {
        val uiState =
            combine(
                inferenceService.connectedModel,
                inferenceService.models,
            ) { current, models ->
                val uiModels = models?.flatMap { (serviceInfo, _) ->
                    serviceInfo.modelsList.map { modelInfo ->
                        ModelInstanceUiModel(modelInfo.id.id, modelInfo.name).also {
                            if (it.id.isBlank()) {
                                throw Exception("Blank id " + modelInfo.name + " ")
                            }
                        }
                    }
                }
                SettingsUiState(
                    uiModels?.find { it.id == current?.id },
                    uiModels,
                )
            }
                .stateIn(viewModelScope, SharingStarted.Eagerly, SettingsUiState(null, null))

        fun selectModel(model: ModelInstanceUiModel) {
            inferenceService.selectModel(
                modelId {
                    id = model.id
                },
            )
        }
    }

data class SettingsUiState(
    val current: ModelInstanceUiModel?,
    val models: List<ModelInstanceUiModel>?,
)
