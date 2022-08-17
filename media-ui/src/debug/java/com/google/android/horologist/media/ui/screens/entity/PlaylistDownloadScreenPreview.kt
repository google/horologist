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

package com.google.android.horologist.media.ui.screens.entity

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.rememberScalingLazyListState
import com.google.android.horologist.compose.tools.WearPreviewDevices
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.state.model.DownloadMediaUiModel
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel
import com.google.android.horologist.media.ui.utils.rememberVectorPainter

@WearPreviewDevices
@Composable
fun PlaylistDownloadScreenPreviewLoading() {
    PlaylistDownloadScreen(
        playlistName = "Playlist name",
        playlistDownloadScreenState = PlaylistDownloadScreenState.Loading(),
        onDownloadClick = { },
        onDownloadItemClick = { },
        onShuffleClick = { },
        onPlayClick = { },
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState()
    )
}

@WearPreviewDevices
@Composable
fun PlaylistDownloadScreenPreviewLoadedNoneDownloaded() {
    PlaylistDownloadScreen(
        playlistName = "Playlist name",
        playlistDownloadScreenState = createPlaylistDownloadScreenStateLoaded(
            playlistModel = playlistUiModel,
            downloadMediaList = unavailableDownloads,
            downloading = false
        ),
        onDownloadClick = { },
        onDownloadItemClick = { },
        onShuffleClick = { },
        onPlayClick = { },
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState(),
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
        playlistName = "Playlist name",
        playlistDownloadScreenState = createPlaylistDownloadScreenStateLoaded(
            playlistModel = playlistUiModel,
            downloadMediaList = unavailableDownloads,
            downloading = true
        ),
        onDownloadClick = { },
        onDownloadItemClick = { },
        onShuffleClick = { },
        onPlayClick = { },
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState(),
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
        playlistName = "Playlist name",
        playlistDownloadScreenState = createPlaylistDownloadScreenStateLoaded(
            playlistModel = playlistUiModel,
            downloadMediaList = mixedDownloads,
            downloading = false
        ),
        onDownloadClick = { },
        onDownloadItemClick = { },
        onShuffleClick = { },
        onPlayClick = { },
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState(),
        downloadItemArtworkPlaceholder = rememberVectorPainter(
            image = Icons.Default.MusicNote,
            tintColor = Color.Blue
        )
    )
}

@WearPreviewDevices
@Composable
fun PlaylistDownloadScreenPreviewLoadedPartiallyDownloadedDownloading() {
    PlaylistDownloadScreen(
        playlistName = "Playlist name",
        playlistDownloadScreenState = createPlaylistDownloadScreenStateLoaded(
            playlistModel = playlistUiModel,
            downloadMediaList = mixedDownloads,
            downloading = true
        ),
        onDownloadClick = { },
        onDownloadItemClick = { },
        onShuffleClick = { },
        onPlayClick = { },
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState(),
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
        playlistName = "Playlist name",
        playlistDownloadScreenState = createPlaylistDownloadScreenStateLoaded(
            playlistModel = playlistUiModel,
            downloadMediaList = availableDownloads,
            downloading = false
        ),
        onDownloadClick = { },
        onDownloadItemClick = { },
        onShuffleClick = { },
        onPlayClick = { },
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState(),
        downloadItemArtworkPlaceholder = rememberVectorPainter(
            image = Icons.Default.MusicNote,
            tintColor = Color.Blue
        )
    )
}

private val unavailableDownloads = listOf(
    DownloadMediaUiModel.Unavailable(
        MediaUiModel(
            id = "id",
            title = "Song name",
            artist = "Artist name",
            artworkUri = "artworkUri"
        )
    ),
    DownloadMediaUiModel.Unavailable(
        MediaUiModel(
            id = "id 2",
            title = "Song name 2",
            artist = "Artist name 2",
            artworkUri = "artworkUri"
        )
    )
)

private val playlistUiModel = PlaylistUiModel(
    id = "id",
    title = "Playlist name"
)

private val mixedDownloads = listOf(
    DownloadMediaUiModel.Available(
        MediaUiModel(
            id = "id",
            title = "Song name",
            artist = "Artist name",
            artworkUri = "artworkUri"
        )
    ),
    DownloadMediaUiModel.Unavailable(
        MediaUiModel(
            id = "id 2",
            title = "Song name 2",
            artist = "Artist name 2",
            artworkUri = "artworkUri"
        )
    )
)

private val availableDownloads = listOf(
    DownloadMediaUiModel.Available(
        MediaUiModel(
            id = "id",
            title = "Song name",
            artist = "Artist name",
            artworkUri = "artworkUri"
        )
    ),
    DownloadMediaUiModel.Available(
        MediaUiModel(
            id = "id 2",
            title = "Song name 2",
            artist = "Artist name 2",
            artworkUri = "artworkUri"
        )
    )
)
