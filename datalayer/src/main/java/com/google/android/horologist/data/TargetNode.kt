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

import kotlinx.coroutines.tasks.await

/**
 * A selector for a Node to connect with over the data client without needing to resolve ahead of
 * time. The implementations could be anything from hardcoded value to querying the
 */
public interface TargetNode {
    /**
     * Return the node id for the given strategy.
     */
    public suspend fun evaluate(dataLayerRegistry: WearDataLayerRegistry): String?

    /**
     * A reference to the Node for this device.
     */
    public object ThisNode : TargetNode {
        override suspend fun evaluate(dataLayerRegistry: WearDataLayerRegistry): String {
            return dataLayerRegistry.nodeClient.localNode.await().id
        }
    }

    /**
     * A reference to the single paired node for the connected phone.
     * All wear devices must have a single connected device, although it may not be available.
     */
    public object PairedPhone : TargetNode {
        override suspend fun evaluate(dataLayerRegistry: WearDataLayerRegistry): String? {
            return dataLayerRegistry.nodeClient.connectedNodes.await()?.firstOrNull()?.id
        }
    }

    /**
     * A reference to a specific node id, via prior configuration.
     */
    public class SpecificNode(
        public val nodeId: String
    ) : TargetNode {
        override suspend fun evaluate(dataLayerRegistry: WearDataLayerRegistry): String {
            return nodeId
        }
    }
}
