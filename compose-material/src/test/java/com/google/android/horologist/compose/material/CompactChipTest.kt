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
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.unit.LayoutDirection
import com.google.accompanist.testharness.TestHarness
import com.google.android.horologist.images.base.paintable.DrawableResPaintable
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable.Companion.asPaintable
import com.google.android.horologist.screenshots.rng.WearLegacyComponentTest
import org.junit.Test

class CompactChipTest : WearLegacyComponentTest() {

    @Test
    fun default() {
        runComponentTest {
            CompactChip(
                label = "Primary label",
                onClick = { },
            )
        }
    }

    @Test
    fun withIcon() {
        runComponentTest {
            CompactChip(
                label = "Primary label",
                onClick = { },
                icon = Icons.Filled.Add.asPaintable(),
            )
        }
    }

    @Test
    fun iconOnly() {
        runComponentTest {
            CompactChip(
                onClick = { },
                icon = Icons.Filled.Add.asPaintable(),
                contentDescription = "Add Icon",
            )
        }
    }

    @Test
    fun disabled() {
        runComponentTest {
            CompactChip(
                label = "Primary label",
                onClick = { },
                icon = Icons.Filled.Add.asPaintable(),
                enabled = false,
            )
        }
    }

    @Test
    fun withLongText() {
        runComponentTest {
            CompactChip(
                label = "Primary label very very very very very very very very very very very very very very very very very long text",
                onClick = { },
            )
        }
    }

    @Test
    fun withLongTextAndLargestFontScale() {
        runComponentTest {
            TestHarness(fontScale = LARGEST_FONT_SCALE) {
                CompactChip(
                    label = "Primary label very very very very very very very very very very very very very very very very very long text",
                    onClick = { },
                )
            }
        }
    }

    @Test
    fun usingDrawableResAsIcon() {
        runComponentTest {
            CompactChip(
                label = "Primary label",
                onClick = { },
                icon = DrawableResPaintable(R.drawable.ic_delete),
            )
        }
    }

    @Test
    fun withIconRtl() {
        runComponentTest {
            TestHarness(layoutDirection = LayoutDirection.Rtl) {
                CompactChip(
                    label = "Primary label",
                    onClick = { },
                    icon = Icons.AutoMirrored.Default.DirectionsBike.asPaintable(),
                )
            }
        }
    }

    @Test
    fun mirrored() {
        runComponentTest {
            CompactChip(
                label = "Primary label",
                onClick = { },
                icon = Icons.AutoMirrored.Default.DirectionsBike.asPaintable(),
                iconRtlMode = IconRtlMode.Mirrored,
            )
        }
    }

    @Test
    fun mirroredRtl() {
        runComponentTest {
            TestHarness(layoutDirection = LayoutDirection.Rtl) {
                CompactChip(
                    label = "Primary label",
                    onClick = { },
                    icon = Icons.AutoMirrored.Default.DirectionsBike.asPaintable(),
                    iconRtlMode = IconRtlMode.Mirrored,
                )
            }
        }
    }

    @Test
    fun usingDrawableResAsIconMirroredRtl() {
        runComponentTest {
            TestHarness(layoutDirection = LayoutDirection.Rtl) {
                CompactChip(
                    label = "Primary label",
                    onClick = { },
                    icon = DrawableResPaintable(R.drawable.ic_media_play),
                    iconRtlMode = IconRtlMode.Mirrored,
                )
            }
        }
    }

    companion object {
        private const val LARGEST_FONT_SCALE = 1.24f
    }
}
