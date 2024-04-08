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

@file:Suppress("DEPRECATION")

package com.google.android.horologist.compose.material

import android.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.materialPath
import androidx.compose.material.icons.outlined.VolumeDown
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ChipDefaults
import com.google.accompanist.testharness.TestHarness
import com.google.android.horologist.images.base.paintable.DrawableResPaintable
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable.Companion.asPaintable
import com.google.android.horologist.screenshots.rng.WearLegacyComponentTest
import org.junit.Test

class ChipTest : WearLegacyComponentTest() {

    @Test
    fun default() {
        runComponentTest {
            Chip(
                label = "Primary label",
                onClick = { },
            )
        }
    }

    @Test
    fun withSecondaryLabel() {
        runComponentTest {
            Chip(
                label = "Primary label",
                onClick = { },
                secondaryLabel = "Secondary label",
            )
        }
    }

    @Test
    fun withIcon() {
        runComponentTest {
            Chip(
                label = "Primary label",
                onClick = { },
                icon = Icons.Default.Image.asPaintable(),
            )
        }
    }

    @Test
    fun withLargeIcon() {
        runComponentTest {
            Chip(
                label = "Primary label",
                onClick = { },
                icon = Icon32dp.asPaintable(),
                largeIcon = true,
            )
        }
    }

    @Test
    fun withSecondaryLabelAndIcon() {
        runComponentTest {
            Chip(
                label = "Primary label",
                onClick = { },
                secondaryLabel = "Secondary label",
                icon = Icons.Default.Image.asPaintable(),
            )
        }
    }

    @Test
    fun withSecondaryLabelAndLargeIcon() {
        runComponentTest {
            Chip(
                label = "Primary label",
                onClick = { },
                secondaryLabel = "Secondary label",
                icon = Icon32dp.asPaintable(),
                largeIcon = true,
            )
        }
    }

    @Test
    fun disabled() {
        runComponentTest {
            Chip(
                label = "Primary label",
                onClick = { },
                secondaryLabel = "Secondary label",
                icon = Icons.Default.Image.asPaintable(),
                enabled = false,
            )
        }
    }

    @Test
    fun withLongText() {
        runComponentTest {
            Chip(
                label = "Primary label very very very very very very very very very very very very very very very very very long text",
                onClick = { },
            )
        }
    }

    @Test
    fun withLongTextAndLargestFontScale() {
        runComponentTest {
            TestHarness(fontScale = largestFontScale) {
                Chip(
                    label = "Primary label very very very very very very very very very very very very very very very very very long text",
                    onClick = { },
                )
            }
        }
    }

    @Test
    fun withLongTextAndMediumFontScale() {
        runComponentTest {
            TestHarness(fontScale = 1.06f) {
                Chip(
                    label = "Primary label very very very very very very very very very very very very very very very very very long text",
                    onClick = { },
                )
            }
        }
    }

    @Test
    fun withSecondaryLabelAndIconAndLongText() {
        runComponentTest {
            Chip(
                label = "Primary label very very very very very very very very long text",
                onClick = { },
                secondaryLabel = "Secondary label very very very very very very very very very long text",
                icon = Icons.Default.Image.asPaintable(),
            )
        }
    }

    @Test
    fun withSecondaryLabelAndIconAndLongTextAndLargestFontScale() {
        runComponentTest {
            TestHarness(fontScale = largestFontScale) {
                Chip(
                    label = "Primary label very very very very very very very very long text",
                    onClick = { },
                    secondaryLabel = "Secondary label very very very very very very very very very long text",
                    icon = Icons.Default.Image.asPaintable(),
                )
            }
        }
    }

    @Test
    fun withSecondaryLabelAndLargeIconAndLongText() {
        runComponentTest {
            Chip(
                label = "Primary label very very very very very very very very long text",
                onClick = { },
                secondaryLabel = "Secondary label very very very very very very very very very long text",
                icon = Icons.Default.Image.asPaintable(),
                largeIcon = true,
            )
        }
    }

    @Test
    fun withSecondaryLabelAndLargeIconAndLongTextAndLargestFontScale() {
        runComponentTest {
            TestHarness(fontScale = largestFontScale) {
                Chip(
                    label = "Primary label very very very very very very very very long text",
                    onClick = { },
                    secondaryLabel = "Secondary label very very very very very very very very very long text",
                    icon = Icons.Default.Image.asPaintable(),
                    largeIcon = true,
                )
            }
        }
    }

    @Test
    fun usingSmallIcon() {
        runComponentTest {
            Chip(
                label = "Primary label",
                onClick = { },
                icon = Icon12dp.asPaintable(),
            )
        }
    }

    @Test
    fun usingDrawableResAsIcon() {
        runComponentTest {
            Chip(
                label = "Primary label",
                onClick = { },
                icon = DrawableResPaintable(R.drawable.ic_delete),
            )
        }
    }

    @Test
    fun withLargeIconUsingSmallIcon() {
        runComponentTest {
            Chip(
                label = "Primary label",
                onClick = { },
                icon = Icon12dp.asPaintable(),
                largeIcon = true,
            )
        }
    }

    @Test
    fun usingExtraLargeIcon() {
        runComponentTest {
            Chip(
                label = "Primary label",
                onClick = { },
                icon = Icon48dp.asPaintable(),
            )
        }
    }

    @Test
    fun withLargeIconUsingExtraLargeIcon() {
        runComponentTest {
            Chip(
                label = "Primary label",
                onClick = { },
                icon = Icon48dp.asPaintable(),
                largeIcon = true,
            )
        }
    }

    @Test
    fun withSecondaryLabelAndIconRtl() {
        runComponentTest {
            TestHarness(layoutDirection = LayoutDirection.Rtl) {
                Chip(
                    label = "Primary label",
                    onClick = { },
                    secondaryLabel = "Secondary label",
                    icon = Icons.Default.Image.asPaintable(),
                )
            }
        }
    }

    @Test
    fun withSecondaryChipColors() {
        runComponentTest {
            Chip(
                label = "Primary label",
                onClick = { },
                secondaryLabel = "Secondary label",
                icon = Icons.Default.Image.asPaintable(),
                colors = ChipDefaults.secondaryChipColors(),
            )
        }
    }

    @Test
    fun withGradientBackgroundChipColors() {
        runComponentTest {
            Chip(
                label = "Primary label",
                onClick = { },
                secondaryLabel = "Secondary label",
                icon = Icons.Default.Image.asPaintable(),
                colors = ChipDefaults.gradientBackgroundChipColors(),
            )
        }
    }

    @Test
    fun withImageBackgroundChipColors() {
        runComponentTest {
            Chip(
                label = "Primary label",
                onClick = { },
                secondaryLabel = "Secondary label",
                icon = Icons.Default.Image.asPaintable(),
                colors = ChipDefaults.imageBackgroundChipColors(
                    backgroundImagePainter = painterResource(id = R.drawable.ic_dialog_alert),
                ),
            )
        }
    }

    @Test
    fun withIconMirrored() {
        runComponentTest {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                @Suppress("Deprecation")
                Chip(
                    label = "Primary label",
                    onClick = { },
                    secondaryLabel = "Secondary label",
                    icon = Icons.Outlined.VolumeDown.asPaintable(),
                    iconRtlMode = IconRtlMode.Mirrored,
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
                viewportHeight = 12f,
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
                viewportHeight = 32f,
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
                viewportHeight = 48f,
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
