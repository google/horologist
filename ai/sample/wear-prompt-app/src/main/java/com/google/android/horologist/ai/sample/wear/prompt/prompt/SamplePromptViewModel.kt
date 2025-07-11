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

package com.google.android.horologist.ai.sample.wear.prompt.prompt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.ai.core.InferenceService
import com.google.android.horologist.ai.core.prompt
import com.google.android.horologist.ai.core.textPrompt
import com.google.android.horologist.ai.ui.model.FailedResponseUiModel
import com.google.android.horologist.ai.ui.model.ModelInstanceUiModel
import com.google.android.horologist.ai.ui.model.PromptOrResponseUiModel
import com.google.android.horologist.ai.ui.model.TextPromptUiModel
import com.google.android.horologist.ai.ui.model.TextResponseUiModel
import com.google.android.horologist.ai.ui.screens.PromptUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SamplePromptViewModel
    @Inject
    constructor(
        private val inferenceService: InferenceService,
    ) : ViewModel() {

        init {
            viewModelScope.launch {
                val current = inferenceService.connectedModel.first()
                if (current == null) {
                    val defaultModel = inferenceService.currentKnownModels().firstOrNull()
                    if (defaultModel != null) {
                        inferenceService.selectModel(defaultModel)
                    }
                }
            }
        }

        private val previousQuestions: MutableStateFlow<List<PromptOrResponseUiModel>> =
            MutableStateFlow(listOf())
        private val pendingQuestion: MutableStateFlow<TextPromptUiModel?> =
            MutableStateFlow(null)

        fun askQuestion(enteredPrompt: String) {
            val textPromptUiModel = TextPromptUiModel(enteredPrompt)
            pendingQuestion.value = textPromptUiModel

            viewModelScope.launch {
                val responseUi = queryForPrompt(enteredPrompt)

                previousQuestions.update {
                    it + listOf(textPromptUiModel, responseUi)
                }

                pendingQuestion.value = null
            }
        }

        private suspend fun queryForPrompt(
            enteredPrompt: String,
        ): PromptOrResponseUiModel {
            return try {
                val response = inferenceService.submit(
                    prompt {
                        textPrompt = textPrompt { text = enteredPrompt }
                    },
                )

                when {
                    response.hasTextResponse() -> TextResponseUiModel(response.textResponse.text)
                    response.hasFailure() -> FailedResponseUiModel(response.failure.message)
                    else -> FailedResponseUiModel("Unhandled response type ${response.responseDataCase}")
                }
            } catch (e: Exception) {
                FailedResponseUiModel(e.toString())
            }
        }

        val uiState: StateFlow<PromptUiState> =
            combine(
                previousQuestions,
                pendingQuestion,
                inferenceService.currentModelInfo,
            ) { prev, curr, info ->
                val modelInfo = info?.first?.let { ModelInstanceUiModel(it.modelId.id, it.name) }
                PromptUiState(modelInfo, prev, curr)
            }.stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = PromptUiState(messages = previousQuestions.value),
            )
    }
