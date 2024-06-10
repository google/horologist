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
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ChipBorder
import androidx.wear.compose.material.ChipColors
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.LocalContentAlpha
import androidx.wear.compose.material.LocalContentColor
import androidx.wear.compose.material.LocalTextStyle
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PlaceholderState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.placeholder
import androidx.wear.compose.material.placeholderShimmer
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION
import com.google.android.horologist.images.base.paintable.Paintable
import com.google.android.horologist.images.base.paintable.PaintableIcon
import androidx.wear.compose.material.Chip as MaterialChip

/**
 * This component is an alternative to [Chip], providing the following:
 * - a convenient way of providing a label and a secondary label;
 * - a convenient way of providing an icon and a placeholder, and choosing their size based on the
 * sizes recommended by the Wear guidelines;
 */
@ExperimentalHorologistApi
@Composable
public fun Chip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    secondaryLabel: String? = null,
    iconRtlMode: IconRtlMode = IconRtlMode.Default,
    icon: Paintable? = null,
    largeIcon: Boolean = false,
    colors: ChipColors = ChipDefaults.primaryChipColors(),
    enabled: Boolean = true,
    placeholderState: PlaceholderState? = null,
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
                        .placeholderIf(placeholderState)
                    if (it is PaintableIcon) {
                        Icon(
                            paintable = it,
                            contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                            modifier = iconModifier,
                            rtlMode = iconRtlMode,
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

    Chip(
        label = label,
        onClick = onClick,
        onLongClick = onLongClick,
        modifier = modifier,
        secondaryLabel = secondaryLabel,
        icon = iconParam,
        largeIcon = largeIcon,
        colors = colors,
        enabled = enabled,
        placeholderState = placeholderState,
    )
}

/**
 * This component is an alternative to [Chip], providing the following:
 * - a convenient way of providing a label and a secondary label;
 * - a convenient way of providing an icon and a placeholder, and choosing their size based on the
 * sizes recommended by the Wear guidelines;
 */
@ExperimentalHorologistApi
@Composable
public fun Chip(
    @StringRes labelId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    @StringRes secondaryLabel: Int? = null,
    iconRtlMode: IconRtlMode = IconRtlMode.Default,
    icon: Paintable? = null,
    largeIcon: Boolean = false,
    colors: ChipColors = ChipDefaults.primaryChipColors(),
    enabled: Boolean = true,
    placeholderState: PlaceholderState? = null,
) {
    Chip(
        label = stringResource(id = labelId),
        onClick = onClick,
        onLongClick = onLongClick,
        modifier = modifier,
        secondaryLabel = secondaryLabel?.let { stringResource(id = it) },
        icon = icon,
        largeIcon = largeIcon,
        colors = colors,
        enabled = enabled,
        iconRtlMode = iconRtlMode,
        placeholderState = placeholderState,
    )
}

/**
 * This component is an alternative to [Chip], providing the following:
 * - a convenient way of providing a label and a secondary label;
 */
@ExperimentalHorologistApi
@Composable
public fun Chip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    secondaryLabel: String? = null,
    icon: (@Composable BoxScope.() -> Unit)? = null,
    largeIcon: Boolean = false,
    colors: ChipColors = ChipDefaults.primaryChipColors(),
    enabled: Boolean = true,
    placeholderState: PlaceholderState? = null,
) {
    val hasSecondaryLabel = secondaryLabel != null
    val hasIcon = icon != null

    val labelParam: (@Composable RowScope.() -> Unit) =
        {
            Text(
                text = label,
                modifier = Modifier.fillMaxWidth()
                    .placeholderIf(placeholderState),
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
                    modifier = Modifier
                        .placeholderIf(placeholderState),
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

    if (onLongClick != null) {
        Chip(
            label = labelParam,
            onClick = onClick,
            onLongClick = onLongClick,
            modifier = modifier
                .fillMaxWidth()
                .clearAndSetSemantics {
                    text = buildAnnotatedString {
                        append(label)
                        if (secondaryLabel != null) {
                            append(", ")
                            append(secondaryLabel)
                        }
                    }
                    role = Role.Button
                    if (!enabled) {
                        disabled()
                    }
                    this.onClick(action = {
                        onClick()
                        true
                    })
                    this.onLongClick(action = {
                        onLongClick()
                        true
                    })
                },
            secondaryLabel = secondaryLabelParam,
            icon = icon,
            colors = colors,
            enabled = enabled,
            contentPadding = contentPadding,
            placeholderState = placeholderState,
        )
    } else {
        MaterialChip(
            label = labelParam,
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .placeholderShimmerIf(placeholderState),
            secondaryLabel = secondaryLabelParam,
            icon = icon,
            colors = colors,
            enabled = enabled,
            contentPadding = contentPadding,
        )
    }
}

/**
 * Temporary copy of Wear Compose Material Chip with support for
 * onLongClick.
 */
@ExperimentalHorologistApi
@Composable
public fun Chip(
    label: @Composable RowScope.() -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    secondaryLabel: (@Composable RowScope.() -> Unit)? = null,
    icon: (@Composable BoxScope.() -> Unit)? = null,
    colors: ChipColors = ChipDefaults.primaryChipColors(),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    contentPadding: PaddingValues = ChipDefaults.ContentPadding,
    shape: Shape = MaterialTheme.shapes.large,
    border: ChipBorder = ChipDefaults.chipBorder(),
    placeholderState: PlaceholderState? = null,
) {
    Chip(
        onClick = onClick,
        colors = colors,
        border = border,
        modifier = modifier,
        onLongClick = onLongClick,
        enabled = enabled,
        contentPadding = contentPadding,
        shape = shape,
        interactionSource = interactionSource,
        role = Role.Button,
        placeholderState = placeholderState,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            // Fill the container height but not its width as chips have fixed size height but we
            // want them to be able to fit their content
            modifier = Modifier.fillMaxHeight(),
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .wrapContentSize(align = Alignment.Center),
                    content = {
                        val color = colors.iconColor(enabled).value
                        CompositionLocalProvider(
                            LocalContentColor provides color,
                            LocalContentAlpha provides color.alpha,
                        ) {
                            icon()
                        }
                    },
                )
                Spacer(modifier = Modifier.size(IconSpacing))
            }
            Column {
                Row(
                    content = {
                        val color = colors.contentColor(enabled).value
                        CompositionLocalProvider(
                            LocalContentColor provides color,
                            LocalContentAlpha provides color.alpha,
                            LocalTextStyle provides MaterialTheme.typography.button,
                        ) {
                            label()
                        }
                    },
                )
                secondaryLabel?.let {
                    Row(
                        content = {
                            val color = colors.secondaryContentColor(enabled).value
                            CompositionLocalProvider(
                                LocalContentColor provides color,
                                LocalContentAlpha provides color.alpha,
                                LocalTextStyle provides MaterialTheme.typography.caption2,
                            ) {
                                secondaryLabel()
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
internal fun Modifier.placeholderIf(
    placeholderState: PlaceholderState?,
    shape: Shape = MaterialTheme.shapes.small,
    color: Color = MaterialTheme.colors.onSurface
        .copy(alpha = 0.1f)
        .compositeOver(MaterialTheme.colors.surface)
): Modifier {
    return if (placeholderState != null) {
        this.placeholder(placeholderState, shape, color)
    } else {
        this
    }
}

@Composable
internal fun Modifier.placeholderShimmerIf(
    placeholderState: PlaceholderState?,
    shape: Shape = MaterialTheme.shapes.small,
    color: Color = MaterialTheme.colors.onSurface,
): Modifier {
    return if (placeholderState != null) {
        this.placeholderShimmer(placeholderState, shape, color)
    } else {
        this
    }
}

/**
 * Temporary copy of Wear Compose Material Chip with support for
 * onLongClick.
 */
@ExperimentalHorologistApi
@Composable
public fun Chip(
    onClick: () -> Unit,
    colors: ChipColors,
    border: ChipBorder,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ChipDefaults.ContentPadding,
    shape: Shape = MaterialTheme.shapes.large,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    role: Role = Role.Button,
    placeholderState: PlaceholderState? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    MaterialChip(
        onClick = onClick,
        colors = colors,
        border = border,
        modifier = modifier.placeholderShimmerIf(placeholderState),
        enabled = enabled,
        contentPadding = PaddingValues(0.dp),
        shape = shape,
        interactionSource = interactionSource,
        role = role,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = onClick,
                    onLongClick = onLongClick,
                    role = role,
                )
                .padding(contentPadding),
        ) {
            content()
        }
    }
}

/**
 * The default size of the spacing between an icon and a text when they are used inside a
 * [Chip].
 */
internal val IconSpacing = 6.dp
