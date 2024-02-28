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

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
import android.content.Context
import android.os.Process
import androidx.annotation.CheckResult
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Node
import com.google.android.horologist.data.ActivityConfig
import com.google.android.horologist.data.AppHelperResult
import com.google.android.horologist.data.AppHelperResultCode
import com.google.android.horologist.data.AppHelperResultCode.APP_HELPER_RESULT_TEMPORARILY_UNAVAILABLE
import com.google.android.horologist.data.TargetNodeId
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.data.WearableApiAvailability
import com.google.android.horologist.data.appHelperResult
import com.google.android.horologist.data.launchRequest
import com.google.android.horologist.data.ownAppConfig
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

internal const val TAG = "DataLayerAppHelper"

/**
 * Base class on which of the Wear and Phone DataLayerAppHelpers are build.
 *
 * Provides utility functions for determining installation status and means to install and launch
 * apps as part of the user's journey.
 */
abstract class DataLayerAppHelper(
    protected val context: Context,
    protected val registry: WearDataLayerRegistry,
) {
    private val activityManager: ActivityManager by lazy { context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager }

    protected val playStoreUri: String = "market://details?id=${context.packageName}"
    protected val remoteActivityHelper: RemoteActivityHelper by lazy { RemoteActivityHelper(context) }

    /**
     * Provides a list of connected nodes and the installation status of the app on these nodes.
     */
    public suspend fun connectedNodes(): List<AppHelperNodeStatus> {
        val connectedNodes = registry.nodeClient.connectedNodes.await()
        val capabilities =
            registry.capabilityClient.getAllCapabilities(CapabilityClient.FILTER_REACHABLE).await()

        val installedPhoneNodes = capabilities[PHONE_CAPABILITY]?.nodes?.map { it.id } ?: setOf()
        val installedWatchNodes = capabilities[WATCH_CAPABILITY]?.nodes?.map { it.id } ?: setOf()
        val allInstalledNodes = installedPhoneNodes + installedWatchNodes

        return connectedNodes.map {
            val appInstallationStatus = if (allInstalledNodes.contains(it.id)) {
                val nodeType = when (it.id) {
                    in installedPhoneNodes -> AppInstallationStatusNodeType.PHONE
                    else -> AppInstallationStatusNodeType.WATCH
                }
                AppInstallationStatus.Installed(nodeType = nodeType)
            } else {
                AppInstallationStatus.NotInstalled
            }

            AppHelperNodeStatus(
                id = it.id,
                displayName = it.displayName,
                isNearby = it.isNearby,
                appInstallationStatus = appInstallationStatus,
                surfacesInfo = getSurfaceStatus(it.id),
            )
        }
    }

    private suspend fun getSurfaceStatus(nodeId: String) = registry.protoFlow(
        targetNodeId = TargetNodeId.SpecificNodeId(nodeId),
        serializer = SurfacesInfoSerializer,
        path = SURFACE_INFO_PATH,
    ).first()

    /**
     * Creates a flow to keep the client updated with the set of connected devices with the app
     * installed.
     *
     * When called from a phone device, multiple watches can be connected to it.
     *
     * When called from a watch device, usually only a single phone device will be connected to it.
     */
    public abstract val connectedAndInstalledNodes: Flow<Set<Node>>

    protected fun connectedAndInstalledNodes(capability: String) = callbackFlow<Set<Node>> {
        suspend fun sendNearbyNodes() {
            val capabilityInfo = registry.capabilityClient.getCapability(
                capability,
                CapabilityClient.FILTER_REACHABLE,
            ).await()

            @Suppress("UNUSED_VARIABLE")
            val unused = trySend(capabilityInfo.nodes.toSet())
        }

        suspend fun listenAndSendChanges() {
            val listener: CapabilityClient.OnCapabilityChangedListener =
                CapabilityClient.OnCapabilityChangedListener { capabilityInfo ->
                    @Suppress("UNUSED_VARIABLE")
                    val unused = trySend(capabilityInfo.nodes.toSet())
                }

            registry.capabilityClient.addListener(listener, capability)
            awaitClose {
                registry.capabilityClient.removeListener(listener)
            }
        }

        sendNearbyNodes()

        listenAndSendChanges()
    }

    /**
     * Launches to the appropriate store on the specified node to allow installation of the app.
     *
     * @param nodeId The node to launch on.
     *
     * @return [AppHelperResultCode] with code for either success or error. In case of error
     * [APP_HELPER_RESULT_TEMPORARILY_UNAVAILABLE], users should be educated to bring the device to
     * proximity, as per docs of [RemoteActivityHelper.availabilityStatus].
     * @throws [IllegalArgumentException] when uri for the app store is not provided and this
     * function is called for a node that is iOS.
     */
    abstract suspend fun installOnNode(nodeId: String): AppHelperResultCode

    /**
     * Starts the companion that relates to the specified node. This will start on the phone,
     * irrespective of whether the specified node is a phone or a watch.
     *
     * When called from a watch node, it is required that the same app is installed on the specified
     * node, otherwise a [timeout](AppHelperResultCode.APP_HELPER_RESULT_TIMEOUT) is expected.
     * See [AppHelperNodeStatus.appInstallationStatus] in order to check the installation status.
     *
     * @param nodeId The node to launch on.
     * @return Whether launch was successful or not.
     */
    @CheckResult
    abstract suspend fun startCompanion(nodeId: String): AppHelperResultCode

    /**
     * Launch an activity, which belongs to the same app (same package name), on the specified node.
     *
     * [Class name][ActivityConfig.getClassFullName] should be a fully qualified class name, such
     * as, "com.example.project.SampleActivity".
     *
     * This call requires that the same app is installed on the specified node, otherwise a
     * [timeout](AppHelperResultCode.APP_HELPER_RESULT_TIMEOUT) is expected.
     * See [AppHelperNodeStatus.appInstallationStatus] in order to check the installation status.
     */
    @CheckResult
    public suspend fun startRemoteActivity(
        nodeId: String,
        config: ActivityConfig,
    ): AppHelperResultCode {
        checkIsForegroundOrThrow()
        val request = launchRequest { activity = config }
        return sendRequestWithTimeout(nodeId, LAUNCH_APP, request.toByteArray())
    }

    /**
     * Launch own app on the specified node.
     *
     * This call requires that the same app is installed on the specified node, otherwise a
     * [timeout](AppHelperResultCode.APP_HELPER_RESULT_TIMEOUT) is expected.
     * See [AppHelperNodeStatus.appInstallationStatus] in order to check the installation status.
     */
    @CheckResult
    public suspend fun startRemoteOwnApp(nodeId: String): AppHelperResultCode {
        checkIsForegroundOrThrow()
        val request = launchRequest { ownApp = ownAppConfig { } }
        return sendRequestWithTimeout(nodeId, LAUNCH_APP, request.toByteArray())
    }

    /**
     * Attempts a [MessageClient#sendRequest] with a timeout, covering both the GMS Timeout that may
     * occur (after 1 minute?) but also a timeout specified by Horologist, as 1 minute is often too
     * long.
     */
    @CheckResult
    protected suspend fun sendRequestWithTimeout(
        nodeId: String,
        path: String,
        data: ByteArray,
        timeoutMs: Long = MESSAGE_REQUEST_TIMEOUT_MS,
    ): AppHelperResultCode {
        val response = try {
            withTimeout(timeoutMs) {
                // Cancellation will not lead to the GMS Task itself being cancelled.
                registry.messageClient.sendRequest(nodeId, path, data).await()
            }
        } catch (timeoutException: TimeoutCancellationException) {
            return AppHelperResultCode.APP_HELPER_RESULT_TIMEOUT
        } catch (e: ApiException) {
            if (e.statusCode == CommonStatusCodes.TIMEOUT) {
                return AppHelperResultCode.APP_HELPER_RESULT_TIMEOUT
            } else {
                throw e
            }
        }
        return AppHelperResult.parseFrom(response).code
    }

    /**
     * Provides a check that the current process is running in the foreground. This is used in all
     * methods within AppHelper that start some form of Activity on the other device, to ensure that
     * apps are not being launched as a result of a background process on the calling device.
     */
    protected fun checkIsForegroundOrThrow() {
        val runningAppProcesses = activityManager.runningAppProcesses ?: emptyList()
        val isForeground = runningAppProcesses.find {
            it.pid == Process.myPid()
        }?.importance == IMPORTANCE_FOREGROUND
        if (!isForeground) {
            throw SecurityException("This method can only be called from the foreground.")
        }
    }

    /**
     * Check whether the data layer is available before use to avoid crashes.
     */
    public suspend fun isAvailable(): Boolean =
        // Check CapabilityClient as a proxy for all APIs being available
        WearableApiAvailability.isAvailable(registry.capabilityClient)

    public companion object {
        public const val DATA_LAYER_APP_HELPER: String = "data_layer_app_helper"
        public const val CAPABILITY_DEVICE_PREFIX = "${DATA_LAYER_APP_HELPER}_device"
        public const val PHONE_CAPABILITY = "${CAPABILITY_DEVICE_PREFIX}_phone"
        public const val WATCH_CAPABILITY = "${CAPABILITY_DEVICE_PREFIX}_watch"
        public const val LAUNCH_APP: String = "/$DATA_LAYER_APP_HELPER/launch_app"
        public const val SURFACE_INFO_PATH: String = "/$DATA_LAYER_APP_HELPER/surface_info"
        public const val MESSAGE_REQUEST_TIMEOUT_MS = 15_000L
    }
}

fun byteArrayForResultCode(resultCode: AppHelperResultCode): ByteArray {
    val response = appHelperResult {
        code = resultCode
    }
    return response.toByteArray()
}
