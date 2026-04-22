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

package com.google.android.horologist.ai.sample.wear.gemini.di

import com.google.android.horologist.ai.sample.wear.gemini.activity.ExposedMethods
import com.google.android.horologist.ai.sample.wear.gemini.service.GeminiModel
import com.google.android.horologist.ai.sample.wear.gemini.service.GeminiSDKInferenceServiceImpl
import com.google.android.horologist.ai.sample.wear.geminilib.BuildConfig.GEMINI_API_KEY
import com.google.genai.Client
import com.google.genai.types.ClientOptions
import com.google.genai.types.FunctionCallingConfig
import com.google.genai.types.FunctionCallingConfigMode
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.HttpOptions
import com.google.genai.types.LatLng
import com.google.genai.types.MediaResolution
import com.google.genai.types.RetrievalConfig
import com.google.genai.types.Tool
import com.google.genai.types.ToolConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {
    @Singleton
    @Provides
    fun client() = Client.builder()
        .apiKey(GEMINI_API_KEY)
        .clientOptions(
            ClientOptions.builder()
                .build(),
        )
        .httpOptions(
            HttpOptions.builder()
                .build(),
        )
        .build()

    @Singleton
    @Provides
    fun geminiSDKService(
        client: Client,
        contentConfig: GenerateContentConfig,
    ) = GeminiSDKInferenceServiceImpl(
        client,
        serviceName = "Device Info",
        configuredModels = listOf(
            GeminiModel.Gemini2dot5Flash,
        ),
        contentConfig = GenerateContentConfig.builder()
            .mediaResolution(MediaResolution.Known.MEDIA_RESOLUTION_LOW)
            .build(),
    )

    @Singleton
    @Provides
    fun generateContentConfig(): GenerateContentConfig = GenerateContentConfig.builder()
        .toolConfig(
            ToolConfig.builder()
                .retrievalConfig(
                    RetrievalConfig.builder()
                        .latLng(
                            LatLng.builder().latitude(51.5332609).longitude(-0.1285781)
                                .build(),
                        )
                        .languageCode("en_GB")
                        .build(),
                )
                .functionCallingConfig(
                    FunctionCallingConfig.builder()
                        .mode(FunctionCallingConfigMode.Known.AUTO)
                        .build(),
                )
                .build(),
        )
        .tools(
            Tool.builder()
//                        googleMaps(GoogleMaps.builder()
//                            .authConfig(AuthConfig.builder()
//                                .build())
//                            .build())
//                    .functionDeclarations(FunctionDeclaration.builder()
//                        .build())
                .functions(
                    ExposedMethods::class.java.getMethod("deviceModel"),
                    ExposedMethods::class.java.getMethod("deviceManufacturer"),
                )
//                    .googleSearchRetrieval(
//                        GoogleSearchRetrieval.builder()
//                            .dynamicRetrievalConfig(
//                                DynamicRetrievalConfig.builder()
//                                    .mode(DynamicRetrievalConfigMode.Known.MODE_DYNAMIC)
//                                    .build()
//                            )
//                            .build()
//                    )
//                    .googleSearch(
//                        GoogleSearch.builder()
//                            .build()
//                    )
                .build(),
        )
        .build()
}
