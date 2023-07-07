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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.AirplanemodeInactive
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import com.google.android.horologist.screenshots.ScreenshotTestRule
import org.junit.Test

class ToggleButtonA11yTest :
    ScreenshotBaseTest(
        ScreenshotTestRule.screenshotTestRuleParams {
            enableA11y = true
            screenTimeText = {}
        }
    ) {

    @Test
    fun default() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                ToggleButton(
                    checkedIcon = Icons.Filled.AirplanemodeActive,
                    notCheckedIcon = Icons.Filled.AirplanemodeInactive,
                    contentDescription = "contentDescription",
                    onCheckedChanged = {}
                )
            }
        }
    }

    @Test
    fun notChecked() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                ToggleButton(
                    checkedIcon = Icons.Filled.AirplanemodeActive,
                    notCheckedIcon = Icons.Filled.AirplanemodeInactive,
                    contentDescription = "contentDescription",
                    onCheckedChanged = {},
                    checked = false
                )
            }
        }
    }

    @Test
    fun disabled() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                ToggleButton(
                    checkedIcon = Icons.Filled.AirplanemodeActive,
                    notCheckedIcon = Icons.Filled.AirplanemodeInactive,
                    contentDescription = "contentDescription",
                    onCheckedChanged = {},
                    enabled = false
                )
            }
        }
    }

    @Test
    fun notCheckedDisabled() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
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
    }

    @Test
    fun text() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                ToggleButton(
                    text = "Monday",
                    onCheckedChanged = {}
                )
            }
        }
    }

    @Test
    fun iconOnly() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
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
    }
}
