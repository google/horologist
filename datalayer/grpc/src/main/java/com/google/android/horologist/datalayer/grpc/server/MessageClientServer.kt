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
import com.google.protobuf.GeneratedMessageLite
import io.grpc.BindableService
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerMethodDefinition
import io.grpc.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import java.io.ByteArrayInputStream

public class MessageClientServer(
    service: BindableService,
    coroutineScope: CoroutineScope
) : BaseMessageClientServer(coroutineScope) {
    private val boundService = service.bindService()

    override suspend fun execute(request: MessageRequest): GeneratedMessageLite<*, *> {
        val method = boundService.getMethod(request.method)

        return executeTyped(method, request) as GeneratedMessageLite<*, *>
    }

    private suspend fun <ReqT, ResT> executeTyped(
        method: ServerMethodDefinition<ReqT, ResT>,
        request: MessageRequest
    ): ResT {
        val serverCallHandler: ServerCallHandler<ReqT, ResT> = method.serverCallHandler
        val call = MessageServerCall<ReqT, ResT>(method.methodDescriptor)

        val listener = serverCallHandler.startCall(call, Metadata())

        val realRequest =
            method.methodDescriptor.parseRequest(ByteArrayInputStream(request.request.value.toByteArray()))

        listener.onReady()
        listener.onMessage(realRequest)
        listener.onHalfClose()

        return call.channel.receive().also {
            listener.onComplete()
        }
    }
}

internal class MessageServerCall<ReqT, ResT>(
    private val _methodDescriptor: MethodDescriptor<ReqT, ResT>
) : ServerCall<ReqT, ResT>() {
    internal val channel: Channel<ResT> = Channel(capacity = 1)

    override fun request(numMessages: Int) {
    }

    override fun sendHeaders(headers: Metadata?) {
    }

    override fun close(status: Status?, trailers: Metadata?) {
        channel.close()
    }

    override fun isCancelled(): Boolean {
        return false
    }

    override fun getMethodDescriptor(): MethodDescriptor<ReqT, ResT> {
        return _methodDescriptor
    }

    override fun sendMessage(message: ResT) {
        channel.trySend(message)
    }
}
