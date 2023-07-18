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

class MessageClientServer(
    service: BindableService,
    coroutineScope: CoroutineScope
) : BaseMessageClientServer(coroutineScope) {
    val boundService = service.bindService()

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

class MessageServerCall<ReqT, ResT>(
    val _methodDescriptor: MethodDescriptor<ReqT, ResT>
) : ServerCall<ReqT, ResT>() {
    val channel: Channel<ResT> = Channel(capacity = 1)

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