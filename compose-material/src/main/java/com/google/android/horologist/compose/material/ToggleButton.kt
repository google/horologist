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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.wear.compose.material.ContentAlpha
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleButton
import androidx.wear.compose.material.ToggleButtonColors
import androidx.wear.compose.material.ToggleButtonDefaults
import androidx.wear.compose.material.contentColorFor

/**
 * This component is an alternative to [ToggleButton], providing the following:
 * - a convenient way of providing text;
 */
@Composable
public fun ToggleButton(
    text: String,
    onCheckedChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    checked: Boolean = true,
    enabled: Boolean = true,
    colors: ToggleButtonColors = ToggleButtonDefaults.toggleButtonColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = CircleShape,
    role: Role = ToggleButtonDefaults.DefaultRole,
    smallSize: Boolean = false
) {
    val stateDescriptionSemantics = stringResource(
        if (checked) {
            R.string.horologist_toggle_button_on_state_description
        } else {
            R.string.horologist_toggle_button_off_state_description
        }
    )

    val newModifier = if (smallSize) {
        modifier.size(ToggleButtonDefaults.SmallToggleButtonSize)
    } else {
        modifier
    }.semantics { stateDescription = stateDescriptionSemantics }

    ToggleButton(
        checked = checked,
        onCheckedChange = onCheckedChanged,
        modifier = newModifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        shape = shape,
        role = role
    ) {
        Text(text = text.take(3))
    }
}

/**§
 * This component is an alternative to [ToggleButton], providing the following:
 * - a convenient way of providing a icons for the checked and not checked states;
 */
@Composable
public fun ToggleButton(
    checkedIcon: Any,
    notCheckedIcon: Any,
    contentDescription: String,
    onCheckedChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    checked: Boolean = true,
    enabled: Boolean = true,
    colors: ToggleButtonColors = ToggleButtonDefaults.toggleButtonColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = CircleShape,
    role: Role = ToggleButtonDefaults.DefaultRole,
    iconRtlMode: IconRtlMode = IconRtlMode.Default,
    smallSize: Boolean = false
) {
    val stateDescriptionSemantics = stringResource(
        if (checked) {
            R.string.horologist_toggle_button_on_state_description
        } else {
            R.string.horologist_toggle_button_off_state_description
        }
    )

    val newModifier = if (smallSize) {
        modifier.size(ToggleButtonDefaults.SmallToggleButtonSize)
    } else {
        modifier
    }.semantics { stateDescription = stateDescriptionSemantics }

    ToggleButton(
        checked = checked,
        onCheckedChange = onCheckedChanged,
        modifier = newModifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        shape = shape,
        role = role
    ) {
        Icon(
            icon = if (checked) {
                checkedIcon
            } else {
                notCheckedIcon
            },
            modifier = modifier,
            contentDescription = contentDescription,
            rtlMode = iconRtlMode
        )
    }
}

/**
 * Contains the default values used by [ToggleButton].
 */
public object ToggleButtonDefaults {

    /**
     * Creates a [ToggleButtonColors] that represents the content colors for an icon-only
     * [ToggleButton].
     */
    @Composable
    public fun iconOnlyColors(): ToggleButtonColors {
        return ToggleButtonDefaults.toggleButtonColors(
            checkedBackgroundColor = Color.Transparent,
            uncheckedBackgroundColor = Color.Transparent,
            disabledCheckedContentColor = contentColorFor(MaterialTheme.colors.surface).copy(alpha = ContentAlpha.disabled)
        )
    }
}
