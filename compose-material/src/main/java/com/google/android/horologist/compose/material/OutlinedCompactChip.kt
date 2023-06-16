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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.wear.compose.material.ChipBorder
import androidx.wear.compose.material.ChipColors
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.LocalContentAlpha
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.OutlinedCompactChip
import androidx.wear.compose.material.Text
import coil.compose.rememberAsyncImagePainter
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION
import com.google.android.horologist.compose.material.util.adjustChipHeightToFontScale

/**
 * This component is an alternative to [OutlinedCompactChip], providing the following:
 * - a convenient way of providing a label;
 * - a convenient way of providing an icon and a placeholder, and choosing their size based on the
 * sizes recommended by the Wear guidelines;
 */
@ExperimentalHorologistApi
@Composable
public fun OutlinedCompactChip(
    modifier: Modifier = Modifier,
    label: String? = null,
    onClick: () -> Unit,
    icon: Any? = null,
    iconRtlMode: IconRtlMode = IconRtlMode.Default,
    largeIcon: Boolean = false,
    placeholder: Painter? = null,
    colors: ChipColors = ChipDefaults.outlinedChipColors(),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    border: ChipBorder = ChipDefaults.outlinedChipBorder()
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
                    when (icon) {
                        is ImageVector ->
                            Icon(
                                imageVector = icon,
                                contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                                modifier = iconModifier,
                                rtlMode = iconRtlMode
                            )

                        is Int ->
                            Icon(
                                id = icon,
                                contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                                modifier = iconModifier
                            )

                        else ->
                            Image(
                                painter = rememberAsyncImagePainter(
                                    model = icon,
                                    placeholder = placeholder
                                ),
                                contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                                modifier = iconModifier,
                                contentScale = ContentScale.Crop,
                                alpha = LocalContentAlpha.current
                            )
                    }
                }
            }
        }
    val hasIcon = icon != null

    val labelParam: (@Composable RowScope.() -> Unit)? =
        label?.let {
            {
                Text(
                    text = label,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = if (hasIcon) TextAlign.Start else TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.caption1
                )
            }
        }

    val contentPadding = if (largeIcon) {
        val verticalPadding = 6.dp // same as ChipDefaults.ChipVerticalPadding
        PaddingValues(
            start = 10.dp,
            top = verticalPadding,
            end = 14.dp, // same as ChipDefaults.ChipHorizontalPadding
            bottom = verticalPadding
        )
    } else {
        ChipDefaults.ContentPadding
    }

    OutlinedCompactChip(
        modifier = modifier
            .adjustChipHeightToFontScale(LocalConfiguration.current.fontScale)
            .fillMaxWidth(),
        onClick = onClick,
        label = labelParam,
        icon = iconParam,
        colors = colors,
        enabled = enabled,
        interactionSource = interactionSource,
        contentPadding = contentPadding,
        border = border
    )
}

/**
 * This component is an alternative to [OutlinedCompactChip], providing the following:
 * - a convenient way of providing a string resource label;
 * - a convenient way of providing an icon and a placeholder, and choosing their size based on the
 * sizes recommended by the Wear guidelines;
 */
@ExperimentalHorologistApi
@Composable
public fun OutlinedCompactChip(
    modifier: Modifier = Modifier,
    @StringRes labelId: Int,
    onClick: () -> Unit,
    icon: Any? = null,
    largeIcon: Boolean = false,
    placeholder: Painter? = null,
    colors: ChipColors = ChipDefaults.outlinedChipColors(),
    enabled: Boolean = true
) {
    OutlinedCompactChip(
        modifier = modifier,
        label = stringResource(id = labelId),
        onClick = onClick,
        icon = icon,
        largeIcon = largeIcon,
        placeholder = placeholder,
        colors = colors,
        enabled = enabled
    )
}
