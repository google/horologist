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

package com.google.android.horologist.datalayer.phone

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import androidx.annotation.CheckResult
import androidx.concurrent.futures.await
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.data.AppHelperResultCode
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.data.apphelper.AppHelperNodeStatus
import com.google.android.horologist.data.apphelper.DataLayerAppHelper
import com.google.android.horologist.data.apphelper.appInstalled
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

private const val SAMSUNG_COMPANION_PKG = "com.samsung.android.app.watchmanager"

/**
 * Subclass of [DataLayerAppHelper] for use on phones.
 */
@ExperimentalHorologistApi
public class PhoneDataLayerAppHelper(
    context: Context,
    registry: WearDataLayerRegistry,
) : DataLayerAppHelper(context, registry) {

    override val connectedAndInstalledNodes: Flow<Set<Node>>
        get() = connectedAndInstalledNodes(WATCH_CAPABILITY)

    override suspend fun installOnNode(nodeId: String): AppHelperResultCode {
        checkIsForegroundOrThrow()

        val intent = Intent(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(Uri.parse(playStoreUri))

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
        val companionPackage = registry.nodeClient.getCompanionPackageForNode(nodeId).await()

        /**
         * Some devices report the wrong companion for actually launching the Companion app: For
         * example, Samsung devices report the plugin packages that handle comms with GW4, GW5
         * etc, whereas the package name for the companion *app* is different.
         */
        val launchPackage = rewriteCompanionPackageName(companionPackage)

        val intent = context.packageManager.getLaunchIntentForPackage(launchPackage)
            ?: return AppHelperResultCode.APP_HELPER_RESULT_NO_COMPANION_FOUND
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            return AppHelperResultCode.APP_HELPER_RESULT_ACTIVITY_NOT_FOUND
        }
        return AppHelperResultCode.APP_HELPER_RESULT_SUCCESS
    }

    /**
     * Filters for watch nodes where the app is installed but Tile is not installed.
     */
    public suspend fun findWatchToInstallTile(): AppHelperNodeStatus? {
        val node = this.connectedNodes()
            .filter {
                it.appInstalled &&
                    registry.nodeClient.getCompanionPackageForNode(it.id).await() == "com.google.android.apps.wear.companion"
            }
            .firstOrNull {
                val uri = Uri.Builder()
                    .scheme(PutDataRequest.WEAR_URI_SCHEME)
                    .path("/tile_tracking_enabled")
                    .authority(it.id)
                    .build()

                registry.dataClient.getDataItem(uri).await() != null
            }
        return node
    }

    /**
     * Checks that the companion app supports deep linking to Tile editor setting.
     */
    public fun checkCompanionVersionSupportTileEditing(): AppHelperResultCode? {
        return try {
            val packageInfo: PackageInfo =
                context.packageManager.getPackageInfo("com.google.android.apps.wear.companion", 0)
            val version = packageInfo.versionName

            val companionVersion = Version.parse(version)
            if (companionVersion != null && companionVersion >= RequiredCompanionVersion) {
                AppHelperResultCode.APP_HELPER_RESULT_SUCCESS
            } else {
                AppHelperResultCode.APP_HELPER_RESULT_INVALID_COMPANION
            }
        } catch (nnfe: PackageManager.NameNotFoundException) {
            AppHelperResultCode.APP_HELPER_RESULT_NO_COMPANION_FOUND
        }
    }

    /**
     * Some devices report back a different packageName from getCompanionPackageForNode() than is
     * the actual package of the Companion app. Where this is the case, this lookup ensures the
     * correct companion app can be launched..
     */
    private fun rewriteCompanionPackageName(companionPackage: String): String {
        val regex = Regex("""com.samsung.*plugin""")
        return if (regex.matches(companionPackage)) {
            SAMSUNG_COMPANION_PKG
        } else {
            companionPackage
        }
    }

    public companion object {
        public val RequiredCompanionVersion: Version = Version.parse("2.1.0.576785526")!!
    }
}
