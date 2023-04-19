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

package com.google.android.horologist.auth.composables.chips

import com.google.android.horologist.base.ui.components.StandardChipType
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import org.junit.Test

class CreateAccountChipTest : ScreenshotBaseTest() {

    @Test
    fun default() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            CreateAccountChip(onClick = {})
        }
    }

    @Test
    fun disabled() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            CreateAccountChip(
                onClick = {},
                enabled = false
            )
        }
    }

    @Test
    fun withSecondaryChipType() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            CreateAccountChip(
                onClick = {},
                chipType = StandardChipType.Secondary
            )
        }
    }

    @Test
    fun withSecondaryChipTypeDisabled() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            CreateAccountChip(
                onClick = {},
                chipType = StandardChipType.Secondary,
                enabled = false
            )
        }
    }
}
