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

package com.google.android.horologist.media.ui.controls

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.android.horologist.media.ui.components.controls.SeekBackButton
import com.google.android.horologist.media.ui.components.controls.SeekButtonIncrement
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import com.google.android.horologist.screenshots.ScreenshotTestRule
import org.junit.Test

class SeekBackButtonA11yTest : ScreenshotBaseTest(
    ScreenshotTestRule.screenshotTestRuleParams {
        enableA11y = true
        screenTimeText = {}
    },
) {

    @Test
    fun incrementIsFive() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                SeekBackButton(
                    onClick = {},
                    seekButtonIncrement = SeekButtonIncrement.Five,
                )
            }
        }
    }

    @Test
    fun incrementIsTen() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                SeekBackButton(
                    onClick = {},
                    seekButtonIncrement = SeekButtonIncrement.Ten,
                )
            }
        }
    }

    @Test
    fun incrementIsThirty() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                SeekBackButton(
                    onClick = {},
                    seekButtonIncrement = SeekButtonIncrement.Thirty,
                )
            }
        }
    }

    @Test
    fun incrementIsOther() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                SeekBackButton(
                    onClick = {},
                    seekButtonIncrement = SeekButtonIncrement.Known(15),
                )
            }
        }
    }

    @Test
    fun incrementIsUnknown() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                SeekBackButton(
                    onClick = {},
                    seekButtonIncrement = SeekButtonIncrement.Unknown,
                )
            }
        }
    }
}
