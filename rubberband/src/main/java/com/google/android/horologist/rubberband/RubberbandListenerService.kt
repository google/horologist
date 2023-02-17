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
import android.content.Intent
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.runBlocking

/**
 * Service to respond to incoming requests from the partnering app on the connected device.
 */
public class RubberbandListenerService : WearableListenerService() {
    override fun onRequest(node: String, path: String, byteArray: ByteArray): Task<ByteArray> {
        val request = byteArray.decodeToString()
        return when (path) {
            Rubberband.LAUNCH_REMOTE_APP -> launchActivity(request)
            Rubberband.LAUNCH_COMPANION_APP -> launchCompanion(request)
            else -> Tasks.forResult(RubberbandResult.ERROR_UNKNOWN_REQUEST.byteArray)
        }
    }

    /**
     * Attempts to launch an activity on the device.
     *
     * @param flattenedString The full component path of the activity to launch e.g.
     *     com.example.myapp/com.example.myapp.MyActivity - This parameter is optional, and if
     *     omitted, the default launch activity for the receiving app will be launched.
     */
    private fun launchActivity(flattenedString: String): Task<ByteArray> {
        try {
            val intent = if (flattenedString.isEmpty()) {
                this.packageManager.getLaunchIntentForPackage(this.packageName)
            } else {
                val (packageName, className) = flattenedString.split("/", limit = 2)
                Intent().apply {
                    setPackage(packageName)
                    setClassName(packageName, className)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }
            startActivity(intent)
        } catch (e: IndexOutOfBoundsException) {
            Log.w(TAG, "Invalid component specified: $flattenedString")
            return Tasks.forResult(RubberbandResult.ERROR_INVALID_COMPONENT.byteArray)
        } catch (e: ActivityNotFoundException) {
            Log.w(TAG, "Activity not found: $flattenedString")
            return Tasks.forResult(RubberbandResult.ERROR_ACTIVITY_NOT_FOUND.byteArray)
        }
        return Tasks.forResult(RubberbandResult.SUCCESS.byteArray)
    }

    /**
     * Attempts to launch the companion app on this device.
     */
    private fun launchCompanion(sourceNode: String): Task<ByteArray> {
        val rubberband = Rubberband(this)
        return runBlocking {
            val result = rubberband.startCompanion(sourceNode)
            Tasks.forResult(result.byteArray)
        }
    }
}
