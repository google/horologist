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

package com.google.android.horologist.media.ui.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.horologist.compose.layout.StateUtils.rememberStateWithLifecycle
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.components.MediaControlButtons
import com.google.android.horologist.media.ui.components.TextMediaDisplay
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.media.ui.state.PlayerViewModel

@OptIn(ExperimentalHorologistMediaUiApi::class)
public typealias MediaDisplay = @Composable ColumnScope.(playerUiState: PlayerUiState) -> Unit

@OptIn(ExperimentalHorologistMediaUiApi::class)
public typealias ControlButtons = @Composable RowScope.(playerUiState: PlayerUiState) -> Unit

@OptIn(ExperimentalHorologistMediaUiApi::class)
public typealias SettingsButtons = @Composable RowScope.(playerUiState: PlayerUiState) -> Unit

@OptIn(ExperimentalHorologistMediaUiApi::class)
public typealias PlayerBackground = @Composable BoxScope.(playerUiState: PlayerUiState) -> Unit

/**
 * Stateful version of [PlayerScreen] that provides default implementation for media display and
 * control buttons.
 * This version listens to [PlayerUiState]s emitted from [PlayerViewModel] to update the screen.
 */
@ExperimentalHorologistMediaUiApi
@Composable
public fun PlayerScreen(
    playerViewModel: PlayerViewModel,
    modifier: Modifier = Modifier,
    mediaDisplay: MediaDisplay = { playerUiState ->
        DefaultPlayerScreenMediaDisplay(playerUiState)
    },
    controlButtons: ControlButtons = { playerUiState ->
        DefaultPlayerScreenControlButtons(playerViewModel, playerUiState)
    },
    buttons: SettingsButtons = {},
    background: PlayerBackground = {}
) {
    val playerUiState by rememberStateWithLifecycle(flow = playerViewModel.playerUiState)

    PlayerScreen(
        mediaDisplay = { mediaDisplay(playerUiState) },
        controlButtons = { controlButtons(playerUiState) },
        buttons = { buttons(playerUiState) },
        modifier = modifier,
        background = { background(playerUiState) },
    )
}

/**
 * Default [MediaDisplay] implementation for [PlayerScreen].
 */
@ExperimentalHorologistMediaUiApi
@Composable
public fun DefaultPlayerScreenMediaDisplay(playerUiState: PlayerUiState) {
    TextMediaDisplay(
        title = playerUiState.mediaItem?.title,
        artist = playerUiState.mediaItem?.artist
    )
}

/**
 * Default [ControlButtons] implementation for [PlayerScreen].
 */
@ExperimentalHorologistMediaUiApi
@Composable
public fun DefaultPlayerScreenControlButtons(
    playerViewModel: PlayerViewModel,
    playerUiState: PlayerUiState,
    showProgress: Boolean = true
) {
    MediaControlButtons(
        onPlayButtonClick = { playerViewModel.play() },
        onPauseButtonClick = { playerViewModel.pause() },
        playPauseButtonEnabled = playerUiState.playPauseEnabled,
        playing = playerUiState.playing,
        onSeekToPreviousButtonClick = { playerViewModel.skipToPreviousMediaItem() },
        seekToPreviousButtonEnabled = playerUiState.seekToPreviousEnabled,
        onSeekToNextButtonClick = { playerViewModel.skipToNextMediaItem() },
        seekToNextButtonEnabled = playerUiState.seekToNextEnabled,
        showProgress = showProgress,
        percent = playerUiState.trackPosition?.percent ?: 0f,
    )
}

/**
 * Media Player screen that offers slots for media display, control buttons, buttons and background.
 */
@ExperimentalHorologistMediaUiApi
@Composable
public fun PlayerScreen(
    mediaDisplay: @Composable ColumnScope.() -> Unit,
    controlButtons: @Composable RowScope.() -> Unit,
    buttons: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    background: @Composable BoxScope.() -> Unit = {}
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        background()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.size(26.dp))

                mediaDisplay()
            }
            Spacer(modifier = Modifier.size(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                controlButtons()
            }
            Spacer(modifier = Modifier.size(8.dp))
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Top
            ) {
                buttons()
            }
        }
    }
}
