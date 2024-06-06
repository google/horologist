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
import org.robolectric.annotation.Config

class OutlinedChipTest : WearLegacyComponentTest() {

    @Test
    fun default() {
        runComponentTest {
            OutlinedChip(
                label = "Primary label",
                onClick = { },
            )
        }
    }

    @Test
    fun withSecondaryLabel() {
        runComponentTest {
            OutlinedChip(
                label = "Primary label",
                onClick = { },
                secondaryLabel = "Secondary label",
            )
        }
    }

    @Test
    fun withIcon() {
        runComponentTest {
            OutlinedChip(
                label = "Primary label",
                onClick = { },
                icon = Icons.Default.Image.asPaintable(),
            )
        }
    }

    @Test
    fun withLargeIcon() {
        runComponentTest {
            OutlinedChip(
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
            OutlinedChip(
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
            OutlinedChip(
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
            OutlinedChip(
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
            OutlinedChip(
                label = "Primary label very very very very very very very very very very very very very very very very very long text",
                onClick = { },
            )
        }
    }

    @Test
    fun withLongTextAndLargestFontScale() {
        runComponentTest {
            TestHarness(fontScale = LARGEST_FONT_SCALE) {
                OutlinedChip(
                    label = "Primary label very very very very very very very very very very very very very very very very very long text",
                    onClick = { },
                )
            }
        }
    }

    @Test
    fun withLongTextAndMediumFontScale() {
        runComponentTest {
            TestHarness(fontScale = MEDIUM_FONT_SCALE) {
                OutlinedChip(
                    label = "Primary label very very very very very very very very very very very very very very very very very long text",
                    onClick = { },
                )
            }
        }
    }

    @Test
    fun withSecondaryLabelAndIconAndLongText() {
        runComponentTest {
            OutlinedChip(
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
            TestHarness(fontScale = LARGEST_FONT_SCALE) {
                OutlinedChip(
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
            OutlinedChip(
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
            TestHarness(fontScale = LARGEST_FONT_SCALE) {
                OutlinedChip(
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
            OutlinedChip(
                label = "Primary label",
                onClick = { },
                icon = Icon12dp.asPaintable(),
            )
        }
    }

    @Test
    fun usingDrawableResAsIcon() {
        runComponentTest {
            OutlinedChip(
                label = "Primary label",
                onClick = { },
                icon = DrawableResPaintable(R.drawable.ic_delete),
            )
        }
    }

    @Test
    fun withLargeIconUsingSmallIcon() {
        runComponentTest {
            OutlinedChip(
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
            OutlinedChip(
                label = "Primary label",
                onClick = { },
                icon = Icon48dp.asPaintable(),
            )
        }
    }

    @Test
    fun withLargeIconUsingExtraLargeIcon() {
        runComponentTest {
            OutlinedChip(
                label = "Primary label",
                onClick = { },
                icon = Icon48dp.asPaintable(),
                largeIcon = true,
            )
        }
    }

    @Test
    @Config(qualifiers = "+ar-rXB-ldrtl")
    fun withSecondaryLabelAndIconRtl() {
        runComponentTest {
            OutlinedChip(
                label = "Primary label",
                onClick = { },
                secondaryLabel = "Secondary label",
                icon = Icons.Default.Image.asPaintable(),
            )
        }
    }

    @Test
    fun withSecondaryChipColors() {
        runComponentTest {
            OutlinedChip(
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
            OutlinedChip(
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
            OutlinedChip(
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
                OutlinedChip(
                    label = "Primary label",
                    onClick = { },
                    secondaryLabel = "Secondary label",
                    icon = Icons.Outlined.VolumeDown.asPaintable(),
                    iconRtlMode = IconRtlMode.Mirrored,
                    colors = ChipDefaults.imageBackgroundChipColors(
                        backgroundImagePainter = painterResource(id = R.drawable.ic_dialog_alert),
                    ),
                )
            }
        }
    }

    companion object {
        private const val LARGEST_FONT_SCALE = 1.18f
        private const val MEDIUM_FONT_SCALE = 1.06f

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
