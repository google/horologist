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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
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
    focusRequester: FocusRequester = rememberActiveFocusRequester()
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
            isLowRes = isLowResInput()
        ),
        background = { background(playerUiState) }
    )
}

/**
 * Default [MediaDisplay] implementation for [PlayerScreen] including player status.
 */
@ExperimentalHorologistApi
@Composable
public fun DefaultMediaInfoDisplay(
    playerUiState: PlayerUiState,
    modifier: Modifier = Modifier
) {
    MediaInfoDisplay(
        media = playerUiState.media,
        loading = !playerUiState.connected || playerUiState.media?.loading == true,
        modifier = modifier
    )
}

/**
 * Default [ControlButtons] implementation for [PlayerScreen].
 */
@ExperimentalHorologistApi
@Composable
public fun DefaultPlayerScreenControlButtons(
    playerController: PlayerUiController,
    playerUiState: PlayerUiState
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
        trackPositionUiModel = playerUiState.trackPositionUiModel
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
    background: @Composable BoxScope.() -> Unit = {}
) {
    val isBig = LocalConfiguration.current.screenHeightDp > 210
    val isRound = LocalConfiguration.current.isScreenRound

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        background()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.38f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isRound) {
                    Spacer(modifier = Modifier.size(if (isBig) 30.dp else 23.dp))
                } else {
                    Spacer(modifier = Modifier.size(24.dp))
                }

                mediaDisplay()
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                controlButtons()
            }
            val bottomPadding = when {
                !isRound -> 4.dp
                isBig -> 12.dp
                else -> 9.dp
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = bottomPadding)
                    .weight(0.33f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                buttons()
            }
        }
    }
}
