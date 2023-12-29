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
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.images.base.util.rememberVectorPainter
import com.google.android.horologist.images.coil.CoilPaintable
import com.google.android.horologist.images.coil.FakeImageLoader
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import org.junit.Test

class OutlinedCompactChipTest : ScreenshotBaseTest() {
    @Test
    fun withPlaceholderIcon() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            // In inspection mode will jump to placeholder
            CompositionLocalProvider(LocalInspectionMode.provides(true)) {
                OutlinedCompactChip(
                    onClick = { },
                    label = "Primary label",
                    icon = CoilPaintable(
                        "iconUri",
                        placeholder = rememberVectorPainter(
                            image = Icons.Default.Image,
                            tintColor = MaterialTheme.colors.primary,
                        ),
                    ),
                )
            }
        }
    }

    @Test
    fun disabledWithIconPlaceholder() {
        screenshotTestRule.setContent(
            isComponent = true,
            takeScreenshot = true,
            fakeImageLoader = FakeImageLoader.Never,
        ) {
            // In inspection mode will jump to placeholder
            CompositionLocalProvider(LocalInspectionMode.provides(true)) {
                OutlinedCompactChip(
                    onClick = { },
                    label = "Primary label",
                    icon = CoilPaintable(
                        "iconUri",
                        placeholder = rememberVectorPainter(
                            image = Icons.Default.Image,
                            tintColor = MaterialTheme.colors.primary,
                        ),
                    ),
                    enabled = false,
                )
            }
        }
    }
}
