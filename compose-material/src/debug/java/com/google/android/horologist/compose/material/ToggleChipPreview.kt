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
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleChipSwitchPreview() {
    ToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label",
        toggleControl = ToggleChipToggleControl.Switch
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleChipRadioPreview() {
    ToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label",
        toggleControl = ToggleChipToggleControl.Radio
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleChipCheckboxPreview() {
    ToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label",
        toggleControl = ToggleChipToggleControl.Checkbox
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleChipUncheckedPreview() {
    ToggleChip(
        checked = false,
        onCheckedChanged = { },
        label = "Primary label",
        toggleControl = ToggleChipToggleControl.Switch
    )
}

@Preview(
    name = "With secondary label",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleChipWithSecondaryLabel() {
    ToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label",
        toggleControl = ToggleChipToggleControl.Switch,
        secondaryLabel = "Secondary label"
    )
}

@Preview(
    name = "With icon",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleChipPreviewWithIcon() {
    ToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label",
        toggleControl = ToggleChipToggleControl.Switch,
        icon = Icons.Default.Image
    )
}

@Preview(
    name = "With secondary label and icon",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleChipPreviewWithSecondaryLabelAndIcon() {
    ToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label",
        toggleControl = ToggleChipToggleControl.Switch,
        secondaryLabel = "Secondary label",
        icon = Icons.Default.Image
    )
}

@Preview(
    name = "Disabled",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleChipPreviewDisabled() {
    ToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label",
        toggleControl = ToggleChipToggleControl.Switch,
        enabled = false
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleChipUncheckedAndDisabledPreview() {
    ToggleChip(
        checked = false,
        onCheckedChanged = { },
        label = "Primary label",
        toggleControl = ToggleChipToggleControl.Switch,
        enabled = false
    )
}
