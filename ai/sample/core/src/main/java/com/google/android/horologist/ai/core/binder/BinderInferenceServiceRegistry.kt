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

package com.google.android.horologist.ai.core.binder

import android.content.Context
import android.content.Intent
import com.google.android.horologist.ai.core.AiGrpcClientLookup
import com.google.android.horologist.ai.core.InferenceServiceGrpcKt
import com.google.android.horologist.ai.core.registry.InferenceServiceRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BinderInferenceServiceRegistry(
    val coroutineScope: CoroutineScope,
    val context: Context,
) : InferenceServiceRegistry {
    override fun models(): Flow<List<InferenceServiceGrpcKt.InferenceServiceCoroutineImplBase>> {
        return flow {
            val intent = Intent("InferenceService")
            val services = context.packageManager.queryIntentServices(intent, 0)

            emit(
                services.map {
                    BinderInferenceService(
                        AiGrpcClientLookup.lookupInferenceService(
                            context,
                            it.serviceInfo.packageName,
                        ),
                    )
                },
            )
        }
    }

    override val priority: Int
        get() = 1
}
