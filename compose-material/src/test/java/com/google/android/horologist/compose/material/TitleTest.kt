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

package com.google.android.horologist.compose.material

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.ui.graphics.Color
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import com.google.android.horologist.screenshots.ScreenshotTestRule
import org.junit.Test

class TitleTest : ScreenshotBaseTest(ScreenshotTestRule.screenshotTestRuleParams { record = true }) {

    @Test
    fun defaultPrimary() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Title(
                text = "Title",
                textType = TextType.Primary
            )
        }
    }

    @Test
    fun primaryWithVeryLongText() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Title(
                text = "Title with a very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very long text",
                textType = TextType.Primary
            )
        }
    }

    @Test
    fun defaultSecondary() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Title(
                text = "Title",
                textType = TextType.Secondary
            )
        }
    }

    @Test
    fun secondaryWithVeryLongText() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Title(
                text = "Title with a very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very long text",
                textType = TextType.Secondary
            )
        }
    }

    @Test
    fun defaultSecondaryWithIcon() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Title(
                text = "Title",
                textType = TextType.Secondary,
                icon = Icons.Outlined.MusicNote,
                iconTint = Color(0xFF946EB1)
            )
        }
    }

    @Test
    fun secondaryWithIconAndVeryLongText() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Title(
                text = "Title with a very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very long text",
                textType = TextType.Secondary,
                icon = Icons.Outlined.MusicNote,
                iconTint = Color(0xFF946EB1)
            )
        }
    }
}
