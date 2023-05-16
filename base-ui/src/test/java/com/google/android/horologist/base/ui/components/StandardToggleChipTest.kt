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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import org.junit.Test

class StandardToggleChipTest : ScreenshotBaseTest() {

    @Test
    fun switch() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            StandardToggleChip(
                checked = true,
                onCheckedChange = { },
                label = "Primary label",
                toggleControl = StandardToggleChipToggleControl.Switch
            )
        }
    }

    @Test
    fun radio() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            StandardToggleChip(
                checked = true,
                onCheckedChange = { },
                label = "Primary label",
                toggleControl = StandardToggleChipToggleControl.Radio
            )
        }
    }

    @Test
    fun checkbox() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            StandardToggleChip(
                checked = true,
                onCheckedChange = { },
                label = "Primary label",
                toggleControl = StandardToggleChipToggleControl.Checkbox
            )
        }
    }

    @Test
    fun withSecondaryLabel() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            StandardToggleChip(
                checked = true,
                onCheckedChange = { },
                label = "Primary label",
                toggleControl = StandardToggleChipToggleControl.Switch,
                secondaryLabel = "Secondary label"
            )
        }
    }

    @Test
    fun withIcon() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            StandardToggleChip(
                checked = true,
                onCheckedChange = { },
                label = "Primary label",
                toggleControl = StandardToggleChipToggleControl.Switch,
                icon = Icons.Default.Image
            )
        }
    }

    @Test
    fun withSecondaryLabelAndIcon() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            StandardToggleChip(
                checked = true,
                onCheckedChange = { },
                label = "Primary label",
                toggleControl = StandardToggleChipToggleControl.Switch,
                secondaryLabel = "Secondary label"
            )
        }
    }

    @Test
    fun disabled() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            StandardToggleChip(
                checked = true,
                onCheckedChange = { },
                label = "Primary label",
                toggleControl = StandardToggleChipToggleControl.Switch,
                enabled = false
            )
        }
    }

    @Test
    fun withLongText() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            StandardToggleChip(
                checked = true,
                onCheckedChange = { },
                label = "Primary label very very very very very very very very very very very very very very very very very long text",
                toggleControl = StandardToggleChipToggleControl.Switch
            )
        }
    }

    @Test
    fun withIconAndLongText() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            StandardToggleChip(
                checked = true,
                onCheckedChange = { },
                label = "Primary label very very very very very very very very very very very very very very very very very long text",
                toggleControl = StandardToggleChipToggleControl.Switch,
                icon = Icons.Default.Image
            )
        }
    }

    @Test
    fun withSecondaryLabelAndLongText() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            StandardToggleChip(
                checked = true,
                onCheckedChange = { },
                label = "Primary label very very very very very very very very long text",
                secondaryLabel = "Secondary label very very very very very very very very very long text",
                toggleControl = StandardToggleChipToggleControl.Switch
            )
        }
    }

    @Test
    fun withIconAndSecondaryLabelAndLongText() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            StandardToggleChip(
                checked = true,
                onCheckedChange = { },
                label = "Primary label very very very very very very very very long text",
                secondaryLabel = "Secondary label very very very very very very very very very long text",
                toggleControl = StandardToggleChipToggleControl.Switch,
                icon = Icons.Default.Image
            )
        }
    }

    @Test
    fun usingSmallIcon() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            StandardToggleChip(
                checked = true,
                onCheckedChange = { },
                label = "Primary label",
                toggleControl = StandardToggleChipToggleControl.Switch,
                icon = Icon12dp
            )
        }
    }

    @Test
    fun usingLargeIcon() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            StandardToggleChip(
                checked = true,
                onCheckedChange = { },
                label = "Primary label",
                toggleControl = StandardToggleChipToggleControl.Switch,
                icon = Icon32dp
            )
        }
    }

    companion object {

        private val Icon12dp: ImageVector
            get() = ImageVector.Builder(
                name = "Icon Small",
                defaultWidth = 12f.dp,
                defaultHeight = 12f.dp,
                viewportWidth = 12f,
                viewportHeight = 12f
            )
                .materialPath {
                    horizontalLineToRelative(12.0f)
                    verticalLineToRelative(12.0f)
                    horizontalLineTo(0.0f)
                    close()
                }
                .build()

        private val Icon32dp: ImageVector
            get() = ImageVector.Builder(
                name = "Icon Large",
                defaultWidth = 32f.dp,
                defaultHeight = 32f.dp,
                viewportWidth = 32f,
                viewportHeight = 32f
            )
                .materialPath {
                    horizontalLineToRelative(32.0f)
                    verticalLineToRelative(32.0f)
                    horizontalLineTo(0.0f)
                    close()
                }
                .build()
    }
}
