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
import android.net.Uri
import androidx.annotation.CheckResult
import androidx.concurrent.futures.await
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.data.AppHelperResultCode
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.data.apphelper.DataLayerAppHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

/**
 * Subclass of [DataLayerAppHelper] for use on phones.
 */
@ExperimentalHorologistApi
public class PhoneDataLayerAppHelper(
    context: Context,
    registry: WearDataLayerRegistry,
) : DataLayerAppHelper(context, registry) {
    private val SAMSUNG_COMPANION_PKG = "com.samsung.android.app.watchmanager"

    override suspend fun installOnNode(nodeId: String): AppHelperResultCode {
        checkIsForegroundOrThrow()

        val intent = Intent(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(Uri.parse(playStoreUri))

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
}
