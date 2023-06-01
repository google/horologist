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

import androidx.compose.ui.unit.LayoutDirection
import com.google.accompanist.testharness.TestHarness
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import org.junit.Test

class SplitToggleChipTest : ScreenshotBaseTest() {

    @Test
    fun switch() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            SplitToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = "Primary label",
                onClick = { },
                toggleControl = ToggleChipToggleControl.Switch
            )
        }
    }

    @Test
    fun radio() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            SplitToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = "Primary label",
                onClick = { },
                toggleControl = ToggleChipToggleControl.Radio
            )
        }
    }

    @Test
    fun checkbox() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            SplitToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = "Primary label",
                onClick = { },
                toggleControl = ToggleChipToggleControl.Checkbox
            )
        }
    }

    @Test
    fun unchecked() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            SplitToggleChip(
                checked = false,
                onCheckedChanged = { },
                label = "Primary label",
                onClick = { },
                toggleControl = ToggleChipToggleControl.Switch
            )
        }
    }

    @Test
    fun withSecondaryLabel() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            SplitToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = "Primary label",
                onClick = { },
                toggleControl = ToggleChipToggleControl.Switch,
                secondaryLabel = "Secondary label"
            )
        }
    }

    @Test
    fun disabled() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            SplitToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = "Primary label",
                onClick = { },
                toggleControl = ToggleChipToggleControl.Switch,
                enabled = false
            )
        }
    }

    @Test
    fun uncheckedAndDisabled() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            SplitToggleChip(
                checked = false,
                onCheckedChanged = { },
                label = "Primary label",
                onClick = { },
                toggleControl = ToggleChipToggleControl.Switch,
                enabled = false
            )
        }
    }

    @Test
    fun withLongText() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            SplitToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = "Primary label very very very very very very very very very very very very very very very very very long text",
                onClick = { },
                toggleControl = ToggleChipToggleControl.Switch
            )
        }
    }

    @Test
    fun withLongTextAndLargestFontScale() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            TestHarness(fontScale = largestFontScale) {
                SplitToggleChip(
                    checked = true,
                    onCheckedChanged = { },
                    label = "Primary label very very very very very very very very very very very very very very very very very long text",
                    onClick = { },
                    toggleControl = ToggleChipToggleControl.Switch
                )
            }
        }
    }

    @Test
    fun withSecondaryLabelAndLongText() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            SplitToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = "Primary label very very very very very very very very long text",
                onClick = { },
                toggleControl = ToggleChipToggleControl.Switch,
                secondaryLabel = "Secondary label very very very very very very very very very long text"
            )
        }
    }

    @Test
    fun withSecondaryLabelAndLongTextAndLargestFontScale() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            TestHarness(fontScale = largestFontScale) {
                SplitToggleChip(
                    checked = true,
                    onCheckedChanged = { },
                    label = "Primary label very very very very very very very very long text",
                    onClick = { },
                    toggleControl = ToggleChipToggleControl.Switch,
                    secondaryLabel = "Secondary label very very very very very very very very very long text"
                )
            }
        }
    }

    @Test
    fun rtl() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            TestHarness(layoutDirection = LayoutDirection.Rtl) {
                SplitToggleChip(
                    checked = true,
                    onCheckedChanged = { },
                    label = "Primary label",
                    onClick = { },
                    toggleControl = ToggleChipToggleControl.Switch,
                    secondaryLabel = "Secondary label"
                )
            }
        }
    }

    companion object {
        private const val largestFontScale = 1.18f
    }
}
