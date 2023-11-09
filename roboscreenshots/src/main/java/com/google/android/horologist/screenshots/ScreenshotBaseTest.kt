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

package com.google.android.horologist.screenshots

import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.screenshots.ScreenshotTestRule.Companion.screenshotTestRuleParams
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowPixelCopy

/**
 * A test class that can be used as base class for tests that require a [ScreenshotTestRule].
 */
@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [33],
    qualifiers = "w227dp-h227dp-small-notlong-round-watch-xhdpi-keyshidden-nonav",
    shadows = [ShadowPixelCopy::class],
)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@ExperimentalHorologistApi
public abstract class ScreenshotBaseTest(
    params: ScreenshotTestRule.ScreenshotTestRuleParams = screenshotTestRuleParams { },
) {

    @get:Rule
    public val screenshotTestRule: ScreenshotTestRule = ScreenshotTestRule(params)

    internal companion object {
        internal const val USE_HARDWARE_RENDERER_NATIVE_ENV = "robolectric.screenshot.hwrdr.native"

        init {
            // Future looking, not in current release
            System.setProperty(USE_HARDWARE_RENDERER_NATIVE_ENV, "true")
        }
    }
}
