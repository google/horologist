/*
 * Copyright 2026 The Android Open Source Project
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

package com.google.android.horologist.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.android.horologist.media.ui.material3.components.MediaControlButtons
import com.google.android.horologist.media.ui.material3.components.MediaDetailsButton
import com.google.android.horologist.media.ui.material3.components.PlayPauseButton
import com.google.android.horologist.media.ui.material3.components.animated.MarqueeTextMediaDisplay
import com.google.android.horologist.media.ui.material3.components.display.LoadingMediaDisplay
import com.google.android.horologist.media.ui.material3.components.display.MessageMediaDisplay
import com.google.android.horologist.media.ui.material3.components.display.NothingPlayingDisplay
import com.google.android.horologist.media.ui.material3.composables.PlaceholderButton
import com.google.android.horologist.media.ui.material3.screens.entity.DefaultEntityScreenHeader
import com.google.android.horologist.media.ui.material3.screens.entity.EntityScreen
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel

/**
 * Media — `:media:ui-material3`.
 *
 * The player screen taken apart: the four states its title area can be in, the transport controls
 * under it, and the entity screen a playlist opens into. Wear-only, so the section is just "Media".
 */
private val WeatherWithYou =
  MediaUiModel.Ready(
    id = "1",
    title = "Weather with You",
    subtitle = "Crowded House",
    appLabel = "Horologist Media",
  )

@Composable
private fun Centred(content: @Composable () -> Unit) {
  CatalogWearTheme {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
  }
}

// --- Title area, one sticker per state ------------------------------------------------------

@MediaCatalog
@Composable
internal fun MediaMarqueeTextMediaDisplay() {
  Centred { MarqueeTextMediaDisplay(title = "Weather with You", artist = "Crowded House") }
}

/** The long-title case — the reason the display marquees at all. */
@MediaCatalog
@Composable
internal fun MediaMarqueeTextMediaDisplayLongTitle() {
  Centred {
    MarqueeTextMediaDisplay(
      title = "Everywhere I Go (I See Your Face) — Extended Remaster",
      artist = "The Very Long Band Name Orchestra",
    )
  }
}

@MediaCatalog
@Composable
internal fun MediaLoadingMediaDisplay() {
  Centred { LoadingMediaDisplay() }
}

@MediaCatalog
@Composable
internal fun MediaNothingPlayingDisplay() {
  Centred { NothingPlayingDisplay() }
}

@MediaCatalog
@Composable
internal fun MediaMessageMediaDisplay() {
  Centred { MessageMediaDisplay(message = "Connect to Wi-Fi to download") }
}

// --- Transport controls ---------------------------------------------------------------------

@MediaCatalog
@Composable
internal fun MediaControlButtonsPlaying() {
  Centred {
    MediaControlButtons(
      onPlayButtonClick = {},
      onPauseButtonClick = {},
      playPauseButtonEnabled = true,
      playing = true,
      onSeekToPreviousButtonClick = {},
      seekToPreviousButtonEnabled = true,
      onSeekToNextButtonClick = {},
      seekToNextButtonEnabled = true,
      trackPositionUiModel = TrackPositionUiModel.Hidden,
    )
  }
}

/** Ends of a queue disable the seek buttons — the state most often got wrong. */
@MediaCatalog
@Composable
internal fun MediaControlButtonsPausedAtQueueEnd() {
  Centred {
    MediaControlButtons(
      onPlayButtonClick = {},
      onPauseButtonClick = {},
      playPauseButtonEnabled = true,
      playing = false,
      onSeekToPreviousButtonClick = {},
      seekToPreviousButtonEnabled = false,
      onSeekToNextButtonClick = {},
      seekToNextButtonEnabled = false,
      trackPositionUiModel = TrackPositionUiModel.Hidden,
    )
  }
}

@MediaCatalog
@Composable
internal fun MediaPlayPauseButtonPlaying() {
  Centred { PlayPauseButton(onPlayClick = {}, onPauseClick = {}, playing = true) }
}

@MediaCatalog
@Composable
internal fun MediaPlayPauseButtonPaused() {
  Centred { PlayPauseButton(onPlayClick = {}, onPauseClick = {}, playing = false) }
}

@MediaCatalog
@Composable
internal fun MediaPlayPauseButtonDisabled() {
  Centred {
    PlayPauseButton(onPlayClick = {}, onPauseClick = {}, playing = false, enabled = false)
  }
}

// --- List / screen surfaces -------------------------------------------------------------------

@MediaCatalog
@Composable
internal fun MediaDetailsButton() {
  Centred { MediaDetailsButton(media = WeatherWithYou, onClick = {}) }
}

@MediaCatalog
@Composable
internal fun MediaPlaceholderButton() {
  Centred { PlaceholderButton() }
}

@MediaCatalog
@Composable
internal fun MediaEntityScreen() {
  CatalogWearTheme {
    EntityScreen(
      headerContent = { DefaultEntityScreenHeader(title = "Songs to test with") },
      content = {
        items(3) { index ->
          MediaDetailsButton(
            media = WeatherWithYou.copy(id = index.toString(), title = "Track ${index + 1}"),
            onClick = {},
          )
        }
      },
    )
  }
}
