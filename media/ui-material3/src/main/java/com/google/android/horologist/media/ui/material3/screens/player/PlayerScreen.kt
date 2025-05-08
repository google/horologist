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

package com.google.android.horologist.media.ui.material3.screens.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.requestFocusOnHierarchyActive
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.audio.ui.material3.volumeRotaryBehavior
import com.google.android.horologist.media.ui.material3.components.MediaControlButtons
import com.google.android.horologist.media.ui.material3.components.MediaInfoDisplay
import com.google.android.horologist.media.ui.material3.util.LARGE_DEVICE_PLAYER_SCREEN_MIDDLE_SECTION_HEIGHT
import com.google.android.horologist.media.ui.material3.util.LARGE_DEVICE_PLAYER_SCREEN_TOP_MARGIN_PERCENTAGE
import com.google.android.horologist.media.ui.material3.util.LARGE_DEVICE_PLAYER_SCREEN_TOP_SECTION_BOTTOM_PADDING
import com.google.android.horologist.media.ui.material3.util.SMALL_DEVICE_PLAYER_SCREEN_MIDDLE_SECTION_HEIGHT
import com.google.android.horologist.media.ui.material3.util.SMALL_DEVICE_PLAYER_SCREEN_TOP_MARGIN_PERCENTAGE
import com.google.android.horologist.media.ui.material3.util.SMALL_DEVICE_PLAYER_SCREEN_TOP_SECTION_BOTTOM_PADDING
import com.google.android.horologist.media.ui.material3.util.SMALL_DEVICE_PLAYER_SCREEN_TOP_SECTION_HEIGHT
import com.google.android.horologist.media.ui.material3.util.getScreenPercentageInDp
import com.google.android.horologist.media.ui.material3.util.isLargeScreen
import com.google.android.horologist.media.ui.state.PlayerUiController
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.media.ui.state.PlayerViewModel
import com.google.android.horologist.media.ui.state.model.MediaUiModel

public typealias MediaDisplay = @Composable (playerUiState: PlayerUiState) -> Unit

public typealias ControlButtons = @Composable (playerUiController: PlayerUiController, playerUiState: PlayerUiState) -> Unit

public typealias SettingsButtons = @Composable (playerUiState: PlayerUiState) -> Unit

public typealias PlayerBackground = @Composable BoxScope.(playerUiState: PlayerUiState) -> Unit

/**
 * Stateful version of [PlayerScreen] that provides default implementation for media display and
 * control buttons.
 * This version listens to [PlayerUiState]s emitted from [PlayerViewModel] to update the screen.
 */
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
    buttons: SettingsButtons = { },
    background: PlayerBackground = {},
    focusRequester: FocusRequester = remember { FocusRequester() },
) {
    val playerUiState by playerViewModel.playerUiState.collectAsStateWithLifecycle()
    val volumeUiState by volumeViewModel.volumeUiState.collectAsStateWithLifecycle()

    PlayerScreen(
        mediaDisplay = { mediaDisplay(playerUiState) },
        controlButtons = { controlButtons(playerViewModel.playerUiController, playerUiState) },
        buttons = { buttons(playerUiState) },
        modifier = modifier
            .requestFocusOnHierarchyActive()
            .rotaryScrollable(
                volumeRotaryBehavior(
                    volumeUiStateProvider = { volumeUiState },
                    onRotaryVolumeInput = { newVolume -> volumeViewModel.setVolume(newVolume) },
                ),
                focusRequester = focusRequester,
            ),
        background = { background(playerUiState) },
    )
}

/**
 * Default [MediaDisplay] implementation for [PlayerScreen] including player status.
 */
@Composable
public fun DefaultMediaInfoDisplay(playerUiState: PlayerUiState, modifier: Modifier = Modifier) {
    MediaInfoDisplay(
        media = playerUiState.media,
        loading = !playerUiState.connected || playerUiState.media is MediaUiModel.Loading,
        modifier = modifier,
    )
}

/**
 * Default [ControlButtons] implementation for [PlayerScreen].
 */
@Composable
public fun DefaultPlayerScreenControlButtons(
    playerController: PlayerUiController,
    playerUiState: PlayerUiState,
    modifier: Modifier = Modifier,
) {
    MediaControlButtons(
        modifier = modifier,
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
@Composable
public fun PlayerScreen(
    mediaDisplay: @Composable () -> Unit,
    controlButtons: @Composable () -> Unit,
    buttons: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    background: @Composable BoxScope.() -> Unit = {},
) {
    val configuration = LocalConfiguration.current
    val middleSectionMinimumHeight = remember(configuration) {
        if (configuration.isLargeScreen) {
            LARGE_DEVICE_PLAYER_SCREEN_MIDDLE_SECTION_HEIGHT
        } else {
            SMALL_DEVICE_PLAYER_SCREEN_MIDDLE_SECTION_HEIGHT
        }
    }
    val topSectionTopPadding = remember(configuration) {
        configuration.getScreenPercentageInDp(
            if (configuration.isLargeScreen) {
                LARGE_DEVICE_PLAYER_SCREEN_TOP_MARGIN_PERCENTAGE
            } else {
                SMALL_DEVICE_PLAYER_SCREEN_TOP_MARGIN_PERCENTAGE
            },
        )
    }
    val topSectionBottomPadding = remember(configuration) {
        if (configuration.isLargeScreen) {
            LARGE_DEVICE_PLAYER_SCREEN_TOP_SECTION_BOTTOM_PADDING
        } else {
            SMALL_DEVICE_PLAYER_SCREEN_TOP_SECTION_BOTTOM_PADDING
        }
    }
    val topSectionMinimumHeight = remember(configuration) {
        if (!configuration.isLargeScreen) {
            SMALL_DEVICE_PLAYER_SCREEN_TOP_SECTION_HEIGHT
        } else {
            Dp.Unspecified
        }
    }
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        background()

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = topSectionMinimumHeight)
                    .weight(1f)
                    .padding(
                        top = topSectionTopPadding,
                        bottom = topSectionBottomPadding,
                    ),
                contentAlignment = Alignment.BottomCenter,
            ) {
                mediaDisplay()
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = middleSectionMinimumHeight)
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                controlButtons()
            }
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                buttons()
            }
        }
    }
}
