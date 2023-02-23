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

package com.google.android.horologist.data

import android.content.ActivityNotFoundException
import android.content.Intent
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.runBlocking

/**
 * Base service to respond to incoming requests from the partnering app on the connected device.
 */
public abstract class DataLayerAppHelperService : WearableListenerService() {
    public abstract val appHelper: DataLayerAppHelper
    override fun onRequest(node: String, path: String, byteArray: ByteArray): Task<ByteArray> {
        if (path != DataLayerAppHelper.LAUNCH_APP) {
            return Tasks.forResult(byteArrayForResultCode(AppHelperResultCode.UNKNOWN_REQUEST))
        }

        val request = LaunchRequest.parseFrom(byteArray)
        val result = when {
            request.hasOwnApp() -> launchOwnApp()
            request.hasActivity() -> launchActivity(request.activity)
            request.hasCompanion() -> launchCompanion(request.companion)
            else -> AppHelperResultCode.UNKNOWN_REQUEST
        }
        return Tasks.forResult(byteArrayForResultCode(result))
    }

    private fun launchOwnApp(): AppHelperResultCode {
        try {
            val intent = this.packageManager.getLaunchIntentForPackage(packageName)
                ?: return AppHelperResultCode.ACTIVITY_NOT_FOUND

            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.w(TAG, "Launch activity not found for : $packageName")
            return AppHelperResultCode.ACTIVITY_NOT_FOUND
        }
        return AppHelperResultCode.SUCCESS
    }

    /**
     * Attempts to launch an activity on the device.
     */
    private fun launchActivity(activityConfig: ActivityConfig): AppHelperResultCode {
        try {
            val intent = Intent().apply {
                setPackage(activityConfig.packageName)
                setClassName(activityConfig.packageName, activityConfig.classFullName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.w(TAG, "Activity not found: $activityConfig")
            return AppHelperResultCode.ACTIVITY_NOT_FOUND
        }
        return AppHelperResultCode.SUCCESS
    }

    /**
     * Attempts to launch the companion app on this device.
     */
    private fun launchCompanion(companionConfig: CompanionConfig): AppHelperResultCode {
        return runBlocking {
            appHelper.startCompanion(companionConfig.sourceNode)
        }
    }
}
