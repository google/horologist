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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.test.FontScale
import com.google.android.horologist.images.base.paintable.DrawableResPaintable
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable.Companion.asPaintable
import com.google.android.horologist.screenshots.rng.WearLegacyComponentTest
import org.junit.Test
import org.robolectric.annotation.Config

class OutlinedCompactChipTest : WearLegacyComponentTest() {

    @Test
    fun default() {
        runComponentTest {
            OutlinedCompactChip(
                onClick = { },
                label = stringResource(com.google.android.horologist.compose.material.R.string.primary_label),
            )
        }
    }

    @Test
    fun withIcon() {
        runComponentTest {
            OutlinedCompactChip(
                onClick = { },
                label = stringResource(com.google.android.horologist.compose.material.R.string.primary_label),
                icon = Icons.Filled.Add.asPaintable(),
            )
        }
    }

    @Test
    fun iconOnly() {
        runComponentTest {
            OutlinedCompactChip(
                onClick = { },
                icon = Icons.Filled.Add.asPaintable(),
            )
        }
    }

    @Test
    fun disabled() {
        runComponentTest {
            OutlinedCompactChip(
                onClick = { },
                label = stringResource(com.google.android.horologist.compose.material.R.string.primary_label),
                icon = Icons.Filled.Add.asPaintable(),
                enabled = false,
            )
        }
    }

    @Test
    fun withLongText() {
        runComponentTest {
            OutlinedCompactChip(
                onClick = { },
                label = "Primary label very very very very very very very very very very very very very very very very very long text",
            )
        }
    }

    @Test
    fun withLongTextAndLargestFontScale() {
        runComponentTest {
            DeviceConfigurationOverride(DeviceConfigurationOverride.FontScale(LARGEST_FONT_SCALE)) {
                OutlinedCompactChip(
                    onClick = { },
                    label = "Primary label very very very very very very very very very very very very very very very very very long text",
                )
            }
        }
    }

    @Test
    fun usingDrawableResAsIcon() {
        runComponentTest {
            OutlinedCompactChip(
                onClick = { },
                label = stringResource(com.google.android.horologist.compose.material.R.string.primary_label),
                icon = DrawableResPaintable(R.drawable.ic_delete),
            )
        }
    }

    @Test
    @Config(qualifiers = "+ar-rXB-ldrtl")
    fun withIconRtl() {
        runComponentTest {
            OutlinedCompactChip(
                onClick = { },
                label = stringResource(com.google.android.horologist.compose.material.R.string.primary_label),
                icon = Icons.AutoMirrored.Default.DirectionsBike.asPaintable(),
            )
        }
    }

    companion object {
        private const val LARGEST_FONT_SCALE = 1.24f
    }
}
