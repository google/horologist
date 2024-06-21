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

import androidx.annotation.StringRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ChipBorder
import androidx.wear.compose.material.ChipColors
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.PlaceholderState
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.util.ChipIcon
import com.google.android.horologist.compose.material.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION
import com.google.android.horologist.compose.material.util.placeholderIf
import com.google.android.horologist.images.base.paintable.Paintable

/**
 * This component is an alternative to [CompactChip], providing the following:
 * - a convenient way of providing an icon and a placeholder;
 */
@ExperimentalHorologistApi
@Composable
public fun CompactChip(
    icon: Paintable,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChipColors = ChipDefaults.primaryChipColors(),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    border: ChipBorder = ChipDefaults.chipBorder(),
    placeholderState: PlaceholderState? = null,
) {
    CompactChip(
        onClick = onClick,
        modifier = modifier,
        label = null,
        icon = icon,
        contentDescription = contentDescription,
        colors = colors,
        enabled = enabled,
        interactionSource = interactionSource,
        border = border,
        placeholderState = placeholderState,
    )
}

@Composable
internal fun CompactChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    icon: Paintable? = null,
    contentDescription: String? = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
    colors: ChipColors = ChipDefaults.primaryChipColors(),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    border: ChipBorder = ChipDefaults.chipBorder(),
    placeholderState: PlaceholderState? = null,
) {
    val showContent = placeholderState == null || placeholderState.isShowContent
    val iconParam: (@Composable BoxScope.() -> Unit)? = icon?.let {
        {
            Row {
                ChipIcon(it, false, placeholderState, contentDescription = contentDescription)
            }
        }
    }
    val hasIcon = icon != null

    val labelParam: (@Composable RowScope.() -> Unit)? = label?.let {
        {
            Text(
                text = if (showContent) label else "",
                modifier = Modifier
                    .run {
                        if (showContent)
                            this
                        else
                            this.width(40.dp)
                    }
                    .placeholderIf(placeholderState),
                textAlign = if (hasIcon) TextAlign.Start else TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        }
    }

    CompactChip(
        modifier = modifier,
        onClick = onClick,
        label = labelParam,
        icon = iconParam,
        colors = colors,
        enabled = enabled,
        interactionSource = interactionSource,
        border = border,
    )
}
