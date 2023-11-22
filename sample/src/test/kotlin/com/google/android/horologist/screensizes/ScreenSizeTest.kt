/*
 * Copyright 2022 The Android Open Source Project
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

package com.google.android.horologist.screensizes

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.test.core.app.ApplicationProvider
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.compose.tools.Device
import com.google.android.horologist.compose.tools.GenericLargeRound
import com.google.android.horologist.compose.tools.GenericSmallRound
import com.google.android.horologist.compose.tools.GooglePixelWatch
import com.google.android.horologist.compose.tools.MobvoiTicWatchPro5
import com.google.android.horologist.compose.tools.SamsungGalaxyWatch5
import com.google.android.horologist.compose.tools.SamsungGalaxyWatch6Large
import com.google.android.horologist.compose.tools.copy
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import com.google.android.horologist.screenshots.ScreenshotTestRule.Companion.screenshotTestRuleParams
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowDisplay

@RunWith(ParameterizedRobolectricTestRunner::class)
abstract class ScreenSizeTest(
    val device: Device,
    val showTimeText: Boolean,
) : ScreenshotBaseTest(
    screenshotTestRuleParams {
        if (!showTimeText) {
            screenTimeText = { }
        }
        testLabel = device.name.lowercase().replace("\\W+".toRegex(), "")
    },
) {
    @Composable
    abstract fun Content()

    @Test
    fun screenshot() {
        runTest { Content() }
    }

    fun runTest(content: @Composable () -> Unit) {
        val shadowDisplay = Shadows.shadowOf(ShadowDisplay.getDefaultDisplay())
        shadowDisplay.setDensity(device.density)
        shadowDisplay.setHeight(device.screenSizePx)
        shadowDisplay.setWidth(device.screenSizePx)

        RuntimeEnvironment.setFontScale(device.fontScale)
        RuntimeEnvironment.setQualifiers("+w${device.screenSizeDp}dp-h${device.screenSizeDp}dp")

        ApplicationProvider.getApplicationContext<Context>().setDisplayScale(device.density)

        screenshotTestRule.setContent(takeScreenshot = true) {
            MaterialTheme(
                typography = MaterialTheme.typography.copy {
                    this.copy(fontWeight = if (device.boldText) FontWeight.Bold else FontWeight.Medium)
                },
                content = content,
            )
        }
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun devices() = listOf(
            MobvoiTicWatchPro5,
            SamsungGalaxyWatch5,
            SamsungGalaxyWatch6Large,
            GooglePixelWatch,
            GenericSmallRound,
            GenericLargeRound,
            GooglePixelWatch.copy("Small Device, Big Fonts", fontScale = 1.24f, boldText = true),
            MobvoiTicWatchPro5.copy("Large Device, Small Fonts", fontScale = 0.94f),
        )

        @Suppress("DEPRECATION")
        fun Context.setDisplayScale(density: Float) = apply {
            // Modified from https://medium.com/@summitkumar/understanding-the-relationship-between-dpi-screen-resolution-and-image-pixel-density-in-android-b0ba27b7307a
            val config = Configuration(resources.configuration)
            config.densityDpi = (density * 160f).toInt()

            resources.updateConfiguration(
                config,
                resources.displayMetrics
            )
        }
    }
}
