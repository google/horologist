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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.FilledTonalButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.media.ui.material3.composables.PlaceholderButton
import com.google.android.horologist.media.ui.material3.composables.Section
import com.google.android.horologist.media.ui.model.R

@WearPreviewDevices
@Composable
fun BrowseScreenPreview() {
  MaterialTheme {
    AppScaffold {
      BrowseScreenPreviewSample(
        trendingSectionState = Section.State.Loaded(list = listOf("Mozart", "Beethoven")),
        downloadsSectionState =
          Section.State.Loaded(
            list =
              listOf(
                "Puccini" to "O mio babbino caro",
                "J.S. Bach" to "Toccata and Fugue in D minor",
              )
          ),
      )
    }
  }
}

@WearPreviewDevices
@Composable
fun BrowseScreenPreviewLoading() {
  MaterialTheme {
    AppScaffold {
      BrowseScreenPreviewSample(
        trendingSectionState = Section.State.Loading,
        downloadsSectionState = Section.State.Loading,
      )
    }
  }
}

@WearPreviewDevices
@Composable
fun BrowseScreenPreviewEmpty() {
  MaterialTheme {
    AppScaffold {
      BrowseScreenPreviewSample(
        trendingSectionState = Section.State.Empty,
        downloadsSectionState = Section.State.Empty,
      )
    }
  }
}

@Composable
private fun BrowseScreenPreviewSample(
  trendingSectionState: Section.State<String>,
  downloadsSectionState: Section.State<Pair<String, String>>,
) {
  BrowseScreen {
    button(
      BrowseScreenPlaylistsSectionButton(
        textId = R.string.horologist_browse_library_playlists_button,
        icon = Icons.AutoMirrored.Default.Login,
        onClick = {},
      )
    )

    section(
      state = trendingSectionState,
      titleId = R.string.horologist_browse_library_title,
      emptyMessageId = R.string.horologist_browse_downloads_empty,
      failedMessageId = null,
    ) {
      loaded { item: String ->
        FilledTonalButton(
          label = { Text(item) },
          onClick = {},
          icon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
          modifier = Modifier.fillMaxWidth(),
        )
      }

      loading {
        PlaceholderButton(
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.filledTonalButtonColors(),
        )
      }
    }

    downloadsSection(state = downloadsSectionState) {
      loaded { item ->
        FilledTonalButton(
          label = { Text(item.first) },
          secondaryLabel = { Text(item.second) },
          onClick = {},
          icon = { Icon(imageVector = Icons.Default.MusicNote, contentDescription = null) },
          modifier = Modifier.fillMaxWidth(),
        )
      }

      loading {
        PlaceholderButton(
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.filledTonalButtonColors(),
        )
      }

      footer {
        FilledTonalButton(
          label = { Text(stringResource(id = R.string.horologist_browse_library_playlists)) },
          onClick = {},
          modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        )
      }
    }

    playlistsSection(
      buttons =
        listOf(
          BrowseScreenPlaylistsSectionButton(
            textId = R.string.horologist_browse_library_playlists_button,
            icon = Icons.AutoMirrored.Default.PlaylistPlay,
            onClick = {},
          ),
          BrowseScreenPlaylistsSectionButton(
            textId = R.string.horologist_browse_library_settings_button,
            icon = Icons.Default.Podcasts,
            onClick = {},
          ),
        )
    )
  }
}
