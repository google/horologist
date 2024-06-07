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

import androidx.compose.ui.res.stringResource
import com.google.accompanist.testharness.TestHarness
import com.google.android.horologist.screenshots.rng.WearLegacyComponentTest
import org.junit.Test
import org.robolectric.annotation.Config

class SplitToggleChipTest : WearLegacyComponentTest() {

    @Test
    fun switch() {
        runComponentTest {
            SplitToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = stringResource(com.google.android.horologist.compose.material.R.string.primary_label),
                onClick = { },
                toggleControl = ToggleChipToggleControl.Switch,
            )
        }
    }

    @Test
    fun radio() {
        runComponentTest {
            SplitToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = stringResource(com.google.android.horologist.compose.material.R.string.primary_label),
                onClick = { },
                toggleControl = ToggleChipToggleControl.Radio,
            )
        }
    }

    @Test
    fun checkbox() {
        runComponentTest {
            SplitToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = stringResource(com.google.android.horologist.compose.material.R.string.primary_label),
                onClick = { },
                toggleControl = ToggleChipToggleControl.Checkbox,
            )
        }
    }

    @Test
    fun unchecked() {
        runComponentTest {
            SplitToggleChip(
                checked = false,
                onCheckedChanged = { },
                label = stringResource(com.google.android.horologist.compose.material.R.string.primary_label),
                onClick = { },
                toggleControl = ToggleChipToggleControl.Switch,
            )
        }
    }

    @Test
    fun withSecondaryLabel() {
        runComponentTest {
            SplitToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = stringResource(com.google.android.horologist.compose.material.R.string.primary_label),
                onClick = { },
                toggleControl = ToggleChipToggleControl.Switch,
                secondaryLabel = "Secondary label",
            )
        }
    }

    @Test
    fun disabled() {
        runComponentTest {
            SplitToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = stringResource(com.google.android.horologist.compose.material.R.string.primary_label),
                onClick = { },
                toggleControl = ToggleChipToggleControl.Switch,
                enabled = false,
            )
        }
    }

    @Test
    fun uncheckedAndDisabled() {
        runComponentTest {
            SplitToggleChip(
                checked = false,
                onCheckedChanged = { },
                label = stringResource(com.google.android.horologist.compose.material.R.string.primary_label),
                onClick = { },
                toggleControl = ToggleChipToggleControl.Switch,
                enabled = false,
            )
        }
    }

    @Test
    fun withLongText() {
        runComponentTest {
            SplitToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = "Primary label very very very very very very very very very very very very very very very very very long text",
                onClick = { },
                toggleControl = ToggleChipToggleControl.Switch,
            )
        }
    }

    @Test
    fun withLongTextAndLargestFontScale() {
        runComponentTest {
            TestHarness(fontScale = largestFontScale) {
                SplitToggleChip(
                    checked = true,
                    onCheckedChanged = { },
                    label = "Primary label very very very very very very very very very very very very very very very very very long text",
                    onClick = { },
                    toggleControl = ToggleChipToggleControl.Switch,
                )
            }
        }
    }

    @Test
    fun withSecondaryLabelAndLongText() {
        runComponentTest {
            SplitToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = "Primary label very very very very very very very very long text",
                onClick = { },
                toggleControl = ToggleChipToggleControl.Switch,
                secondaryLabel = "Secondary label very very very very very very very very very long text",
            )
        }
    }

    @Test
    fun withSecondaryLabelAndLongTextAndLargestFontScale() {
        runComponentTest {
            TestHarness(fontScale = largestFontScale) {
                SplitToggleChip(
                    checked = true,
                    onCheckedChanged = { },
                    label = "Primary label very very very very very very very very long text",
                    onClick = { },
                    toggleControl = ToggleChipToggleControl.Switch,
                    secondaryLabel = "Secondary label very very very very very very very very very long text",
                )
            }
        }
    }

    @Test
    @Config(qualifiers = "+ar-rXB-ldrtl")
    fun rtl() {
        runComponentTest {
            SplitToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = stringResource(com.google.android.horologist.compose.material.R.string.primary_label),
                onClick = { },
                toggleControl = ToggleChipToggleControl.Switch,
                secondaryLabel = "Secondary label",
            )
        }
    }

    companion object {
        private const val largestFontScale = 1.18f
    }
}
