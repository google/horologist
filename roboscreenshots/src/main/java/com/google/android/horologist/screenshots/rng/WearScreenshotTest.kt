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

@file:OptIn(ExperimentalRoborazziApi::class, ExperimentalCoilApi::class)

package com.google.android.horologist.screenshots.rng

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.graphics.HardwareRendererCompat
import androidx.wear.compose.material.MaterialTheme
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.compose.LocalImageLoader
import coil.test.FakeImageLoaderEngine
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.ThresholdValidator
import com.github.takahirom.roborazzi.captureScreenRoboImage
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ResponsiveTimeText
import com.google.android.horologist.screenshots.FixedTimeSource
import org.junit.Rule
import org.junit.rules.TestName
import org.junit.runner.RunWith
import org.junit.experimental.categories.Category
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@Config(
    sdk = [33],
    qualifiers = RobolectricDeviceQualifiers.WearOSLargeRound,
)
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Category(ScreenshotTest::class)
public abstract class WearScreenshotTest {
    @get:Rule
    public val composeRule: ComposeContentTestRule = createComposeRule()

    @get:Rule
    public val testInfo: TestName = TestName()

    public open val device: WearDevice? = null

    // Allow for individual tolerances to be set on each test, should be between 0.0 and 1.0
    public open val tolerance: Float = 0.0f

    public open val imageLoader: FakeImageLoaderEngine? = null

    public fun runTest(
        suffix: String? = null,
        device: WearDevice? = this.device,
        applyDeviceConfig: Boolean = true,
        content: @Composable () -> Unit,
    ) {
        withDrawingEnabled {
            if (applyDeviceConfig && device != null) {
                RuntimeEnvironment.setQualifiers("+w${device.dp}dp-h${device.dp}dp")
                RuntimeEnvironment.setFontScale(device.fontScale)
            }

            composeRule.setContent {
                withImageLoader(imageLoader) {
                    TestScaffold {
                        content()
                    }
                }
            }
            captureScreenshot(suffix.orEmpty())
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

    @Composable
    public open fun TestScaffold(content: @Composable () -> Unit) {
        AppScaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            timeText = { ResponsiveTimeText(timeSource = FixedTimeSource) },
        ) {
            content()
        }
    }

    public open fun testName(suffix: String): String =
        "src/test/screenshots/${this.javaClass.simpleName}_${device?.id ?: WearDevice.GenericLargeRound.id}$suffix.png"

    public companion object {
        internal const val USE_HARDWARE_RENDERER_NATIVE_ENV = "robolectric.screenshot.hwrdr.native"

        init {
            useHardwareRenderer()
        }

        public fun useHardwareRenderer() {
            System.setProperty(USE_HARDWARE_RENDERER_NATIVE_ENV, "true")
        }

        @Composable
        internal fun withImageLoader(
            imageLoaderEngine: FakeImageLoaderEngine?,
            content: @Composable () -> Unit,
        ) {
            if (imageLoaderEngine == null) {
                content()
            } else {
                val imageLoader = ImageLoader.Builder(LocalContext.current)
                    .components { add(imageLoaderEngine) }
                    .build()
                @Suppress("DEPRECATION")
                (
                    CompositionLocalProvider(LocalImageLoader provides imageLoader) {
                        content()
                    }
                    )
            }
        }

        public fun <R> withDrawingEnabled(block: () -> R): R {
            val wasDrawingEnabled = HardwareRendererCompat.isDrawingEnabled()
            try {
                if (!wasDrawingEnabled) {
                    HardwareRendererCompat.setDrawingEnabled(true)
                }
                return block.invoke()
            } finally {
                if (!wasDrawingEnabled) {
                    HardwareRendererCompat.setDrawingEnabled(false)
                }
            }
        }
    }
}
