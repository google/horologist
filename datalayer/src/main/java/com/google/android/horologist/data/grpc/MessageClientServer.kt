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

import android.net.Uri
import com.google.android.gms.wearable.MessageClient
import com.google.android.horologist.data.MessageRequest
import com.google.protobuf.Any
import io.grpc.Metadata
import io.grpc.ServerMethodDefinition
import io.grpc.ServerServiceDefinition
import java.io.ByteArrayInputStream

class MessageClientServer(
    val definition: ServerServiceDefinition,
    val messageClient: MessageClient,
    val path: String
) {
    fun handleIncomingMessage(data: ByteArray, sourceNodeId: String) {
        println("incoming MessageRequest with ${data.size} bytes")
        val request = MessageRequest.parseFrom(data)
        val method = definition.getMethod(request.method)
        val payload: Any = request.request
        startCall(method, sourceNodeId, payload)
    }

    private fun <RequestT, ResponseT> startCall(
        method: ServerMethodDefinition<RequestT, ResponseT>,
        sourceNodeId: String,
        payload: Any
    ) {
        val callHandler = method.serverCallHandler
        val call = MessageClientServerCall(method, this, sourceNodeId)
        val listener = callHandler.startCall(call, Metadata())

        listener.onReady()

        val request = method.methodDescriptor.parseRequest(ByteArrayInputStream(payload.toByteArray()))

        println("Processing request $request")

        listener.onMessage(request)
    }

    fun start() {
        val uri = Uri.parse("wear://*$path")
        messageClient.addListener(
            { handleIncomingMessage(it.data, it.sourceNodeId) },
            uri,
            MessageClient.FILTER_LITERAL
        )
    }
}