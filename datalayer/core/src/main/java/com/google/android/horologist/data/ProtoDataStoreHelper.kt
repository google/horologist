/*
 * Copyright 2022 The Android Open Source Project
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

package com.google.android.horologist.data

import com.google.android.horologist.data.store.ProtoDataListener
import kotlinx.coroutines.CoroutineScope

/**
 * Helper methods for accessing the DataStore for the Data Layer, using reflection to
 * lookup the serializer.
 */
object ProtoDataStoreHelper {
    inline fun <reified T : Any> WearDataLayerRegistry.protoFlow(node: TargetNodeId) = this.protoFlow(
        node,
        serializers.serializerForType<T>(),
        WearDataLayerRegistry.dataStorePath(T::class)
    )

    inline fun <reified T : Any> WearDataLayerRegistry.protoDataStore(coroutineScope: CoroutineScope) =
        this.protoDataStore(
            WearDataLayerRegistry.dataStorePath(T::class),
            coroutineScope,
            serializers.serializerForType<T>()
        )

    inline fun <reified T : Any> WearDataLayerRegistry.registerProtoDataListener(listener: ProtoDataListener<T>) {
        this.registerProtoDataListener(
            WearDataLayerRegistry.dataStorePath(T::class),
            listener
        )
    }
}
