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

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.google.android.horologist.ai.sample.wear.gemini.service

import android.util.Log
import com.google.android.horologist.ai.core.InferenceServiceGrpcKt
import com.google.android.horologist.ai.core.PromptRequest
import com.google.android.horologist.ai.core.Response
import com.google.android.horologist.ai.core.ResponseBundle
import com.google.android.horologist.ai.core.ServiceInfo
import com.google.android.horologist.ai.core.failure
import com.google.android.horologist.ai.core.imageResponse
import com.google.android.horologist.ai.core.modelId
import com.google.android.horologist.ai.core.modelInfo
import com.google.android.horologist.ai.core.response
import com.google.android.horologist.ai.core.responseBundle
import com.google.android.horologist.ai.core.serviceInfo
import com.google.android.horologist.ai.core.textResponse
import com.google.genai.Client
import com.google.genai.types.Content
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.GenerateImagesConfig
import com.google.genai.types.MediaResolution
import com.google.genai.types.Part
import com.google.protobuf.ByteString
import com.google.protobuf.Empty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import kotlin.jvm.optionals.getOrNull

class GeminiSDKInferenceServiceImpl(
    val client: Client,
    val serviceName: String = "Gemini API",
    val configuredModels: List<GeminiModel> = GeminiModel.All,
    val contentConfig: GenerateContentConfig = GenerateContentConfig.builder()
        .mediaResolution(MediaResolution.Known.MEDIA_RESOLUTION_LOW)
        .build(),
    val imagesConfig: GenerateImagesConfig = GenerateImagesConfig.builder()
        // Vertex only
//                .outputGcsUri(BuildConfig.GCS_URI)
        .build(),
) :
    InferenceServiceGrpcKt.InferenceServiceCoroutineImplBase() {
        override suspend fun answerPrompt(request: PromptRequest): ResponseBundle {
            return responseBundle {
                responses += answerPromptWithStream(request).toList()
            }
        }

        override fun answerPromptWithStream(request: PromptRequest): Flow<Response> {
            val model = configuredModels.first { it.name == request.modelId.id }

            return if (model.isImagesOnly) {
                geminiGenerateImages(request, model.name)
            } else {
                geminiGenerateContentStream(request, model.name)
            }.catch {
                emit(
                    response {
                        failure = failure {
                            message = "Gemini query failed: $it"
                        }
                    },
                )
            }
        }

        private fun geminiGenerateImages(request: PromptRequest, modelId: String): Flow<Response> =
            flow {
                val responses = client.models.generateImages(
                    modelId,
                    request.toTextPrompt(),
                    imagesConfig,
                )

                emitAll(
                    responses.generatedImages().getOrNull().orEmpty().asFlow().mapNotNull {
                        val image = it.image().getOrNull() ?: return@mapNotNull null
                        response {
                            imageResponse = imageResponse {
                                if (image.gcsUri().isPresent) {
                                    gcsUrl = image.gcsUri().get()
                                } else {
                                    encoded = ByteString.copyFrom(image.imageBytes().get())
                                }
                            }
                        }
                    },
                )
            }

        private fun geminiGenerateContentStream(
            request: PromptRequest,
            modelId: String,
        ): Flow<Response> {
            // TODO handle stream and multiple parts

            val responseStream = try {
                client.models.generateContentStream(
                    modelId,
                    request.toContent(),
                    contentConfig,
                )
            } catch (e: Exception) {
                Log.w("Gemini", "Gemini query failed", e)
                return flowOf(
                    response {
                        failure = failure {
                            message = "Gemini query failed: $e"
                        }
                    },
                )
            }

            return flow {
                with(Dispatchers.IO) {
                    this@flow.emitAll(
                        responseStream.iterator().asFlow().flatMapConcat { generateContentResponse ->
                            generateContentResponse.parts().orEmpty().asFlow().map {
                                response {
                                    Log.i("Gemini", it.toString())
                                    textResponse = textResponse {
                                        text = it.text().orElse("--")
                                    }
                                }
                            }
                        },
                    )
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

        private fun PromptRequest.toTextPrompt(): String {
            if (prompt.hasTextPrompt()) {
                return prompt.textPrompt.text!!
            } else {
                throw IllegalArgumentException("Prompt must be a text prompt")
            }
        }

        private fun PromptRequest.toContent(): Content {
            return Content.builder().parts(Part.fromText(toTextPrompt())).build()
        }
    }
