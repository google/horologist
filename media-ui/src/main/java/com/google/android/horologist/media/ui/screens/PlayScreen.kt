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
 * Stateful version of [PlayScreen] that provides default implementation for media display and
 * control buttons.
 * This version listens to [PlayerUiState]s emitted from [PlayerViewModel] to update the screen.
 */
@ExperimentalHorologistMediaUiApi
@Composable
public fun PlayScreen(
    playerViewModel: PlayerViewModel,
    modifier: Modifier = Modifier,
    mediaDisplay: PlayScreenMediaDisplay = PlayScreenDefaults.defaultMediaDisplay(),
    controlButtons: PlayScreenControlButtons =
        PlayScreenDefaults.defaultControlButtons(playerViewModel),
    buttons: @Composable RowScope.(PlayerUiState) -> Unit = {},
    background: @Composable BoxScope.(PlayerUiState) -> Unit = {}
) {
    val playerUiState by rememberStateWithLifecycle(flow = playerViewModel.playerUiState)

    PlayScreen(
        mediaDisplay = mediaDisplay.content(playerUiState),
        controlButtons = controlButtons.content(playerUiState),
        buttons = { buttons(playerUiState) },
        modifier = modifier,
        background = { background(playerUiState) },
    )
}

/**
 * Represents the content of control buttons on [PlayScreen].
 */
@ExperimentalHorologistMediaUiApi
public interface PlayScreenControlButtons {

    @Composable
    public fun content(playerUiState: PlayerUiState): @Composable RowScope.() -> Unit
}

/**
 * Represents the content of media display on [PlayScreen].
 */
@ExperimentalHorologistMediaUiApi
public interface PlayScreenMediaDisplay {

    @Composable
    public fun content(playerUiState: PlayerUiState): @Composable ColumnScope.() -> Unit
}

/**
 * Contains the default values used by [PlayScreen].
 */
@ExperimentalHorologistMediaUiApi
public object PlayScreenDefaults {

    public fun defaultMediaDisplay(): PlayScreenMediaDisplay = DefaultPlayScreenMediaDisplay()

    public fun customMediaDisplay(
        content: @Composable ColumnScope.() -> Unit
    ): PlayScreenMediaDisplay = object : PlayScreenMediaDisplay {

        @Composable
        override fun content(playerUiState: PlayerUiState): @Composable ColumnScope.() -> Unit = content
    }

    public fun defaultControlButtons(
        playerViewModel: PlayerViewModel,
        showProgress: Boolean = true
    ): PlayScreenControlButtons = DefaultPlayScreenControlButtons(
        onPlayClick = { playerViewModel.prepareAndPlay() },
        onPauseClick = { playerViewModel.pause() },
        onSeekToPreviousButtonClick = { playerViewModel.seekToPreviousMediaItem() },
        onSeekToNextButtonClick = { playerViewModel.seekToNextMediaItem() },
        showProgress = showProgress
    )

    public fun customControlButtons(
        content: @Composable RowScope.() -> Unit
    ): PlayScreenControlButtons = object : PlayScreenControlButtons {

        @Composable
        override fun content(playerUiState: PlayerUiState): @Composable (RowScope.() -> Unit) = content
    }
}

/**
 * Default [PlayScreenMediaDisplay] implementation.
 */
@ExperimentalHorologistMediaUiApi
private class DefaultPlayScreenMediaDisplay : PlayScreenMediaDisplay {

    @Composable
    override fun content(playerUiState: PlayerUiState): @Composable ColumnScope.() -> Unit = {
        TextMediaDisplay(
            title = playerUiState.mediaItem?.title,
            artist = playerUiState.mediaItem?.artist
        )
    }
}

/**
 * Default [PlayScreenControlButtons] implementation.
 */
@ExperimentalHorologistMediaUiApi
private class DefaultPlayScreenControlButtons(
    private val onPlayClick: () -> Unit,
    private val onPauseClick: () -> Unit,
    private val onSeekToPreviousButtonClick: () -> Unit,
    private val onSeekToNextButtonClick: () -> Unit,
    private val showProgress: Boolean,
) : PlayScreenControlButtons {

    @Composable
    override fun content(playerUiState: PlayerUiState): @Composable RowScope.() -> Unit = {
        MediaControlButtons(
            onPlayClick = onPlayClick,
            onPauseClick = onPauseClick,
            playPauseEnabled = playerUiState.playPauseEnabled,
            playing = playerUiState.playing,
            percent = playerUiState.trackPosition?.percent ?: 0f,
            onSeekToPreviousButtonClick = onSeekToPreviousButtonClick,
            seekToPreviousButtonEnabled = playerUiState.seekToPreviousEnabled,
            onSeekToNextButtonClick = onSeekToNextButtonClick,
            seekToNextButtonEnabled = playerUiState.seekToNextEnabled,
            showProgress = showProgress,
        )
    }
}

/**
 * Media Play screen that offers slots for media display, control buttons, buttons and background.
 */
@ExperimentalHorologistMediaUiApi
@Composable
public fun PlayScreen(
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
