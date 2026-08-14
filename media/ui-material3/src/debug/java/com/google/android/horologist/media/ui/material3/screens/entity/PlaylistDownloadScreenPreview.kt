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

package com.google.android.horologist.media.ui.material3.screens.entity

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.media.ui.state.model.DownloadMediaUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel

@WearPreviewDevices
@Composable
fun PlaylistDownloadScreenPreview() {
  MaterialTheme {
    AppScaffold {
      PlaylistDownloadScreen(
        playlistName = "Rock Classics",
        playlistDownloadScreenState =
          createPlaylistDownloadScreenStateLoaded(
            playlistModel = PlaylistUiModel(id = "rock", title = "Rock Classics"),
            downloadMediaList =
              listOf(
                DownloadMediaUiModel.Downloaded(
                  id = "1",
                  title = "Bohemian Rhapsody",
                  artist = "Queen",
                ),
                DownloadMediaUiModel.Downloading(
                  id = "2",
                  title = "Hotel California",
                  progress = DownloadMediaUiModel.Progress.InProgress(progress = 0.5f),
                  size = DownloadMediaUiModel.Size.Unknown,
                ),
                DownloadMediaUiModel.NotDownloaded(
                  id = "3",
                  title = "Sweet Child O' Mine",
                  artist = "Guns N' Roses",
                ),
              ),
          ),
        onDownloadButtonClick = {},
        onCancelDownloadButtonClick = {},
        onDownloadItemClick = {},
        onDownloadItemInProgressClick = {},
        onShuffleButtonClick = {},
        onPlayButtonClick = {},
      )
    }
  }
}

@WearPreviewDevices
@Composable
fun PlaylistDownloadScreenPreviewLoading() {
  MaterialTheme {
    AppScaffold {
      PlaylistDownloadScreen(
        playlistName = "Rock Classics",
        playlistDownloadScreenState = PlaylistDownloadScreenState.Loading,
        onDownloadButtonClick = {},
        onCancelDownloadButtonClick = {},
        onDownloadItemClick = {},
        onDownloadItemInProgressClick = {},
        onShuffleButtonClick = {},
        onPlayButtonClick = {},
      )
    }
  }
}
