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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimatable
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.audio.ui.components.animated.LocalStaticPreview
import com.google.android.horologist.media.ui.R
import com.google.android.horologist.media.ui.components.PlayPauseButton
import com.google.android.horologist.media.ui.state.ProgressStateHolder
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel

@ExperimentalHorologistApi
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
        val composition: LottieComposition? by rememberLottieComposition(
            spec = LottieCompositionSpec.Asset(
                "lottie/PlayPause.json"
            )
        )
        val lottieProgress =
            animateLottieProgressAsState(playing = playing, composition = composition)

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
                onClick = {
                    if (playing) {
                        onPauseClick()
                    } else {
                        onPlayClick()
                    }
                },
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
                LottieAnimation(
                    modifier = Modifier
                        .size(iconSize)
                        .align(Alignment.Center)
                        .graphicsLayer(alpha = LocalContentAlpha.current),
                    composition = composition,
                    progress = { lottieProgress.value }
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
    val clipSpec = remember { LottieClipSpec.Frame(max = 14) }
    val lottieProgress = animateLottieCompositionAsState(
        composition = composition,
        clipSpec = clipSpec
    ) as LottieAnimatable
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

@ExperimentalHorologistApi
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
    backgroundColor: Color = MaterialTheme.colors.onBackground.copy(alpha = 0.10f)
) {
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
        val progress by ProgressStateHolder.fromTrackPositionUiModel(trackPositionUiModel)
        if (trackPositionUiModel.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                indicatorColor = progressColor,
                trackColor = trackColor,
                strokeWidth = progressStrokeWidth
            )
        } else if (trackPositionUiModel.showProgress) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = progress,
                indicatorColor = progressColor,
                trackColor = trackColor,
                strokeWidth = progressStrokeWidth
            )
        }
    }
}
