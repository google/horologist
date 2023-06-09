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

import com.google.android.horologist.data.MessageRequest
import com.google.android.horologist.data.XRequest
import com.google.android.horologist.data.messageRequest
import com.google.protobuf.Any
import com.google.protobuf.AnyProto
import com.google.protobuf.any
import com.google.protobuf.kotlin.toByteString
import io.grpc.ClientCall
import io.grpc.Metadata
import io.grpc.MethodDescriptor

class MessageClientCall<ReqT, RespT>(
    val channel: MessageClientChannel,
    val methodDescriptor: MethodDescriptor<ReqT, RespT>
): ClientCall<ReqT, RespT>() {
    override fun start(responseListener: Listener<RespT>, headers: Metadata) {
        responseListener.onReady()
        println("Client.start")
    }

    override fun request(numMessages: Int) {
        println("Client.request")
    }

    override fun cancel(message: String?, cause: Throwable?) {
        println("Client.cancel")
    }

    override fun halfClose() {
        println("Client.halfClose")
    }

    override fun sendMessage(message: ReqT) {
        println("Client.sendMessage")
        val data = methodDescriptor.streamRequest(message).use {
            it.readBytes()
        }
        val request = messageRequest {
            this.method = methodDescriptor.fullMethodName
            this.request = any {
                this.value = data.toByteString()
            }
        }
        val realData = request.toByteArray()
        channel.messageClient.sendMessage(channel.nodeId, channel.path, realData)
    }
}