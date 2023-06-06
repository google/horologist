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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.materialPath
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import com.google.accompanist.testharness.TestHarness
import com.google.android.horologist.compose.material.util.rememberVectorPainter
import com.google.android.horologist.compose.tools.coil.FakeImageLoader
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import org.junit.Test

class ChipSecondaryTest : ScreenshotBaseTest() {

    @Test
    fun default() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Chip(
                label = "Primary label",
                onClick = { },
                chipType = ChipType.Secondary
            )
        }
    }

    @Test
    fun withSecondaryLabel() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Chip(
                label = "Primary label",
                onClick = { },
                secondaryLabel = "Secondary label",
                chipType = ChipType.Secondary
            )
        }
    }

    @Test
    fun withIcon() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Chip(
                label = "Primary label",
                onClick = { },
                icon = Icons.Default.Image,
                chipType = ChipType.Secondary
            )
        }
    }

    @Test
    fun withLargeIcon() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Chip(
                label = "Primary label",
                onClick = { },
                icon = Icon32dp,
                largeIcon = true,
                chipType = ChipType.Secondary
            )
        }
    }

    @Test
    fun withSecondaryLabelAndIcon() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Chip(
                label = "Primary label",
                onClick = { },
                secondaryLabel = "Secondary label",
                icon = Icons.Default.Image,
                chipType = ChipType.Secondary
            )
        }
    }

    @Test
    fun withSecondaryLabelAndLargeIcon() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Chip(
                label = "Primary label",
                onClick = { },
                secondaryLabel = "Secondary label",
                icon = Icon32dp,
                largeIcon = true,
                chipType = ChipType.Secondary
            )
        }
    }

    @Test
    fun disabled() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Chip(
                label = "Primary label",
                onClick = { },
                secondaryLabel = "Secondary label",
                icon = Icons.Default.Image,
                chipType = ChipType.Secondary,
                enabled = false
            )
        }
    }

    @Test
    fun withLongText() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Chip(
                label = "Primary label very very very very very very very very very very very very very very very very very long text",
                onClick = { },
                chipType = ChipType.Secondary
            )
        }
    }

    @Test
    fun withLongTextAndLargestFontScale() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            TestHarness(fontScale = largestFontScale) {
                Chip(
                    label = "Primary label very very very very very very very very very very very very very very very very very long text",
                    onClick = { },
                    chipType = ChipType.Secondary
                )
            }
        }
    }

    @Test
    fun withSecondaryLabelAndLongText() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Chip(
                label = "Primary label very very very very very very very very long text",
                onClick = { },
                secondaryLabel = "Secondary label very very very very very very very very very long text",
                icon = Icons.Default.Image,
                chipType = ChipType.Secondary
            )
        }
    }

    @Test
    fun withSecondaryLabelAndLongTextAndLargestFontScale() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            TestHarness(fontScale = largestFontScale) {
                Chip(
                    label = "Primary label very very very very very very very very long text",
                    onClick = { },
                    secondaryLabel = "Secondary label very very very very very very very very very long text",
                    icon = Icons.Default.Image,
                    chipType = ChipType.Secondary
                )
            }
        }
    }

    @Test
    fun usingSmallIcon() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Chip(
                label = "Primary label",
                onClick = { },
                icon = Icon12dp,
                chipType = ChipType.Secondary
            )
        }
    }

    @Test
    fun withLargeIconUsingSmallIcon() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Chip(
                label = "Primary label",
                onClick = { },
                icon = Icon12dp,
                largeIcon = true,
                chipType = ChipType.Secondary
            )
        }
    }

    @Test
    fun usingExtraLargeIcon() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Chip(
                label = "Primary label",
                onClick = { },
                icon = Icon48dp,
                chipType = ChipType.Secondary
            )
        }
    }

    @Test
    fun withLargeIconUsingExtraLargeIcon() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Chip(
                label = "Primary label",
                onClick = { },
                icon = Icon48dp,
                largeIcon = true,
                chipType = ChipType.Secondary
            )
        }
    }

    @Test
    fun withPlaceholderIcon() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Chip(
                label = "Primary label",
                onClick = { },
                icon = Icons.Default.Image,
                chipType = ChipType.Secondary
            )
        }
    }

    @Test
    fun withProgressIcon() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Chip(
                label = "Primary label",
                onClick = { },
                icon = {
                    ChipIconWithProgress(
                        progress = 75f,
                        icon = Icon48dp,
                        largeIcon = true
                    )
                },
                chipType = ChipType.Secondary
            )
        }
    }

    @Test
    fun withSquareIcon() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            // This was made to showcase that the icon can be any composable in this version of
            // Chip.
            Chip(
                label = "Primary label",
                onClick = { },
                icon = {
                    Box(
                        Modifier
                            .size(32.dp)
                            .background(Color.White)
                    )
                },
                chipType = ChipType.Secondary
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
                Chip(
                    label = "Primary label",
                    onClick = { },
                    secondaryLabel = "Secondary label",
                    icon = "iconUri",
                    placeholder = rememberVectorPainter(
                        image = Icons.Default.Image,
                        tintColor = Color.Black
                    ),
                    chipType = ChipType.Secondary,
                    enabled = false
                )
            }
        }
    }

    companion object {
        private const val largestFontScale = 1.18f

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

        private val Icon48dp: ImageVector
            get() = ImageVector.Builder(
                name = "Icon Extra Large",
                defaultWidth = 48f.dp,
                defaultHeight = 48f.dp,
                viewportWidth = 48f,
                viewportHeight = 48f
            )
                .materialPath {
                    horizontalLineToRelative(48.0f)
                    verticalLineToRelative(48.0f)
                    horizontalLineTo(0.0f)
                    close()
                }
                .build()
    }
}
