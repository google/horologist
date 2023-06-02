/*
 * Copyright 2022 The Android Open Source Project
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.LocalContentAlpha
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import coil.compose.rememberAsyncImagePainter
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION
import com.google.android.horologist.compose.material.util.adjustChipHeightToFontScale

/**
 * This composable fulfils the redlines of the following components:
 * - Primary or Secondary chip - according to [chipType] value;
 * - Standard chip - when [largeIcon] value is `false`;
 * - Chip with small or large avatar - according to [largeIcon] value;
 */
@ExperimentalHorologistApi
@Composable
public fun Chip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryLabel: String? = null,
    icon: Any? = null,
    largeIcon: Boolean = false,
    placeholder: Painter? = null,
    chipType: ChipType = ChipType.Primary,
    enabled: Boolean = true
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
                    if (icon is ImageVector) {
                        val iconTint = when (chipType) {
                            ChipType.Primary -> MaterialTheme.colors.onPrimary
                            ChipType.Secondary -> MaterialTheme.colors.onSurface
                        }

                        Icon(
                            imageVector = icon,
                            contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                            modifier = Modifier
                                .size(iconSize)
                                .clip(CircleShape),
                            tint = iconTint
                        )
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = icon,
                                placeholder = placeholder
                            ),
                            contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                            modifier = Modifier
                                .size(iconSize)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            alpha = LocalContentAlpha.current
                        )
                    }
                }
            }
        }

    Chip(
        label = label,
        onClick = onClick,
        modifier = modifier,
        secondaryLabel = secondaryLabel,
        icon = iconParam,
        largeIcon = largeIcon,
        chipType = chipType,
        enabled = enabled
    )
}

/**
 * An implementation of [Chip] allowing full customization of the icon displayed.
 */
@ExperimentalHorologistApi
@Composable
public fun Chip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryLabel: String? = null,
    icon: (@Composable BoxScope.() -> Unit)? = null,
    largeIcon: Boolean = false,
    chipType: ChipType = ChipType.Primary,
    enabled: Boolean = true
) {
    val hasSecondaryLabel = secondaryLabel != null
    val hasIcon = icon != null

    val labelParam: (@Composable RowScope.() -> Unit) =
        {
            val textColor = when (chipType) {
                ChipType.Primary -> MaterialTheme.colors.onPrimary
                ChipType.Secondary -> MaterialTheme.colors.onSurface
            }

            Text(
                text = label,
                color = textColor,
                modifier = Modifier.fillMaxWidth(),
                textAlign = if (hasSecondaryLabel || hasIcon) TextAlign.Left else TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = if (hasSecondaryLabel) 1 else 2,
                style = MaterialTheme.typography.button
            )
        }

    val secondaryLabelParam: (@Composable RowScope.() -> Unit)? =
        secondaryLabel?.let {
            {
                val textColor = when (chipType) {
                    ChipType.Primary -> MaterialTheme.colors.onPrimary
                    ChipType.Secondary -> MaterialTheme.colors.onSurfaceVariant
                }

                Text(
                    text = secondaryLabel,
                    color = textColor,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.caption2
                )
            }
        }

    val contentPadding = if (largeIcon) {
        val horizontalPadding = 10.dp
        val verticalPadding = 6.dp // same as Chip.ChipVerticalPadding
        PaddingValues(horizontal = horizontalPadding, vertical = verticalPadding)
    } else {
        ChipDefaults.ContentPadding
    }

    Chip(
        label = labelParam,
        onClick = onClick,
        modifier = modifier
            .adjustChipHeightToFontScale(LocalConfiguration.current.fontScale)
            .fillMaxWidth(),
        secondaryLabel = secondaryLabelParam,
        icon = icon,
        colors = when (chipType) {
            ChipType.Primary -> ChipDefaults.primaryChipColors()
            ChipType.Secondary -> ChipDefaults.secondaryChipColors()
        },
        enabled = enabled,
        contentPadding = contentPadding
    )
}

/**
 * An alternative function to [Chip] that allows a string resource id to be passed as label.
 */
@ExperimentalHorologistApi
@Composable
public fun Chip(
    @StringRes labelId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryLabel: String? = null,
    icon: Any? = null,
    largeIcon: Boolean = false,
    placeholder: Painter? = null,
    chipType: ChipType = ChipType.Primary,
    enabled: Boolean = true
) {
    Chip(
        label = stringResource(id = labelId),
        onClick = onClick,
        modifier = modifier,
        secondaryLabel = secondaryLabel,
        icon = icon,
        largeIcon = largeIcon,
        placeholder = placeholder,
        chipType = chipType,
        enabled = enabled
    )
}

public enum class ChipType {
    Primary, Secondary
}
