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

package com.google.android.horologist.ai.sample.phone.di

import android.content.Context
import com.google.android.horologist.ai.core.AiGrpcClientLookup.lookupInferenceService
import com.google.android.horologist.ai.core.InferenceServiceGrpcKt
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.ViewModelLifecycle
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import io.grpc.ManagedChannel

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    @ViewModelScoped
    @Provides
    fun selfService(
        @ApplicationContext context: Context,
        lifecycle: ViewModelLifecycle,
    ): InferenceServiceGrpcKt.InferenceServiceCoroutineStub {
        return lookupInferenceService(
            context,
            context.packageName,
        ).apply {
            lifecycle.addOnClearedListener {
                (channel as? ManagedChannel?)?.shutdownNow()
            }
        }
    }
}
