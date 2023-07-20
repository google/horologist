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

package com.google.android.horologist.media.ui.screens.browse

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FeaturedPlayList
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.compose.layout.belowTimeTextPreview
import com.google.android.horologist.compose.material.util.rememberVectorPainter
import com.google.android.horologist.media.ui.state.model.PlaylistDownloadUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel
import com.google.android.horologist.media.ui.uamp.UampTheme

@WearPreviewDevices
@Composable
fun PlaylistDownloadBrowseScreenPreview() {
    PlaylistDownloadBrowseScreen(
        columnState = belowTimeTextPreview(),
        browseScreenState = BrowseScreenState.Loaded(downloadList),
        onDownloadItemClick = { },
        onDownloadItemInProgressClick = { },
        onPlaylistsClick = { },
        onSettingsClick = { },
        downloadItemArtworkPlaceholder = rememberVectorPainter(
            image = Icons.Default.FeaturedPlayList,
            tintColor = Color.Green
        )
    )
}

@WearPreviewDevices
@Composable
fun PlaylistDownloadBrowseScreenPreviewNoDownloads() {
    PlaylistDownloadBrowseScreen(
        columnState = belowTimeTextPreview(),
        browseScreenState = BrowseScreenState.Loaded(emptyList()),
        onDownloadItemClick = { },
        onDownloadItemInProgressClick = { },
        onPlaylistsClick = { },
        onSettingsClick = { }
    )
}

@WearPreviewDevices
@Composable
fun PlaylistDownloadBrowseScreenPreviewLoading() {
    PlaylistDownloadBrowseScreen(
        columnState = belowTimeTextPreview(),
        browseScreenState = BrowseScreenState.Loading,
        onDownloadItemClick = { },
        onDownloadItemInProgressClick = { },
        onPlaylistsClick = { },
        onSettingsClick = { }
    )
}

@WearPreviewDevices
@Composable
fun PlaylistDownloadBrowseScreenPreviewUampTheme() {
    UampTheme {
        PlaylistDownloadBrowseScreen(
            columnState = belowTimeTextPreview(),
            browseScreenState = BrowseScreenState.Loaded(downloadList),
            onDownloadItemClick = { },
            onDownloadItemInProgressClick = { },
            onPlaylistsClick = { },
            onSettingsClick = { },
            downloadItemArtworkPlaceholder = rememberVectorPainter(
                image = Icons.Default.FeaturedPlayList,
                tintColor = Color.Green
            )
        )
    }
}

private val downloadList = buildList {
    add(
        PlaylistDownloadUiModel.InProgress(
            PlaylistUiModel(
                id = "id",
                title = "Rock Classics",
                artworkUri = "https://www.example.com/album1.png"
            ),
            percentage = 15
        )
    )

    add(
        PlaylistDownloadUiModel.Completed(
            PlaylistUiModel(
                id = "id",
                title = "Pop Punk",
                artworkUri = "https://www.example.com/album2.png"
            )
        )
    )
}
