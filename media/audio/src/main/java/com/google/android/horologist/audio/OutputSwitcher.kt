/*
 * Copyright 2021 The Android Open Source Project
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

package com.google.android.horologist.audio

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo

/**
 * Support for launching the system output switcher.
 *
 * https://developer.android.com/guide/topics/media/media-routing#output-switcher.
 *
 */
public object OutputSwitcher {
    /**
     * Open the Output Switcher Dialog.
     */
    public fun Context.launchSystemMediaOutputSwitcherUi(): Boolean {
        val outputSwitcherLaunchIntent: Intent = Intent(OUTPUT_SWITCHER_INTENT_ACTION_NAME)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra(EXTRA_OUTPUT_SWITCHER_PACKAGE_NAME, packageName)
        val outputSwitcherSystemComponentName =
            getSystemOrSystemUpdatedAppComponent(outputSwitcherLaunchIntent)
        if (outputSwitcherSystemComponentName != null) {
            outputSwitcherLaunchIntent.component = outputSwitcherSystemComponentName
            startActivity(outputSwitcherLaunchIntent)
            return true
        }
        return false
    }

    private fun Context.getSystemOrSystemUpdatedAppComponent(intent: Intent): ComponentName? {
        val packageManager = packageManager
        val resolveInfos = packageManager.queryIntentActivities(intent, 0)
        for (resolveInfo in resolveInfos) {
            val activityInfo = resolveInfo.activityInfo
            if (activityInfo?.applicationInfo == null) {
                continue
            }
            val appInfo = activityInfo.applicationInfo
            val systemAndUpdatedSystemAppFlags =
                ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP
            if (systemAndUpdatedSystemAppFlags and appInfo.flags != 0) {
                return ComponentName(activityInfo.packageName, activityInfo.name)
            }
        }
        return null
    }

    private const val EXTRA_OUTPUT_SWITCHER_PACKAGE_NAME = "com.android.settings.panel.extra.PACKAGE_NAME"
    private const val OUTPUT_SWITCHER_INTENT_ACTION_NAME = "com.android.settings.panel.action.MEDIA_OUTPUT"
}
