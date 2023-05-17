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

package com.google.android.horologist.base.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardToggleChipSwitchPreview() {
    StandardToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label",
        toggleControl = StandardToggleChipToggleControl.Switch
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardToggleChipRadioPreview() {
    StandardToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label",
        toggleControl = StandardToggleChipToggleControl.Radio
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardToggleChipCheckboxPreview() {
    StandardToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label",
        toggleControl = StandardToggleChipToggleControl.Checkbox
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardToggleChipUncheckedPreview() {
    StandardToggleChip(
        checked = false,
        onCheckedChanged = { },
        label = "Primary label",
        toggleControl = StandardToggleChipToggleControl.Switch
    )
}

@Preview(
    name = "With secondary label",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardToggleChipWithSecondaryLabel() {
    StandardToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label",
        toggleControl = StandardToggleChipToggleControl.Switch,
        secondaryLabel = "Secondary label"
    )
}

@Preview(
    name = "With icon",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardToggleChipPreviewWithIcon() {
    StandardToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label",
        toggleControl = StandardToggleChipToggleControl.Switch,
        icon = Icons.Default.Image
    )
}

@Preview(
    name = "With secondary label and icon",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardToggleChipPreviewWithSecondaryLabelAndIcon() {
    StandardToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label",
        toggleControl = StandardToggleChipToggleControl.Switch,
        secondaryLabel = "Secondary label"
    )
}

@Preview(
    name = "Disabled",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardToggleChipPreviewDisabled() {
    StandardToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label",
        toggleControl = StandardToggleChipToggleControl.Switch,
        enabled = false
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardToggleChipUncheckedAndDisabledPreview() {
    StandardToggleChip(
        checked = false,
        onCheckedChanged = { },
        label = "Primary label",
        toggleControl = StandardToggleChipToggleControl.Switch,
        enabled = false
    )
}
