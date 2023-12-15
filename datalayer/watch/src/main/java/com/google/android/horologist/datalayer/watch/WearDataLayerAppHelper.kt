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
import androidx.wear.phone.interactions.PhoneTypeHelper
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.tasks.await

private const val TAG = "DataLayerAppHelper"

/**
 * Subclass of [DataLayerAppHelper] for use on Wear devices.
 */
@ExperimentalHorologistApi
public class WearDataLayerAppHelper(
    context: Context,
    registry: WearDataLayerRegistry,
    scope: CoroutineScope,
    private val appStoreUri: String? = null,
) :
    DataLayerAppHelper(context, registry) {

        private val surfacesInfoDataStore by lazy {
            registry.protoDataStore(
                path = SURFACE_INFO_PATH,
                coroutineScope = scope,
                serializer = SurfacesInfoSerializer,
            )
        }

        /**
         * Return the [SurfacesInfo] of this node.
         */
        public val surfacesInfo: Flow<SurfacesInfo> = surfacesInfoDataStore.data

        override suspend fun installOnNode(node: String) {
            checkIsForegroundOrThrow()
            if (appStoreUri != null &&
                PhoneTypeHelper.getPhoneDeviceType(context) == PhoneTypeHelper.DEVICE_TYPE_IOS
            ) {
                val intent = Intent(Intent.ACTION_VIEW)
                    .addCategory(Intent.CATEGORY_BROWSABLE)
                    .setData(Uri.parse(appStoreUri))
                remoteActivityHelper.startRemoteActivity(intent, node).await()
            } else if (PhoneTypeHelper.getPhoneDeviceType(context) == PhoneTypeHelper.DEVICE_TYPE_ANDROID) {
                val intent = Intent(Intent.ACTION_VIEW)
                    .addCategory(Intent.CATEGORY_BROWSABLE)
                    .setData(Uri.parse(playStoreUri))
                remoteActivityHelper.startRemoteActivity(intent, node).await()
            }
        }

        @CheckResult
        override suspend fun startCompanion(node: String): AppHelperResultCode {
            checkIsForegroundOrThrow()
            val localNode = registry.nodeClient.localNode.await()
            val request = launchRequest {
                companion = companionConfig {
                    sourceNode = localNode.id
                }
            }
            return sendRequestWithTimeout(node, LAUNCH_APP, request.toByteArray())
        }

        /**
         * Marks a tile as installed. Call this in [TileService#onTileAddEvent]. Supplying a name is
         * mandatory to disambiguate from the installation or removal of other tiles your app may have.
         *
         * @param tileName The name of the tile.
         */
        public suspend fun markTileAsInstalled(tileName: String) {
            surfacesInfoDataStore.updateData { info ->
                val tile = tileInfo {
                    timestamp = System.currentTimeMillis().toProtoTimestamp()
                    name = tileName
                }
                info.copy {
                    val exists = tiles.find { it.equalWithoutTimestamp(tile) } != null
                    if (!exists) {
                        tiles.add(tile)
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
         * use. Typically this should be called when any pairing/login has been completed.
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
         * Marks a tile as removed. Call this in [TileService#onTileRemoveEvent]. Supplying a name is
         * mandatory to disambiguate from the installation or removal of other tiles your app may have.
         *
         * @param tileName The name of the tile.
         */
        public suspend fun markTileAsRemoved(tileName: String) {
            surfacesInfoDataStore.updateData { info ->
                val tile = tileInfo {
                    timestamp = System.currentTimeMillis().toProtoTimestamp()
                    name = tileName
                }
                info.copy {
                    val filtered = tiles.filter { !tile.equalWithoutTimestamp(it) }
                    if (filtered.size != tiles.size) {
                        tiles.clear()
                        tiles.addAll(filtered)
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
         * @param complicationName The name of the complication, to disambiguate from others.
         * @param complicationInstanceId Passed from [ComplicationDataSourceService.onComplicationDeactivated]
         * @param complicationType Passed from [ComplicationDataSourceService.onComplicationDeactivated]
         */
        public suspend fun markComplicationAsDeactivated(
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
                    val filtered = complications.filter { !complication.equalWithoutTimestamp(it) }
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
