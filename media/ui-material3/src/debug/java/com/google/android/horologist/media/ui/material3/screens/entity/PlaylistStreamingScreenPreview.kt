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
fun PlaylistStreamingScreenPreview() {
  MaterialTheme {
    AppScaffold {
      PlaylistStreamingScreen(
        playlistName = "Daily Mix 1",
        playlistDownloadScreenState =
          createPlaylistDownloadScreenStateLoaded(
            playlistModel = PlaylistUiModel(id = "mix1", title = "Daily Mix 1"),
            downloadMediaList =
              listOf(
                DownloadMediaUiModel.Downloaded(
                  id = "1",
                  title = "Song Title 1",
                  artist = "Artist Name 1",
                ),
                DownloadMediaUiModel.Downloaded(
                  id = "2",
                  title = "Song Title 2",
                  artist = "Artist Name 2",
                ),
              ),
          ),
        onShuffleButtonClick = {},
        onPlayButtonClick = {},
        onPlayItemClick = {},
      )
    }
  }
}
