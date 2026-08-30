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

package com.google.android.horologist.media.ui.screens.playlists

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FeaturedPlayList
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.images.base.util.rememberVectorPainter
import com.google.android.horologist.media.ui.SampleArtworkUri
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel
import ee.schimke.composeai.preview.ScrollMode
import ee.schimke.composeai.preview.ScrollingPreview

@WearPreviewLargeRound
@ScrollingPreview(modes = [ScrollMode.LONG, ScrollMode.END])
@Composable
fun PlaylistsScreenLongPreview() {
  MaterialTheme {
    AppScaffold(
      modifier = Modifier.fillMaxSize().background(Color.Black),
      timeText = { TimeText() },
    ) {
      PlaylistsScreen(
        playlistsScreenState =
          PlaylistsScreenState.Loaded(
            buildList {
              repeat(10) { index ->
                add(PlaylistUiModel(id = "$index", title = "Playlist #$index"))
              }
            }
          ),
        onPlaylistItemClick = {},
      )
    }
  }
}

@WearPreviewDevices
@Composable
fun PlaylistsScreenPreview() {
  PlaylistsScreen(
    playlistsScreenState =
      PlaylistsScreenState.Loaded(
        buildList {
          add(PlaylistUiModel(id = "id", title = "Rock Classics", artworkUri = SampleArtworkUri))
          add(PlaylistUiModel(id = "id", title = "Pop Punk", artworkUri = SampleArtworkUri))
        }
      ),
    onPlaylistItemClick = {},
    playlistItemArtworkPlaceholder =
      rememberVectorPainter(
        image = Icons.AutoMirrored.Default.FeaturedPlayList,
        tintColor = Color.Green,
      ),
  )
}

@WearPreviewDevices
@Composable
fun PlaylistsScreenPreviewLoading() {
  PlaylistsScreen(playlistsScreenState = PlaylistsScreenState.Loading, onPlaylistItemClick = {})
}

@WearPreviewDevices
@Composable
fun PlaylistsScreenPreviewFailed() {
  PlaylistsScreen(playlistsScreenState = PlaylistsScreenState.Failed, onPlaylistItemClick = {})
}

@WearPreviewDevices
@Composable
fun PlaylistsScreenPreviewCustomLayout() {
  PlaylistsScreen(
    playlists = listOf(Pair("Rock Classics", "Downloading 73%.."), Pair("Pop Punk", "Completed")),
    playlistContent = { (name, status) ->
      Chip(label = name, onClick = {}, secondaryLabel = status)
    },
  )
}
