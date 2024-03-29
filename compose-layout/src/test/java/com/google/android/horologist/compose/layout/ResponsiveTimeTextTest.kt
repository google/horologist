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

package com.google.android.horologist.compose.layout

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.TimeSource
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import org.junit.Test

class ResponsiveTimeTextTest : ScreenshotBaseTest() {
    @Test
    fun defaultTimeText() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ResponsiveTimeText(
                timeSource = object : TimeSource {
                    override val currentTime: String
                        @Composable get() = "10:10"
                },
            )
        }
    }
}
