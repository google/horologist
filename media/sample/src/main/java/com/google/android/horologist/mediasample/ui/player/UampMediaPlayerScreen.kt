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

package com.google.android.horologist.mediasample.ui.player

import androidx.activity.compose.ReportDrawnAfter
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.onehandedgesture.LocalOneHandedGestureEnabled
import androidx.wear.compose.material3.onehandedgesture.OneHandedGestureAction
import androidx.wear.compose.material3.onehandedgesture.OneHandedGestureClickIndicator
import androidx.wear.compose.material3.onehandedgesture.OneHandedGestureClickIndicatorState
import androidx.wear.compose.material3.onehandedgesture.OneHandedGestureConfiguration
import androidx.wear.compose.material3.onehandedgesture.OneHandedGestureIndicatorSize
import androidx.wear.compose.material3.onehandedgesture.OneHandedGesturePriority
import androidx.wear.compose.material3.onehandedgesture.oneHandedGesture
import androidx.wear.compose.material3.onehandedgesture.rememberOneHandedGestureConfiguration
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.audio.ui.components.toAudioOutputUi
import com.google.android.horologist.images.coil.CoilPaintable
import com.google.android.horologist.media.ui.material3.components.ButtonGroupLayout
import com.google.android.horologist.media.ui.material3.components.ButtonGroupLayoutDefaults
import com.google.android.horologist.media.ui.material3.components.PlayPauseButtonContent
import com.google.android.horologist.media.ui.material3.components.PlayPauseProgressButton
import com.google.android.horologist.media.ui.material3.components.PodcastControlButtons
import com.google.android.horologist.media.ui.material3.components.animated.AnimatedMediaInfoDisplay
import com.google.android.horologist.media.ui.material3.components.animated.AnimatedPlayPauseButtonContent
import com.google.android.horologist.media.ui.material3.components.animated.AnimatedPlayPauseProgressButton
import com.google.android.horologist.media.ui.material3.components.animated.AnimatedSeekToNextButton
import com.google.android.horologist.media.ui.material3.components.animated.AnimatedSeekToPreviousButton
import com.google.android.horologist.media.ui.material3.components.background.ArtworkImageBackground
import com.google.android.horologist.media.ui.material3.components.background.ColorBackground
import com.google.android.horologist.media.ui.material3.components.controls.SeekToNextButton
import com.google.android.horologist.media.ui.material3.components.controls.SeekToPreviousButton
import com.google.android.horologist.media.ui.material3.screens.player.DefaultMediaInfoDisplay
import com.google.android.horologist.media.ui.material3.screens.player.PlayerScreen
import com.google.android.horologist.media.ui.state.PlayerUiController
import com.google.android.horologist.media.ui.state.PlayerUiState
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun UampMediaPlayerScreen(
  mediaPlayerScreenViewModel: MediaPlayerScreenViewModel,
  volumeViewModel: VolumeViewModel,
  onVolumeClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val volumeUiState by volumeViewModel.volumeUiState.collectAsStateWithLifecycle()
  val audioOutput by volumeViewModel.audioOutput.collectAsStateWithLifecycle()
  val settingsState by mediaPlayerScreenViewModel.settingsState.collectAsStateWithLifecycle()

  PlayerScreen(
    modifier = modifier,
    background = {
      val artworkColor = (it.media as? MediaUiModel.Ready)?.artworkColor
      if (artworkColor != null) {
        ColorBackground(color = artworkColor, modifier = Modifier.fillMaxSize())
      } else {
        ArtworkImageBackground(
          artwork = (it.media as? MediaUiModel.Ready)?.artwork as? CoilPaintable,
          colorScheme = MaterialTheme.colorScheme,
          modifier = Modifier.fillMaxSize(),
        )
      }
    },
    playerViewModel = mediaPlayerScreenViewModel,
    volumeViewModel = volumeViewModel,
    mediaDisplay = { playerUiState ->
      if (settingsState.animated) {
        AnimatedMediaInfoDisplay(
          media = playerUiState.media,
          loading = !playerUiState.connected || playerUiState.media is MediaUiModel.Loading,
        )
      } else {
        DefaultMediaInfoDisplay(playerUiState)
      }
    },
    buttons = { state ->
      UampSettingsButtons(
        volumeUiState = volumeUiState,
        audioOutputUi = audioOutput.toAudioOutputUi(),
        onVolumeClick = onVolumeClick,
        enabled = state.connected && state.media != null,
      )
    },
    controlButtons = { playerUiController, playerUiState ->
      if (settingsState.podcastControls) {
        PlayerScreenPodcastControlButtons(playerUiController, playerUiState)
      } else {
        UampMediaControlButtons(playerUiController, playerUiState, settingsState.animated)
      }
    },
  )

  ReportDrawnAfter { mediaPlayerScreenViewModel.playerState.filterNotNull().first() }
}

@Composable
public fun PlayerScreenPodcastControlButtons(
  playerUiController: PlayerUiController,
  playerUiState: PlayerUiState,
  modifier: Modifier = Modifier,
) {
  PodcastControlButtons(
    modifier = modifier,
    playerController = playerUiController,
    playerUiState = playerUiState,
  )
}

@Composable
private fun UampMediaControlButtons(
  playerUiController: PlayerUiController,
  playerUiState: PlayerUiState,
  animated: Boolean,
  colorScheme: ColorScheme = MaterialTheme.colorScheme,
) {
  val gestureConfig =
    rememberOneHandedGestureConfiguration(
      action = OneHandedGestureAction.Primary,
      gestureId = "play_pause_gesture",
      priority = OneHandedGesturePriority.Clickable,
    )
  val indicatorState = remember { OneHandedGestureClickIndicatorState() }
  val coroutineScope = rememberCoroutineScope()

  val interactionSources = remember { Array(3) { MutableInteractionSource() } }
  val buttonPressedStateList = interactionSources.map { it.collectIsPressedAsState() }
  val isAnyButtonPressed = remember { derivedStateOf { buttonPressedStateList.any { it.value } } }

  val leftButtonPadding = ButtonGroupLayoutDefaults.getSideButtonsPadding(isLeftButton = true)
  val rightButtonPadding = ButtonGroupLayoutDefaults.getSideButtonsPadding(isLeftButton = false)

  CompositionLocalProvider(LocalOneHandedGestureEnabled provides playerUiState.playPauseEnabled) {
    ButtonGroupLayout(
      interactionSources = interactionSources,
      leftButton = {
        if (animated) {
          AnimatedSeekToPreviousButton(
            modifier = Modifier.weight(1f).animateWidth(it).fillMaxSize(),
            onClick = { playerUiController.skipToPreviousMedia() },
            enabled = playerUiState.seekToPreviousEnabled,
            interactionSource = it,
            buttonPadding = leftButtonPadding,
            onRepeatableClick = { playerUiController.seekBack() },
          )
        } else {
          SeekToPreviousButton(
            modifier = Modifier.weight(1f).padding(leftButtonPadding).fillMaxSize(),
            onClick = { playerUiController.skipToPreviousMedia() },
            enabled = playerUiState.seekToPreviousEnabled,
          )
        }
      },
      middleButton = { interactionSource ->
        val playAction = { playerUiController.play() }
        val pauseAction = { playerUiController.pause() }
        val gestureModifier =
          rememberOneHandedGesture(
              gestureConfiguration = gestureConfig,
              onGestureLabel = if (playerUiState.playing) "pause" else "play",
              interactionSource = interactionSource,
              onGestureAvailable = { coroutineScope.launch { indicatorState.showIndicator() } },
              onGesture = { if (playerUiState.playing) pauseAction() else playAction() },
            )
            .weight(0.5f)
            .minWidth(ButtonGroupLayoutDefaults.middleButtonSize)
            .animateWidth(interactionSource)
            .requiredHeight(ButtonGroupLayoutDefaults.middleButtonSize)

        if (animated) {
          AnimatedPlayPauseProgressButton(
            onPlayClick = playAction,
            onPauseClick = pauseAction,
            enabled = playerUiState.playPauseEnabled,
            playing = playerUiState.playing,
            interactionSource = interactionSource,
            trackPositionUiModel = playerUiState.trackPositionUiModel,
            modifier = gestureModifier,
            colorScheme = colorScheme,
            isAnyButtonPressed = isAnyButtonPressed,
            content = {
              OneHandedGestureClickIndicator(
                gestureConfiguration = gestureConfig,
                state = indicatorState,
                gestureIndicatorSize = OneHandedGestureIndicatorSize.Medium,
                gestureIndicatorTint = colorScheme.onPrimary,
              ) {
                AnimatedPlayPauseButtonContent(playerUiState.playing)
              }
            },
          )
        } else {
          PlayPauseProgressButton(
            onPlayClick = playAction,
            onPauseClick = pauseAction,
            enabled = playerUiState.playPauseEnabled,
            playing = playerUiState.playing,
            interactionSource = interactionSource,
            trackPositionUiModel = playerUiState.trackPositionUiModel,
            modifier = gestureModifier,
            colorScheme = colorScheme,
            content = {
              OneHandedGestureClickIndicator(
                gestureConfiguration = gestureConfig,
                state = indicatorState,
                gestureIndicatorSize = OneHandedGestureIndicatorSize.Medium,
                gestureIndicatorTint = colorScheme.onPrimary,
              ) {
                PlayPauseButtonContent(playerUiState.playing)
              }
            },
          )
        }
      },
      rightButton = {
        if (animated) {
          AnimatedSeekToNextButton(
            modifier = Modifier.weight(1f).animateWidth(it).fillMaxSize(),
            onClick = { playerUiController.skipToNextMedia() },
            interactionSource = it,
            buttonPadding = rightButtonPadding,
            onRepeatableClick = { playerUiController.seekForward() },
            enabled = playerUiState.seekToNextEnabled,
          )
        } else {
          SeekToNextButton(
            modifier = Modifier.weight(1f).padding(rightButtonPadding).fillMaxSize(),
            onClick = { playerUiController.skipToNextMedia() },
            enabled = playerUiState.seekToNextEnabled,
          )
        }
      },
    )
  }
}

@Composable
private fun rememberOneHandedGesture(
  gestureConfiguration: OneHandedGestureConfiguration,
  onGestureLabel: String?,
  interactionSource: MutableInteractionSource? = null,
  onGestureAvailable: () -> Unit,
  onGesture: suspend () -> Unit,
): Modifier {
  val currentOnGestureAvailable by rememberUpdatedState(onGestureAvailable)
  val currentOnGesture by rememberUpdatedState(onGesture)
  return remember(gestureConfiguration, onGestureLabel, interactionSource) {
    Modifier.oneHandedGesture(
      gestureConfiguration = gestureConfiguration,
      onGestureLabel = onGestureLabel,
      interactionSource = interactionSource,
      onGestureAvailable = { currentOnGestureAvailable() },
      onGesture = { currentOnGesture() },
    )
  }
}
