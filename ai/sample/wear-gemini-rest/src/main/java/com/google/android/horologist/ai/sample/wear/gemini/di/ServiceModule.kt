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

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.generationConfig
import com.google.android.horologist.ai.sample.wear.gemini.BuildConfig
import com.google.android.horologist.ai.sample.wear.gemini.service.GeminiSDKInferenceServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Qualifier

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {
    @ServiceScoped
    @Provides
    fun modelConfig(): GenerationConfig = generationConfig {
        temperature = 0.7f
    }

    @ServiceScoped
    @Provides
    @ChatModel
    fun textModel(
        config: GenerationConfig,
    ) = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = config,
    )

    @ServiceScoped
    @Provides
    @MultiModelModel
    fun multiModelModel(
        config: GenerationConfig,
    ) = GenerativeModel(
        modelName = "gemini-pro-vision",
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = config,
    )

    @ServiceScoped
    @Provides
    fun geminiSDKService(
        @ChatModel model: GenerativeModel,
    ) = GeminiSDKInferenceServiceImpl(model)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ChatModel

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MultiModelModel
