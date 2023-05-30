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
import com.google.android.horologist.base.ui.common.StandardToggleChipToggleControl
import com.google.android.horologist.base.ui.utils.FontScaleUtils.largestFontScale

@Preview(
    name = "With long text",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardToggleChipOverflowPreviewWithLongText() {
    StandardToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label very very very very very very very very very very very very very very very very very long text",
        toggleControl = StandardToggleChipToggleControl.Switch
    )
}

@Preview(
    name = "With long text",
    backgroundColor = 0xff000000,
    showBackground = true,
    fontScale = largestFontScale,
    group = "Largest font scale"
)
@Composable
fun StandardToggleChipOverflowPreviewWithLongTextAndLargestFontScale() {
    StandardToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label very very very very very very very very very very very very very very very very very long text",
        toggleControl = StandardToggleChipToggleControl.Switch
    )
}

@Preview(
    name = "With icon and long text",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardToggleChipOverflowPreviewWithIconAndLongText() {
    StandardToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label very very very very very very very very very very very very very very very very very long text",
        toggleControl = StandardToggleChipToggleControl.Switch,
        icon = Icons.Default.Image
    )
}

@Preview(
    name = "With icon and long text",
    backgroundColor = 0xff000000,
    showBackground = true,
    fontScale = largestFontScale,
    group = "Largest font scale"
)
@Composable
fun StandardToggleChipOverflowPreviewWithIconAndLongTextAndLargestFontScale() {
    StandardToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label very very very very very very very very very very very very very very very very very long text",
        toggleControl = StandardToggleChipToggleControl.Switch,
        icon = Icons.Default.Image
    )
}

@Preview(
    name = "With secondary label and long text",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardToggleChipOverflowPreviewWithSecondaryLabelAndLongText() {
    StandardToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label very very very very very very very very long text",
        secondaryLabel = "Secondary label very very very very very very very very very long text",
        toggleControl = StandardToggleChipToggleControl.Switch
    )
}

@Preview(
    name = "With secondary label and long text",
    backgroundColor = 0xff000000,
    showBackground = true,
    fontScale = largestFontScale,
    group = "Largest font scale"
)
@Composable
fun StandardToggleChipOverflowPreviewWithSecondaryLabelAndLongTextAndLargestFontScale() {
    StandardToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label very very very very very very very very long text",
        secondaryLabel = "Secondary label very very very very very very very very very long text",
        toggleControl = StandardToggleChipToggleControl.Switch
    )
}

@Preview(
    name = "With icon, secondary label and long text",
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StandardToggleChipOverflowPreviewWithIconAndSecondaryLabelAndLongText() {
    StandardToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label very very very very very very very very long text",
        secondaryLabel = "Secondary label very very very very very very very very very long text",
        toggleControl = StandardToggleChipToggleControl.Switch,
        icon = Icons.Default.Image
    )
}

@Preview(
    name = "With icon, secondary label and long text",
    backgroundColor = 0xff000000,
    showBackground = true,
    fontScale = largestFontScale,
    group = "Largest font scale"
)
@Composable
fun StandardToggleChipOverflowPreviewWithIconAndSecondaryLabelAndLongTextAndLargestFontScale() {
    StandardToggleChip(
        checked = true,
        onCheckedChanged = { },
        label = "Primary label very very very very very very very very long text",
        secondaryLabel = "Secondary label very very very very very very very very very long text",
        toggleControl = StandardToggleChipToggleControl.Switch,
        icon = Icons.Default.Image
    )
}
