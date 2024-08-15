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
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.AirplanemodeInactive
import com.google.accompanist.testharness.TestHarness
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable.Companion.asPaintable
import com.google.android.horologist.screenshots.rng.WearLegacyComponentTest
import org.junit.Test
import org.robolectric.annotation.Config

class ToggleButtonTest : WearLegacyComponentTest() {

    @Test
    fun default() {
        runComponentTest {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive.asPaintable(),
                notCheckedIcon = Icons.Filled.AirplanemodeInactive.asPaintable(),
                contentDescription = "contentDescription",
                onCheckedChanged = {},
            )
        }
    }

    @Test
    fun notChecked() {
        runComponentTest {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive.asPaintable(),
                notCheckedIcon = Icons.Filled.AirplanemodeInactive.asPaintable(),
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                checked = false,
            )
        }
    }

    @Test
    fun disabled() {
        runComponentTest {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive.asPaintable(),
                notCheckedIcon = Icons.Filled.AirplanemodeInactive.asPaintable(),
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                enabled = false,
            )
        }
    }

    @Test
    fun notCheckedDisabled() {
        runComponentTest {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive.asPaintable(),
                notCheckedIcon = Icons.Filled.AirplanemodeInactive.asPaintable(),
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                checked = false,
                enabled = false,
            )
        }
    }

    @Test
    fun text() {
        runComponentTest {
            ToggleButton(
                text = "Monday",
                onCheckedChanged = {},
            )
        }
    }

    @Test
    fun textNotChecked() {
        runComponentTest {
            ToggleButton(
                text = "Monday",
                onCheckedChanged = {},
                checked = false,
            )
        }
    }

    @Test
    fun textDisabled() {
        runComponentTest {
            ToggleButton(
                text = "Monday",
                onCheckedChanged = {},
                enabled = false,
            )
        }
    }

    @Test
    fun textNotCheckedDisabled() {
        runComponentTest {
            ToggleButton(
                text = "Monday",
                onCheckedChanged = {},
                checked = false,
                enabled = false,
            )
        }
    }

    @Test
    fun small() {
        runComponentTest {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive.asPaintable(),
                notCheckedIcon = Icons.Filled.AirplanemodeInactive.asPaintable(),
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                smallSize = true,
            )
        }
    }

    @Test
    fun smallNotChecked() {
        runComponentTest {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive.asPaintable(),
                notCheckedIcon = Icons.Filled.AirplanemodeInactive.asPaintable(),
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                checked = false,
                smallSize = true,
            )
        }
    }

    @Test
    fun smallDisabled() {
        runComponentTest {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive.asPaintable(),
                notCheckedIcon = Icons.Filled.AirplanemodeInactive.asPaintable(),
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                enabled = false,
                smallSize = true,
            )
        }
    }

    fun smallNotCheckedDisabled() {
        runComponentTest {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive.asPaintable(),
                notCheckedIcon = Icons.Filled.AirplanemodeInactive.asPaintable(),
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                checked = false,
                enabled = false,
                smallSize = true,
            )
        }
    }

    fun iconOnly() {
        runComponentTest {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive.asPaintable(),
                notCheckedIcon = Icons.Filled.AirplanemodeInactive.asPaintable(),
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                colors = ToggleButtonDefaults.iconOnlyColors(),
                smallSize = true,
            )
        }
    }

    fun iconOnlyNotChecked() {
        runComponentTest {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive.asPaintable(),
                notCheckedIcon = Icons.Filled.AirplanemodeInactive.asPaintable(),
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                checked = false,
                colors = ToggleButtonDefaults.iconOnlyColors(),
                smallSize = true,
            )
        }
    }

    fun iconOnlyDisabled() {
        runComponentTest {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive.asPaintable(),
                notCheckedIcon = Icons.Filled.AirplanemodeInactive.asPaintable(),
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                enabled = false,
                colors = ToggleButtonDefaults.iconOnlyColors(),
                smallSize = true,
            )
        }
    }

    fun iconOnlyNotCheckedDisabled() {
        runComponentTest {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive.asPaintable(),
                notCheckedIcon = Icons.Filled.AirplanemodeInactive.asPaintable(),
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                checked = false,
                enabled = false,
                colors = ToggleButtonDefaults.iconOnlyColors(),
                smallSize = true,
            )
        }
    }

    @Test
    @Config(qualifiers = "+ar-rXB-ldrtl")
    fun rtl() {
        runComponentTest {
            ToggleButton(
                checkedIcon = Icons.AutoMirrored.Filled.VolumeUp.asPaintable(),
                notCheckedIcon = Icons.AutoMirrored.Filled.VolumeOff.asPaintable(),
                contentDescription = "contentDescription",
                onCheckedChanged = {},
            )
        }
    }

    @Test
    fun textWithLargestFontScale() {
        runComponentTest {
            TestHarness(fontScale = largestFontScale) {
                ToggleButton(
                    text = "Monday",
                    onCheckedChanged = {},
                )
            }
        }
    }

    companion object {

        private const val largestFontScale = 1.18f
    }
}
