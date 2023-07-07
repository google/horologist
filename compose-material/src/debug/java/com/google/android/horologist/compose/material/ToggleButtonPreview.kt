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
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.AirplanemodeInactive
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleButtonPreview() {
    ToggleButton(
        checkedIcon = Icons.Filled.AirplanemodeActive,
        notCheckedIcon = Icons.Filled.AirplanemodeInactive,
        contentDescription = "contentDescription",
        onCheckedChanged = {}
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleButtonPreviewNotChecked() {
    ToggleButton(
        checkedIcon = Icons.Filled.AirplanemodeActive,
        notCheckedIcon = Icons.Filled.AirplanemodeInactive,
        contentDescription = "contentDescription",
        onCheckedChanged = {},
        checked = false
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleButtonPreviewDisabled() {
    ToggleButton(
        checkedIcon = Icons.Filled.AirplanemodeActive,
        notCheckedIcon = Icons.Filled.AirplanemodeInactive,
        contentDescription = "contentDescription",
        onCheckedChanged = {},
        enabled = false
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleButtonPreviewNotCheckedDisabled() {
    ToggleButton(
        checkedIcon = Icons.Filled.AirplanemodeActive,
        notCheckedIcon = Icons.Filled.AirplanemodeInactive,
        contentDescription = "contentDescription",
        onCheckedChanged = {},
        checked = false,
        enabled = false
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleButtonPreviewText() {
    ToggleButton(
        text = "Monday",
        onCheckedChanged = {}
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleButtonPreviewTextNotChecked() {
    ToggleButton(
        text = "Monday",
        onCheckedChanged = {},
        checked = false
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleButtonPreviewSmall() {
    ToggleButton(
        checkedIcon = Icons.Filled.AirplanemodeActive,
        notCheckedIcon = Icons.Filled.AirplanemodeInactive,
        contentDescription = "contentDescription",
        onCheckedChanged = {},
        smallSize = true
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleButtonPreviewSmallNotChecked() {
    ToggleButton(
        checkedIcon = Icons.Filled.AirplanemodeActive,
        notCheckedIcon = Icons.Filled.AirplanemodeInactive,
        contentDescription = "contentDescription",
        onCheckedChanged = {},
        checked = false,
        smallSize = true
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleButtonPreviewIconOnly() {
    ToggleButton(
        checkedIcon = Icons.Filled.AirplanemodeActive,
        notCheckedIcon = Icons.Filled.AirplanemodeInactive,
        contentDescription = "contentDescription",
        onCheckedChanged = {},
        colors = ToggleButtonDefaults.iconOnlyColors(),
        smallSize = true
    )
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ToggleButtonPreviewIconOnlyNotChecked() {
    ToggleButton(
        checkedIcon = Icons.Filled.AirplanemodeActive,
        notCheckedIcon = Icons.Filled.AirplanemodeInactive,
        contentDescription = "contentDescription",
        onCheckedChanged = {},
        checked = false,
        colors = ToggleButtonDefaults.iconOnlyColors(),
        smallSize = true
    )
}
