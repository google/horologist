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

package com.google.android.horologist.media.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.google.android.horologist.images.base.paintable.DrawableResPaintable
import com.google.android.horologist.images.base.paintable.Paintable
import com.google.android.horologist.logo.R
import com.google.android.horologist.media.ui.components.animated.MarqueeTextMediaDisplay
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import com.google.android.horologist.screenshots.ScreenshotTestRule
import org.junit.Test

class MarqueeTextMediaDisplayTest : ScreenshotBaseTest(
    ScreenshotTestRule.screenshotTestRuleParams {
        screenTimeText = {}
    },
) {
    @Test
    fun givenTitleAndArtist_thenTitleAndArtistAreDisplayed() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            MarqueeTextMediaDisplay(title = "Sorrow", artist = "David Bowie")
        }
    }

    @Test
    fun givenPaintableIcon_thenTintedIconIsDisplayed() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            MarqueeTextMediaDisplay(
                title = "Sorrow",
                artist = "David Bowie",
                titleIcon = DrawableResPaintable(R.drawable.ic_stat_horologist),
            )
        }
    }

    @Test
    fun givenPaintable_thenImageIsDisplayed() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            MarqueeTextMediaDisplay(
                title = "Sorrow",
                artist = "David Bowie",
                titleIcon = object : Paintable {
                    @Composable
                    override fun rememberPainter() = painterResource(id = R.drawable.horologist_logo)
                },
            )
        }
    }
}
