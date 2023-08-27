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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material.ChipBorder
import androidx.wear.compose.material.ChipColors
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.LocalContentAlpha
import androidx.wear.compose.material.Text
import coil.compose.rememberAsyncImagePainter
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION

@ExperimentalHorologistApi
@Composable
public fun CompactChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    icon: Any? = null,
    iconRtlMode: IconRtlMode = IconRtlMode.Default,
    placeholder: Painter? = null,
    colors: ChipColors = ChipDefaults.primaryChipColors(),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    border: ChipBorder = ChipDefaults.chipBorder()
) {
    val iconParam: (@Composable BoxScope.() -> Unit)? =
        icon?.let {
            {
                Row {
                    val iconModifier = Modifier
                        .size(ChipDefaults.SmallIconSize)
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

    val labelParam: (@Composable RowScope.() -> Unit) = {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = label,
            textAlign = if (hasIcon) TextAlign.Start else TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }

    CompactChip(
        modifier = modifier,
        onClick = onClick,
        label = labelParam,
        icon = iconParam,
        colors = colors,
        enabled = enabled,
        interactionSource = interactionSource,
        border = border
    )
}

/**
 * This component is an alternative to [CompactChip], providing the following:
 * - a convenient way of providing a string resource label;
 * - a convenient way of providing an icon and a placeholder, and choosing their size based on the
 * sizes recommended by the Wear guidelines;
 */
@ExperimentalHorologistApi
@Composable
public fun CompactChip(
    @StringRes labelId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Any? = null,
    iconRtlMode: IconRtlMode = IconRtlMode.Default,
    placeholder: Painter? = null,
    colors: ChipColors = ChipDefaults.primaryChipColors(),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    border: ChipBorder = ChipDefaults.chipBorder()
) {
    CompactChip(
        onClick = onClick,
        modifier = modifier,
        label = stringResource(id = labelId),
        icon = icon,
        iconRtlMode = iconRtlMode,
        placeholder = placeholder,
        colors = colors,
        enabled = enabled,
        interactionSource = interactionSource,
        border = border
    )
}

@ExperimentalHorologistApi
@Composable
public fun CompactChip(
    icon: Any,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconRtlMode: IconRtlMode = IconRtlMode.Default,
    placeholder: Painter? = null,
    colors: ChipColors = ChipDefaults.primaryChipColors(),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    border: ChipBorder = ChipDefaults.chipBorder()
) {
    val iconParam: (@Composable BoxScope.() -> Unit) =
        {
            Row {
                val iconModifier = Modifier
                    .size(ChipDefaults.SmallIconSize)
                when (icon) {
                    is ImageVector ->
                        Icon(
                            imageVector = icon,
                            contentDescription = contentDescription,
                            modifier = iconModifier,
                            rtlMode = iconRtlMode
                        )

                    is Int ->
                        Icon(
                            id = icon,
                            contentDescription = contentDescription,
                            modifier = iconModifier
                        )

                    else ->
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = icon,
                                placeholder = placeholder
                            ),
                            contentDescription = contentDescription,
                            modifier = iconModifier,
                            contentScale = ContentScale.Crop,
                            alpha = LocalContentAlpha.current
                        )
                }
            }
        }
    CompactChip(
        modifier = modifier,
        onClick = onClick,
        label = null,
        icon = iconParam,
        colors = colors,
        enabled = enabled,
        interactionSource = interactionSource,
        border = border
    )
}
