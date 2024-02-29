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

package com.google.android.horologist.materialcomponents

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.ToggleChip
import com.google.android.horologist.compose.material.ToggleChipToggleControl

@Composable
internal fun SampleToggleChipScreen(
    modifier: Modifier = Modifier,
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(),
    )

    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(
            columnState = columnState,
            modifier = modifier,
        ) {
            item {
                ToggleChip(
                    checked = true,
                    onCheckedChanged = { },
                    label = "Primary label",
                    toggleControl = ToggleChipToggleControl.Switch,
                )
            }
            item {
                ToggleChip(
                    checked = true,
                    onCheckedChanged = { },
                    label = "Primary label",
                    toggleControl = ToggleChipToggleControl.Radio,
                )
            }
            item {
                ToggleChip(
                    checked = true,
                    onCheckedChanged = { },
                    label = "Primary label",
                    toggleControl = ToggleChipToggleControl.Checkbox,
                )
            }
            item {
                ToggleChip(
                    checked = false,
                    onCheckedChanged = { },
                    label = "Primary label",
                    toggleControl = ToggleChipToggleControl.Switch,
                )
            }
            item {
                ToggleChip(
                    checked = true,
                    onCheckedChanged = { },
                    label = "Primary label",
                    toggleControl = ToggleChipToggleControl.Switch,
                    secondaryLabel = "Secondary label",
                )
            }
            item {
                ToggleChip(
                    checked = true,
                    onCheckedChanged = { },
                    label = "Primary label",
                    toggleControl = ToggleChipToggleControl.Switch,
                    icon = Icons.Default.Image,
                )
            }
            item {
                ToggleChip(
                    checked = true,
                    onCheckedChanged = { },
                    label = "Primary label",
                    toggleControl = ToggleChipToggleControl.Switch,
                    secondaryLabel = "Secondary label",
                    icon = Icons.Default.Image,
                )
            }
            item {
                ToggleChip(
                    checked = true,
                    onCheckedChanged = { },
                    label = "Primary label",
                    toggleControl = ToggleChipToggleControl.Switch,
                    enabled = false,
                )
            }
            item {
                ToggleChip(
                    checked = false,
                    onCheckedChanged = { },
                    label = "Primary label",
                    toggleControl = ToggleChipToggleControl.Switch,
                    enabled = false,
                )
            }
        }
    }
}
