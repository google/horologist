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

@file:OptIn(ExperimentalHorologistMediaUiApi::class)

package com.google.android.horologist.media.ui.screens.playlists

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FeaturedPlayList
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.android.horologist.base.ui.components.StandardChip
import com.google.android.horologist.base.ui.components.StandardChipType
import com.google.android.horologist.base.ui.util.rememberVectorPainter
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.tools.WearPreviewDevices
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel

@WearPreviewDevices
@Composable
fun PlaylistsScreenPreview() {
    PlaylistsScreen(
        columnConfig = ScalingLazyColumnDefaults.belowTimeText().create(),
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
        columnConfig = ScalingLazyColumnDefaults.belowTimeText().create(),
        playlistsScreenState = PlaylistsScreenState.Loading(),
        onPlaylistItemClick = { }
    )
}

@WearPreviewDevices
@Composable
fun PlaylistsScreenPreviewFailed() {
    PlaylistsScreen(
        columnConfig = ScalingLazyColumnDefaults.belowTimeText().create(),
        playlistsScreenState = PlaylistsScreenState.Failed(),
        onPlaylistItemClick = { }
    )
}

@WearPreviewDevices
@Composable
fun PlaylistsScreenPreviewCustomLayout() {
    PlaylistsScreen(
        columnConfig = ScalingLazyColumnDefaults.belowTimeText().create(),
        playlists = listOf(
            Pair("Rock Classics", "Downloading 73%.."),
            Pair("Pop Punk", "Completed")
        ),
        playlistContent = { (name, status) ->
            StandardChip(
                label = name,
                onClick = { },
                secondaryLabel = status,
                chipType = StandardChipType.Primary
            )
        }
    )
}
