/*
 * Copyright 2024 The Android Open Source Project
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

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.google.android.horologist.ai.core

import com.google.android.horologist.ai.core.registry.CombinedInferenceServiceRegistry
import com.google.protobuf.empty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InferenceService
    @Inject
    constructor(
        val registry: CombinedInferenceServiceRegistry,
        val coroutineScope: CoroutineScope,
    ) {
        val connectedModel = MutableStateFlow<ModelId?>(null)

        val models = flow {
            val models = registry.models().first()
            emit(
                coroutineScope {
                    // TODO subscribe
                    models.map { remote ->
                        async {
                            try {
                                val serviceInfo = remote.serviceInfo(empty { })
                                Pair(serviceInfo, remote)
                            } catch (e: Exception) {
                                println("Failing for $remote")
                                e.printStackTrace()
                                null
                            }
                        }
                    }
                }.awaitAll().filterNotNull(),
            )
        }.stateIn(coroutineScope, SharingStarted.Eagerly, null)

        val currentModelInfo: Flow<Pair<ModelInfo, ServiceInfo>?> =
            combine(connectedModel, models) { currentId, currentModels ->
                currentModels?.firstNotNullOfOrNull { (serviceInfo, _) ->
                    serviceInfo.modelsList.find {
                        it.id == currentId
                    }?.let {
                        Pair(it, serviceInfo)
                    }
                }
            }

        suspend fun submit(prompt: Prompt): Response {
            val currentModel = connectedModel.value ?: throw Exception("No model selected")

            val (_, service) = models.value?.first { it.first.modelsList.find { it.id == currentModel } != null }
                ?: throw Exception("Service missing")

            return service.answerPrompt(
                promptRequest {
                    this.prompt = prompt
                    this.model = currentModel
                },
            )
        }

        fun selectModel(modelId: ModelId?) {
            connectedModel.value = modelId
        }
    }
