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

package com.google.android.horologist.ai.sample.wear.prompt.di

import com.google.android.horologist.ai.sample.wear.gemini.service.GeminiSDKInferenceServiceImpl
import com.google.android.horologist.ai.sample.wear.geminilib.BuildConfig
import com.google.genai.Client
import com.google.genai.types.ClientOptions
import com.google.genai.types.HttpOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {
    @ServiceScoped
    @Provides
    fun client() = Client.builder()
        .apiKey(BuildConfig.GEMINI_API_KEY)
        .clientOptions(
            ClientOptions.builder()
                .build(),
        )
        .httpOptions(
            HttpOptions.builder()
                .build(),
        )
        .build()

    @ServiceScoped
    @Provides
    fun geminiSDKService(
        client: Client,
    ) = GeminiSDKInferenceServiceImpl(client)
}
