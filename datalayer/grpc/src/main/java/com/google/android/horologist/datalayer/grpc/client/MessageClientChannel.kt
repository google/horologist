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

import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.data.TargetNodeId
import com.google.android.horologist.data.WearDataLayerRegistry
import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.MethodDescriptor
import kotlinx.coroutines.CoroutineScope

public class MessageClientChannel(
    internal val nodeId: TargetNodeId,
    internal val path: String,
    private val wearDataLayerRegistry: WearDataLayerRegistry,
    private val coroutineScope: CoroutineScope
) : Channel() {
    override fun <RequestT, ResponseT> newCall(
        methodDescriptor: MethodDescriptor<RequestT, ResponseT>,
        callOptions: CallOptions
    ): ClientCall<RequestT, ResponseT> {
        return MessageClientCall(this, methodDescriptor, coroutineScope, wearDataLayerRegistry)
    }

    // TODO something better than this for the authority
    override fun authority(): String = nodeId.toString()
}
