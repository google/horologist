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
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.components.MediaControlButtons
import com.google.android.horologist.media.ui.components.TextMediaDisplay
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.media.ui.state.PlayerViewModel
import com.google.android.horologist.media.ui.utils.StateUtils.rememberStateWithLifecycle

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
    mediaDisplay: PlayerScreenMediaDisplay = PlayerScreenDefaults.defaultMediaDisplay(),
    controlButtons: PlayerScreenControlButtons =
        PlayerScreenDefaults.defaultControlButtons(playerViewModel),
    buttons: @Composable RowScope.(PlayerUiState) -> Unit = {},
    background: @Composable BoxScope.(PlayerUiState) -> Unit = {}
) {
    val playerUiState by rememberStateWithLifecycle(flow = playerViewModel.playerUiState)

    PlayerScreen(
        mediaDisplay = { with(mediaDisplay) { Content(playerUiState) } },
        controlButtons = { with(controlButtons) { Content(playerUiState) } },
        buttons = { buttons(playerUiState) },
        modifier = modifier,
        background = { background(playerUiState) },
    )
}

/**
 * Represents the content of media display on [PlayerScreen].
 */
@ExperimentalHorologistMediaUiApi
public interface PlayerScreenMediaDisplay {

    @Composable
    public fun ColumnScope.Content(playerUiState: PlayerUiState)
}

/**
 * Represents the content of control buttons on [PlayerScreen].
 */
@ExperimentalHorologistMediaUiApi
public interface PlayerScreenControlButtons {

    @Composable
    public fun RowScope.Content(playerUiState: PlayerUiState)
}

/**
 * Contains the default values used by [PlayerScreen].
 */
@ExperimentalHorologistMediaUiApi
public object PlayerScreenDefaults {

    public fun defaultMediaDisplay(): PlayerScreenMediaDisplay = DefaultPlayerScreenMediaDisplay()

    public fun customMediaDisplay(
        content: @Composable () -> Unit
    ): PlayerScreenMediaDisplay = object : PlayerScreenMediaDisplay {

        @Composable
        override fun ColumnScope.Content(playerUiState: PlayerUiState) {
            content()
        }
    }

    public fun defaultControlButtons(
        playerViewModel: PlayerViewModel,
        showProgress: Boolean = true
    ): PlayerScreenControlButtons = DefaultPlayerScreenControlButtons(
        onPlayClick = { playerViewModel.play() },
        onPauseClick = { playerViewModel.pause() },
        onSeekToPreviousButtonClick = { playerViewModel.skipToPreviousMediaItem() },
        onSeekToNextButtonClick = { playerViewModel.skipToNextMediaItem() },
        showProgress = showProgress
    )

    public fun customControlButtons(
        content: @Composable () -> Unit
    ): PlayerScreenControlButtons = object : PlayerScreenControlButtons {

        @Composable
        override fun RowScope.Content(playerUiState: PlayerUiState) {
            content()
        }
    }
}

/**
 * Default [PlayerScreenMediaDisplay] implementation.
 */
@ExperimentalHorologistMediaUiApi
private class DefaultPlayerScreenMediaDisplay : PlayerScreenMediaDisplay {

    @Composable
    override fun ColumnScope.Content(playerUiState: PlayerUiState) {
        TextMediaDisplay(
            title = playerUiState.mediaItem?.title,
            artist = playerUiState.mediaItem?.artist
        )
    }
}

/**
 * Default [PlayerScreenControlButtons] implementation.
 */
@ExperimentalHorologistMediaUiApi
private class DefaultPlayerScreenControlButtons(
    private val onPlayClick: () -> Unit,
    private val onPauseClick: () -> Unit,
    private val onSeekToPreviousButtonClick: () -> Unit,
    private val onSeekToNextButtonClick: () -> Unit,
    private val showProgress: Boolean,
) : PlayerScreenControlButtons {

    @Composable
    override fun RowScope.Content(playerUiState: PlayerUiState) {
        MediaControlButtons(
            onPlayButtonClick = onPlayClick,
            onPauseButtonClick = onPauseClick,
            playPauseButtonEnabled = playerUiState.playPauseEnabled,
            playing = playerUiState.playing,
            onSeekToPreviousButtonClick = onSeekToPreviousButtonClick,
            seekToPreviousButtonEnabled = playerUiState.seekToPreviousEnabled,
            onSeekToNextButtonClick = onSeekToNextButtonClick,
            seekToNextButtonEnabled = playerUiState.seekToNextEnabled,
            showProgress = showProgress,
            percent = playerUiState.trackPosition?.percent ?: 0f,
        )
    }
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
