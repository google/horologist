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

package com.google.android.horologist.rubberband

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.wear.phone.interactions.PhoneTypeHelper
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityClient.OnCapabilityChangedListener
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableStatusCodes
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.tasks.await

public const val TAG: String = "Rubberband"

/**
 * Provides basic functionality for promoting use of Phone and Wear apps together, encouraging
 * detection of presence and means to install and launch on either.
 *
 * Is not intended to provide complex interactions between apps on both devices, for example,
 * ongoing communication between both apps or current running status.
 *
 * @param context Context
 * @param appStoreUri The path to your app on the App Store, if you have an iOS equivalent. See
 *     https://developer.apple.com/library/archive/qa/qa1633/_index.html for details.
 */
public class Rubberband(private val context: Context, private val appStoreUri: String? = null) {
    // The installation of individual Tiles is tracked via Local Capabilities with this prefix.
    private val tilePrefix = "rubberband_tile_"
    private val installedCapabilityUri = "wear://*/$RUBBERBAND_INSTALLED"

    private val playStoreUri = "market://details?id=${context.packageName}"

    private val isWatch = context.packageManager.hasSystemFeature(PackageManager.FEATURE_WATCH)

    private val capabilityClient by lazy { Wearable.getCapabilityClient(context) }
    private val messageClient by lazy { Wearable.getMessageClient(context) }
    private val nodeClient by lazy { Wearable.getNodeClient(context) }
    private val remoteActivityHelper by lazy { RemoteActivityHelper(context) }

    /**
     * Some devices report back a different packageName from getCompanionPackageForNode() than is
     * the actual package of the Companion app. Where this is the case, this lookup ensures the
     * correct companion app can be launched. (pursuing whether this is a bug or not).
     */
    private val companionLookup = mapOf(
        "com.samsung.android.waterplugin" to "com.samsung.android.app.watchmanager",
        "com.samsung.android.heartplugin" to "com.samsung.android.app.watchmanager"
    )

    /**
     * Provides a list of connected nodes and the installation status of the app on these nodes.
     */
    public suspend fun connectedNodes(): List<NodeStatus> {
        val connectedNodes = nodeClient.connectedNodes.await()
        val nearbyNodes = connectedNodes.filter { it.isNearby }
        val capabilities =
            capabilityClient.getAllCapabilities(CapabilityClient.FILTER_REACHABLE).await()
        val nodesToTiles = mapNodesToTiles(capabilities)
        val installedNodes = capabilities[RUBBERBAND_INSTALLED]?.nodes?.map { it.id } ?: setOf()

        return nearbyNodes.map {
            NodeStatus(
                id = it.id,
                displayName = it.displayName,
                isAppInstalled = installedNodes.contains(it.id),
                installedTiles = nodesToTiles[it.id] ?: setOf()
            )
        }
    }

    /**
     * Creates a flow to keep the client updated with the set of connected devices with the app
     * installed.
     */
    public val connectedAndInstalledNodes = callbackFlow<Set<Node>> {
        val listener = object : OnCapabilityChangedListener {
            override fun onCapabilityChanged(capability: CapabilityInfo) {
                trySend(capability.nodes.filter { it.isNearby }.toSet())
            }
        }
        val info =
            capabilityClient.getCapability(installedCapabilityUri, CapabilityClient.FILTER_LITERAL)
                .await()
        trySend(info.nodes.filter { it.isNearby }.toSet())
        capabilityClient.addListener(
            listener,
            Uri.parse(installedCapabilityUri),
            CapabilityClient.FILTER_LITERAL
        )
        awaitClose {
            capabilityClient.removeListener(listener)
        }
    }

    /**
     * Marks a tile as installed. Call this in [TileService#onTileAddEvent]. Supplying a name is
     * mandatory to disambiguate from the installation or removal of other tiles your app may have.
     *
     * @param tileName The name of the tile.
     */
    public suspend fun markTileAsInstalled(tileName: String) {
        require(tileName.isNotEmpty())
        try {
            capabilityClient.addLocalCapability("$tilePrefix$tileName").await()
        } catch (e: ApiException) {
            if (e.statusCode != WearableStatusCodes.DUPLICATE_CAPABILITY) {
                throw e
            }
        }
    }

    /**
     * Marks a tile as removed. Call this in [TileService#onTileRemoveEvent]. Supplying a name is
     * mandatory to disambiguate from the installation or removal of other tiles your app may have.
     *
     * @param tileName The name of the tile.
     */
    public suspend fun markTileAsRemoved(tileName: String) {
        require(tileName.isNotEmpty())
        capabilityClient.removeLocalCapability("$tilePrefix$tileName").await()
    }

    /**
     * Launches to the appropriate store on the specified node to allow installation of the app.
     *
     * @param node The node to launch on.
     */
    public suspend fun installOnNode(node: String) {
        if (!isWatch ||
            PhoneTypeHelper.getPhoneDeviceType(context) == PhoneTypeHelper.DEVICE_TYPE_ANDROID
        ) {
            val intent = Intent(Intent.ACTION_VIEW)
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .setData(Uri.parse(playStoreUri))
            remoteActivityHelper.startRemoteActivity(intent, node).await()
        } else if (appStoreUri != null &&
            PhoneTypeHelper.getPhoneDeviceType(context) == PhoneTypeHelper.DEVICE_TYPE_IOS
        ) {
            val intent = Intent(Intent.ACTION_VIEW)
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .setData(Uri.parse(appStoreUri))
            remoteActivityHelper.startRemoteActivity(intent, node).await()
        }
    }

    /**
     * Starts the companion that relates to the specified node. This will start on the phone,
     * irrespective of whether the specified node is a phone or a watch.
     *
     * @param node The node to launch on.
     * @return Whether launch was successful or not.
     */
    public suspend fun startCompanion(node: String): RubberbandResult {
        if (isWatch) {
            val localNode = nodeClient.localNode.await()
            val response =
                messageClient.sendRequest(node, LAUNCH_COMPANION_APP, localNode.id.toByteArray())
                    .await()
            return rubberBandResultFromByteArray(response)
        } else {
            val companionPackage = nodeClient.getCompanionPackageForNode(node).await()

            /**
             * Some devices report the wrong companion for actually launching the Companion app: For
             * example, Samsung devices report the plugin packages that handle comms with GW4, GW5
             * etc, whereas the package name for the companion *app* is different.
             */
            val launchPackage = companionLookup.getOrDefault(companionPackage, companionPackage)

            val intent = context.packageManager.getLaunchIntentForPackage(launchPackage)
                ?: return RubberbandResult.ERROR_NO_COMPANION_FOUND
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                return RubberbandResult.ERROR_ACTIVITY_NOT_FOUND
            }
        }
        return RubberbandResult.SUCCESS
    }

    /**
     * Launch an activity on the specified node.
     *
     * @param node The node to launch on.
     * @param componentPath The path of the component to launch, in long form. e.g.
     *     com.example.myapp/com.example.myapp.MyActivity
     *     This parameter is optional. If omitted, the default launch activity for the current
     *     package will be launched on the specified node.
     * @return Whether launch was successful or not.
     */
    public suspend fun startRemoteApp(node: String, componentPath: String = ""): RubberbandResult {
        val response =
            messageClient.sendRequest(node, LAUNCH_REMOTE_APP, componentPath.toByteArray()).await()
        return rubberBandResultFromByteArray(response)
    }

    /**
     * Creates a lookup to easily determine which devices have which Tiles installed on them.
     */
    private fun mapNodesToTiles(capabilities: Map<String, CapabilityInfo>): Map<String, Set<String>> {
        val idToTileSet = mutableMapOf<String, Set<String>>()
        capabilities
            .entries.filter { it.key.startsWith(tilePrefix) }
            .forEach { entry ->
                val tileName = entry.key.removePrefix(tilePrefix)
                entry.value.nodes.forEach { node ->
                    idToTileSet.merge(node.id, setOf(tileName)) { s1, s2 -> s1 + s2 }
                }
            }
        return idToTileSet
    }

    public companion object {
        public const val RUBBERBAND_INSTALLED: String = "rubberband"
        public const val LAUNCH_REMOTE_APP: String = "/launch_remote_app"
        public const val LAUNCH_COMPANION_APP: String = "/launch_companion_app"
    }
}
