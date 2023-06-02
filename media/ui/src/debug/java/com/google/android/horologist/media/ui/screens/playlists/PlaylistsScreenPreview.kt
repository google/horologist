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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FeaturedPlayList
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.compose.layout.belowTimeTextPreview
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ChipType
import com.google.android.horologist.compose.material.util.rememberVectorPainter
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel

@WearPreviewDevices
@Composable
fun PlaylistsScreenPreview() {
    PlaylistsScreen(
        columnState = belowTimeTextPreview(),
        playlistsScreenState = PlaylistsScreenState.Loaded(
            buildList {
                add(
                    PlaylistUiModel(
                        id = "id",
                        title = "Rock Classics",
                        artworkUri = "https://www.example.com/album1.png"
                    )
                )
                add(
                    PlaylistUiModel(
                        id = "id",
                        title = "Pop Punk",
                        artworkUri = "https://www.example.com/album2.png"
                    )
                )
            }
        ),
        onPlaylistItemClick = { },
        playlistItemArtworkPlaceholder = rememberVectorPainter(
            image = Icons.Default.FeaturedPlayList,
            tintColor = Color.Green
        )
    )
}

@WearPreviewDevices
@Composable
fun PlaylistsScreenPreviewLoading() {
    PlaylistsScreen(
        columnState = belowTimeTextPreview(),
        playlistsScreenState = PlaylistsScreenState.Loading,
        onPlaylistItemClick = { }
    )
}

@WearPreviewDevices
@Composable
fun PlaylistsScreenPreviewFailed() {
    PlaylistsScreen(
        columnState = belowTimeTextPreview(),
        playlistsScreenState = PlaylistsScreenState.Failed,
        onPlaylistItemClick = { }
    )
}

@WearPreviewDevices
@Composable
fun PlaylistsScreenPreviewCustomLayout() {
    PlaylistsScreen(
        columnState = belowTimeTextPreview(),
        playlists = listOf(
            Pair("Rock Classics", "Downloading 73%.."),
            Pair("Pop Punk", "Completed")
        ),
        playlistContent = { (name, status) ->
            Chip(
                label = name,
                onClick = { },
                secondaryLabel = status,
                chipType = ChipType.Primary
            )
        }
    )
}
