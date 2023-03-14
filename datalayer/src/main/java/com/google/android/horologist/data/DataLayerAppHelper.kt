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

package com.google.android.horologist.data

import android.content.Context
import android.net.Uri
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

internal const val TAG = "DataLayerAppHelper"

/**
 * Base class on which of the Wear and Phone DataLayerAppHelpers are build.
 *
 * Provides utility functions for determining installation status and means to install and launch
 * apps as part of the user's journey.
 */
abstract class DataLayerAppHelper(protected val context: Context) {
    private val installedDeviceCapabilityUri = "wear://*/$CAPABILITY_DEVICE_PREFIX"

    // The installation of individual Tiles or complications are tracked via Local Capabilities with
    // these prefixes.
    protected val tilePrefix = "${DATA_LAYER_APP_HELPER_CAPABILITY}_tile_"
    protected val complicationPrefix = "${DATA_LAYER_APP_HELPER_CAPABILITY}_complication_"
    protected val playStoreUri = "market://details?id=${context.packageName}"

    protected val capabilityClient by lazy { Wearable.getCapabilityClient(context) }
    protected val messageClient by lazy { Wearable.getMessageClient(context) }
    protected val nodeClient by lazy { Wearable.getNodeClient(context) }
    protected val remoteActivityHelper by lazy { RemoteActivityHelper(context) }

    /**
     * Provides a list of connected nodes and the installation status of the app on these nodes.
     */
    public suspend fun connectedNodes(): List<AppHelperNodeStatus> {
        val connectedNodes = nodeClient.connectedNodes.await()
        val nearbyNodes = connectedNodes.filter { it.isNearby }
        val capabilities =
            capabilityClient.getAllCapabilities(CapabilityClient.FILTER_REACHABLE).await()
        val nodesToTiles = mapNodesToSurface(tilePrefix, capabilities)
        val nodesToComplications = mapNodesToSurface(complicationPrefix, capabilities)
        val installedPhoneNodes = capabilities[PHONE_CAPABILITY]?.nodes?.map { it.id } ?: setOf()
        val installedWatchNodes = capabilities[WATCH_CAPABILITY]?.nodes?.map { it.id } ?: setOf()
        val allInstalledNodes = installedPhoneNodes + installedWatchNodes

        return nearbyNodes.map {
            AppHelperNodeStatus(
                id = it.id,
                displayName = it.displayName,
                isAppInstalled = allInstalledNodes.contains(it.id),
                installedTiles = nodesToTiles[it.id] ?: setOf(),
                installedComplications = nodesToComplications[it.id] ?: setOf(),
                nodeType = when (it.id) {
                    in installedPhoneNodes -> AppHelperNodeType.PHONE
                    in installedWatchNodes -> AppHelperNodeType.WATCH
                    else -> AppHelperNodeType.UNKNOWN
                }
            )
        }
    }

    /**
     * Creates a flow to keep the client updated with the set of connected devices with the app
     * installed.
     */
    public val connectedAndInstalledNodes = callbackFlow<Set<Node>> {
        val listener = object : CapabilityClient.OnCapabilityChangedListener {
            override fun onCapabilityChanged(capability: CapabilityInfo) {
                trySend(capability.nodes.filter { it.isNearby }.toSet())
            }
        }
        val info =
            capabilityClient.getCapability(
                installedDeviceCapabilityUri,
                CapabilityClient.FILTER_PREFIX
            )
                .await()
        trySend(info.nodes.filter { it.isNearby }.toSet())
        capabilityClient.addListener(
            listener,
            Uri.parse(installedDeviceCapabilityUri),
            CapabilityClient.FILTER_PREFIX
        )
        awaitClose {
            capabilityClient.removeListener(listener)
        }
    }

    /**
     * Launches to the appropriate store on the specified node to allow installation of the app.
     *
     * @param node The node to launch on.
     */

    abstract suspend fun installOnNode(node: String)

    /**
     * Starts the companion that relates to the specified node. This will start on the phone,
     * irrespective of whether the specified node is a phone or a watch.
     *
     * @param node The node to launch on.
     * @return Whether launch was successful or not.
     */
    abstract suspend fun startCompanion(node: String): AppHelperResultCode

    /**
     * Launch an activity on the specified node.
     */
    public suspend fun startRemoteActivity(
        node: String,
        config: ActivityConfig
    ): AppHelperResultCode {
        val request = launchRequest { activity = config }
        val response = messageClient.sendRequest(node, LAUNCH_APP, request.toByteArray()).await()
        return AppHelperResult.parseFrom(response).code
    }

    /**
     * Launch own app on the specified node.
     */
    public suspend fun startRemoteOwnApp(node: String): AppHelperResultCode {
        val request = launchRequest { ownApp = ownAppConfig { } }
        val response = messageClient.sendRequest(node, LAUNCH_APP, request.toByteArray()).await()
        return AppHelperResult.parseFrom(response).code
    }

    /**
     * Creates a lookup to determine which devices have which a given surface installed on them.
     */
    private fun mapNodesToSurface(
        surfacePrefix: String,
        capabilities: Map<String, CapabilityInfo>
    ): Map<String, Set<String>> {
        val idToSurfaceSet = mutableMapOf<String, Set<String>>()
        capabilities
            .entries.filter { it.key.startsWith(surfacePrefix) }
            .forEach { entry ->
                val name = entry.key.removePrefix(surfacePrefix)
                for (node in entry.value.nodes) {
                    val previousValue = idToSurfaceSet[node.id]
                    if (previousValue == null) {
                        idToSurfaceSet[node.id] = setOf(name)
                    } else {
                        idToSurfaceSet[node.id] = previousValue + name
                    }
                }
            }
        return idToSurfaceSet
    }

    public companion object {
        public const val DATA_LAYER_APP_HELPER_CAPABILITY: String = "data_layer_app_helper"
        public const val CAPABILITY_DEVICE_PREFIX = "${DATA_LAYER_APP_HELPER_CAPABILITY}_device"
        public const val PHONE_CAPABILITY = "${CAPABILITY_DEVICE_PREFIX}_phone"
        public const val WATCH_CAPABILITY = "${CAPABILITY_DEVICE_PREFIX}_watch"
        public const val LAUNCH_APP: String = "/launch_app"
    }
}

fun byteArrayForResultCode(resultCode: AppHelperResultCode): ByteArray {
    val response = appHelperResult {
        code = resultCode
    }
    return response.toByteArray()
}
