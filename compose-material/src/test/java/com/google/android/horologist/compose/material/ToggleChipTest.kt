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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.testharness.TestHarness
import com.google.android.horologist.screenshots.rng.WearLegacyComponentTest
import org.junit.Test
import org.robolectric.annotation.Config

class ToggleChipTest : WearLegacyComponentTest() {

    @Test
    fun switch() {
        runComponentTest {
            ToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = stringResource(R.string.primary_label),
                toggleControl = ToggleChipToggleControl.Switch,
            )
        }
    }

    @Test
    fun radio() {
        runComponentTest {
            ToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = stringResource(R.string.primary_label),
                toggleControl = ToggleChipToggleControl.Radio,
            )
        }
    }

    @Test
    fun checkbox() {
        runComponentTest {
            ToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = stringResource(R.string.primary_label),
                toggleControl = ToggleChipToggleControl.Checkbox,
            )
        }
    }

    @Test
    fun unchecked() {
        runComponentTest {
            ToggleChip(
                checked = false,
                onCheckedChanged = { },
                label = stringResource(R.string.primary_label),
                toggleControl = ToggleChipToggleControl.Switch,
            )
        }
    }

    @Test
    fun withSecondaryLabel() {
        runComponentTest {
            ToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = stringResource(R.string.primary_label),
                toggleControl = ToggleChipToggleControl.Switch,
                secondaryLabel = stringResource(com.google.android.horologist.compose.material.R.string.secondary_label),
            )
        }
    }

    @Test
    fun withIcon() {
        runComponentTest {
            ToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = stringResource(R.string.primary_label),
                toggleControl = ToggleChipToggleControl.Switch,
                icon = Icons.Default.Image,
            )
        }
    }

    @Test
    fun withSecondaryLabelAndIcon() {
        runComponentTest {
            ToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = stringResource(R.string.primary_label),
                toggleControl = ToggleChipToggleControl.Switch,
                secondaryLabel = stringResource(com.google.android.horologist.compose.material.R.string.secondary_label),
                icon = Icons.Default.Image,
            )
        }
    }

    @Test
    fun disabled() {
        runComponentTest {
            ToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = stringResource(R.string.primary_label),
                toggleControl = ToggleChipToggleControl.Switch,
                enabled = false,
            )
        }
    }

    @Test
    fun uncheckedAndDisabled() {
        runComponentTest {
            ToggleChip(
                checked = false,
                onCheckedChanged = { },
                label = stringResource(R.string.primary_label),
                toggleControl = ToggleChipToggleControl.Switch,
                enabled = false,
            )
        }
    }

    @Test
    fun withLongText() {
        runComponentTest {
            ToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = "Primary label very very very very very very very very very very very very very very very very very long text",
                toggleControl = ToggleChipToggleControl.Switch,
            )
        }
    }

    @Test
    fun withLongTextAndLargestFontScale() {
        runComponentTest {
            TestHarness(fontScale = largestFontScale) {
                ToggleChip(
                    checked = true,
                    onCheckedChanged = { },
                    label = "Primary label very very very very very very very very very very very very very very very very very long text",
                    toggleControl = ToggleChipToggleControl.Switch,
                )
            }
        }
    }

    @Test
    fun withIconAndLongText() {
        runComponentTest {
            ToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = "Primary label very very very very very very very very very very very very very very very very very long text",
                toggleControl = ToggleChipToggleControl.Switch,
                icon = Icons.Default.Image,
            )
        }
    }

    @Test
    fun withIconAndLongTextAndLargestFontScale() {
        runComponentTest {
            TestHarness(fontScale = largestFontScale) {
                ToggleChip(
                    checked = true,
                    onCheckedChanged = { },
                    label = "Primary label very very very very very very very very very very very very very very very very very long text",
                    toggleControl = ToggleChipToggleControl.Switch,
                    icon = Icons.Default.Image,
                )
            }
        }
    }

    @Test
    fun withSecondaryLabelAndLongText() {
        runComponentTest {
            ToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = "Primary label very very very very very very very very long text",
                secondaryLabel = "Secondary label very very very very very very very very very long text",
                toggleControl = ToggleChipToggleControl.Switch,
            )
        }
    }

    @Test
    fun withSecondaryLabelAndLongTextAndLargestFontScale() {
        runComponentTest {
            TestHarness(fontScale = largestFontScale) {
                ToggleChip(
                    checked = true,
                    onCheckedChanged = { },
                    label = "Primary label very very very very very very very very long text",
                    secondaryLabel = "Secondary label very very very very very very very very very long text",
                    toggleControl = ToggleChipToggleControl.Switch,
                )
            }
        }
    }

    @Test
    fun withIconAndSecondaryLabelAndLongText() {
        runComponentTest {
            ToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = "Primary label very very very very very very very very long text",
                secondaryLabel = "Secondary label very very very very very very very very very long text",
                toggleControl = ToggleChipToggleControl.Switch,
                icon = Icons.Default.Image,
            )
        }
    }

    @Test
    fun withIconAndSecondaryLabelAndLongTextAndLargestFontScale() {
        runComponentTest {
            TestHarness(fontScale = largestFontScale) {
                ToggleChip(
                    checked = true,
                    onCheckedChanged = { },
                    label = "Primary label very very very very very very very very long text",
                    secondaryLabel = "Secondary label very very very very very very very very very long text",
                    toggleControl = ToggleChipToggleControl.Switch,
                    icon = Icons.Default.Image,
                )
            }
        }
    }

    @Test
    fun usingSmallIcon() {
        runComponentTest {
            ToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = stringResource(R.string.primary_label),
                toggleControl = ToggleChipToggleControl.Switch,
                icon = Icon12dp,
            )
        }
    }

    @Test
    fun usingLargeIcon() {
        runComponentTest {
            ToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = stringResource(R.string.primary_label),
                toggleControl = ToggleChipToggleControl.Switch,
                icon = Icon32dp,
            )
        }
    }

    // This test is redundant to "withSecondaryLabelAndIcon" test, but it's added to help compare
    // with "defaultRtl" test, as it uses a different icon that is easier to see mirrored.
    @Test
    fun default() {
        runComponentTest {
            ToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = stringResource(R.string.primary_label),
                toggleControl = ToggleChipToggleControl.Switch,
                secondaryLabel = stringResource(com.google.android.horologist.compose.material.R.string.secondary_label),
                icon = Icons.Default.PlayArrow,
            )
        }
    }

    @Test
    @Config(qualifiers = "+ar-rXB-ldrtl")
    fun defaultRtl() {
        runComponentTest {
            ToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = stringResource(R.string.primary_label),
                toggleControl = ToggleChipToggleControl.Switch,
                secondaryLabel = stringResource(com.google.android.horologist.compose.material.R.string.secondary_label),
                icon = Icons.Default.PlayArrow,
            )
        }
    }

    @Test
    fun mirrored() {
        runComponentTest {
            ToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = stringResource(R.string.primary_label),
                toggleControl = ToggleChipToggleControl.Switch,
                secondaryLabel = stringResource(com.google.android.horologist.compose.material.R.string.secondary_label),
                icon = Icons.Default.PlayArrow,
                iconRtlMode = IconRtlMode.Mirrored,
            )
        }
    }

    @Test
    @Config(qualifiers = "+ar-rXB-ldrtl")
    fun mirroredRtl() {
        runComponentTest {
            ToggleChip(
                checked = true,
                onCheckedChanged = { },
                label = stringResource(R.string.primary_label),
                toggleControl = ToggleChipToggleControl.Switch,
                secondaryLabel = stringResource(com.google.android.horologist.compose.material.R.string.secondary_label),
                icon = Icons.Default.PlayArrow,
                iconRtlMode = IconRtlMode.Mirrored,
            )
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
    }
}
