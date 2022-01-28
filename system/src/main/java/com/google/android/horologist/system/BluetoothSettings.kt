/*
 * Copyright 2021 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.horologist.system

import android.content.Context
import android.content.Intent
import android.provider.Settings

/**
 * Support for launching the user directly into the bluetooth settings page in order to connect
 * to a headset.
 *
 * https://developer.android.com/training/wearables/overlays/audio?hl=ca
 */
object BluetoothSettings {
    /**
     * Open the bluetooth settings activity and optionally close after connection established.
     */
    fun Context.launchBluetoothSettings(closeOnConnect: Boolean = true) {
        val intent = with (Intent(Settings.ACTION_BLUETOOTH_SETTINGS)) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            putExtra("EXTRA_CONNECTION_ONLY", true)
            if (closeOnConnect) {
                putExtra("EXTRA_CLOSE_ON_CONNECT", true)
            }
            putExtra("android.bluetooth.devicepicker.extra.FILTER_TYPE", FILTER_TYPE_AUDIO)
        }
        startActivity(intent)
    }

    const val FILTER_TYPE_AUDIO = 1
}