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

import android.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.LayoutDirection
import androidx.wear.compose.material.MaterialTheme
import com.google.accompanist.testharness.TestHarness
import com.google.android.horologist.compose.material.util.rememberVectorPainter
import com.google.android.horologist.compose.tools.coil.FakeImageLoader
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import org.junit.Test

class OutlinedCompactChipTest : ScreenshotBaseTest() {

    @Test
    fun default() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            OutlinedCompactChip(
                label = "Primary label",
                onClick = { }
            )
        }
    }

    @Test
    fun withIcon() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            OutlinedCompactChip(
                label = "Primary label",
                onClick = { },
                icon = Icons.Filled.Add
            )
        }
    }

    @Test
    fun iconOnly() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            OutlinedCompactChip(
                onClick = { },
                icon = Icons.Filled.Add
            )
        }
    }

    @Test
    fun disabled() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            OutlinedCompactChip(
                label = "Primary label",
                onClick = { },
                icon = Icons.Filled.Add,
                enabled = false
            )
        }
    }

    @Test
    fun withLongText() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            OutlinedCompactChip(
                label = "Primary label very very very very very very very very very very very very very very very very very long text",
                onClick = { }
            )
        }
    }

    @Test
    fun withLongTextAndLargestFontScale() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            TestHarness(fontScale = largestFontScale) {
                OutlinedCompactChip(
                    label = "Primary label very very very very very very very very very very very very very very very very very long text",
                    onClick = { }
                )
            }
        }
    }

    @Test
    fun usingDrawableResAsIcon() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            OutlinedCompactChip(
                label = "Primary label",
                onClick = { },
                icon = R.drawable.ic_delete
            )
        }
    }

    @Test
    fun withPlaceholderIcon() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            OutlinedCompactChip(
                label = "Primary label",
                onClick = { },
                icon = Icons.Default.Image,
                placeholder = rememberVectorPainter(
                    image = Icons.Default.Image,
                    tintColor = MaterialTheme.colors.primary
                )
            )
        }
    }

    @Test
    fun disabledWithIconPlaceholder() {
        screenshotTestRule.setContent(
            isComponent = true,
            takeScreenshot = true,
            fakeImageLoader = FakeImageLoader.Never
        ) {
            // In inspection mode will jump to placeholder
            CompositionLocalProvider(LocalInspectionMode.provides(true)) {
                OutlinedCompactChip(
                    label = "Primary label",
                    onClick = { },
                    icon = "iconUri",
                    placeholder = rememberVectorPainter(
                        image = Icons.Default.Image,
                        tintColor = MaterialTheme.colors.primary
                    ),
                    enabled = false
                )
            }
        }
    }

    @Test
    fun defaultRtl() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            TestHarness(layoutDirection = LayoutDirection.Rtl) {
                OutlinedCompactChip(
                    label = "Primary label",
                    onClick = { },
                    icon = Icons.Default.Image
                )
            }
        }
    }

    @Test
    fun mirroredRtlDefault() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            TestHarness(layoutDirection = LayoutDirection.Rtl) {
                OutlinedCompactChip(
                    label = "Primary label",
                    onClick = { },
                    icon = Icons.Default.Image,
                    iconRtlMode = IconRtlMode.Mirrored
                )
            }
        }
    }

    companion object {
        private const val largestFontScale = 1.18f
    }
}
