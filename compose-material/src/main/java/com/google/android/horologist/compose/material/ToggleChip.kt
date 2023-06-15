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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.ToggleChipColors
import androidx.wear.compose.material.ToggleChipDefaults
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION
import com.google.android.horologist.compose.material.util.adjustChipHeightToFontScale

/**
 * This component is an alternative to [ToggleChip], providing the following:
 * - a convenient way of providing a label and a secondary label;
 * - a convenient way of choosing the toggle control;
 * - a convenient way of providing an icon and setting the icon to be mirrored in RTL mode;
 */
@ExperimentalHorologistApi
@Composable
public fun ToggleChip(
    checked: Boolean,
    onCheckedChanged: (Boolean) -> Unit,
    label: String,
    toggleControl: ToggleChipToggleControl,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconRtlMode: IconRtlMode = IconRtlMode.Default,
    secondaryLabel: String? = null,
    colors: ToggleChipColors = ToggleChipDefaults.toggleChipColors(),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val hasSecondaryLabel = secondaryLabel != null

    val labelParam: (@Composable RowScope.() -> Unit) =
        {
            Text(
                text = label,
                modifier = Modifier.fillMaxWidth(),
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

    val toggleControlParam: (@Composable () -> Unit) = {
        Icon(
            imageVector = when (toggleControl) {
                ToggleChipToggleControl.Switch -> ToggleChipDefaults.switchIcon(checked)
                ToggleChipToggleControl.Radio -> ToggleChipDefaults.radioIcon(checked)
                ToggleChipToggleControl.Checkbox -> ToggleChipDefaults.checkboxIcon(checked)
            },
            contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
            rtlMode = IconRtlMode.Mirrored
        )
    }

    val iconParam: (@Composable BoxScope.() -> Unit)? =
        icon?.let {
            {
                Row {
                    Icon(
                        imageVector = icon,
                        contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                        modifier = Modifier
                            .size(ChipDefaults.IconSize)
                            .clip(CircleShape),
                        rtlMode = iconRtlMode
                    )
                }
            }
        }

    val stateDescriptionSemantics = stringResource(
        if (checked) {
            R.string.horologist_toggle_chip_on_content_description
        } else {
            R.string.horologist_toggle_chip_off_content_description
        }
    )
    ToggleChip(
        checked = checked,
        onCheckedChange = onCheckedChanged,
        label = labelParam,
        toggleControl = toggleControlParam,
        modifier = modifier
            .adjustChipHeightToFontScale(LocalConfiguration.current.fontScale)
            .fillMaxWidth()
            .semantics {
                stateDescription = stateDescriptionSemantics
            },
        appIcon = iconParam,
        secondaryLabel = secondaryLabelParam,
        colors = colors,
        enabled = enabled,
        interactionSource = interactionSource
    )
}
