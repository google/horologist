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

package com.google.android.horologist.audio.ui.components.actions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ButtonDefaults.buttonColors
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.composables.UnboundedRippleButton
import com.google.android.horologist.compose.material.Icon
import com.google.android.horologist.compose.material.IconRtlMode
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable.Companion.asPaintable

/**
 * An icon button to launch a screen to control the system.
 */
@Composable
public fun SettingsButton(
    onClick: () -> Unit,
    badgeVector: ImageVector? = null,
    badgeColor: Color = MaterialTheme.colors.primary,
    imageVector: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    iconRtlMode: IconRtlMode = IconRtlMode.Default,
    enabled: Boolean = true,
    iconSize: Dp = 26.dp,
    badgeSize: Dp = 16.dp,
    iconAlignment: Alignment = Alignment.Center,
    iconPadding: PaddingValues? = null,
    tapTargetSize: Dp = 52.dp,
) {
    UnboundedRippleButton(
        modifier = modifier.size(tapTargetSize),
        onClick = onClick,
        colors = buttonColors(
            backgroundColor = Color.Transparent,
            disabledBackgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colors.onSurface,
        ),
        enabled = enabled,
        rippleRadius = tapTargetSize / 2,
    ) {
        Box() {
            Icon(
                paintable = imageVector.asPaintable(),
                contentDescription = contentDescription,
                modifier = Modifier.size(iconSize).border(width = 0.dp, color = Color.Transparent, shape = CircleShape),
                rtlMode = iconRtlMode,
            )
            if (badgeVector != null) {
                Icon(
                    paintable = badgeVector!!.asPaintable(),
                    contentDescription = contentDescription,
                    modifier = Modifier.size(badgeSize)
                        .align(Alignment.CenterEnd)
                        .offset(badgeSize - 2.dp)
                        .background(color = badgeColor, shape = CircleShape),
                )
            }
        }
    }
}
