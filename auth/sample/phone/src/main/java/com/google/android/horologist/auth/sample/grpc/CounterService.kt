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
import com.google.android.horologist.auth.sample.shared.grpc.CounterServiceGrpcKt
import com.google.android.horologist.auth.sample.shared.grpc.GrpcDemoProto
import com.google.android.horologist.auth.sample.shared.grpc.copy
import com.google.android.horologist.auth.sample.toProtoTimestamp
import com.google.protobuf.Empty
import kotlinx.coroutines.flow.first

class CounterService(val dataStore: DataStore<GrpcDemoProto.CounterValue>) :
    CounterServiceGrpcKt.CounterServiceCoroutineImplBase() {
    override suspend fun getCounter(request: Empty): GrpcDemoProto.CounterValue {
        return dataStore.data.first()
    }

    override suspend fun increment(request: GrpcDemoProto.CounterDelta): GrpcDemoProto.CounterValue {
        return dataStore.updateData {
            it.copy {
                this.value = this.value + request.delta
                this.updated = System.currentTimeMillis().toProtoTimestamp()
            }
        }
    }
}
