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

@file:OptIn(ExperimentalFoundationApi::class)

package com.google.android.horologist.compose.material

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.unit.Dp
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.ButtonDefaults.DefaultButtonSize
import androidx.wear.compose.material.ButtonDefaults.DefaultIconSize
import androidx.wear.compose.material.ButtonDefaults.LargeButtonSize
import androidx.wear.compose.material.ButtonDefaults.LargeIconSize
import androidx.wear.compose.material.ButtonDefaults.SmallButtonSize
import androidx.wear.compose.material.ButtonDefaults.SmallIconSize
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.images.base.paintable.DrawableResPaintable
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable
import com.google.android.horologist.images.base.paintable.PaintableIcon
import androidx.wear.compose.material.Button as MaterialButton

/**
 * This component is an alternative to [Button], providing the following:
 * - a convenient way of providing an icon and choosing its size from a range of sizes recommended
 * by the Wear guidelines;
 */
@ExperimentalHorologistApi
@Composable
public fun Button(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    colors: ButtonColors = ButtonDefaults.primaryButtonColors(),
    buttonSize: ButtonSize = ButtonSize.Default,
    enabled: Boolean = true,
) {
    Button(
        icon = ImageVectorPaintable(imageVector),
        contentDescription = contentDescription,
        onClick = onClick,
        onLongClick = onLongClick,
        modifier = modifier,
        colors = colors,
        buttonSize = buttonSize,
        enabled = enabled,
    )
}

/**
 * This component is an alternative to [Button], providing the following:
 * - a convenient way of providing an icon and choosing its size from a range of sizes recommended
 * by the Wear guidelines;
 */
@ExperimentalHorologistApi
@Composable
public fun Button(
    @DrawableRes id: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    colors: ButtonColors = ButtonDefaults.primaryButtonColors(),
    buttonSize: ButtonSize = ButtonSize.Default,
    enabled: Boolean = true,
) {
    Button(
        icon = DrawableResPaintable(id),
        contentDescription = contentDescription,
        onClick = onClick,
        onLongClick = onLongClick,
        modifier = modifier,
        colors = colors,
        buttonSize = buttonSize,
        enabled = enabled,
    )
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
internal fun Button(
    icon: PaintableIcon,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    colors: ButtonColors = ButtonDefaults.primaryButtonColors(),
    buttonSize: ButtonSize = ButtonSize.Default,
    enabled: Boolean = true,
) {
    if (onLongClick != null) {
        val interactionSource = remember { MutableInteractionSource() }
        MaterialButton(
            onClick = onClick,
            modifier = modifier
                .size(buttonSize.tapTargetSize)
                .clearAndSetSemantics {
                    role = Role.Button
                    this.contentDescription = contentDescription
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
            enabled = enabled,
            colors = colors,
            interactionSource = interactionSource,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .combinedClickable(
                        interactionSource = interactionSource,
                        indication = null, // From material Button
                        enabled = enabled,
                        onClick = onClick,
                        onLongClick = onLongClick,
                        role = Role.Button,
                    ),
            ) {
                val iconModifier = Modifier
                    .size(buttonSize.iconSize)
                    .align(Alignment.Center)

                Icon(
                    paintable = icon,
                    contentDescription = contentDescription,
                    modifier = iconModifier,
                )
            }
        }
    } else {
        MaterialButton(
            onClick = onClick,
            modifier = modifier.size(buttonSize.tapTargetSize),
            enabled = enabled,
            colors = colors,
        ) {
            val iconModifier = Modifier
                .size(buttonSize.iconSize)
                .align(Alignment.Center)

            Icon(
                paintable = icon,
                contentDescription = contentDescription,
                modifier = iconModifier,
            )
        }
    }
}

@ExperimentalHorologistApi
public sealed class ButtonSize(
    public val iconSize: Dp,
    public val tapTargetSize: Dp,
) {
    public object Default :
        ButtonSize(iconSize = DefaultIconSize, tapTargetSize = DefaultButtonSize)

    public object Large : ButtonSize(iconSize = LargeIconSize, tapTargetSize = LargeButtonSize)
    public object Small : ButtonSize(iconSize = SmallIconSize, tapTargetSize = SmallButtonSize)

    /**
     * Custom sizes should follow the [accessibility principles and guidance for touch targets](https://developer.android.com/training/wearables/accessibility#set-minimum).
     */
    public data class Custom(val customIconSize: Dp, val customTapTargetSize: Dp) :
        ButtonSize(iconSize = customIconSize, tapTargetSize = customTapTargetSize)
}
