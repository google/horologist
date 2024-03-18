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

@file:OptIn(ExperimentalRoborazziApi::class)

package com.google.android.horologist.screenshots.rng

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.ThresholdValidator
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@Config(
    sdk = [33],
    qualifiers = "w227dp-h227dp-small-notlong-round-watch-xhdpi-keyshidden-nonav",
)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public abstract class WearScreenshotTest {
    @get:Rule
    public val composeRule: ComposeContentTestRule = createComposeRule()

    public abstract val device: WearDevice

    // Allow for individual tolerances to be set on each test, should be between 0.0 and 1.0
    public open val tolerance: Float = 0.0f

    public open fun runTest(content: @Composable () -> Unit) {
        RuntimeEnvironment.setQualifiers("+w${device.dp}dp-h${device.dp}dp")
        RuntimeEnvironment.setFontScale(device.fontScale)

        composeRule.setContent {
            content()
        }

        val suffix = ""

        captureScreenshot(suffix)
    }

    public fun captureScreenshot(suffix: String) {
        composeRule.onRoot().captureRoboImage(
            filePath = "src/test/screenshots/${this.javaClass.simpleName}_${device.id}$suffix.png",
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
}
