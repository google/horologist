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
import com.google.android.horologist.media.ui.state.model.DownloadMediaItemUiModel
import com.google.android.horologist.media.ui.state.model.MediaItemUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel
import com.google.android.horologist.media.ui.utils.rememberVectorPainter

@WearPreviewDevices
@Composable
fun EntityScreenPreviewLoading() {
    EntityScreen(
        entityScreenState = EntityScreenState.Loading("Artist name"),
        onDownloadClick = { },
        onDownloadItemClick = { },
        onShuffleClick = { },
        onPlayClick = { },
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState(),
    )
}

@WearPreviewDevices
@Composable
fun EntityScreenPreviewLoadedNoneDownloaded() {
    EntityScreen(
        entityScreenState = EntityScreenState.Loaded(
            playlistUiModel = playlistUiModel,
            downloadList = unavailableDownloads,
            downloading = false,
        ),
        onDownloadClick = { },
        onDownloadItemClick = { },
        onShuffleClick = { },
        onPlayClick = { },
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState(),
        downloadItemArtworkPlaceholder = rememberVectorPainter(
            image = Icons.Default.MusicNote,
            tintColor = Color.Blue,
        )
    )
}

@WearPreviewDevices
@Composable
fun EntityScreenPreviewLoadedNoneDownloadedDownloading() {
    EntityScreen(
        entityScreenState = EntityScreenState.Loaded(
            playlistUiModel = playlistUiModel,
            downloadList = unavailableDownloads,
            downloading = true,
        ),
        onDownloadClick = { },
        onDownloadItemClick = { },
        onShuffleClick = { },
        onPlayClick = { },
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState(),
        downloadItemArtworkPlaceholder = rememberVectorPainter(
            image = Icons.Default.MusicNote,
            tintColor = Color.Blue,
        )
    )
}

@WearPreviewDevices
@Composable
fun EntityScreenPreviewLoadedPartiallyDownloaded() {
    EntityScreen(
        entityScreenState = EntityScreenState.Loaded(
            playlistUiModel = playlistUiModel,
            downloadList = mixedDownloads,
            downloading = false,
        ),
        onDownloadClick = { },
        onDownloadItemClick = { },
        onShuffleClick = { },
        onPlayClick = { },
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState(),
        downloadItemArtworkPlaceholder = rememberVectorPainter(
            image = Icons.Default.MusicNote,
            tintColor = Color.Blue,
        )
    )
}

@WearPreviewDevices
@Composable
fun EntityScreenPreviewLoadedPartiallyDownloadedDownloading() {
    EntityScreen(
        entityScreenState = EntityScreenState.Loaded(
            playlistUiModel = playlistUiModel,
            downloadList = mixedDownloads,
            downloading = true,
        ),
        onDownloadClick = { },
        onDownloadItemClick = { },
        onShuffleClick = { },
        onPlayClick = { },
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState(),
        downloadItemArtworkPlaceholder = rememberVectorPainter(
            image = Icons.Default.MusicNote,
            tintColor = Color.Blue,
        )
    )
}

@WearPreviewDevices
@Composable
fun EntityScreenPreviewLoadedFullyDownloaded() {
    EntityScreen(
        entityScreenState = EntityScreenState.Loaded(
            playlistUiModel = playlistUiModel,
            downloadList = availableDownloads,
            downloading = false,
        ),
        onDownloadClick = { },
        onDownloadItemClick = { },
        onShuffleClick = { },
        onPlayClick = { },
        focusRequester = FocusRequester(),
        scalingLazyListState = rememberScalingLazyListState(),
        downloadItemArtworkPlaceholder = rememberVectorPainter(
            image = Icons.Default.MusicNote,
            tintColor = Color.Blue,
        )
    )
}

private val unavailableDownloads = listOf(
    DownloadMediaItemUiModel.Unavailable(
        MediaItemUiModel(
            id = "id",
            title = "Song name",
            artist = "Artist name",
            artworkUri = "artworkUri",
        )
    ),
    DownloadMediaItemUiModel.Unavailable(
        MediaItemUiModel(
            id = "id 2",
            title = "Song name 2",
            artist = "Artist name 2",
            artworkUri = "artworkUri",
        )
    ),
)

private val playlistUiModel = PlaylistUiModel(
    id = "id",
    title = "Playlist name"
)

private val mixedDownloads = listOf(
    DownloadMediaItemUiModel.Available(
        MediaItemUiModel(
            id = "id",
            title = "Song name",
            artist = "Artist name",
            artworkUri = "artworkUri",
        )
    ),
    DownloadMediaItemUiModel.Unavailable(
        MediaItemUiModel(
            id = "id 2",
            title = "Song name 2",
            artist = "Artist name 2",
            artworkUri = "artworkUri",
        )
    ),
)

private val availableDownloads = listOf(
    DownloadMediaItemUiModel.Available(
        MediaItemUiModel(
            id = "id",
            title = "Song name",
            artist = "Artist name",
            artworkUri = "artworkUri",
        )
    ),
    DownloadMediaItemUiModel.Available(
        MediaItemUiModel(
            id = "id 2",
            title = "Song name 2",
            artist = "Artist name 2",
            artworkUri = "artworkUri",
        )
    ),
)
