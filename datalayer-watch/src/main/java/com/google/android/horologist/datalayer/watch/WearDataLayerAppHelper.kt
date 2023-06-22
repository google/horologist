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
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.data.ActivityLaunched
import com.google.android.horologist.data.AppHelperResultCode
import com.google.android.horologist.data.ComplicationInfo
import com.google.android.horologist.data.TileInfo
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.data.apphelper.DataLayerAppHelper
import com.google.android.horologist.data.apphelper.SurfaceInfoSerializer
import com.google.android.horologist.data.companionConfig
import com.google.android.horologist.data.complicationInfo
import com.google.android.horologist.data.copy
import com.google.android.horologist.data.launchRequest
import com.google.android.horologist.data.tileInfo
import com.google.protobuf.Timestamp
import kotlinx.coroutines.CoroutineScope
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
    private val appStoreUri: String? = null
) :
    DataLayerAppHelper(context, registry) {

    private val surfaceInfoDataStore by lazy {
        registry.protoDataStore(
            path = DataLayerAppHelper.SURFACE_INFO_PATH,
            coroutineScope = scope,
            serializer = SurfaceInfoSerializer
        )
    }

    override suspend fun installOnNode(node: String) {
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
        surfaceInfoDataStore.updateData { info ->
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
        surfaceInfoDataStore.updateData { info ->
            info.copy {
                if (!activityLaunched.activityLaunchedOnce) {
                    activityLaunched = ActivityLaunched.newBuilder()
                        .setActivityLaunchedOnce(true)
                        .setTimestamp(System.currentTimeMillis().toProtoTimestamp())
                        .build()
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
        surfaceInfoDataStore.updateData { info ->
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
     * [ComplicationDataSourceService#onComplicationActivated].
     *
     * @param complicationName The name of the complication, to disambiguate from others.
     * @param complicationInstanceId Passed from onComplicationActivated
     * @param complicationType Passedfrom onComplicationActivated
     */
    public suspend fun markComplicationAsActivated(
        complicationName: String,
        complicationInstanceId: Int,
        complicationType: ComplicationType
    ) {
        surfaceInfoDataStore.updateData { info ->
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
     * [ComplicationDataSourceService#onComplicationDeactivated].
     *
     * @param complicationName The name of the complication, to disambiguate from others.
     * @param complicationInstanceId Passed from onComplicationDeactivated
     * @param complicationType Passedfrom onComplicationDeactivated
     */
    public suspend fun markComplicationAsDeactivated(
        complicationName: String,
        complicationInstanceId: Int,
        complicationType: ComplicationType
    ) {
        surfaceInfoDataStore.updateData { info ->
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
