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

package com.google.android.horologist.compose.material

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.AirplanemodeInactive
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.ui.unit.LayoutDirection
import com.google.accompanist.testharness.TestHarness
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import org.junit.Test

class ToggleButtonTest : ScreenshotBaseTest() {

    @Test
    fun default() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive,
                notCheckedIcon = Icons.Filled.AirplanemodeInactive,
                contentDescription = "contentDescription",
                onCheckedChanged = {}
            )
        }
    }

    @Test
    fun notChecked() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive,
                notCheckedIcon = Icons.Filled.AirplanemodeInactive,
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                checked = false
            )
        }
    }

    @Test
    fun disabled() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive,
                notCheckedIcon = Icons.Filled.AirplanemodeInactive,
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                enabled = false
            )
        }
    }

    @Test
    fun notCheckedDisabled() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive,
                notCheckedIcon = Icons.Filled.AirplanemodeInactive,
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                checked = false,
                enabled = false
            )
        }
    }

    @Test
    fun text() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                text = "Monday",
                onCheckedChanged = {}
            )
        }
    }

    @Test
    fun textNotChecked() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                text = "Monday",
                onCheckedChanged = {},
                checked = false
            )
        }
    }

    @Test
    fun textDisabled() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                text = "Monday",
                onCheckedChanged = {},
                enabled = false
            )
        }
    }

    @Test
    fun textNotCheckedDisabled() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                text = "Monday",
                onCheckedChanged = {},
                checked = false,
                enabled = false
            )
        }
    }

    @Test
    fun small() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive,
                notCheckedIcon = Icons.Filled.AirplanemodeInactive,
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                smallSize = true
            )
        }
    }

    @Test
    fun smallNotChecked() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive,
                notCheckedIcon = Icons.Filled.AirplanemodeInactive,
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                checked = false,
                smallSize = true
            )
        }
    }

    @Test
    fun smallDisabled() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive,
                notCheckedIcon = Icons.Filled.AirplanemodeInactive,
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                enabled = false,
                smallSize = true
            )
        }
    }

    fun smallNotCheckedDisabled() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive,
                notCheckedIcon = Icons.Filled.AirplanemodeInactive,
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                checked = false,
                enabled = false,
                smallSize = true
            )
        }
    }

    fun iconOnly() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive,
                notCheckedIcon = Icons.Filled.AirplanemodeInactive,
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                colors = ToggleButtonDefaults.iconOnlyColors(),
                smallSize = true
            )
        }
    }

    fun iconOnlyNotChecked() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive,
                notCheckedIcon = Icons.Filled.AirplanemodeInactive,
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                checked = false,
                colors = ToggleButtonDefaults.iconOnlyColors(),
                smallSize = true
            )
        }
    }

    fun iconOnlyDisabled() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive,
                notCheckedIcon = Icons.Filled.AirplanemodeInactive,
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                enabled = false,
                colors = ToggleButtonDefaults.iconOnlyColors(),
                smallSize = true
            )
        }
    }

    fun iconOnlyNotCheckedDisabled() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive,
                notCheckedIcon = Icons.Filled.AirplanemodeInactive,
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                checked = false,
                enabled = false,
                colors = ToggleButtonDefaults.iconOnlyColors(),
                smallSize = true
            )
        }
    }

    @Test
    fun rtl() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            TestHarness(layoutDirection = LayoutDirection.Rtl) {
                ToggleButton(
                    checkedIcon = Icons.Filled.VolumeUp,
                    notCheckedIcon = Icons.Filled.VolumeOff,
                    contentDescription = "contentDescription",
                    onCheckedChanged = {}
                )
            }
        }
    }

    @Test
    fun mirrored() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            ToggleButton(
                checkedIcon = Icons.Filled.VolumeUp,
                notCheckedIcon = Icons.Filled.VolumeOff,
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                iconRtlMode = IconRtlMode.Mirrored
            )
        }
    }

    @Test
    fun mirroredRtl() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            TestHarness(layoutDirection = LayoutDirection.Rtl) {
                ToggleButton(
                    checkedIcon = Icons.Filled.VolumeUp,
                    notCheckedIcon = Icons.Filled.VolumeOff,
                    contentDescription = "contentDescription",
                    onCheckedChanged = {},
                    iconRtlMode = IconRtlMode.Mirrored
                )
            }
        }
    }

    @Test
    fun textWithLargestFontScale() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            TestHarness(fontScale = largestFontScale) {
                ToggleButton(
                    text = "Monday",
                    onCheckedChanged = {}
                )
            }
        }
    }

    companion object {

        private const val largestFontScale = 1.18f
    }
}
