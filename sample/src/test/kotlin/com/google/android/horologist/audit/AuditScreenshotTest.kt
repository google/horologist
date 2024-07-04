/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.audit

import androidx.compose.runtime.Composable
import com.google.android.horologist.screenshots.rng.WearDevice
import com.google.android.horologist.screenshots.rng.WearScreenshotTest
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner

@RunWith(ParameterizedRobolectricTestRunner::class)
public abstract class AuditScreenshotTest(override val device: WearDevice) :
    WearScreenshotTest() {
    public override val tolerance: Float = 0.02f

    abstract val audit: AuditNavigation

    override fun testName(suffix: String): String {
        return "src/test/screenshots/${audit.id}${suffix}_${device.id}.png"
    }

    public fun runTest(content: @Composable () -> Unit) {
        runTest(suffix = null, content = content)
    }

    public companion object {
        // Below the breakpoint
        val Device204 = WearDevice(
            id = "device204",
            modelName = "Generic 204",
            screenSizePx = 408,
            density = 2.0f,
        )
        // Above the breakpoint
        val Device228 = WearDevice(
            id = "device228",
            modelName = "Generic 228",
            screenSizePx = 456,
            density = 2.0f,
        )
        // Above any known device, but to test further than needed
        val Device240 = WearDevice(
            id = "device240",
            modelName = "Generic 240",
            screenSizePx = 480,
            density = 2.0f,
        )

        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        public fun devices(): List<WearDevice> = listOf(
            WearDevice.GooglePixelWatch,
            WearDevice.GooglePixelWatchLargeFont,
            WearDevice.SamsungGalaxyWatch6SmallFont,
            Device204,
            Device228,
            Device240
        )
    }
}
