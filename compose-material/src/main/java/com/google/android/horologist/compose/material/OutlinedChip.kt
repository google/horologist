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
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ChipColors
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.LocalContentAlpha
import androidx.wear.compose.material.OutlinedChip
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION
import com.google.android.horologist.images.base.paintable.Paintable
import com.google.android.horologist.images.base.paintable.PaintableIcon
/**
 * This component is an alternative to [OutlinedChip], providing the following:
 * - a convenient way of providing a label and a secondary label;
 * - a convenient way of providing an icon and a placeholder, and choosing their size based on the
 * sizes recommended by the Wear guidelines;
 */
@ExperimentalHorologistApi
@Composable
public fun OutlinedChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconRtlMode: IconRtlMode = IconRtlMode.Default,
    secondaryLabel: String? = null,
    icon: Paintable? = null,
    largeIcon: Boolean = false,
    colors: ChipColors = ChipDefaults.outlinedChipColors(),
    enabled: Boolean = true,
) {
    val iconParam: (@Composable BoxScope.() -> Unit)? =
        icon?.let {
            {
                val iconSize = if (largeIcon) {
                    ChipDefaults.LargeIconSize
                } else {
                    ChipDefaults.IconSize
                }

                Row {
                    val iconModifier = Modifier
                        .size(iconSize)
                        .clip(CircleShape)
                    if (it is PaintableIcon) {
                        Icon(
                            paintable = it,
                            rtlMode = iconRtlMode,
                            contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                            modifier = iconModifier,
                        )
                    } else {
                        Image(
                            painter = it.rememberPainter(),
                            contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                            modifier = iconModifier,
                            contentScale = ContentScale.Crop,
                            alpha = LocalContentAlpha.current,
                        )
                    }
                }
            }
        }

    OutlinedChip(
        label = label,
        onClick = onClick,
        modifier = modifier,
        secondaryLabel = secondaryLabel,
        icon = iconParam,
        largeIcon = largeIcon,
        colors = colors,
        enabled = enabled,
    )
}

/**
 * This component is an alternative to [OutlinedChip], providing the following:
 * - a convenient way of providing a label and a secondary label;
 * - a convenient way of providing an icon and a placeholder, and choosing their size based on the
 * sizes recommended by the Wear guidelines;
 */
@ExperimentalHorologistApi
@Composable
public fun OutlinedChip(
    @StringRes labelId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconRtlMode: IconRtlMode = IconRtlMode.Default,
    @StringRes secondaryLabel: Int? = null,
    icon: Paintable? = null,
    largeIcon: Boolean = false,
    colors: ChipColors = ChipDefaults.outlinedChipColors(),
    enabled: Boolean = true,
) {
    OutlinedChip(
        label = stringResource(id = labelId),
        onClick = onClick,
        modifier = modifier,
        secondaryLabel = secondaryLabel?.let { stringResource(id = it) },
        icon = icon,
        largeIcon = largeIcon,
        iconRtlMode = iconRtlMode,
        colors = colors,
        enabled = enabled,
    )
}

/**
 * This component is an alternative to [OutlinedChip], providing the following:
 * - a convenient way of providing a label and a secondary label;
 */
@ExperimentalHorologistApi
@Composable
public fun OutlinedChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryLabel: String? = null,
    icon: (@Composable BoxScope.() -> Unit)? = null,
    largeIcon: Boolean = false,
    colors: ChipColors = ChipDefaults.outlinedChipColors(),
    enabled: Boolean = true,
) {
    val hasSecondaryLabel = secondaryLabel != null
    val hasIcon = icon != null

    val labelParam: (@Composable RowScope.() -> Unit) =
        {
            Text(
                text = label,
                modifier = Modifier.fillMaxWidth(),
                textAlign = if (hasSecondaryLabel || hasIcon) TextAlign.Start else TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = if (hasSecondaryLabel) 1 else 2,
            )
        }

    val secondaryLabelParam: (@Composable RowScope.() -> Unit)? =
        secondaryLabel?.let {
            {
                Text(
                    text = secondaryLabel,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }
        }

    val contentPadding = if (largeIcon) {
        val verticalPadding = ChipDefaults.ChipVerticalPadding
        PaddingValues(
            start = 10.dp,
            top = verticalPadding,
            end = ChipDefaults.ChipHorizontalPadding,
            bottom = verticalPadding,
        )
    } else {
        ChipDefaults.ContentPadding
    }

    OutlinedChip(
        label = labelParam,
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(),
        secondaryLabel = secondaryLabelParam,
        icon = icon,
        colors = colors,
        enabled = enabled,
        contentPadding = contentPadding,
    )
}
