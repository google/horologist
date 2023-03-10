/*
 * Copyright 2022 The Android Open Source Project
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

package com.google.android.horologist.data

import com.google.android.gms.wearable.CapabilityClient
import kotlinx.coroutines.tasks.await

/**
 * A selector for a Node to connect with over the data client without needing to resolve ahead of
 * time. The implementations could be anything from a hardcoded value to querying the CapabilityClient.
 */
public interface TargetNodeId {
    /**
     * Return the node id for the given strategy.
     */
    public suspend fun evaluate(dataLayerRegistry: WearDataLayerRegistry): String?

    /**
     * A reference to the Node for this device.
     */
    public object ThisNodeId : TargetNodeId {
        override suspend fun evaluate(dataLayerRegistry: WearDataLayerRegistry): String {
            return dataLayerRegistry.nodeClient.localNode.await().id
        }
    }

    /**
     * A reference to the single paired node for the connected phone.
     * All wear devices must have a single connected device, although it may not be available.
     */
    public object PairedPhone : TargetNodeId {
        override suspend fun evaluate(dataLayerRegistry: WearDataLayerRegistry): String? {
            val capabilitySearch = dataLayerRegistry.capabilityClient.getCapability(
                HOROLOGIST_PHONE,
                CapabilityClient.FILTER_ALL
            ).await()

            return capabilitySearch.nodes.singleOrNull()?.id
        }
    }

    /**
     * A reference to a specific node id, via prior configuration.
     */
    public class SpecificNodeId(
        public val nodeId: String
    ) : TargetNodeId {
        override suspend fun evaluate(dataLayerRegistry: WearDataLayerRegistry): String {
            return nodeId
        }
    }

    companion object {
        const val HOROLOGIST_PHONE = "horologist_phone"
        const val HOROLOGIST_WATCH = "horologist_watch"
    }
}
