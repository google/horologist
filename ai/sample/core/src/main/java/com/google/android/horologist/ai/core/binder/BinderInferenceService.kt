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

import com.google.android.horologist.ai.core.InferenceServiceGrpcKt
import com.google.android.horologist.ai.core.PromptRequest
import com.google.android.horologist.ai.core.Response
import com.google.android.horologist.ai.core.ResponseBundle
import com.google.android.horologist.ai.core.ServiceInfo
import com.google.protobuf.Empty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class BinderInferenceService(
    val stub: InferenceServiceGrpcKt.InferenceServiceCoroutineStub,
) : InferenceServiceGrpcKt.InferenceServiceCoroutineImplBase() {
    override suspend fun serviceInfo(request: Empty): ServiceInfo {
        return try {
            stub.serviceInfo(request)
        } catch (e: Exception) {
            println("Failing instance at ${stub.channel}")
            e.printStackTrace()
            com.google.android.horologist.ai.core.serviceInfo {
                name = "Failed"
            }
        }
    }

    override suspend fun answerPrompt(request: PromptRequest): ResponseBundle {
        return stub.answerPrompt(request)
    }

    override fun answerPromptWithStream(request: PromptRequest): Flow<Response> {
        return flow {
            emitAll(answerPrompt(request).responsesList.asFlow())
        }
    }
}
