@file:OptIn(ExperimentalHorologistApi::class)

package com.google.android.horologist.datalayer.grpc.client

import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.data.TargetNodeId
import com.google.android.horologist.data.WearDataLayerRegistry
import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.MethodDescriptor
import kotlinx.coroutines.CoroutineScope

class MessageClientChannel(
    val nodeId: TargetNodeId,
    val path: String,
    val wearDataLayerRegistry: WearDataLayerRegistry,
    val coroutineScope: CoroutineScope
): Channel() {
    override fun <RequestT, ResponseT> newCall(
        methodDescriptor: MethodDescriptor<RequestT, ResponseT>,
        callOptions: CallOptions
    ): ClientCall<RequestT, ResponseT> {
        return MessageClientCall(this, methodDescriptor, coroutineScope, wearDataLayerRegistry)
    }

    // TODO something better than this for the authority
    override fun authority(): String = nodeId.toString()
}