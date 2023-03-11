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
import android.util.Log
import androidx.wear.phone.interactions.PhoneTypeHelper
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.WearableStatusCodes
import com.google.android.horologist.data.AppHelperResult
import com.google.android.horologist.data.AppHelperResultCode
import com.google.android.horologist.data.DataLayerAppHelper
import com.google.android.horologist.data.ExperimentalHorologistDataLayerApi
import com.google.android.horologist.data.companionConfig
import com.google.android.horologist.data.launchRequest
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.tasks.await

private const val TAG = "DataLayerAppHelper"

/**
 * Subclass of [DataLayerAppHelper] for use on Wear devices.
 */
@ExperimentalHorologistDataLayerApi
public class WearDataLayerAppHelper(context: Context, private val appStoreUri: String? = null) :
    DataLayerAppHelper(context) {
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
        val localNode = nodeClient.localNode.await()
        val request = launchRequest {
            companion = companionConfig {
                sourceNode = localNode.id
            }
        }
        val response =
            messageClient.sendRequest(node, LAUNCH_APP, request.toByteArray())
                .await()
        return AppHelperResult.parseFrom(response).code
    }

    /**
     * Marks a tile as installed. Call this in [TileService#onTileAddEvent]. Supplying a name is
     * mandatory to disambiguate from the installation or removal of other tiles your app may have.
     *
     * @param tileName The name of the tile.
     */
    public suspend fun markTileAsInstalled(tileName: String): Unit = markSurfaceAsInstalled(tilePrefix, tileName)

    /**
     * Marks a tile as removed. Call this in [TileService#onTileRemoveEvent]. Supplying a name is
     * mandatory to disambiguate from the installation or removal of other tiles your app may have.
     *
     * @param tileName The name of the tile.
     */
    public suspend fun markTileAsRemoved(tileName: String): Unit = markSurfaceAsRemoved(tilePrefix, tileName)

    /**
     * Marks a complication as active on the current watch face. Call this in
     * [ComplicationDataSourceService#onComplicationActivated]. Supplying a name is mandatory to
     * disambiguate from the installation or removal of other complications your app may have.
     *
     * @param complicationName The name of the complication.
     */
    public suspend fun markComplicationAsActivated(complicationName: String): Unit = markSurfaceAsInstalled(complicationPrefix, complicationName)

    /**
     * Marks a complication as deactivated. Call this in
     * [ComplicationDataSourceService#onComplicationDeactivated]. Supplying a name is mandatory to
     * disambiguate from the installation or removal of other complications your app may have.
     *
     * @param complicationName The name of the complication.
     */
    public suspend fun markComplicationAsDeactivated(complicationName: String): Unit = markSurfaceAsRemoved(complicationPrefix, complicationName)

    private suspend fun markSurfaceAsInstalled(surfacePrefix: String, name: String) {
        require(name.isNotEmpty())
        try {
            capabilityClient.addLocalCapability("$surfacePrefix$name").await()
        } catch (e: ApiException) {
            if (e.statusCode != WearableStatusCodes.DUPLICATE_CAPABILITY) {
                throw e
            }
        }
    }

    private suspend fun markSurfaceAsRemoved(surfacePrefix: String, name: String) {
        require(name.isNotEmpty())
        try {
            capabilityClient.removeLocalCapability("$surfacePrefix$name").await()
        } catch (e: ApiException) {
            if (e.statusCode != WearableStatusCodes.UNKNOWN_CAPABILITY) {
                throw e
            }
            Log.w(TAG, "Unknown capability: $surfacePrefix$name")
        }
    }
}
