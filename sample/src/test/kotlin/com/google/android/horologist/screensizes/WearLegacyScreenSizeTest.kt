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

@file:Suppress("DEPRECATION")
@file:OptIn(ExperimentalRoborazziApi::class)

package com.google.android.horologist.screensizes

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.font.FontWeight
import androidx.test.core.app.ApplicationProvider
import androidx.wear.compose.material.MaterialTheme
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.ThresholdValidator
import com.github.takahirom.roborazzi.captureScreenRoboImage
import com.google.android.horologist.compose.tools.Device
import com.google.android.horologist.compose.tools.GenericLargeRound
import com.google.android.horologist.compose.tools.GenericSmallRound
import com.google.android.horologist.compose.tools.GooglePixelWatch
import com.google.android.horologist.compose.tools.MobvoiTicWatchPro5
import com.google.android.horologist.compose.tools.SamsungGalaxyWatch5
import com.google.android.horologist.compose.tools.SamsungGalaxyWatch6Large
import com.google.android.horologist.compose.tools.copy
import com.google.android.horologist.screenshots.rng.WearScreenshotTest.Companion.useHardwareRenderer
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowDisplay

@Config(
    sdk = [33],
    qualifiers = RobolectricDeviceQualifiers.WearOSLargeRound,
)
@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
abstract class WearLegacyScreenSizeTest(
    val device: Device,
    val showTimeText: Boolean,
) {
    @get:Rule
    public val composeRule: ComposeContentTestRule = createComposeRule()

    @Composable
    abstract fun Content()

    @get:Rule
    public val testInfo: TestName = TestName()

    // Allow for individual tolerances to be set on each test, should be between 0.0 and 1.0
    public open val tolerance: Float = 0.0f

    @Test
    fun screenshot() {
        runTest { Content() }
    }

    fun testName(suffix: String): String =
        "src/test/snapshots/images/${this.javaClass.`package`?.name}_${this.javaClass.simpleName}_${testInfo.methodName}_${
            device.name.lowercase().replace("\\W+".toRegex(), "")
        }$suffix.png"

    fun runTest(
        capture: Boolean = true,
        content: @Composable () -> Unit,
    ) {
        val shadowDisplay = Shadows.shadowOf(ShadowDisplay.getDefaultDisplay())
        shadowDisplay.setDensity(device.density)
        shadowDisplay.setHeight(device.screenSizePx)
        shadowDisplay.setWidth(device.screenSizePx)

        RuntimeEnvironment.setFontScale(device.fontScale)
        RuntimeEnvironment.setQualifiers("+w${device.screenSizeDp}dp-h${device.screenSizeDp}dp")

        ApplicationProvider.getApplicationContext<Context>().setDisplayScale(device.density)

        composeRule.setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
            ) {
                MaterialTheme(
                    typography = MaterialTheme.typography.copy {
                        this.copy(fontWeight = if (device.boldText) FontWeight.Bold else FontWeight.Medium)
                    },
                    content = content,
                )
            }
        }

        if (capture) {
            captureScreenshot("")
        }
    }

    public fun captureScreenshot(suffix: String = "") {
        captureScreenRoboImage(
            filePath = testName(suffix),
            roborazziOptions = RoborazziOptions(
                recordOptions = RoborazziOptions.RecordOptions(
                    applyDeviceCrop = true,
                ),
                compareOptions = RoborazziOptions.CompareOptions(
                    resultValidator = ThresholdValidator(tolerance),
                ),
            ),
        )
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
            // Modified from https://sergiosastre.hashnode.dev/efficient-testing-with-robolectric-roborazzi-across-many-ui-states-devices-and-configurations?ref=twitter-share
            val config = Configuration(resources.configuration)
            config.densityDpi = (density * 160f).toInt()

            resources.updateConfiguration(
                config,
                resources.displayMetrics,
            )
        }

        init {
            useHardwareRenderer()
        }
    }
}
