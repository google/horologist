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

package com.google.android.horologist.ai.core.registry

import com.google.android.horologist.ai.core.InferenceServiceGrpcKt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class CombinedInferenceServiceRegistry(
    val registries: List<InferenceServiceRegistry>,
) : InferenceServiceRegistry {
    override fun models(): Flow<List<InferenceServiceGrpcKt.InferenceServiceCoroutineImplBase>> {
        return registries.asFlow()
            .flatMapConcat { registry -> registry.models() }
            .toList()
            .asFlow()
    }
}
