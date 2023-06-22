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

package com.google.android.horologist.media.ui.components.animated

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.LocalContentAlpha
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ProgressIndicatorDefaults
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.android.horologist.audio.ui.components.animated.LocalStaticPreview
import com.google.android.horologist.media.ui.R
import com.google.android.horologist.media.ui.animation.PlaybackProgressAnimation.PLAYBACK_PROGRESS_ANIMATION_SPEC
import com.google.android.horologist.media.ui.components.PlayPauseButton
import com.google.android.horologist.media.ui.state.ProgressStateHolder
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
public fun AnimatedPlayPauseButton(
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    playing: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.iconButtonColors(),
    iconSize: Dp = 30.dp,
    tapTargetSize: DpSize = DpSize(60.dp, 60.dp),
    backgroundColor: Color = MaterialTheme.colors.onBackground.copy(alpha = 0.10f),
    progress: @Composable () -> Unit = {}
) {
    if (LocalStaticPreview.current) {
        PlayPauseButton(
            onPlayClick = onPlayClick,
            onPauseClick = onPauseClick,
            playing = playing,
            modifier = modifier,
            enabled = enabled,
            colors = colors,
            iconSize = iconSize,
            tapTargetSize = tapTargetSize,
            progress = progress,
            backgroundColor = backgroundColor
        )
    } else {
        val compositionResult = rememberLottieComposition(
            spec = LottieCompositionSpec.Asset(
                "lottie/PlayPause.json"
            )
        )
        val lottieProgress =
            animateLottieProgressAsState(playing = playing, composition = compositionResult.value)
        Box(
            modifier = modifier
                .size(tapTargetSize)
                .fillMaxSize()
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            progress()
            val pauseContentDescription =
                stringResource(id = R.string.horologist_pause_button_content_description)
            val playContentDescription =
                stringResource(id = R.string.horologist_play_button_content_description)

            Button(
                onClick = { if (playing) onPauseClick() else onPlayClick() },
                modifier = modifier
                    .size(tapTargetSize)
                    .semantics {
                        contentDescription = if (playing) {
                            pauseContentDescription
                        } else {
                            playContentDescription
                        }
                    },
                enabled = enabled,
                colors = colors
            ) {
                val contentModifier = Modifier
                    .size(iconSize)
                    .align(Alignment.Center)
                    .graphicsLayer(alpha = LocalContentAlpha.current)

                LottieAnimationWithPlaceholder(
                    lottieCompositionResult = compositionResult,
                    progress = { lottieProgress.value },
                    placeholder = if (playing) LottiePlaceholders.Pause else LottiePlaceholders.Play,
                    contentDescription = if (playing) pauseContentDescription else playContentDescription,
                    modifier = contentModifier
                )
            }
        }
    }
}

@Composable
private fun animateLottieProgressAsState(
    playing: Boolean,
    composition: LottieComposition?
): State<Float> {
    val lottieProgress = rememberLottieAnimatable()
    var firstTime by remember { mutableStateOf(true) }

    // Ensures lottie initializes to the correct progress with the playing state.
    LaunchedEffect(firstTime) {
        firstTime = false
        if (playing) {
            lottieProgress.snapTo(progress = 1f)
        } else {
            lottieProgress.snapTo(progress = 0f)
        }
    }

    LaunchedEffect(playing) {
        val targetValue = if (playing) 1f else 0f
        if (lottieProgress.progress < targetValue) {
            lottieProgress.animate(composition, speed = 1f)
        } else if (lottieProgress.progress > targetValue) {
            lottieProgress.animate(composition, speed = -1f)
        }
    }

    return lottieProgress
}

@Composable
public fun AnimatedPlayPauseProgressButton(
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    playing: Boolean,
    trackPositionUiModel: TrackPositionUiModel,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.iconButtonColors(),
    iconSize: Dp = 30.dp,
    tapTargetSize: DpSize = DpSize(60.dp, 60.dp),
    progressStrokeWidth: Dp = 4.dp,
    progressColor: Color = MaterialTheme.colors.primary,
    trackColor: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.10f),
    backgroundColor: Color = MaterialTheme.colors.onBackground.copy(alpha = 0.10f),
    rotateProgressIndicator: Flow<Unit> = flowOf()
) {
    val animatedProgressColor = animateColorAsState(
        targetValue = progressColor,
        animationSpec = tween(450, 0, LinearEasing),
        "Progress Colour"
    )

    AnimatedPlayPauseButton(
        onPlayClick = onPlayClick,
        onPauseClick = onPauseClick,
        enabled = enabled,
        playing = playing,
        modifier = modifier,
        colors = colors,
        iconSize = iconSize,
        tapTargetSize = tapTargetSize,
        backgroundColor = backgroundColor
    ) {
        if (trackPositionUiModel.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                indicatorColor = animatedProgressColor.value,
                trackColor = trackColor,
                strokeWidth = progressStrokeWidth
            )
        } else if (trackPositionUiModel.showProgress) {
            val progress = ProgressStateHolder.fromTrackPositionUiModel(trackPositionUiModel)

            CircularProgressIndicatorFast(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(animateChangeAsRotation(rotateProgressIndicator)),
                progress = progress,
                indicatorColor = animatedProgressColor.value,
                trackColor = trackColor,
                strokeWidth = progressStrokeWidth,
                tapTargetSize = tapTargetSize
            )
        }
    }
}

@Composable
private fun animateChangeAsRotation(rotateProgressIndicator: Flow<Unit>): Float {
    val progressIndicatorRotation by produceState(0f, rotateProgressIndicator) {
        rotateProgressIndicator.collectLatest { value += 360 }
    }
    val animatedProgressIndicatorRotation by animateFloatAsState(
        targetValue = progressIndicatorRotation,
        animationSpec = PLAYBACK_PROGRESS_ANIMATION_SPEC,
        label = "Progress Indicator Rotation"
    )
    return animatedProgressIndicatorRotation
}

// From https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:wear/tiles/tiles-material/src/main/java/androidx/wear/tiles/material/CircularProgressIndicator.java?q=CircularProgressIndicator
@Composable
private fun CircularProgressIndicatorFast(
    progress: State<Float>,
    modifier: Modifier = Modifier,
    startAngle: Float = 270f,
    endAngle: Float = startAngle,
    indicatorColor: Color = MaterialTheme.colors.primary,
    trackColor: Color = MaterialTheme.colors.onBackground.copy(alpha = 0.1f),
    strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
    tapTargetSize: DpSize
) {
    val progressSteps = with(LocalDensity.current) {
        (tapTargetSize.width.toPx() * Math.PI).roundToInt()
    }
    val truncatedProgress by remember {
        derivedStateOf {
            roundProgress(
                progress = progress.value,
                progressSteps = progressSteps
            )
        }
    }
    val semanticsProgress by remember {
        derivedStateOf {
            roundProgress(
                progress = progress.value,
                progressSteps = 100
            )
        }
    }

    val stroke = with(LocalDensity.current) {
        Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
    }

    Canvas(
        modifier
            .progressSemantics(semanticsProgress)
            .focusable()
    ) {
        val backgroundSweep = 360f - ((startAngle - endAngle) % 360 + 360) % 360
        val progressSweep = backgroundSweep * truncatedProgress
        // Draw a background
        drawCircularIndicator(
            startAngle,
            backgroundSweep,
            trackColor,
            stroke
        )

        // Draw a progress
        drawCircularIndicator(
            startAngle,
            progressSweep,
            indicatorColor,
            stroke
        )
    }
}

private fun roundProgress(progress: Float, progressSteps: Int) = if (progress == 0f) {
    0f
} else {
    (
        floor(
            progress * progressSteps
        ) / progressSteps
        ).coerceIn(0.001f..1f)
}

private fun DrawScope.drawCircularIndicator(
    startAngle: Float,
    sweep: Float,
    color: Color,
    stroke: Stroke
) {
    // To draw this circle we need a rect with edges that line up with the midpoint of the stroke.
    // To do this we need to remove half the stroke width from the total diameter for both sides.
    val diameter = min(size.width, size.height)
    val diameterOffset = stroke.width / 2
    val arcDimen = diameter - 2 * diameterOffset
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = Offset(
            diameterOffset + (size.width - diameter) / 2,
            diameterOffset + (size.height - diameter) / 2
        ),
        size = Size(arcDimen, arcDimen),
        style = stroke
    )
}
