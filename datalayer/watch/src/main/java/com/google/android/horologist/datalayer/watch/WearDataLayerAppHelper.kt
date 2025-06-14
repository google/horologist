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

package com.google.android.horologist.datalayer.watch

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.CheckResult
import androidx.concurrent.futures.await
import androidx.datastore.core.DataStore
import androidx.wear.phone.interactions.PhoneTypeHelper
import androidx.wear.remote.interactions.RemoteActivityHelper
import androidx.wear.tiles.TileService
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService
import com.google.android.gms.wearable.Node
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.data.AppHelperResultCode
import com.google.android.horologist.data.ComplicationInfo
import com.google.android.horologist.data.SurfacesInfo
import com.google.android.horologist.data.TileInfo
import com.google.android.horologist.data.UsageStatus
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.data.activityLaunched
import com.google.android.horologist.data.apphelper.DataLayerAppHelper
import com.google.android.horologist.data.apphelper.SurfacesInfoSerializer
import com.google.android.horologist.data.companionConfig
import com.google.android.horologist.data.complicationInfo
import com.google.android.horologist.data.copy
import com.google.android.horologist.data.launchRequest
import com.google.android.horologist.data.tileInfo
import com.google.android.horologist.data.usageInfo
import com.google.protobuf.Timestamp
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

private const val TAG = "DataLayerAppHelper"

/**
 * Subclass of [DataLayerAppHelper] for use on Wear devices.
 *
 * Parameter [appStoreUri] should be provided when using functions like [installOnNode] with an iOS
 * device.
 */
@ExperimentalHorologistApi
public class WearDataLayerAppHelper internal constructor(
    context: Context,
    registry: WearDataLayerRegistry,
    private val appStoreUri: String?,
    private val scope: CoroutineScope,
    surfacesInfoDataStoreFn: () -> DataStore<SurfacesInfo>,
) : DataLayerAppHelper(context, registry) {
    public constructor(
        context: Context,
        registry: WearDataLayerRegistry,
        scope: CoroutineScope,
        appStoreUri: String? = null,
    ) : this(context, registry, appStoreUri, scope, {
        registry.protoDataStore(
            path = SURFACE_INFO_PATH,
            coroutineScope = scope,
            serializer = SurfacesInfoSerializer,
        )
    })

    private val surfacesInfoDataStore by lazy { surfacesInfoDataStoreFn() }

    /**
     * Return the [SurfacesInfo] of this node.
     */
    public val surfacesInfo: Flow<SurfacesInfo> by lazy { surfacesInfoDataStore.data }

    override val connectedAndInstalledNodes: Flow<Set<Node>>
        get() = connectedAndInstalledNodes(PHONE_CAPABILITY)

    override suspend fun installOnNode(nodeId: String): AppHelperResultCode {
        checkIsForegroundOrThrow()

        val intent = when (PhoneTypeHelper.getPhoneDeviceType(context)) {
            PhoneTypeHelper.DEVICE_TYPE_ANDROID -> {
                Intent(Intent.ACTION_VIEW)
                    .addCategory(Intent.CATEGORY_BROWSABLE)
                    .setData(Uri.parse(playStoreUri))
            }

            PhoneTypeHelper.DEVICE_TYPE_IOS -> {
                requireNotNull(appStoreUri) {
                    "The uri for the app store should be provided when using this function with " +
                        "an iOS device."
                }

                Intent(Intent.ACTION_VIEW)
                    .addCategory(Intent.CATEGORY_BROWSABLE)
                    .setData(Uri.parse(appStoreUri))
            }

            else -> {
                return AppHelperResultCode.APP_HELPER_RESULT_CANNOT_DETERMINE_DEVICE_TYPE
            }
        }

        val availabilityStatus = remoteActivityHelper.availabilityStatus.first()

        // As per documentation, calls should be made when status is either STATUS_AVAILABLE
        // or STATUS_UNKNOWN.
        when (availabilityStatus) {
            RemoteActivityHelper.STATUS_UNAVAILABLE -> {
                return AppHelperResultCode.APP_HELPER_RESULT_UNAVAILABLE
            }

            RemoteActivityHelper.STATUS_TEMPORARILY_UNAVAILABLE -> {
                return AppHelperResultCode.APP_HELPER_RESULT_TEMPORARILY_UNAVAILABLE
            }
        }

        try {
            remoteActivityHelper.startRemoteActivity(intent, nodeId).await()
        } catch (e: RemoteActivityHelper.RemoteIntentException) {
            return AppHelperResultCode.APP_HELPER_RESULT_ERROR_STARTING_ACTIVITY
        }
        return AppHelperResultCode.APP_HELPER_RESULT_SUCCESS
    }

    @CheckResult
    override suspend fun startCompanion(nodeId: String): AppHelperResultCode {
        checkIsForegroundOrThrow()
        val localNode = registry.nodeClient.localNode.await()
        val request = launchRequest {
            companion = companionConfig {
                sourceNode = localNode.id
            }
        }
        return sendRequestWithTimeout(nodeId, LAUNCH_APP, request.toByteArray())
    }

    /**
     * Updates the list of currently installed tiles on this watch.
     *
     * This function has some limitations on older SDK versions, please see
     * the docs for [TileService#getActiveTilesAsync](https://developer.android.com/reference/androidx/wear/tiles/TileService#getActiveTilesAsync(android.content.Context,java.util.concurrent.Executor))
     */
    @OptIn(ExperimentalStdlibApi::class)
    public suspend fun updateInstalledTiles() {
        val executor = scope.coroutineContext[CoroutineDispatcher]?.asExecutor()

        require(executor != null) {
            "Executor is null, something is wrong during initalization of WearDataLayerAppHelper"
        }

        val activeTiles = TileService.getActiveTilesAsync(
            context,
            executor,
        ).await()

        surfacesInfoDataStore.updateData { info ->
            info.copy {
                tiles.clear()
                for (activeTileIdentifier in activeTiles) {
                    tiles.add(
                        tileInfo {
                            timestamp = System.currentTimeMillis().toProtoTimestamp()
                            name = activeTileIdentifier.componentName.className
                        },
                    )
                }
            }
        }
    }

    /**
     * Marks that the main activity has been launched at least once.
     */
    public suspend fun markActivityLaunchedOnce() {
        surfacesInfoDataStore.updateData { info ->
            info.copy {
                val launchTimestamp = System.currentTimeMillis().toProtoTimestamp()
                if (usageInfo.usageStatus == UsageStatus.USAGE_STATUS_UNSPECIFIED) {
                    usageInfo = usageInfo {
                        usageStatus = UsageStatus.USAGE_STATUS_LAUNCHED_ONCE
                        timestamp = launchTimestamp
                    }
                }

                // Temporarily support previous location for this information in [ActivityLaunched]
                // Remove in the longer term
                if (!activityLaunched.activityLaunchedOnce) {
                    activityLaunched = activityLaunched {
                        activityLaunchedOnce = true
                        timestamp = launchTimestamp
                    }
                }
            }
        }
    }

    /**
     * Marks that the necessary setup steps have been completed in the app such that it is ready for
     * use. Typically this should be called when any pairing/login has been completed. If used for
     * prompting login, it should also be called during startup if login happened before
     */
    public suspend fun markSetupComplete() {
        surfacesInfoDataStore.updateData { info ->
            info.copy {
                if (usageInfo.usageStatus != UsageStatus.USAGE_STATUS_SETUP_COMPLETE) {
                    usageInfo = usageInfo {
                        usageStatus = UsageStatus.USAGE_STATUS_SETUP_COMPLETE
                        timestamp = System.currentTimeMillis().toProtoTimestamp()
                    }
                }
            }
        }
    }

    /**
     * Marks that the app is no longer considered in a fully setup state. For example, the user has
     * logged out. This will roll the state back to the app having been used once - if the setup
     * had previously been completed, but will have no effect if this is not the case.
     */
    public suspend fun markSetupNoLongerComplete() {
        surfacesInfoDataStore.updateData { info ->
            info.copy {
                if (usageInfo.usageStatus == UsageStatus.USAGE_STATUS_SETUP_COMPLETE) {
                    usageInfo = usageInfo {
                        usageStatus = UsageStatus.USAGE_STATUS_LAUNCHED_ONCE
                        timestamp = System.currentTimeMillis().toProtoTimestamp()
                    }
                }
            }
        }
    }

    /**
     * Marks a complication as activated. Call this in
     * [ComplicationDataSourceService.onComplicationActivated].
     *
     * @param complicationName The name of the complication, to disambiguate from others.
     * @param complicationInstanceId Passed from [ComplicationDataSourceService.onComplicationActivated]
     * @param complicationType Passed from [ComplicationDataSourceService.onComplicationActivated]
     */
    public suspend fun markComplicationAsActivated(
        complicationName: String,
        complicationInstanceId: Int,
        complicationType: ComplicationType,
    ) {
        surfacesInfoDataStore.updateData { info ->
            val complication = complicationInfo {
                timestamp = System.currentTimeMillis().toProtoTimestamp()
                name = complicationName
                instanceId = complicationInstanceId
                type = complicationType.name
            }
            info.copy {
                val exists = complications.find { it.equalWithoutTimestamp(complication) } != null
                if (!exists) {
                    complications.add(complication)
                }
            }
        }
    }

    /**
     * Marks a complication as deactivated. Call this in
     * [ComplicationDataSourceService.onComplicationDeactivated].
     *
     * @param complicationInstanceId Passed from [ComplicationDataSourceService.onComplicationDeactivated]
     */
    public suspend fun markComplicationAsDeactivated(
        complicationInstanceId: Int,
    ) {
        surfacesInfoDataStore.updateData { info ->
            info.copy {
                val filtered = complications.filterNot { it.instanceId == complicationInstanceId }
                if (filtered.size != complications.size) {
                    complications.clear()
                    complications.addAll(filtered)
                }
            }
        }
    }

    /**
     * Compares equality of [TileInfo] excluding timestamp, as when the Tile was added is not
     * relevant.
     */
    private fun TileInfo.equalWithoutTimestamp(other: TileInfo): Boolean =
        this.copy { timestamp = Timestamp.getDefaultInstance() } == other.copy {
            timestamp = Timestamp.getDefaultInstance()
        }

    /**
     * Compares equality of [ComplicationInfo] excluding timestamp, as when the Complication was
     * added is not relevant.
     */
    private fun ComplicationInfo.equalWithoutTimestamp(other: ComplicationInfo): Boolean =
        this.copy { timestamp = Timestamp.getDefaultInstance() } == other.copy {
            timestamp = Timestamp.getDefaultInstance()
        }

    internal companion object {
        internal fun Long.toProtoTimestamp(): Timestamp {
            return Timestamp.newBuilder()
                .setSeconds(this / 1000)
                .setNanos((this % 1000).toInt() * 1000000)
                .build()
        }
    }
}
