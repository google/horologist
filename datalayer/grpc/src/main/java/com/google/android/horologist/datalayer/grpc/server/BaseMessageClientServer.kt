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

package com.google.android.horologist.datalayer.grpc.server

import com.google.android.horologist.datalayer.grpc.proto.DataLayerGrpc.MessageRequest
import com.google.android.horologist.datalayer.grpc.proto.messageResponse
import com.google.protobuf.GeneratedMessageLite
import com.google.protobuf.any
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

public abstract class BaseMessageClientServer(
    private val coroutineScope: CoroutineScope
) {
    public fun handleIncomingMessage(data: ByteArray): Deferred<ByteArray> {
        val request = MessageRequest.parseFrom(data)

        return coroutineScope.async {
            val result = execute(request)
            messageResponse {
                response = any {
                    this.value = result.toByteString()
                }
            }
                .toByteArray()
        }
    }

    public abstract suspend fun execute(request: MessageRequest): GeneratedMessageLite<*, *>
}
