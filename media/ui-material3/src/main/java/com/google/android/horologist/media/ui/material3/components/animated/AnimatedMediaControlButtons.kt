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

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.wear.compose.material3.ButtonGroupScope
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import com.google.android.horologist.media.ui.material3.components.ButtonGroupLayout
import com.google.android.horologist.media.ui.material3.components.ButtonGroupLayoutDefaults
import com.google.android.horologist.media.ui.material3.util.BUTTON_GROUP_ITEMS_COUNT
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Standard media control buttons, showing [SeekToPreviousButton], [PlayPauseProgressButton] and
 * [SeekToNextButton].
 */
@Composable
public fun AnimatedMediaControlButtons(
    onPlayButtonClick: () -> Unit,
    onPauseButtonClick: () -> Unit,
    playPauseButtonEnabled: Boolean,
    playing: Boolean,
    onSeekToPreviousButtonClick: () -> Unit,
    seekToPreviousButtonEnabled: Boolean,
    onSeekToNextButtonClick: () -> Unit,
    seekToNextButtonEnabled: Boolean,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    onSeekToPreviousRepeatableClick: (() -> Unit)? = null,
    onSeekToPreviousRepeatableClickEnd: (() -> Unit)? = null,
    onSeekToNextRepeatableClick: (() -> Unit)? = null,
    onSeekToNextRepeatableClickEnd: (() -> Unit)? = null,
    trackPositionUiModel: TrackPositionUiModel,
    rotateProgressIndicator: Flow<Unit> = flowOf(),
) {
    val interactionSources = remember { Array(BUTTON_GROUP_ITEMS_COUNT) { MutableInteractionSource() } }
    val buttonPressedStateList = interactionSources.map { it.collectIsPressedAsState() }
    val isAnyButtonPressed = remember {
        derivedStateOf { buttonPressedStateList.any { it.value } }
    }

    val leftButtonPadding = ButtonGroupLayoutDefaults.getSideButtonsPadding(isLeftButton = true)
    val rightButtonPadding = ButtonGroupLayoutDefaults.getSideButtonsPadding(isLeftButton = false)

    ButtonGroupLayout(
        modifier = modifier,
        interactionSources = interactionSources,
        leftButton = {
            AnimatedSeekToPreviousButton(
                modifier = Modifier.animateWidth(it).fillMaxSize(),
                onClick = onSeekToPreviousButtonClick,
                enabled = seekToPreviousButtonEnabled,
                interactionSource = it,
                buttonPadding = leftButtonPadding,
                onRepeatableClick = onSeekToPreviousRepeatableClick,
                onRepeatableClickEnd = onSeekToPreviousRepeatableClickEnd,
            )
        },
        middleButton = {
            if (trackPositionUiModel.showProgress) {
                AnimatedPlayPauseProgressButton(
                    onPlayClick = onPlayButtonClick,
                    onPauseClick = onPauseButtonClick,
                    enabled = playPauseButtonEnabled,
                    playing = playing,
                    interactionSource = it,
                    trackPositionUiModel = trackPositionUiModel,
                    modifier = Modifier.minWidth(ButtonGroupLayoutDefaults.middleButtonSize)
                        .animateWidth(it).fillMaxSize(),
                    colorScheme = colorScheme,
                    rotateProgressIndicator = rotateProgressIndicator,
                    isAnyButtonPressed = isAnyButtonPressed,
                )
            } else {
                AnimatedPlayPauseButton(
                    onPlayClick = onPlayButtonClick,
                    onPauseClick = onPauseButtonClick,
                    enabled = playPauseButtonEnabled,
                    colorScheme = colorScheme,
                    playing = playing,
                    interactionSource = it,
                    modifier = Modifier.minWidth(ButtonGroupLayoutDefaults.middleButtonSize)
                        .animateWidth(it).fillMaxSize(),
                )
            }
        },
        rightButton = {
            AnimatedSeekToNextButton(
                modifier = Modifier.animateWidth(it).fillMaxSize(),
                onClick = onSeekToNextButtonClick,
                interactionSource = it,
                buttonPadding = rightButtonPadding,
                onRepeatableClick = onSeekToNextRepeatableClick,
                onRepeatableClickEnd = onSeekToNextRepeatableClickEnd,
                enabled = seekToNextButtonEnabled,
            )
        },
    )
}

/**
 * Standard and custom action media control buttons, showing a [PlayPauseProgressButton] as the
 * middle button, and allowing custom buttons to be passed for left and right buttons.
 */
@Composable
public fun AnimatedMediaControlButtons(
    onPlayButtonClick: () -> Unit,
    onPauseButtonClick: () -> Unit,
    playPauseButtonEnabled: Boolean,
    playing: Boolean,
    leftButton: @Composable ButtonGroupScope.(MutableInteractionSource) -> Unit,
    rightButton: @Composable ButtonGroupScope.(MutableInteractionSource) -> Unit,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    trackPositionUiModel: TrackPositionUiModel,
    rotateProgressIndicator: Flow<Unit> = flowOf(),
) {
    val interactionSources = remember { Array(BUTTON_GROUP_ITEMS_COUNT) { MutableInteractionSource() } }
    val buttonPressedStateList = interactionSources.map { it.collectIsPressedAsState() }
    val isAnyButtonPressed = remember {
        derivedStateOf { buttonPressedStateList.any { it.value } }
    }

    ButtonGroupLayout(
        modifier = modifier,
        interactionSources = interactionSources,
        leftButton = leftButton,
        middleButton = {
            if (trackPositionUiModel.showProgress) {
                AnimatedPlayPauseProgressButton(
                    onPlayClick = onPlayButtonClick,
                    onPauseClick = onPauseButtonClick,
                    enabled = playPauseButtonEnabled,
                    playing = playing,
                    interactionSource = it,
                    trackPositionUiModel = trackPositionUiModel,
                    modifier = Modifier.minWidth(ButtonGroupLayoutDefaults.middleButtonSize)
                        .animateWidth(it).fillMaxSize(),
                    colorScheme = colorScheme,
                    rotateProgressIndicator = rotateProgressIndicator,
                    isAnyButtonPressed = isAnyButtonPressed,
                )
            } else {
                AnimatedPlayPauseButton(
                    onPlayClick = onPlayButtonClick,
                    onPauseClick = onPauseButtonClick,
                    enabled = playPauseButtonEnabled,
                    playing = playing,
                    interactionSource = it,
                    modifier = Modifier.minWidth(ButtonGroupLayoutDefaults.middleButtonSize)
                        .animateWidth(it).fillMaxSize(),
                    colorScheme = colorScheme,
                )
            }
        },
        rightButton = rightButton,
    )
}
