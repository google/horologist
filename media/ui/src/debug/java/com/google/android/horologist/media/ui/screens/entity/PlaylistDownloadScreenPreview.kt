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

package com.google.android.horologist.media.ui.screens.entity

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.compose.layout.belowTimeTextPreview
import com.google.android.horologist.compose.material.util.rememberVectorPainter
import com.google.android.horologist.media.ui.state.model.DownloadMediaUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel

@WearPreviewDevices
@Composable
fun PlaylistDownloadScreenPreviewLoading() {
    PlaylistDownloadScreen(
        columnState = belowTimeTextPreview(),
        playlistName = "Playlist name",
        playlistDownloadScreenState = PlaylistDownloadScreenState.Loading,
        onDownloadButtonClick = { },
        onCancelDownloadButtonClick = { },
        onDownloadItemClick = { },
        onDownloadItemInProgressClick = { },
        onShuffleButtonClick = { },
        onPlayButtonClick = { }
    )
}

@WearPreviewDevices
@Composable
fun PlaylistDownloadScreenPreviewLoadedNoneDownloaded() {
    PlaylistDownloadScreen(
        columnState = belowTimeTextPreview(),
        playlistName = "Playlist name",
        playlistDownloadScreenState = createPlaylistDownloadScreenStateLoaded(
            playlistModel = playlistUiModel,
            downloadMediaList = notDownloaded
        ),
        onDownloadButtonClick = { },
        onCancelDownloadButtonClick = { },
        onDownloadItemClick = { },
        onDownloadItemInProgressClick = { },
        onShuffleButtonClick = { },
        onPlayButtonClick = { },
        downloadItemArtworkPlaceholder = rememberVectorPainter(
            image = Icons.Default.MusicNote,
            tintColor = Color.Blue
        )
    )
}

@WearPreviewDevices
@Composable
fun PlaylistDownloadScreenPreviewLoadedNoneDownloadedDownloading() {
    PlaylistDownloadScreen(
        columnState = belowTimeTextPreview(),
        playlistName = "Playlist name",
        playlistDownloadScreenState = createPlaylistDownloadScreenStateLoaded(
            playlistModel = playlistUiModel,
            downloadMediaList = notDownloadedAndDownloading
        ),
        onDownloadButtonClick = { },
        onCancelDownloadButtonClick = { },
        onDownloadItemClick = { },
        onDownloadItemInProgressClick = { },
        onShuffleButtonClick = { },
        onPlayButtonClick = { },
        downloadItemArtworkPlaceholder = rememberVectorPainter(
            image = Icons.Default.MusicNote,
            tintColor = Color.Blue
        )
    )
}

@WearPreviewDevices
@Composable
fun PlaylistDownloadScreenPreviewLoadedPartiallyDownloaded() {
    PlaylistDownloadScreen(
        columnState = belowTimeTextPreview(),
        playlistName = "Playlist name",
        playlistDownloadScreenState = createPlaylistDownloadScreenStateLoaded(
            playlistModel = playlistUiModel,
            downloadMediaList = downloadedNotDownloaded
        ),
        onDownloadButtonClick = { },
        onCancelDownloadButtonClick = { },
        onDownloadItemClick = { },
        onDownloadItemInProgressClick = { },
        onShuffleButtonClick = { },
        onPlayButtonClick = { },
        downloadItemArtworkPlaceholder = rememberVectorPainter(
            image = Icons.Default.MusicNote,
            tintColor = Color.Blue
        )
    )
}

@WearPreviewDevices
@Composable
fun PlaylistDownloadScreenPreviewLoadedPartiallyDownloadedDownloadingUnknownSize() {
    PlaylistDownloadScreen(
        columnState = belowTimeTextPreview(),
        playlistName = "Playlist name",
        playlistDownloadScreenState = createPlaylistDownloadScreenStateLoaded(
            playlistModel = playlistUiModel,
            downloadMediaList = downloadedAndDownloadingUnknown
        ),
        onDownloadButtonClick = { },
        onCancelDownloadButtonClick = { },
        onDownloadItemClick = { },
        onDownloadItemInProgressClick = { },
        onShuffleButtonClick = { },
        onPlayButtonClick = { },
        downloadItemArtworkPlaceholder = rememberVectorPainter(
            image = Icons.Default.MusicNote,
            tintColor = Color.Blue
        )
    )
}

@WearPreviewDevices
@Composable
fun PlaylistDownloadScreenPreviewLoadedPartiallyDownloadedDownloadingWaiting() {
    PlaylistDownloadScreen(
        columnState = belowTimeTextPreview(),
        playlistName = "Playlist name",
        playlistDownloadScreenState = createPlaylistDownloadScreenStateLoaded(
            playlistModel = playlistUiModel,
            downloadMediaList = downloadedAndDownloadingWaiting
        ),
        onDownloadButtonClick = { },
        onCancelDownloadButtonClick = { },
        onDownloadItemClick = { },
        onDownloadItemInProgressClick = { },
        onShuffleButtonClick = { },
        onPlayButtonClick = { },
        downloadItemArtworkPlaceholder = rememberVectorPainter(
            image = Icons.Default.MusicNote,
            tintColor = Color.Blue
        )
    )
}

@WearPreviewDevices
@Composable
fun PlaylistDownloadScreenPreviewLoadedFullyDownloaded() {
    PlaylistDownloadScreen(
        columnState = belowTimeTextPreview(),
        playlistName = "Playlist name",
        playlistDownloadScreenState = createPlaylistDownloadScreenStateLoaded(
            playlistModel = playlistUiModel,
            downloadMediaList = downloaded
        ),
        onDownloadButtonClick = { },
        onCancelDownloadButtonClick = { },
        onDownloadItemClick = { },
        onDownloadItemInProgressClick = { },
        onShuffleButtonClick = { },
        onPlayButtonClick = { },
        downloadItemArtworkPlaceholder = rememberVectorPainter(
            image = Icons.Default.MusicNote,
            tintColor = Color.Blue
        )
    )
}

@WearPreviewDevices
@Composable
fun PlaylistDownloadScreenPreviewFailed() {
    PlaylistDownloadScreen(
        columnState = belowTimeTextPreview(),
        playlistName = "Playlist name",
        playlistDownloadScreenState = PlaylistDownloadScreenState.Failed,
        onDownloadButtonClick = { },
        onCancelDownloadButtonClick = { },
        onDownloadItemClick = { },
        onDownloadItemInProgressClick = { },
        onShuffleButtonClick = { },
        onPlayButtonClick = { }
    )
}

private val playlistUiModel = PlaylistUiModel(
    id = "id",
    title = "Playlist name"
)

private val notDownloaded = listOf(
    DownloadMediaUiModel.NotDownloaded(
        id = "id",
        title = "Song name",
        artist = "Artist name",
        artworkUri = "artworkUri"
    ),
    DownloadMediaUiModel.NotDownloaded(
        id = "id 2",
        title = "Song name 2",
        artist = "Artist name 2",
        artworkUri = "artworkUri"
    )
)

private val notDownloadedAndDownloading = listOf(
    DownloadMediaUiModel.NotDownloaded(
        id = "id",
        title = "Song name",
        artist = "Artist name",
        artworkUri = "artworkUri"
    ),
    DownloadMediaUiModel.Downloading(
        id = "id 2",
        title = "Song name 2",
        progress = DownloadMediaUiModel.Progress.InProgress(78f),
        size = DownloadMediaUiModel.Size.Known(sizeInBytes = 123456L),
        artworkUri = "artworkUri"
    )
)

private val downloadedAndDownloadingUnknown = listOf(
    DownloadMediaUiModel.Downloaded(
        id = "id",
        title = "Song name",
        artist = "Artist name",
        artworkUri = "artworkUri"
    ),
    DownloadMediaUiModel.Downloading(
        id = "id 2",
        title = "Song name 2",
        progress = DownloadMediaUiModel.Progress.InProgress(78f),
        size = DownloadMediaUiModel.Size.Unknown,
        artworkUri = "artworkUri"
    )
)

private val downloadedAndDownloadingWaiting = listOf(
    DownloadMediaUiModel.Downloaded(
        id = "id",
        title = "Song name",
        artist = "Artist name",
        artworkUri = "artworkUri"
    ),
    DownloadMediaUiModel.Downloading(
        id = "id 2",
        title = "Song name 2",
        progress = DownloadMediaUiModel.Progress.Waiting,
        size = DownloadMediaUiModel.Size.Unknown,
        artworkUri = "artworkUri"
    )
)

private val downloadedNotDownloaded = listOf(
    DownloadMediaUiModel.Downloaded(
        id = "id",
        title = "Song name",
        artist = "Artist name",
        artworkUri = "artworkUri"
    ),
    DownloadMediaUiModel.NotDownloaded(
        id = "id 2",
        title = "Song name 2",
        artist = "Artist name 2",
        artworkUri = "artworkUri"
    )
)

private val downloaded = listOf(
    DownloadMediaUiModel.Downloaded(
        id = "id",
        title = "Song name",
        artist = "Artist name",
        artworkUri = "artworkUri"
    ),
    DownloadMediaUiModel.Downloaded(
        id = "id 2",
        title = "Song name 2",
        artist = "Artist name 2",
        artworkUri = "artworkUri"
    )
)
