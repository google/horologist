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

package com.google.android.horologist.compose.material;

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.AirplanemodeInactive
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.FavoriteBorder
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import org.junit.Test

class ToggleButtonTest : ScreenshotBaseTest() {

    @Test
    fun testDefaultWithIcon() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                onCheckedChange = {},
                icon = Icons.Filled.AirplanemodeActive
            )
        }
    }

    @Test
    fun testDefaultWithIcon2() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                checked = false,
                onCheckedChange = {},
                icon = Icons.Filled.AirplanemodeInactive
            )
        }
    }

    @Test
    fun testDefaultWithIcon3() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                checked = true,
                onCheckedChange = {},
                icon = Icons.Filled.AirplanemodeActive
            )
        }
    }

    @Test
    fun testDefaultWithText() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                onCheckedChange = {},
                text = "Mon"
            )
        }
    }

    @Test
    fun testDefaultWithLongText() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                onCheckedChange = {},
                text = "Monday is the first day of the week."
            )
        }
    }

    @Test
    fun testSmallWithIcon() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                variant = ToggleButtonVariants.Small,
                onCheckedChange = {},
                icon = Icons.Filled.VolumeOff
            )
        }
    }

    @Test
    fun testSmallWithIcon2() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                variant = ToggleButtonVariants.Small,
                checked = false,
                onCheckedChange = {},
                icon = Icons.Filled.VolumeUp
            )
        }
    }

    @Test
    fun testIconOnly1() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                variant = ToggleButtonVariants.IconOnly,
                onCheckedChange = {},
                icon = Icons.Filled.Favorite
            )
        }
    }

    @Test
    fun testIconOnly2() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                variant = ToggleButtonVariants.IconOnly,
                checked = false,
                onCheckedChange = {},
                icon = Icons.Outlined.FavoriteBorder
            )
        }
    }

    @Test
    fun testInvalidIcon() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                checked = true,
                onCheckedChange = {},
                text = "te",
                variant = ToggleButtonVariants.IconOnly
            )
        }
}
}

