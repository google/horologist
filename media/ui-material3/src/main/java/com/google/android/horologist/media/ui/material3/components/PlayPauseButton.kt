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

package com.google.android.horologist.media.ui.material3.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.pillStar
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.IconButtonColors
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ProgressIndicatorDefaults
import com.google.android.horologist.media.ui.material3.components.controls.MediaButtonDefaults
import com.google.android.horologist.media.ui.material3.components.controls.PauseButton
import com.google.android.horologist.media.ui.material3.components.controls.PlayButton
import com.google.android.horologist.media.ui.state.ProgressStateHolder
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import kotlin.math.min

/**
 * Play/Pause button which is shown in middle of the [MediaControlButtons].
 *
 * @param onPlayClick Callback to invoke when the play button is clicked.
 * @param onPauseClick Callback to invoke when the pause button is clicked.
 * @param playing Whether the button should be in the play or pause state.
 * @param modifier The modifier to apply to the button.
 * @param colorScheme The color scheme used for the button.
 * @param enabled Whether the button is enabled.
 * @param colors The colors to use for the button.
 * @param iconSize The size of the icon.
 * @param progress The progress indicator to display.
 */
@Composable
public fun PlayPauseButton(
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    playing: Boolean,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    enabled: Boolean = true,
    colors: IconButtonColors = MediaButtonDefaults.playPauseButtonDefaultColors(colorScheme),
    iconSize: Dp = IconButtonDefaults.LargeIconSize,
    progress: @Composable () -> Unit = {},
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        progress()

        if (playing) {
            PauseButton(
                onClick = onPauseClick,
                enabled = enabled,
                modifier = modifier,
                colorScheme = colorScheme,
                iconSize = iconSize,
                colors = colors,
            )
        } else {
            PlayButton(
                onClick = onPlayClick,
                enabled = enabled,
                modifier = modifier,
                colorScheme = colorScheme,
                iconSize = iconSize,
                colors = colors,
            )
        }
    }
}

/**
 * [PlayPauseButton] with a circular progress indicator.
 *
 * @param onPlayClick Callback to invoke when the play button is clicked.
 * @param onPauseClick Callback to invoke when the pause button is clicked.
 * @param playing Whether the button should be in the play or pause state.
 * @param trackPositionUiModel The track position UI model.
 * @param modifier The modifier to apply to the button.
 * @param colorScheme The color scheme used for the button.
 * @param enabled Whether the button is enabled.
 * @param colors The colors to use for the button.
 * @param iconSize The size of the icon.
 * @param progressStrokeWidth The width of the progress indicator stroke.
 * @param indicatorColor The color of the progress indicator.
 * @param trackColor The color of the progress indicator track.
 */
@Composable
public fun PlayPauseProgressButton(
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    playing: Boolean,
    trackPositionUiModel: TrackPositionUiModel,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    enabled: Boolean = true,
    colors: IconButtonColors = MediaButtonDefaults.playPauseButtonDefaultColors(colorScheme),
    iconSize: Dp = IconButtonDefaults.LargeIconSize,
    progressStrokeWidth: Dp = 4.dp,
    indicatorColor: Color = colorScheme.secondaryDim,
    trackColor: Color = colorScheme.secondary.copy(alpha = 0.3f),
) {
    PlayPauseButton(
        onPlayClick = onPlayClick,
        onPauseClick = onPauseClick,
        enabled = enabled,
        playing = playing,
        modifier = modifier,
        colorScheme = colorScheme,
        iconSize = iconSize,
        colors = colors,
    ) {
        val progress by ProgressStateHolder.fromTrackPositionUiModel(trackPositionUiModel)
        if (trackPositionUiModel.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                colors = ProgressIndicatorDefaults.colors(
                    indicatorColor = indicatorColor,
                    trackColor = trackColor,
                ),
                strokeWidth = progressStrokeWidth,
            )
        } else if (trackPositionUiModel.showProgress) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = { progress },
                colors = ProgressIndicatorDefaults.colors(
                    indicatorColor = indicatorColor,
                    trackColor = trackColor,
                ),
                strokeWidth = progressStrokeWidth,
            )
        }
    }
}

/**
 * Defaults object for [PlayPauseButton].
 */
internal object PlayPauseButtonDefaults {

    fun pillPolygon(width: Float, height: Float) = RoundedPolygon(
        vertices = floatArrayOf(
            0f, -height / 2f,
            width / 2f, -height / 2f,
            width / 2f, 0f,
            width / 2f, height / 2f,
            0f, height / 2f,
            -width / 2f, height / 2f,
            -width / 2f, 0f,
            -width / 2f, -height / 2f,
        ),
        rounding = CornerRounding(min(width / 2f, height / 2f)),
        centerX = 0f,
        centerY = 0f,
    )

    fun indicatorScallopPolygon(density: Density, size: Dp): RoundedPolygon {
        with(density) {
            val cornerRadius = 12.dp.toPx()
            return RoundedPolygon.pillStar(
                numVerticesPerRadius = 10,
                width = size.toPx() / 2f,
                height = size.toPx() / 2f,
                innerRadiusRatio = 0.8f,
                rounding = CornerRounding(cornerRadius),
            )
        }
    }

    fun buttonScallopPolygon(density: Density, size: Dp): RoundedPolygon {
        with(density) {
            val cornerRadius = 8.dp.toPx()
            return RoundedPolygon.pillStar(
                numVerticesPerRadius = 10,
                width = size.toPx() / 2f,
                height = size.toPx() / 2f,
                innerRadiusRatio = 0.82f,
                rounding = CornerRounding(cornerRadius),
            )
        }
    }
}
