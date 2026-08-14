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

package com.google.android.horologist.media.ui.material3.screens.playerlibrarypager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.material3.components.actions.VolumeButton
import com.google.android.horologist.media.ui.material3.components.MediaControlButtons
import com.google.android.horologist.media.ui.material3.components.display.TextMediaDisplay
import com.google.android.horologist.media.ui.material3.screens.player.PlayerScreen
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.flowOf

@WearPreviewDevices
@Composable
fun PlayerLibraryPagerScreenPreview() {
  MaterialTheme {
    AppScaffold(
      modifier = Modifier.fillMaxSize().background(Color.Black),
      timeText = { TimeText() },
    ) {
      val pagerState = rememberPagerState(initialPage = 0) { 2 }
      PlayerLibraryPagerScreen(
        pagerState = pagerState,
        volumeUiState = { VolumeUiState(5, 10) },
        displayVolumeIndicatorEvents = flowOf(),
        playerScreen = {
          PlayerScreen(
            mediaDisplay = {
              TextMediaDisplay(title = "Starboy", subtitle = "The Weeknd ft. Daft Punk")
            },
            controlButtons = {
              MediaControlButtons(
                playing = true,
                onPlayButtonClick = {},
                onPauseButtonClick = {},
                onSeekToNextButtonClick = {},
                onSeekToPreviousButtonClick = {},
                playPauseButtonEnabled = true,
                seekToNextButtonEnabled = true,
                seekToPreviousButtonEnabled = true,
                trackPositionUiModel = TrackPositionUiModel.Actual(0.4f, 40.seconds, 100.seconds),
              )
            },
            buttons = { VolumeButton(onVolumeClick = {}, volumeUiState = VolumeUiState(5, 10)) },
          )
        },
        libraryScreen = {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Library Page")
          }
        },
        page = 0,
      )
    }
  }
}
