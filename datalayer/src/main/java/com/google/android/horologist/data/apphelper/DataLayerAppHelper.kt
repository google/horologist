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

package com.google.android.horologist.data.apphelper

import android.content.Context
import android.net.Uri
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.Node
import com.google.android.horologist.data.ActivityConfig
import com.google.android.horologist.data.AppHelperResult
import com.google.android.horologist.data.AppHelperResultCode
import com.google.android.horologist.data.TargetNodeId
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.data.appHelperResult
import com.google.android.horologist.data.launchRequest
import com.google.android.horologist.data.ownAppConfig
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

internal const val TAG = "DataLayerAppHelper"

/**
 * Base class on which of the Wear and Phone DataLayerAppHelpers are build.
 *
 * Provides utility functions for determining installation status and means to install and launch
 * apps as part of the user's journey.
 */
abstract class DataLayerAppHelper(
    protected val context: Context,
    protected val registry: WearDataLayerRegistry
) {
    private val installedDeviceCapabilityUri = "wear://*/$CAPABILITY_DEVICE_PREFIX"
    protected val playStoreUri = "market://details?id=${context.packageName}"
    protected val remoteActivityHelper by lazy { RemoteActivityHelper(context) }

    /**
     * Provides a list of connected nodes and the installation status of the app on these nodes.
     */
    public suspend fun connectedNodes(): List<AppHelperNodeStatus> {
        val connectedNodes = registry.nodeClient.connectedNodes.await()
        val nearbyNodes = connectedNodes.filter { it.isNearby }
        val capabilities =
            registry.capabilityClient.getAllCapabilities(CapabilityClient.FILTER_REACHABLE).await()

        val installedPhoneNodes = capabilities[PHONE_CAPABILITY]?.nodes?.map { it.id } ?: setOf()
        val installedWatchNodes = capabilities[WATCH_CAPABILITY]?.nodes?.map { it.id } ?: setOf()
        val allInstalledNodes = installedPhoneNodes + installedWatchNodes

        return nearbyNodes.map {
            AppHelperNodeStatus(
                id = it.id,
                displayName = it.displayName,
                isAppInstalled = allInstalledNodes.contains(it.id),
                surfacesInfo = getSurfaceStatus(it.id),
                nodeType = when (it.id) {
                    in installedPhoneNodes -> AppHelperNodeType.PHONE
                    in installedWatchNodes -> AppHelperNodeType.WATCH
                    else -> AppHelperNodeType.UNKNOWN
                }
            )
        }
    }

    private suspend fun getSurfaceStatus(nodeId: String) = registry.protoFlow(
        targetNodeId = TargetNodeId.SpecificNodeId(nodeId),
        serializer = SurfaceInfoSerializer,
        path = SURFACE_INFO_PATH
    ).first()

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

        val allCaps =
            registry.capabilityClient.getAllCapabilities(
                CapabilityClient.FILTER_REACHABLE
            ).await()
        val installedCaps = allCaps.filter { it.key.startsWith(CAPABILITY_DEVICE_PREFIX) }
            .values.flatMap { it.nodes }.filter { it.isNearby }.toSet()

        trySend(installedCaps)
        registry.capabilityClient.addListener(
            listener,
            Uri.parse(installedDeviceCapabilityUri),
            CapabilityClient.FILTER_PREFIX
        )
        awaitClose {
            registry.capabilityClient.removeListener(listener)
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
        val response =
            registry.messageClient.sendRequest(node, LAUNCH_APP, request.toByteArray()).await()
        return AppHelperResult.parseFrom(response).code
    }

    /**
     * Launch own app on the specified node.
     */
    public suspend fun startRemoteOwnApp(node: String): AppHelperResultCode {
        val request = launchRequest { ownApp = ownAppConfig { } }
        val response =
            registry.messageClient.sendRequest(node, LAUNCH_APP, request.toByteArray()).await()
        return AppHelperResult.parseFrom(response).code
    }

    public companion object {
        public const val DATA_LAYER_APP_HELPER_CAPABILITY: String = "data_layer_app_helper"
        public const val CAPABILITY_DEVICE_PREFIX = "${DATA_LAYER_APP_HELPER_CAPABILITY}_device"
        public const val PHONE_CAPABILITY = "${CAPABILITY_DEVICE_PREFIX}_phone"
        public const val WATCH_CAPABILITY = "${CAPABILITY_DEVICE_PREFIX}_watch"
        public const val LAUNCH_APP: String = "/launch_app"
        public const val SURFACE_INFO_PATH: String = "/surface_info"
    }
}

fun byteArrayForResultCode(resultCode: AppHelperResultCode): ByteArray {
    val response = appHelperResult {
        code = resultCode
    }
    return response.toByteArray()
}
