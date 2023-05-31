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

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.wear.compose.material.ToggleChipColors
import androidx.wear.compose.material.ToggleChipDefaults
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.ToggleChip
import com.google.android.horologist.compose.material.ToggleChipToggleControl

/**
 * This composable fulfils the redlines of the following components:
 * - Toggle chips
 */
@Suppress("DEPRECATION")
@Deprecated(
    "Replaced by ToggleChip in Horologist Material Compose library",
    replaceWith = ReplaceWith(
        "ToggleChip",
        "com.google.android.horologist.compose.material.ToggleChip"
    )
)
@ExperimentalHorologistApi
@Composable
public fun StandardToggleChip(
    checked: Boolean,
    onCheckedChanged: (Boolean) -> Unit,
    label: String,
    toggleControl: StandardToggleChipToggleControl,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    secondaryLabel: String? = null,
    colors: ToggleChipColors = ToggleChipDefaults.toggleChipColors(),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    ToggleChip(
        checked = checked,
        onCheckedChanged = onCheckedChanged,
        label = label,
        toggleControl = when (toggleControl) {
            StandardToggleChipToggleControl.Switch -> ToggleChipToggleControl.Switch
            StandardToggleChipToggleControl.Radio -> ToggleChipToggleControl.Radio
            StandardToggleChipToggleControl.Checkbox -> ToggleChipToggleControl.Checkbox
        },
        modifier = modifier,
        icon = icon,
        secondaryLabel = secondaryLabel,
        colors = colors,
        enabled = enabled,
        interactionSource = interactionSource
    )
}

@Deprecated(
    "Replaced by ToggleChipToggleControl in Horologist Material Compose library",
    replaceWith = ReplaceWith(
        "ToggleChipToggleControl",
        "com.google.android.horologist.compose.material.ToggleChipToggleControl"
    )
)
@ExperimentalHorologistApi
public enum class StandardToggleChipToggleControl {
    Switch, Radio, Checkbox
}
