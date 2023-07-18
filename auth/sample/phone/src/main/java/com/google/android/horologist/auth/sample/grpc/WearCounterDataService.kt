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

package com.google.android.horologist.auth.sample.grpc

import androidx.datastore.core.DataStore
import androidx.lifecycle.lifecycleScope
import com.google.android.horologist.auth.sample.shared.datalayer.CounterValueSerializer
import com.google.android.horologist.auth.sample.shared.grpc.CounterServiceGrpcKt
import com.google.android.horologist.auth.sample.shared.grpc.GrpcDemoProto.CounterValue
import com.google.android.horologist.data.ProtoDataStoreHelper.protoDataStore
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.datalayer.grpc.server.BaseGrpcDataService

class WearCounterDataService : BaseGrpcDataService<CounterServiceGrpcKt.CounterServiceCoroutineImplBase>() {

    private val dataStore: DataStore<CounterValue> by lazy {
        registry.protoDataStore<CounterValue>(lifecycleScope)
    }

    override val registry: WearDataLayerRegistry by lazy {
        WearDataLayerRegistry.fromContext(
            application = applicationContext,
            coroutineScope = lifecycleScope
        ).apply {
            registerSerializer(CounterValueSerializer)
        }
    }

    override fun buildService(): CounterServiceGrpcKt.CounterServiceCoroutineImplBase {
        return CounterService(dataStore)
    }
}
