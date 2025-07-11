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

import android.content.Context
import com.google.android.horologist.ai.core.binder.BinderInferenceServiceRegistry
import com.google.android.horologist.ai.core.datalayer.DataLayerInferenceServiceRegistry
import com.google.android.horologist.ai.core.dummy.DummyInferenceServiceImpl
import com.google.android.horologist.ai.core.registry.CombinedInferenceServiceRegistry
import com.google.android.horologist.ai.core.registry.LocalInferenceServiceRegistry
import com.google.android.horologist.data.WearDataLayerRegistry
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InferenceServicesModule {

    @Provides
    @Singleton
    fun binderRegistry(
        @ApplicationContext context: Context,
        coroutineScope: CoroutineScope,
    ): BinderInferenceServiceRegistry {
        return BinderInferenceServiceRegistry(coroutineScope, context)
    }

    @Provides
    @Singleton
    fun dataLayerRegistry(
        dataLayerRegistry: WearDataLayerRegistry,
        coroutineScope: CoroutineScope,
    ): DataLayerInferenceServiceRegistry {
        return DataLayerInferenceServiceRegistry(dataLayerRegistry, coroutineScope)
    }

    @Provides
    @Singleton
    fun localRegistry(): LocalInferenceServiceRegistry {
        return LocalInferenceServiceRegistry(
            listOf(DummyInferenceServiceImpl("dummy-local")),
            priority = Int.MIN_VALUE,
        )
    }

    @Provides
    @Singleton
    fun combinedRegistry(
        binder: BinderInferenceServiceRegistry,
        local: LocalInferenceServiceRegistry,
        dataLayer: DataLayerInferenceServiceRegistry,
    ): CombinedInferenceServiceRegistry {
        return CombinedInferenceServiceRegistry(listOf(local, binder, dataLayer))
    }
}
