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

package com.google.android.horologist.media.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.google.android.horologist.media.ui.components.animated.MarqueeTextMediaDisplay
import com.google.android.horologist.media.ui.components.display.LoadingMediaDisplay
import com.google.android.horologist.media.ui.components.display.TextMediaDisplay
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import com.google.android.horologist.screenshots.ScreenshotTestRule
import org.junit.Test

class LoadingMediaDisplayTest: ScreenshotBaseTest(ScreenshotTestRule.screenshotTestRuleParams {
    screenTimeText = {}
}) {
    @Test
    fun default() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            LoadingMediaDisplay()
        }
    }

    @Test
    fun loadingMediaDisplay_textMediaDisplay_overlay() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            LoadingMediaDisplay(modifier = Modifier.alpha(0.5f))
            TextMediaDisplay(title = "Sorrow", subtitle = "David Bowie")
        }
    }

    @Test
    fun loadingMediaDisplay_marqueeTextMediaDisplay_overlay() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            LoadingMediaDisplay(modifier = Modifier.alpha(0.5f))
            MarqueeTextMediaDisplay(title = "Sorrow", artist = "David Bowie")
        }
    }
}