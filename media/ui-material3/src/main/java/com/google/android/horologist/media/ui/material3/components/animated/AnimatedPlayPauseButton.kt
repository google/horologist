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

package com.google.android.horologist.media.ui.material3.components.animated

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.LocalContentColor
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ProgressIndicatorDefaults
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.google.android.horologist.media.ui.animation.PlaybackProgressAnimation.PLAYBACK_PROGRESS_ANIMATION_SPEC
import com.google.android.horologist.media.ui.material3.composables.UnboundedRippleIconButton
import com.google.android.horologist.media.ui.material3.util.LARGE_DEVICE_PLAYER_SCREEN_MIDDLE_BUTTON_SIZE
import com.google.android.horologist.media.ui.material3.util.LOTTIE_DYNAMIC_PROPERTY_KEY_PATH
import com.google.android.horologist.media.ui.material3.util.SMALL_DEVICE_PLAYER_SCREEN_MIDDLE_BUTTON_SIZE
import com.google.android.horologist.media.ui.material3.util.isLargeScreen
import com.google.android.horologist.media.ui.model.R
import com.google.android.horologist.media.ui.state.ProgressStateHolder
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf

/**
 * A button that animates between a play and pause icon.
 *
 * @param onPlayClick Callback to invoke when the play button is clicked.
 * @param onPauseClick Callback to invoke when the pause button is clicked.
 * @param playing Whether the button should be in the play or pause state.
 * @param modifier The modifier to apply to the button.
 * @param shape The shape of the button.
 * @param colorScheme The color scheme used for the button.
 * @param interactionSource The interaction source to use for the button.
 * @param enabled Whether the button is enabled.
 * @param iconSize The size of the icon.
 * @param progressIndicator The progress indicator to display.
 */
@Composable
public fun AnimatedPlayPauseButton(
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    playing: Boolean,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    interactionSource: MutableInteractionSource? = null,
    enabled: Boolean = true,
    iconSize: Dp = IconButtonDefaults.LargeIconSize,
    progressIndicator: @Composable () -> Unit = {},
) {
    val compositionResult =
        rememberLottieComposition(spec = LottieCompositionSpec.Asset("lottie/M3PlayPause.json"))
    val lottieProgress =
        animateLottieProgressAsState(playing = playing, composition = compositionResult.value)
    val pauseContentDescription =
        stringResource(id = R.string.horologist_pause_button_content_description)
    val playContentDescription =
        stringResource(id = R.string.horologist_play_button_content_description)

    val configuration = LocalConfiguration.current
    val scallopShapeSize = remember(configuration) {
        if (configuration.isLargeScreen) {
            LARGE_DEVICE_PLAYER_SCREEN_MIDDLE_BUTTON_SIZE
        } else {
            SMALL_DEVICE_PLAYER_SCREEN_MIDDLE_BUTTON_SIZE
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        progressIndicator()

        UnboundedRippleIconButton(
            onClick = { if (playing) onPauseClick() else onPlayClick() },
            shape = shape,
            interactionSource = interactionSource,
            modifier = Modifier.fillMaxSize(),
            enabled = enabled,
            rippleRadius = scallopShapeSize / 2f,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
                disabledContainerColor = colorScheme.onSurface.copy(0.12f),
                disabledContentColor = colorScheme.onSurface.copy(0.38f),
            ),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                LottieAnimationWithPlaceholder(
                    lottieCompositionResult = compositionResult,
                    progress = { lottieProgress.value },
                    placeholder = if (playing) LottiePlaceholders.Pause else LottiePlaceholders.Play,
                    contentDescription = if (playing) pauseContentDescription else playContentDescription,
                    modifier = Modifier
                        .size(iconSize)
                        .align(Alignment.Center),
                    dynamicProperties = rememberLottieDynamicProperties(
                        rememberLottieDynamicProperty(
                            property = LottieProperty.COLOR,
                            value = LocalContentColor.current.toArgb(),
                            keyPath = LOTTIE_DYNAMIC_PROPERTY_KEY_PATH,
                        ),
                        rememberLottieDynamicProperty(
                            property = LottieProperty.STROKE_COLOR,
                            value = LocalContentColor.current.toArgb(),
                            keyPath = LOTTIE_DYNAMIC_PROPERTY_KEY_PATH,
                        ),
                        rememberLottieDynamicProperty(
                            property = LottieProperty.OPACITY,
                            value = LocalContentColor.current.opacityForLottieAnimation(),
                            keyPath = LOTTIE_DYNAMIC_PROPERTY_KEY_PATH,
                        ),
                    ),
                )
            }
        }
    }
}

/**
 * A button that animates between a play and pause icon.
 *
 * @param onPlayClick Callback to invoke when the play button is clicked.
 * @param onPauseClick Callback to invoke when the pause button is clicked.
 * @param playing Whether the button should be in the play or pause state.
 * @param trackPositionUiModel The track position UI model.
 * @param colorScheme The color scheme used for the button.
 * @param modifier The modifier to apply to the button.
 * @param enabled Whether the button is enabled.
 * @param interactionSource The interaction source to use for the button.
 * @param isAnyButtonPressed Whether any button is pressed.
 * @param iconSize The size of the icon.
 * @param progressStrokeWidth The width of the progress stroke.
 * @param rotateProgressIndicator The flow of events to rotate the progress indicator.
 */
@Composable
public fun AnimatedPlayPauseProgressButton(
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    playing: Boolean,
    trackPositionUiModel: TrackPositionUiModel,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    isAnyButtonPressed: State<Boolean> = remember { mutableStateOf(false) },
    iconSize: Dp = IconButtonDefaults.LargeIconSize,
    progressStrokeWidth: Dp = 4.dp,
    rotateProgressIndicator: Flow<Unit> = flowOf(), // TODO(b/379052971) Fix song change indicator motion
) {
    val configuration = LocalConfiguration.current
    val transition = updateTransition(
        targetState = isAnyButtonPressed.value,
        label = SHAPE_TRANSITION_LABEL,
    )
    val shapeMorphProgress = animatedScallopShapeProgress(
        transition = transition,
    )
    val infiniteTransition = rememberInfiniteTransition("Rotation transition")
    val rotationProgress = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (shapeMorphProgress.value == 1f && playing) -0.1f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = ROTATION_TRANSITION_LABEL,
    )

    val scallopShape = remember(playing, shapeMorphProgress, trackPositionUiModel.isLoading) {
        RotatingMorphedScallopShape(
            playing = playing && !trackPositionUiModel.isLoading,
            isLargeScreen = configuration.isLargeScreen,
            morphProgress = shapeMorphProgress,
            rotationProgress = rotationProgress,
        )
    }

    AnimatedPlayPauseButton(
        onPlayClick = onPlayClick,
        onPauseClick = onPauseClick,
        enabled = enabled,
        playing = playing,
        modifier = modifier,
        colorScheme = colorScheme,
        interactionSource = interactionSource,
        iconSize = iconSize,
        shape = scallopShape,
    ) {
        if (trackPositionUiModel.isLoading && shapeMorphProgress.value == 1f) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                colors = ProgressIndicatorDefaults.colors(
                    indicatorColor = colorScheme.secondaryDim,
                    trackColor = colorScheme.secondary.copy(0.3f),
                ),
                strokeWidth = progressStrokeWidth,
            )
        } else if (trackPositionUiModel.showProgress) {
            val progress = ProgressStateHolder.fromTrackPositionUiModel(trackPositionUiModel)
            RotatingWavyProgressIndicator(
                playing = playing,
                progress = progress,
                shapeMorphProgress = shapeMorphProgress,
                rotationProgress = rotationProgress,
                strokeWidth = progressStrokeWidth,
                colorScheme = colorScheme,
                modifier = Modifier.fillMaxSize(),
                // .rotate(animateChangeAsRotation(rotateProgressIndicator)),
            )
        }
    }
}

@Composable
private fun animateLottieProgressAsState(
    playing: Boolean,
    composition: LottieComposition?,
): State<Float> {
    val lottieProgress = rememberLottieAnimatable()
    var firstTime by remember { mutableStateOf(true) }

    // Ensures lottie initializes to the correct progress with the playing state.
    LaunchedEffect(firstTime) {
        firstTime = false
        if (playing) {
            lottieProgress.snapTo(progress = 0f)
        } else {
            lottieProgress.snapTo(progress = 1f)
        }
    }

    LaunchedEffect(playing) {
        val targetValue = if (playing) 0f else 1f
        if (lottieProgress.progress < targetValue) {
            lottieProgress.animate(composition, speed = 10f)
        } else if (lottieProgress.progress > targetValue) {
            lottieProgress.animate(composition, speed = -10f)
        }
    }

    return lottieProgress
}

@Composable
private fun animateChangeAsRotation(rotateProgressIndicator: Flow<Unit>): Float {
    val progressIndicatorRotation by produceState(0f, rotateProgressIndicator) {
        rotateProgressIndicator.collectLatest { value += 360 }
    }
    val animatedProgressIndicatorRotation by animateFloatAsState(
        targetValue = progressIndicatorRotation,
        animationSpec = PLAYBACK_PROGRESS_ANIMATION_SPEC,
        label = "Progress Indicator Rotation",
    )
    return animatedProgressIndicatorRotation
}

private const val SHAPE_TRANSITION_LABEL = "Shape transition"
private const val ROTATION_TRANSITION_LABEL = "Rotation transition"
