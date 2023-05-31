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

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.horologist.base.ui.common.StandardToggleChipToggleControl

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardSplitToggleChipSwitchPreview() {
    StandardSplitToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label",
        onClick = { },
        toggleControl = StandardToggleChipToggleControl.Switch
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardSplitToggleChipRadioPreview() {
    StandardSplitToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label",
        onClick = { },
        toggleControl = StandardToggleChipToggleControl.Radio
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardSplitToggleChipCheckboxPreview() {
    StandardSplitToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label",
        onClick = { },
        toggleControl = StandardToggleChipToggleControl.Checkbox
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardSplitToggleChipUncheckedPreview() {
    StandardSplitToggleChip(
        checked = false,
        onCheckedChanged = { },
        label = "Primary label",
        onClick = { },
        toggleControl = StandardToggleChipToggleControl.Switch
    )
}

@Preview(
    name = "With secondary label",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardSplitToggleChipWithSecondaryLabel() {
    StandardSplitToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label",
        onClick = { },
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
fun StandardSplitToggleChipPreviewDisabled() {
    StandardSplitToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label",
        onClick = { },
        toggleControl = StandardToggleChipToggleControl.Switch,
        enabled = false
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardSplitToggleChipUncheckedAndDisabledPreview() {
    StandardSplitToggleChip(
        checked = false,
        onCheckedChanged = { },
        label = "Primary label",
        onClick = { },
        toggleControl = StandardToggleChipToggleControl.Switch,
        enabled = false
    )
}
