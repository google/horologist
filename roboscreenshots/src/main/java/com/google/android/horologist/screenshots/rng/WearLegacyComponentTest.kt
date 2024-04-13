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
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import coil.annotation.ExperimentalCoilApi
import coil.test.FakeImageLoaderEngine
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.ThresholdValidator
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.android.horologist.screenshots.rng.WearScreenshotTest.Companion.useHardwareRenderer
import com.google.android.horologist.screenshots.rng.WearScreenshotTest.Companion.withDrawingEnabled
import com.google.android.horologist.screenshots.rng.WearScreenshotTest.Companion.withImageLoader
import org.junit.Rule
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
public abstract class WearLegacyComponentTest {

    @get:Rule
    public val testInfo: TestName = TestName()

    public open fun testName(suffix: String): String =
        "src/test/snapshots/images/" +
            "${this.javaClass.`package`?.name}_${this.javaClass.simpleName}_" +
            "${testInfo.methodName}$suffix.png"

    public open val device: WearDevice? = null

    // Allow for individual tolerances to be set on each test, should be between 0.0 and 1.0
    public open val tolerance: Float = 0.0f

    public open val imageLoader: FakeImageLoaderEngine? = null

    open val forceHardware: Boolean = false

    public fun runComponentTest(
        background: Color? = if (forceHardware) Color.Black.copy(alpha = 0.3f) else null,
        content: @Composable () -> Unit,
    ) {
        withDrawingEnabled(forceHardware) {
            device?.let {
                RuntimeEnvironment.setQualifiers("+w${it.dp}dp-h${it.dp}dp")
                RuntimeEnvironment.setFontScale(it.fontScale)
            }
            captureRoboImage(
                filePath = testName(""),
                roborazziOptions = RoborazziOptions(
                    recordOptions = RoborazziOptions.RecordOptions(
                        applyDeviceCrop = false,
                    ),
                    compareOptions = RoborazziOptions.CompareOptions(
                        resultValidator = ThresholdValidator(tolerance),
                    ),
                ),
            ) {
                withImageLoader(imageLoader) {
                    Box(
                        modifier = Modifier.run {
                            if (background != null) {
                                background(background)
                            } else {
                                this
                            }
                        },
                    ) {
                        ComponentScaffold {
                            content()
                        }
                    }
                }
            }
        }
    }

    @Composable
    public open fun ComponentScaffold(content: @Composable () -> Unit) {
        content()
    }

    internal companion object {
        init {
            useHardwareRenderer()
        }
    }
}
