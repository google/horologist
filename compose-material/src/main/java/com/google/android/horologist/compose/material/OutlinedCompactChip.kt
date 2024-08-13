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
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material.ChipBorder
import androidx.wear.compose.material.ChipColors
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.LocalContentAlpha
import androidx.wear.compose.material.OutlinedCompactChip
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION
import com.google.android.horologist.images.base.paintable.Paintable
import com.google.android.horologist.images.base.paintable.PaintableIcon
/**
 * This component is an alternative to [OutlinedCompactChip], providing the following:
 * - a convenient way of providing a label;
 * - a convenient way of providing an icon and a placeholder;
 */
@ExperimentalHorologistApi
@Composable
public fun OutlinedCompactChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Paintable? = null,
    colors: ChipColors = ChipDefaults.outlinedChipColors(),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    border: ChipBorder = ChipDefaults.outlinedChipBorder(),
) {
    OutlinedCompactChip(
        onClick = onClick,
        modifier = modifier,
        label = label,
        icon = icon,
        contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
        colors = colors,
        enabled = enabled,
        interactionSource = interactionSource,
        border = border,
    )
}

/**
 * This component is an alternative to [OutlinedCompactChip], providing the following:
 * - a convenient way of providing a string resource label;
 * - a convenient way of providing an icon and a placeholder;
 */
@ExperimentalHorologistApi
@Composable
public fun OutlinedCompactChip(
    @StringRes labelId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Paintable? = null,
    colors: ChipColors = ChipDefaults.outlinedChipColors(),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    border: ChipBorder = ChipDefaults.outlinedChipBorder(),
) {
    OutlinedCompactChip(
        onClick = onClick,
        modifier = modifier,
        label = stringResource(id = labelId),
        icon = icon,
        colors = colors,
        enabled = enabled,
        interactionSource = interactionSource,
        border = border,
    )
}

/**
 * This component is an alternative to [OutlinedCompactChip], providing the following:
 * - a convenient way of providing a label;
 * - a convenient way of providing an icon and a placeholder;
 */
@ExperimentalHorologistApi
@Composable
public fun OutlinedCompactChip(
    icon: Paintable,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChipColors = ChipDefaults.outlinedChipColors(),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    border: ChipBorder = ChipDefaults.outlinedChipBorder(),
) {
    OutlinedCompactChip(
        onClick = onClick,
        modifier = modifier,
        label = null,
        icon = icon,
        contentDescription = contentDescription,
        colors = colors,
        enabled = enabled,
        interactionSource = interactionSource,
        border = border,
    )
}

@Composable
internal fun OutlinedCompactChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    icon: Paintable? = null,
    contentDescription: String? = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
    colors: ChipColors = ChipDefaults.outlinedChipColors(),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    border: ChipBorder = ChipDefaults.outlinedChipBorder(),
) {
    val iconParam: (@Composable BoxScope.() -> Unit)? = icon?.let {
        {
            Row {
                val iconModifier = Modifier.size(ChipDefaults.SmallIconSize)

                if (it is PaintableIcon) {
                    Icon(
                        paintable = it,
                        contentDescription = contentDescription,
                        modifier = iconModifier,
                    )
                } else {
                    Image(
                        painter = it.rememberPainter(),
                        contentDescription = contentDescription,
                        modifier = iconModifier,
                        contentScale = ContentScale.Crop,
                        alpha = LocalContentAlpha.current,
                    )
                }
            }
        }
    }
    val hasIcon = icon != null

    val labelParam: (@Composable RowScope.() -> Unit)? = label?.let {
        {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = label,
                textAlign = if (hasIcon) TextAlign.Start else TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        }
    }

    OutlinedCompactChip(
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
