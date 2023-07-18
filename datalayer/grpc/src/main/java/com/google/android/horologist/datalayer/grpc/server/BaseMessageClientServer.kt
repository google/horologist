package com.google.android.horologist.datalayer.grpc.server

import com.google.android.horologist.datalayer.grpc.proto.DataLayerGrpc.MessageRequest
import com.google.android.horologist.datalayer.grpc.proto.messageResponse
import com.google.protobuf.GeneratedMessageLite
import com.google.protobuf.any
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

abstract class BaseMessageClientServer(
    val coroutineScope: CoroutineScope
) {
    fun handleIncomingMessage(data: ByteArray): Deferred<ByteArray> {
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

    abstract suspend fun execute(request: MessageRequest): GeneratedMessageLite<*, *>
}