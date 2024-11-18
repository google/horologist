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

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.google.android.horologist.networks.battery

import android.app.Application
import android.os.BatteryManager
import android.os.PowerManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class BatteryStatusMonitorTest {
    private lateinit var powerManager: PowerManager
    private lateinit var batteryManager: BatteryManager
    private lateinit var batteryStatusMonitor: BatteryStatusMonitor

    @Before
    fun setup() {
        val applicationContext = ApplicationProvider.getApplicationContext<Application>()
        batteryManager = applicationContext.getSystemService(BatteryManager::class.java)
        powerManager = applicationContext.getSystemService(PowerManager::class.java)
        batteryStatusMonitor = BatteryStatusMonitor(applicationContext)
    }

    @Test
    fun readsFromBatteryManager() = runTest {
        var status = batteryStatusMonitor.status.first()

        assertThat(status).isEqualTo(
            BatteryStatusMonitor.BatteryStatus(
                batteryManager.isCharging,
                powerManager.isDeviceIdleMode,
                powerManager.isPowerSaveMode,
            ),
        )

        shadowOf(batteryManager).setIsCharging(false)
        status = batteryStatusMonitor.status.first()

        assertThat(status).isEqualTo(
            BatteryStatusMonitor.BatteryStatus(
                false,
                powerManager.isDeviceIdleMode,
                powerManager.isPowerSaveMode,
            ),
        )

        shadowOf(batteryManager).setIsCharging(true)
        status = batteryStatusMonitor.status.first()

        assertThat(status).isEqualTo(
            BatteryStatusMonitor.BatteryStatus(
                true,
                powerManager.isDeviceIdleMode,
                powerManager.isPowerSaveMode,
            ),
        )
    }
}
