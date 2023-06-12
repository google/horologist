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

package com.google.android.horologist.data.apphelper

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.os.ConfigurationCompat
import com.google.android.horologist.data.AppInfo
import com.google.android.horologist.data.DeviceInfo
import com.google.android.horologist.data.NodeInfo
import com.google.android.horologist.data.appInfo
import com.google.android.horologist.data.apphelper.Util.toProtoTimestamp
import com.google.android.horologist.data.deviceInfo
import com.google.android.horologist.data.nodeInfo

class NodeInfoFetcher(
    val application: Context
) {
    val packageManager: PackageManager
        get() = application.packageManager

    fun fetch(current: NodeInfo): NodeInfo = nodeInfo {
        device = fetchDeviceInfo()
        app = fetchAppInfo(current.app)
    }

    @Suppress("DEPRECATION")
    private fun fetchAppInfo(current: AppInfo): AppInfo {
        val info =
            packageManager.getPackageInfo(application.packageName, 0)

        return if (info.lastUpdateTime / 1000 > current.updateTime.seconds) {
            appInfo {
                installTime = info.firstInstallTime.toProtoTimestamp()
                updateTime = info.lastUpdateTime.toProtoTimestamp()
                appVersion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    info.longVersionCode
                } else {
                    info.versionCode.toLong()
                }
            }
        } else {
            current
        }
    }

    private fun fetchDeviceInfo(): DeviceInfo {
        val features =
            application.packageManager.systemAvailableFeatures

        val currentLocale =
            ConfigurationCompat.getLocales(application.resources.configuration)[0].toString()
        return deviceInfo {
            apiVersion = Build.VERSION.SDK_INT
            model = Build.MODEL
            device = Build.DEVICE
            product = Build.PRODUCT
            manufacturer = Build.MANUFACTURER
            locale = currentLocale
            feature.addAll(features.mapNotNull { it.name }.sorted())
        }
    }
}
