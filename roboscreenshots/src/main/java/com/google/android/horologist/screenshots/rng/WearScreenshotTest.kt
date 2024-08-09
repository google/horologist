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

import android.util.LayoutDirection.RTL
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.LayoutDirection
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.TimeText
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
import com.google.android.horologist.screenshots.FixedTimeSource
import org.junit.Rule
import org.junit.experimental.categories.Category
import org.junit.rules.TestName
import org.junit.runner.RunWith
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
        captureScreenshot: Boolean = true,
        content: @Composable () -> Unit,
    ) {
        if (applyDeviceConfig && device != null) {
            RuntimeEnvironment.setQualifiers("+w${device.dp}dp-h${device.dp}dp" + (if (device.isRound) "" else "-notround"))
            RuntimeEnvironment.setFontScale(device.fontScale)
        }

        composeRule.setContent {
            withImageLoader(imageLoader) {
                TestScaffold {
                    content()
                }
            }
        }
        if (captureScreenshot) {
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
        CorrectLayout {
            AppScaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                timeText = { TimeText(timeSource = FixedTimeSource) },
            ) {
                content()
            }
        }
    }

    public open fun testName(suffix: String): String =
        "src/test/screenshots/${this.javaClass.simpleName}_${device?.id ?: WearDevice.GenericLargeRound.id}$suffix.png"

    public companion object {
        internal const val PIXEL_COPY_RENDER_MODE = "robolectric.pixelCopyRenderMode"

        init {
            useHardwareRenderer()
        }

        public fun useHardwareRenderer() {
            System.setProperty(PIXEL_COPY_RENDER_MODE, "hardware")
        }

        @Composable
        public fun withImageLoader(
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
                CompositionLocalProvider(LocalImageLoader provides imageLoader) {
                    content()
                }
            }
        }

        @Composable
        public fun CorrectLayout(
            content: @Composable () -> Unit,
        ) {
            // TODO why needed
            val layoutDirection = when (LocalConfiguration.current.layoutDirection) {
                RTL -> LayoutDirection.Rtl
                else -> LayoutDirection.Ltr
            }
            CompositionLocalProvider(value = LocalLayoutDirection provides layoutDirection) {
                content()
            }
        }
    }
}
