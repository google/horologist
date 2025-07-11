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

package com.google.android.horologist.ai.sample.wear.gemini.service

import android.util.Log
import com.google.android.horologist.ai.core.InferenceServiceGrpcKt
import com.google.android.horologist.ai.core.PromptRequest
import com.google.android.horologist.ai.core.Response
import com.google.android.horologist.ai.core.ServiceInfo
import com.google.android.horologist.ai.core.failure
import com.google.android.horologist.ai.core.modelId
import com.google.android.horologist.ai.core.modelInfo
import com.google.android.horologist.ai.core.response
import com.google.android.horologist.ai.core.serviceInfo
import com.google.android.horologist.ai.core.textResponse
import com.google.genai.Client
import com.google.genai.types.GenerateContentConfig
import com.google.protobuf.Empty

class GeminiSDKInferenceServiceImpl(
    val client: Client,
    val serviceName: String = "Gemini API",
    val configuredModels: List<GeminiModel> = GeminiModel.All,
) :
    InferenceServiceGrpcKt.InferenceServiceCoroutineImplBase() {
        override suspend fun answerPrompt(request: PromptRequest): Response {
            if (request.prompt.hasTextPrompt()) {
                val query = request.prompt.textPrompt.text!!
                return geminiQuery(query, request.modelId.id)
            } else {
                return response {
                    failure = failure {
                        message = "Unhandled request type $request"
                    }
                }
            }
        }

        private suspend fun geminiQuery(query: String, modelId: String): Response {
            // TODO handle stream and multiple parts

            val answer = try {
                client.models.generateContentStream(
                    modelId,
                    query,
                    GenerateContentConfig.builder()
                        .build(),
                ).first()
            } catch (e: Exception) {
                Log.w("Gemini", "Gemini query failed", e)
                return response {
                    failure = failure {
                        message = "Gemini query failed: $e"
                    }
                }
            }

            if (answer.text() != null) {
                return response {
                    textResponse = textResponse {
                        text = answer.text()!!
                    }
                }
            }

            return response {
                failure = failure {
                    message = "Gemini query failed: No text content"
                }
            }
        }

        override suspend fun serviceInfo(request: Empty): ServiceInfo {
            return serviceInfo {
                name = serviceName
                models += configuredModels.map {
                    modelInfo {
                        modelId = modelId { id = it.name }
                        name = it.displayName
                    }
                }
            }
        }
    }
