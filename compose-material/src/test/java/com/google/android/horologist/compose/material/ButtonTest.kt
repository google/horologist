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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.ui.unit.LayoutDirection
import androidx.wear.compose.material.ButtonDefaults
import com.google.accompanist.testharness.TestHarness
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import org.junit.Test

internal class ButtonTest : ScreenshotBaseTest() {

    @Test
    fun default() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Button(
                imageVector = Icons.Default.Check,
                contentDescription = "contentDescription",
                onClick = { }
            )
        }
    }

    @Test
    fun disabled() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Button(
                imageVector = Icons.Default.Check,
                contentDescription = "contentDescription",
                onClick = { },
                enabled = false
            )
        }
    }

    @Test
    fun large() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Button(
                imageVector = Icons.Default.Check,
                contentDescription = "contentDescription",
                onClick = { },
                buttonSize = ButtonSize.Large
            )
        }
    }

    @Test
    fun small() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Button(
                imageVector = Icons.Default.Check,
                contentDescription = "contentDescription",
                onClick = { },
                buttonSize = ButtonSize.Small
            )
        }
    }

    @Test
    fun withSecondaryButtonColors() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Button(
                imageVector = Icons.Default.Check,
                contentDescription = "contentDescription",
                onClick = { },
                colors = ButtonDefaults.secondaryButtonColors()
            )
        }
    }

    @Test
    fun withIconButtonColors() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Button(
                imageVector = Icons.Default.Check,
                contentDescription = "contentDescription",
                onClick = { },
                colors = ButtonDefaults.iconButtonColors()
            )
        }
    }

    @Test
    fun usingDrawableResAsIcon() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Button(
                id = android.R.drawable.ic_media_play,
                contentDescription = "contentDescription",
                onClick = { }
            )
        }
    }

    @Test
    fun usingDrawableResAsIconRtl() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            TestHarness(layoutDirection = LayoutDirection.Rtl) {
                Button(
                    id = android.R.drawable.ic_media_play,
                    contentDescription = "contentDescription",
                    onClick = { }
                )
            }
        }
    }

    @Test
    fun usingDrawableResAsIconMirrored() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Button(
                id = android.R.drawable.ic_media_play,
                contentDescription = "contentDescription",
                onClick = { },
                iconRtlMode = IconRtlMode.Mirrored
            )
        }
    }

    @Test
    fun usingDrawableResAsIconMirroredRtl() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            TestHarness(layoutDirection = LayoutDirection.Rtl) {
                Button(
                    id = android.R.drawable.ic_media_play,
                    contentDescription = "contentDescription",
                    onClick = { },
                    iconRtlMode = IconRtlMode.Mirrored
                )
            }
        }
    }

    @Test
    fun defaultRtl() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            TestHarness(layoutDirection = LayoutDirection.Rtl) {
                Button(
                    imageVector = Icons.Default.Check,
                    contentDescription = "contentDescription",
                    onClick = { }
                )
            }
        }
    }

    @Test
    fun mirrored() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Button(
                imageVector = Icons.Default.DirectionsBike,
                contentDescription = "contentDescription",
                onClick = { },
                iconRtlMode = IconRtlMode.Mirrored
            )
        }
    }

    @Test
    fun mirroredRtl() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            TestHarness(layoutDirection = LayoutDirection.Rtl) {
                Button(
                    imageVector = Icons.Default.DirectionsBike,
                    contentDescription = "contentDescription",
                    onClick = { },
                    iconRtlMode = IconRtlMode.Mirrored
                )
            }
        }
    }
}
