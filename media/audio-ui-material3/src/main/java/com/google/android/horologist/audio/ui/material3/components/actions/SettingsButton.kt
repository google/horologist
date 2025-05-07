/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.audio.ui.material3.components.actions

import android.graphics.Matrix
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import androidx.graphics.shapes.transformed
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButtonColors
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.minimumInteractiveComponentSize
import com.google.android.horologist.audio.ui.material3.DisabledContainerAlpha
import com.google.android.horologist.audio.ui.material3.DisabledContentAlpha
import com.google.android.horologist.audio.ui.material3.toDisabledColor
import com.google.android.horologist.audio.ui.material3.toShape

/** An icon button to launch a screen to control the system. */
@Composable
public fun SettingsButton(
    onClick: () -> Unit,
    imageVector: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    alignment: Alignment = Alignment.Center,
    buttonColors: IconButtonColors = SettingsButtonDefaults.buttonColors(),
    shape: Shape = CircleShape,
    iconSize: Dp = ICON_SIZE,
    badgeVector: ImageVector? = null,
    badgeShape: Shape = defaultBadgeShape(),
    badgeColors: IconButtonColors = SettingsButtonDefaults.badgeColors(),
    border: BorderStroke? = null,
) {
    val buttonContentColor =
        rememberUpdatedState(buttonColors.run { if (enabled) contentColor else disabledContentColor })
    val buttonContainerColor =
        rememberUpdatedState(
            buttonColors.run { if (enabled) containerColor else disabledContainerColor },
        )
    Box(
        contentAlignment = alignment,
        modifier =
            modifier
                .fillMaxSize()
                .clickable(
                    onClick = onClick,
                    enabled = enabled,
                    indication = null,
                    interactionSource = null,
                    role = Role.Button,
                ),
    ) {
        Box(
            modifier =
                Modifier.minimumInteractiveComponentSize()
                    .size(BUTTON_WIDTH, BUTTON_HEIGHT),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .then(border?.let { Modifier.border(border = it, shape = shape) } ?: Modifier)
                        .background(color = buttonContainerColor.value, shape = shape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(iconSize),
                    tint = buttonContentColor.value,
                )
            }
            badgeVector?.let {
                val badgeContentColor =
                    rememberUpdatedState(
                        badgeColors.run { if (enabled) contentColor else disabledContentColor },
                    )
                val badgeContainerColor =
                    rememberUpdatedState(
                        badgeColors.run { if (enabled) containerColor else disabledContainerColor },
                    )
                Box(
                    modifier =
                        Modifier.size(BADGE_SIZE)
                            .align(Alignment.TopEnd)
                            .offset(x = BADGE_SIZE / 2)
                            .background(badgeContainerColor.value, badgeShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier.size(BADGE_ICON_SIZE),
                        tint = badgeContentColor.value,
                    )
                }
            }
        }
    }
}

/** Default values for [SettingsButton]. */
public object SettingsButtonDefaults {
    /** Button colors for [SettingsButton]. */
    @Composable
    public fun buttonColors(colorScheme: ColorScheme = MaterialTheme.colorScheme): IconButtonColors =
        IconButtonDefaults.iconButtonColors(
            containerColor = colorScheme.onSurface.copy(alpha = 0.16f),
            contentColor = colorScheme.onSurface,
            disabledContainerColor = colorScheme.onSurface.toDisabledColor(disabledAlpha = 0.16f),
            disabledContentColor = colorScheme.onSurface.toDisabledColor(DisabledContentAlpha),
        )

    /** Colors for the settings item badge. */
    @Composable
    public fun badgeColors(colorScheme: ColorScheme = MaterialTheme.colorScheme): IconButtonColors =
        IconButtonDefaults.filledIconButtonColors(
            containerColor = colorScheme.primaryDim,
            contentColor = colorScheme.onPrimary,
            disabledContainerColor = colorScheme.onSurface.toDisabledColor(DisabledContainerAlpha),
            disabledContentColor = colorScheme.onSurface.toDisabledColor(DisabledContentAlpha),
        )

    /** Button colors for [SettingsButton] in the ambient mode. */
    @Composable
    public fun ambientButtonColors(colorScheme: ColorScheme = MaterialTheme.colorScheme): IconButtonColors =
        IconButtonDefaults.iconButtonColors(
            containerColor = Color.Transparent,
            contentColor = colorScheme.onSurface,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = colorScheme.onSurface,
        )

    @Composable
    public fun outlinedButtonBorder(
        colorScheme: ColorScheme = MaterialTheme.colorScheme,
    ): BorderStroke =
        BorderStroke(1.dp, colorScheme.onSurface.toDisabledColor(DisabledContentAlpha))
}

@Composable
internal fun defaultBadgeShape(): Shape = RoundedPolygon.star(
    numVerticesPerRadius = 7,
    innerRadius = .75f,
    rounding = CornerRounding(radius = .5f),
).transformed(Matrix().apply { setRotate(-90f) }).normalized().toShape()

private val BUTTON_WIDTH = 44.dp
private val BUTTON_HEIGHT = 32.dp
private val ICON_SIZE = 24.dp
private val BADGE_SIZE = 18.dp
private val BADGE_ICON_SIZE = 14.dp
