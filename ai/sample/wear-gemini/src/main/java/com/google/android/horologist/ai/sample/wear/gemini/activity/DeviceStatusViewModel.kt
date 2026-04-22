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

package com.google.android.horologist.ai.sample.wear.gemini.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.ai.sample.wear.gemini.service.GeminiModel
import com.google.genai.Client
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.GenerateImagesConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DeviceStatusViewModel
    @Inject
    constructor(
        client: Client,
        val contentConfig: GenerateContentConfig,
    ) : ViewModel() {
        val uiState = flow {
            val model: String = ExposedMethods.deviceModel()
            val manufacturer: String = ExposedMethods.deviceManufacturer()

            val imageGen = withContext(Dispatchers.IO) {
                async {
                    val images = client.models.generateImages(
                        GeminiModel.Imagen4.name,
                        "Generate an image for this android device $manufacturer $model",
                        GenerateImagesConfig.builder()
                            .numberOfImages(1)
                            .build(),
                    )
                    images.generatedImages().get().first().image().get().imageBytes().get()
                }
            }

            val descriptionGen = withContext(Dispatchers.IO) {
                async {
                    client.models.generateContent(
                        GeminiModel.Gemini2dot5Flash.name,
                        "Make a poem about the device and its manufacturer",
                        contentConfig,
                    ).parts()?.get(0)?.text()?.get()
                }
            }

            emit(Loaded(imageGen.await(), descriptionGen.await()))
        }.stateIn(viewModelScope, SharingStarted.Lazily, Loading)
    }
