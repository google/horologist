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

package com.google.android.horologist.data.grpc

import com.google.android.horologist.data.CommandServiceGrpcKt
import com.google.android.horologist.data.XRequest
import com.google.android.horologist.data.XResponse
import com.google.android.horologist.data.xResponse
import com.google.protobuf.Empty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CommandServiceImpl: CommandServiceGrpcKt.CommandServiceCoroutineImplBase() {
    override suspend fun sendCommand(request: Empty): Empty {
        println("sendCommand")
        return Empty.getDefaultInstance()
    }

    override suspend fun sendCommandX(request: XRequest): XResponse {
        println("sendCommandX")
        return xResponse { x = request.x }
    }

    override fun streamCommandX(requests: Flow<XRequest>): Flow<XResponse> {
        println("streamCommandX")
        return requests.map { xResponse { x = it.x } }
    }
}