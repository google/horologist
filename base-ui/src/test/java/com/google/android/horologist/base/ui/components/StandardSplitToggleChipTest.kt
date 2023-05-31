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

package com.google.android.horologist.base.ui.components

import com.google.accompanist.testharness.TestHarness
import com.google.android.horologist.base.ui.common.StandardToggleChipToggleControl
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import org.junit.Test

class StandardSplitToggleChipTest : ScreenshotBaseTest() {

    @Test
    fun switch() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            StandardSplitToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = "Primary label",
                onClick = { },
                toggleControl = StandardToggleChipToggleControl.Switch
            )
        }
    }

    @Test
    fun radio() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            StandardSplitToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = "Primary label",
                onClick = { },
                toggleControl = StandardToggleChipToggleControl.Radio
            )
        }
    }

    @Test
    fun checkbox() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            StandardSplitToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = "Primary label",
                onClick = { },
                toggleControl = StandardToggleChipToggleControl.Checkbox
            )
        }
    }

    @Test
    fun unchecked() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            StandardSplitToggleChip(
                checked = false,
                onCheckedChanged = { },
                label = "Primary label",
                onClick = { },
                toggleControl = StandardToggleChipToggleControl.Switch
            )
        }
    }

    @Test
    fun withSecondaryLabel() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            StandardSplitToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = "Primary label",
                onClick = { },
                toggleControl = StandardToggleChipToggleControl.Switch,
                secondaryLabel = "Secondary label"
            )
        }
    }

    @Test
    fun disabled() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            StandardSplitToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = "Primary label",
                onClick = { },
                toggleControl = StandardToggleChipToggleControl.Switch,
                enabled = false
            )
        }
    }

    @Test
    fun uncheckedAndDisabled() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            StandardSplitToggleChip(
                checked = false,
                onCheckedChanged = { },
                label = "Primary label",
                onClick = { },
                toggleControl = StandardToggleChipToggleControl.Switch,
                enabled = false
            )
        }
    }

    @Test
    fun withLongText() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            StandardSplitToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = "Primary label very very very very very very very very very very very very very very very very very long text",
                onClick = { },
                toggleControl = StandardToggleChipToggleControl.Switch
            )
        }
    }

    @Test
    fun withLongTextAndLargestFontScale() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            TestHarness(fontScale = largestFontScale) {
                StandardSplitToggleChip(
                    checked = true,
                    onCheckedChanged = { },
                    label = "Primary label very very very very very very very very very very very very very very very very very long text",
                    onClick = { },
                    toggleControl = StandardToggleChipToggleControl.Switch
                )
            }
        }
    }

    @Test
    fun withSecondaryLabelAndLongText() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            StandardSplitToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = "Primary label very very very very very very very very long text",
                onClick = { },
                toggleControl = StandardToggleChipToggleControl.Switch,
                secondaryLabel = "Secondary label very very very very very very very very very long text"
            )
        }
    }

    @Test
    fun withSecondaryLabelAndLongTextAndLargestFontScale() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            TestHarness(fontScale = largestFontScale) {
                StandardSplitToggleChip(
                    checked = true,
                    onCheckedChanged = { },
                    label = "Primary label very very very very very very very very long text",
                    onClick = { },
                    toggleControl = StandardToggleChipToggleControl.Switch,
                    secondaryLabel = "Secondary label very very very very very very very very very long text"
                )
            }
        }
    }

    companion object {
        private const val largestFontScale = 1.18f
    }
}
