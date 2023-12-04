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

package com.google.android.horologist.networks.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BATTERY_LOW
import android.content.Intent.ACTION_BATTERY_OKAY
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.BatteryManager.ACTION_CHARGING
import android.os.BatteryManager.ACTION_DISCHARGING
import android.os.PowerManager
import android.os.PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED
import android.os.PowerManager.ACTION_POWER_SAVE_MODE_CHANGED
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.RECEIVER_EXPORTED
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class BatteryStatusMonitor(
    private val context: Context,
) {
    private val powerManager: PowerManager = context.getSystemService(PowerManager::class.java)
    private val batteryManager: BatteryManager =
        context.getSystemService(BatteryManager::class.java)

    val subscriptionFlow = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                trySend(batteryStatus())
            }
        }

        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter().apply {
                addAction(ACTION_DEVICE_IDLE_MODE_CHANGED)
                addAction(ACTION_POWER_SAVE_MODE_CHANGED)
                addAction(ACTION_CHARGING)
                addAction(ACTION_DISCHARGING)
                addAction(ACTION_BATTERY_LOW)
                addAction(ACTION_BATTERY_OKAY)
            },
            RECEIVER_EXPORTED,
        )

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }

    val status: Flow<BatteryStatus> = flow {
        emit(batteryStatus())
        emitAll(subscriptionFlow)
    }

    private fun batteryStatus() =
        BatteryStatus(
            batteryManager.isCharging,
            powerManager.isDeviceIdleMode,
            powerManager.isPowerSaveMode,
        )

    data class BatteryStatus(
        val charging: Boolean,
        val deviceIdleMode: Boolean,
        val powerSaveMode: Boolean,
    )
}
