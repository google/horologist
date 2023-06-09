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

import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.ServerCall
import io.grpc.ServerMethodDefinition
import io.grpc.Status
import okio.Buffer

class MessageClientServerCall<ReqT, RespT>(
    val method: ServerMethodDefinition<ReqT, RespT>,
    val server: MessageClientServer,
    val nodeId: String
) : ServerCall<ReqT, RespT>() {
    override fun request(numMessages: Int) {
        TODO("Not yet implemented")
    }

    override fun sendHeaders(headers: Metadata?) {
        TODO("Not yet implemented")
    }

    override fun close(status: Status?, trailers: Metadata?) {
        TODO("Not yet implemented")
    }

    override fun isCancelled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getMethodDescriptor(): MethodDescriptor<ReqT, RespT> {
        return method.methodDescriptor
    }

    override fun sendMessage(message: RespT) {
        val data = Buffer().apply {
            method.methodDescriptor.streamResponse(message).use {
                readFrom(it)
            }
        }

        server.messageClient.sendMessage(nodeId, "", data.readByteArray())
    }
}