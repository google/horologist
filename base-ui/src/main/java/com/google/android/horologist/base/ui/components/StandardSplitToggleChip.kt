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
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.SplitToggleChip
import androidx.wear.compose.material.SplitToggleChipColors
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChipDefaults
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.base.ui.R
import com.google.android.horologist.base.ui.common.StandardToggleChipToggleControl
import com.google.android.horologist.base.ui.util.adjustChipHeightToFontScale

/**
 * This composable fulfils the redlines of the following components:
 * - SplitToggle chips
 */

@ExperimentalHorologistApi
@Composable
public fun StandardSplitToggleChip(
    checked: Boolean,
    onCheckedChanged: (Boolean) -> Unit,
    label: String,
    onClick: () -> Unit,
    toggleControl: StandardToggleChipToggleControl,
    modifier: Modifier = Modifier,
    secondaryLabel: String? = null,
    colors: SplitToggleChipColors = ToggleChipDefaults.splitToggleChipColors(),
    enabled: Boolean = true,
    checkedInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    clickInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val hasSecondaryLabel = secondaryLabel != null

    val labelParam: (@Composable RowScope.() -> Unit) =
        {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = label,
                color = MaterialTheme.colors.onSurface,
                textAlign = TextAlign.Left,
                overflow = TextOverflow.Ellipsis,
                maxLines = if (hasSecondaryLabel) 1 else 2,
                style = MaterialTheme.typography.button
            )
        }

    val secondaryLabelParam: (@Composable RowScope.() -> Unit)? =
        secondaryLabel?.let {
            {
                Text(
                    text = secondaryLabel,
                    color = MaterialTheme.colors.onSurfaceVariant,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.caption2
                )
            }
        }

    val toggleControlParam: (@Composable BoxScope.() -> Unit) = {
        Icon(
            imageVector = when (toggleControl) {
                StandardToggleChipToggleControl.Switch -> ToggleChipDefaults.switchIcon(checked)
                StandardToggleChipToggleControl.Radio -> ToggleChipDefaults.radioIcon(checked)
                StandardToggleChipToggleControl.Checkbox -> ToggleChipDefaults.checkboxIcon(checked)
            },
            contentDescription = stringResource(
                if (checked) {
                    R.string.horologist_standard_split_toggle_chip_on_content_description
                } else {
                    R.string.horologist_standard_split_toggle_chip_off_content_description
                }
            )
        )
    }

    SplitToggleChip(
        checked = checked,
        onCheckedChange = onCheckedChanged,
        label = labelParam,
        onClick = onClick,
        toggleControl = toggleControlParam,
        modifier = modifier
            .adjustChipHeightToFontScale(LocalConfiguration.current.fontScale)
            .fillMaxWidth(),
        secondaryLabel = secondaryLabelParam,
        colors = colors,
        enabled = enabled,
        checkedInteractionSource = checkedInteractionSource,
        clickInteractionSource = clickInteractionSource
    )
}
