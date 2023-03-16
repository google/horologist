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
import androidx.wear.phone.interactions.PhoneTypeHelper
import androidx.wear.watchface.complications.data.ComplicationType
import com.google.android.horologist.data.AppHelperResult
import com.google.android.horologist.data.AppHelperResultCode
import com.google.android.horologist.data.ExperimentalHorologistDataLayerApi
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.data.apphelper.DataLayerAppHelper
import com.google.android.horologist.data.apphelper.SurfaceInfoSerializer
import com.google.android.horologist.data.companionConfig
import com.google.android.horologist.data.complicationInfo
import com.google.android.horologist.data.copy
import com.google.android.horologist.data.launchRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.tasks.await

private const val TAG = "DataLayerAppHelper"

/**
 * Subclass of [DataLayerAppHelper] for use on Wear devices.
 */
@ExperimentalHorologistDataLayerApi
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

    override suspend fun startCompanion(node: String): AppHelperResultCode {
        val localNode = registry.nodeClient.localNode.await()
        val request = launchRequest {
            companion = companionConfig {
                sourceNode = localNode.id
            }
        }
        val response =
            registry.messageClient.sendRequest(node, LAUNCH_APP, request.toByteArray())
                .await()
        return AppHelperResult.parseFrom(response).code
    }

    /**
     * Marks a tile as installed. Call this in [TileService#onTileAddEvent]. Supplying a name is
     * mandatory to disambiguate from the installation or removal of other tiles your app may have.
     *
     * @param tileName The name of the tile.
     */
    public suspend fun markTileAsInstalled(tileName: String) {
        surfaceInfoDataStore.updateData {
                info ->
            info.copy {
                if (!tileNames.contains(tileName)) tileNames.add(tileName)
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
        surfaceInfoDataStore.updateData {
                info ->
            info.copy {
                if (tileNames.contains(tileName)) {
                    val reducedTiles = tileNames.filter { it != tileName }
                    tileNames.clear()
                    tileNames.addAll(reducedTiles)
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
        surfaceInfoDataStore.updateData {
                info ->
            val complication = complicationInfo {
                name = complicationName
                instanceId = complicationInstanceId
                type = complicationType.name
            }
            info.copy {
                if (!complications.contains(complication)) complications.add(complication)
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
        surfaceInfoDataStore.updateData {
                info ->
            val complication = complicationInfo {
                name = complicationName
                instanceId = complicationInstanceId
                type = complicationType.name
            }
            info.copy {
                if (complications.contains(complication)) {
                    val reducedComplications = complications.filter { it != complication }
                    complications.clear()
                    complications.addAll(reducedComplications)
                }
            }
        }
    }
}
