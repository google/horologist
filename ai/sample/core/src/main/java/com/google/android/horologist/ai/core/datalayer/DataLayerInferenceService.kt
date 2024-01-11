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

package com.google.android.horologist.ai.core.datalayer

import com.google.android.gms.wearable.Node
import com.google.android.horologist.ai.core.InferenceServiceGrpcKt
import com.google.android.horologist.ai.core.PromptRequest
import com.google.android.horologist.ai.core.Response
import com.google.android.horologist.ai.core.ServiceInfo
import com.google.android.horologist.ai.core.copy
import com.google.android.horologist.data.TargetNodeId
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.datalayer.grpc.GrpcExtensions.grpcClient
import com.google.protobuf.Empty
import kotlinx.coroutines.CoroutineScope

class DataLayerInferenceService(
    dataLayerRegistry: WearDataLayerRegistry,
    val node: Node,
    coroutineScope: CoroutineScope,
) : InferenceServiceGrpcKt.InferenceServiceCoroutineImplBase() {
    val proxy = dataLayerRegistry.grpcClient(
        TargetNodeId.SpecificNodeId(node.id),
        coroutineScope = coroutineScope,
    ) {
        InferenceServiceGrpcKt.InferenceServiceCoroutineStub(it)
    }

    override suspend fun serviceInfo(request: Empty): ServiceInfo {
        return proxy.serviceInfo(request).copy {
            name = "$name@${node.displayName}"
        }
    }

    override suspend fun answerPrompt(request: PromptRequest): Response {
        return proxy.answerPrompt(request)
    }
}
