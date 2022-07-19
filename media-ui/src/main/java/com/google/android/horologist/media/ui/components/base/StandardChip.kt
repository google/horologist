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

package com.google.android.horologist.media.ui.components.base

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import coil.compose.rememberAsyncImagePainter

/**
 * This composable fulfils the redlines of the following components:
 * - Primary or Secondary chip - according to [chipType] value;
 * - Standard chip - when [largeIcon] value is `false`;
 * - Chip with small or large avatar - according to [largeIcon] value;
 */
@Composable
internal fun StandardChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryLabel: String? = null,
    icon: Any? = null,
    largeIcon: Boolean = false,
    placeholder: Painter? = null,
    chipType: StandardChipType = StandardChipType.Primary,
    enabled: Boolean = true,
) {
    val hasSecondaryLabel = secondaryLabel != null
    val hasIcon = icon != null

    val labelParam: (@Composable RowScope.() -> Unit) =
        {
            Text(
                text = label,
                modifier = Modifier.fillMaxWidth(),
                textAlign = if (hasSecondaryLabel || hasIcon) TextAlign.Left else TextAlign.Center,
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
                        Icon(
                            imageVector = icon,
                            contentDescription = null, // hidden from talkback
                            modifier = Modifier
                                .size(iconSize)
                                .clip(CircleShape),
                        )
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = icon,
                                placeholder = placeholder
                            ),
                            contentDescription = null, // hidden from talkback
                            modifier = Modifier
                                .size(iconSize)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
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
        modifier = modifier.fillMaxWidth(),
        secondaryLabel = secondaryLabelParam,
        icon = iconParam,
        colors = when (chipType) {
            StandardChipType.Primary -> ChipDefaults.primaryChipColors()
            StandardChipType.Secondary -> ChipDefaults.secondaryChipColors()
        },
        enabled = enabled,
        contentPadding = contentPadding,
    )
}

internal enum class StandardChipType {
    Primary,
    Secondary
}
