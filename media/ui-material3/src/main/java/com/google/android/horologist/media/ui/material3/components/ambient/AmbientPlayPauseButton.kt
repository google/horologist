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

package com.google.android.horologist.media.ui.material3.components.ambient

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.toPath
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import com.google.android.horologist.media.ui.material3.colorscheme.DisabledContainerAlpha
import com.google.android.horologist.media.ui.material3.colorscheme.toDisabledColor
import com.google.android.horologist.media.ui.material3.components.PlayPauseButtonDefaults
import com.google.android.horologist.media.ui.material3.components.animated.scaleToSize
import com.google.android.horologist.media.ui.material3.components.controls.PauseButton
import com.google.android.horologist.media.ui.material3.components.controls.PlayButton
import com.google.android.horologist.media.ui.material3.util.LARGE_DEVICE_PLAYER_SCREEN_MIDDLE_BUTTON_SIZE
import com.google.android.horologist.media.ui.material3.util.MIDDLE_BUTTON_PROGRESS_STROKE_WIDTH
import com.google.android.horologist.media.ui.material3.util.SMALL_DEVICE_PLAYER_SCREEN_MIDDLE_BUTTON_SIZE
import com.google.android.horologist.media.ui.material3.util.isLargeScreen

/**
 * A play pause button to display in the ambient mode.
 *
 * @param onPlayClick Callback to invoke when the play button is clicked.
 * @param onPauseClick Callback to invoke when the pause button is clicked.
 * @param playing Whether the button should be in the play or pause state.
 * @param colorScheme The [ColorScheme] used for the button.
 * @param modifier The modifier to apply to the button.
 * @param enabled Whether the button is enabled.
 * @param iconSize The size of the icon.
 */
@Composable
public fun AmbientPlayPauseButton(
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    playing: Boolean,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    enabled: Boolean = true,
    iconSize: Dp = IconButtonDefaults.LargeIconSize,
) {
    val density = LocalDensity.current
    val isLargeScreen = LocalConfiguration.current.isLargeScreen
    val scallopSize = remember {
        if (isLargeScreen) {
            LARGE_DEVICE_PLAYER_SCREEN_MIDDLE_BUTTON_SIZE
        } else {
            SMALL_DEVICE_PLAYER_SCREEN_MIDDLE_BUTTON_SIZE
        }
    }
    val scallopHeight = remember(scallopSize) {
        with(density) { (scallopSize - MIDDLE_BUTTON_PROGRESS_STROKE_WIDTH).toPx() }
    }

    val scallopPolygon = remember(scallopSize, scallopHeight) {
        PlayPauseButtonDefaults.indicatorScallopPolygon(density, scallopSize)
            .scaleToSize(scallopHeight)
    }

    val circlePolygon = remember(scallopHeight) {
        PlayPauseButtonDefaults.pillPolygon(scallopHeight, scallopHeight)
    }
    val pathMap = remember { mutableStateMapOf<Boolean, Path>() }
    val path = remember(playing) {
        pathMap.getOrPut(playing) {
            if (playing) {
                scallopPolygon
            } else {
                circlePolygon
            }.toPath().asComposePath()
        }
    }
    val iconButtonColors = IconButtonDefaults.iconButtonColors(
        containerColor = Color.Transparent,
        contentColor = colorScheme.primary,
        disabledContainerColor = Color.Transparent,
        disabledContentColor = colorScheme.onSurface.toDisabledColor(),
    )

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .drawBehind {
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f

                    val translatedPath = Path().apply { addPath(path, Offset(centerX, centerY)) }
                    drawPath(
                        path = translatedPath,
                        color = if (enabled) {
                            colorScheme.primaryDim
                        } else {
                            colorScheme.onSurface.toDisabledColor(DisabledContainerAlpha)
                        },
                        style = Stroke(1.dp.toPx(), cap = StrokeCap.Round),
                    )
                },
        contentAlignment = Alignment.Center,
    ) {
        if (playing) {
            PauseButton(
                onClick = onPauseClick,
                enabled = enabled,
                modifier = Modifier,
                colorScheme = colorScheme,
                iconSize = iconSize,
                colors = iconButtonColors,
            )
        } else {
            PlayButton(
                onClick = onPlayClick,
                enabled = enabled,
                modifier = Modifier,
                colorScheme = colorScheme,
                iconSize = iconSize,
                colors = iconButtonColors,
            )
        }
    }
}
