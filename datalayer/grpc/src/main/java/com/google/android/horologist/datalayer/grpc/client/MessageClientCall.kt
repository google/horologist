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

@file:OptIn(ExperimentalHorologistApi::class)

package com.google.android.horologist.datalayer.grpc.client

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.WearableStatusCodes
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.datalayer.grpc.proto.DataLayerGrpc.MessageResponse
import com.google.android.horologist.datalayer.grpc.proto.messageRequest
import com.google.protobuf.any
import com.google.protobuf.kotlin.toByteString
import io.grpc.ClientCall
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.MethodDescriptor.MethodType
import io.grpc.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayInputStream

public class MessageClientCall<ReqT, RespT>(
    private val channel: MessageClientChannel,
    private val methodDescriptor: MethodDescriptor<ReqT, RespT>,
    private val coroutineScope: CoroutineScope,
    private val wearDataLayerRegistry: WearDataLayerRegistry
) : ClientCall<ReqT, RespT>() {
    private lateinit var responseListener: Listener<RespT>

    private val messageClient: MessageClient = wearDataLayerRegistry.messageClient

    init {
        check(methodDescriptor.type == MethodType.UNARY)
    }

    override fun start(responseListener: Listener<RespT>, headers: Metadata) {
        this.responseListener = responseListener
        responseListener.onReady()
    }

    override fun request(numMessages: Int) {
    }

    override fun cancel(message: String?, cause: Throwable?) {
    }

    override fun halfClose() {
    }

    override fun sendMessage(message: ReqT) {
        val realData = requestToBytes(message)
        coroutineScope.launch {
            val realNodeId = channel.nodeId.evaluate(wearDataLayerRegistry)

            if (realNodeId == null) {
                responseListener.onClose(Status.UNAVAILABLE, Metadata())
            } else {
                try {
                    // TODO move out of coroutineScope after we early resolve realNodeId
                    val requestTask = messageClient.sendRequest(realNodeId, channel.path, realData)

                    val responseBytes = requestTask.await()

                    val response = bytesToResponse(responseBytes)

                    responseListener.onMessage(response)
                    responseListener.onClose(Status.OK, Metadata())
                } catch (apie: ApiException) {
                    handleException(apie)
                }
            }
        }
    }

    private fun requestToBytes(message: ReqT): ByteArray? {
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
        return realData
    }

    private fun bytesToResponse(responseBytes: ByteArray?): RespT {
        val wrappedResponse = MessageResponse.parseFrom(responseBytes)

        return methodDescriptor.parseResponse(ByteArrayInputStream(wrappedResponse.response.value.toByteArray()))
    }

    private fun handleException(apie: ApiException) {
        if (apie.statusCode == WearableStatusCodes.TIMEOUT) {
            responseListener.onClose(Status.DEADLINE_EXCEEDED, Metadata())
        } else {
            responseListener.onClose(Status.UNKNOWN, Metadata())
        }
    }
}
