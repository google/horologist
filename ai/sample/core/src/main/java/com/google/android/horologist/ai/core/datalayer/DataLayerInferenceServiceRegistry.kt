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

import com.google.android.gms.wearable.CapabilityClient
import com.google.android.horologist.ai.core.InferenceServiceGrpcKt
import com.google.android.horologist.ai.core.registry.InferenceServiceRegistry
import com.google.android.horologist.data.WearDataLayerRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class DataLayerInferenceServiceRegistry(
    val dataLayerRegistry: WearDataLayerRegistry,
    val coroutineScope: CoroutineScope,
) : InferenceServiceRegistry {
    override fun models(): Flow<List<InferenceServiceGrpcKt.InferenceServiceCoroutineImplBase>> {
        return flow {
            val allCapabilities = dataLayerRegistry.capabilityClient.getAllCapabilities(CapabilityClient.FILTER_ALL).await()
            println("Capabilties")
            allCapabilities.forEach { (key, list) ->
                println(key)
                list.nodes.forEach {
                    println(it.id + " " + it.displayName + " " + it.isNearby)
                }
            }
            println("End")

            val capabilities = dataLayerRegistry.capabilityClient.getCapability(
                CAPABILITY_INFERENCE_SERVICE,
                CapabilityClient.FILTER_REACHABLE,
            ).await()

            emit(
                capabilities.nodes.map { node ->
                    DataLayerInferenceService(dataLayerRegistry, node, coroutineScope)
                },
            )
        }
    }

    override val priority: Int
        get() = 2

    companion object {
        val CAPABILITY_INFERENCE_SERVICE = "InferenceService"
    }
}
