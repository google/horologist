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

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.SplitToggleChip
import androidx.wear.compose.material.SplitToggleChipColors
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChipDefaults
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION
import com.google.android.horologist.compose.material.util.adjustChipHeightToFontScale

/**
 * This component is an alternative to [SplitToggleChip], providing the following:
 * - a convenient way of providing a label and a secondary label;
 * - a convenient way of choosing the toggle control;
 */
@ExperimentalHorologistApi
@Composable
public fun SplitToggleChip(
    checked: Boolean,
    onCheckedChanged: (Boolean) -> Unit,
    label: String,
    onClick: () -> Unit,
    toggleControl: ToggleChipToggleControl,
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
                textAlign = TextAlign.Start,
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
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.caption2
                )
            }
        }

    val toggleControlParam: (@Composable BoxScope.() -> Unit) = {
        val stateDescriptionSemantics = stringResource(
            if (checked) {
                R.string.horologist_split_toggle_chip_on_content_description
            } else {
                R.string.horologist_split_toggle_chip_off_content_description
            }
        )
        Icon(
            imageVector = when (toggleControl) {
                ToggleChipToggleControl.Switch -> ToggleChipDefaults.switchIcon(checked)
                ToggleChipToggleControl.Radio -> ToggleChipDefaults.radioIcon(checked)
                ToggleChipToggleControl.Checkbox -> ToggleChipDefaults.checkboxIcon(checked)
            },
            contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
            modifier = Modifier.semantics {
                stateDescription = stateDescriptionSemantics
            },
            rtlMode = IconRtlMode.Mirrored
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
