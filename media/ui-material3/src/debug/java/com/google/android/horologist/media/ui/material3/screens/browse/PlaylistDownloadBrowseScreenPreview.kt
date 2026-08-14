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

package com.google.android.horologist.media.ui.material3.screens.browse

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.media.ui.state.model.PlaylistDownloadUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel

@WearPreviewDevices
@Composable
fun PlaylistDownloadBrowseScreenPreview() {
  MaterialTheme {
    AppScaffold {
      PlaylistDownloadBrowseScreen(
        browseScreenState =
          BrowseScreenState.Loaded(
            downloadList =
              listOf(
                PlaylistDownloadUiModel.Completed(
                  PlaylistUiModel(id = "id", title = "Downloaded Playlist")
                ),
                PlaylistDownloadUiModel.InProgress(
                  PlaylistUiModel(id = "id", title = "Downloading Playlist"),
                  percentage = 65,
                ),
              )
          ),
        onDownloadItemClick = {},
        onDownloadItemInProgressClick = {},
        onPlaylistsClick = {},
        onSettingsClick = {},
      )
    }
  }
}

@WearPreviewDevices
@Composable
fun PlaylistDownloadBrowseScreenPreviewLoading() {
  MaterialTheme {
    AppScaffold {
      PlaylistDownloadBrowseScreen(
        browseScreenState = BrowseScreenState.Loading,
        onDownloadItemClick = {},
        onDownloadItemInProgressClick = {},
        onPlaylistsClick = {},
        onSettingsClick = {},
      )
    }
  }
}
