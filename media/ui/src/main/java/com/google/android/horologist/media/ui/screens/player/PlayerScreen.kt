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

@file:OptIn(ExperimentalWearFoundationApi::class)

package com.google.android.horologist.media.ui.screens.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.audio.ui.rotaryVolumeControlsWithFocus
import com.google.android.horologist.compose.rotaryinput.RotaryDefaults.isLowResInput
import com.google.android.horologist.media.ui.components.MediaControlButtons
import com.google.android.horologist.media.ui.components.MediaInfoDisplay
import com.google.android.horologist.media.ui.state.PlayerUiController
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.media.ui.state.PlayerViewModel

public typealias MediaDisplay = @Composable ColumnScope.(playerUiState: PlayerUiState) -> Unit

public typealias ControlButtons = @Composable RowScope.(playerUiController: PlayerUiController, playerUiState: PlayerUiState) -> Unit

public typealias SettingsButtons = @Composable RowScope.(playerUiState: PlayerUiState) -> Unit

public typealias PlayerBackground = @Composable BoxScope.(playerUiState: PlayerUiState) -> Unit

/**
 * Stateful version of [PlayerScreen] that provides default implementation for media display and
 * control buttons.
 * This version listens to [PlayerUiState]s emitted from [PlayerViewModel] to update the screen.
 */
@ExperimentalHorologistApi
@Composable
public fun PlayerScreen(
    playerViewModel: PlayerViewModel,
    volumeViewModel: VolumeViewModel,
    modifier: Modifier = Modifier,
    mediaDisplay: MediaDisplay = { playerUiState ->
        DefaultMediaInfoDisplay(playerUiState)
    },
    controlButtons: ControlButtons = { playerUiController, playerUiState ->
        DefaultPlayerScreenControlButtons(playerUiController, playerUiState)
    },
    buttons: SettingsButtons = {},
    background: PlayerBackground = {},
    focusRequester: FocusRequester = rememberActiveFocusRequester(),
) {
    val playerUiState by playerViewModel.playerUiState.collectAsStateWithLifecycle()
    val volumeUiState by volumeViewModel.volumeUiState.collectAsStateWithLifecycle()

    PlayerScreen(
        mediaDisplay = { mediaDisplay(playerUiState) },
        controlButtons = { controlButtons(playerViewModel.playerUiController, playerUiState) },
        buttons = {
            buttons(playerUiState)
        },
        modifier = modifier.rotaryVolumeControlsWithFocus(
            focusRequester = focusRequester,
            volumeUiStateProvider = { volumeUiState },
            onRotaryVolumeInput = { newVolume -> volumeViewModel.setVolume(newVolume) },
            localView = LocalView.current,
            isLowRes = isLowResInput(),
        ),
        background = { background(playerUiState) },
    )
}

/**
 * Default [MediaDisplay] implementation for [PlayerScreen] including player status.
 */
@ExperimentalHorologistApi
@Composable
public fun DefaultMediaInfoDisplay(
    playerUiState: PlayerUiState,
    modifier: Modifier = Modifier,
) {
    MediaInfoDisplay(
        media = playerUiState.media,
        loading = !playerUiState.connected || playerUiState.media?.loading == true,
        modifier = modifier,
    )
}

/**
 * Default [ControlButtons] implementation for [PlayerScreen].
 */
@ExperimentalHorologistApi
@Composable
public fun DefaultPlayerScreenControlButtons(
    playerController: PlayerUiController,
    playerUiState: PlayerUiState,
) {
    MediaControlButtons(
        onPlayButtonClick = playerController::play,
        onPauseButtonClick = playerController::pause,
        playPauseButtonEnabled = playerUiState.playPauseEnabled,
        playing = playerUiState.playing,
        onSeekToPreviousButtonClick = playerController::skipToPreviousMedia,
        seekToPreviousButtonEnabled = playerUiState.seekToPreviousEnabled,
        onSeekToNextButtonClick = playerController::skipToNextMedia,
        seekToNextButtonEnabled = playerUiState.seekToNextEnabled,
        trackPositionUiModel = playerUiState.trackPositionUiModel,
    )
}

/**
 * Media Player screen that offers slots for media display, control buttons, buttons and background.
 */
@ExperimentalHorologistApi
@Composable
public fun PlayerScreen(
    mediaDisplay: @Composable ColumnScope.() -> Unit,
    controlButtons: @Composable RowScope.() -> Unit,
    buttons: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    background: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        background()

        ConstraintLayout(
            modifier = Modifier.fillMaxSize(),
        ) {
            val (topSection, middleSection, bottomSection) = createRefs()
            val startGuideline = createGuidelineFromStart(0.0938f)
            val endGuideline = createGuidelineFromEnd(0.0938f)
            val topGuideline = createGuidelineFromTop(0.12f)
            val bottomGuideline = createGuidelineFromBottom(0.063f)

            Column(
                modifier = Modifier
                    .constrainAs(topSection) {
                        top.linkTo(topGuideline)
                        start.linkTo(startGuideline)
                        end.linkTo(endGuideline)
                        bottom.linkTo(middleSection.top)
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                mediaDisplay()
            }
            Row(
                modifier = Modifier
                    .constrainAs(middleSection) {
                        top.linkTo(topGuideline)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(bottomGuideline)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                controlButtons()
            }
            Row(
                modifier = Modifier
                    .constrainAs(bottomSection) {
                        top.linkTo(middleSection.bottom)
                        start.linkTo(startGuideline)
                        end.linkTo(endGuideline)
                        bottom.linkTo(bottomGuideline)
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                buttons()
            }
        }
    }
}
