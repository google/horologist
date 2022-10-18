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

@file:OptIn(
    ExperimentalHorologistMediaUiApi::class,
    ExperimentalHorologistPaparazziApi::class,
    ExperimentalHorologistComposeToolsApi::class
)

package com.google.android.horologist.media.ui.screens.entity

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.ScalingLazyListState
import com.google.android.horologist.compose.tools.ExperimentalHorologistComposeToolsApi
import com.google.android.horologist.compose.tools.a11y.ComposeA11yExtension
import com.google.android.horologist.compose.tools.a11y.forceState
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.PlayerLibraryPreview
import com.google.android.horologist.media.ui.state.model.DownloadMediaUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel
import com.google.android.horologist.media.ui.utils.rememberVectorPainter
import com.google.android.horologist.paparazzi.ExperimentalHorologistPaparazziApi
import com.google.android.horologist.paparazzi.RoundNonFullScreenDevice
import com.google.android.horologist.paparazzi.WearPaparazzi
import com.google.android.horologist.paparazzi.WearSnapshotHandler
import com.google.android.horologist.paparazzi.a11y.A11ySnapshotHandler
import com.google.android.horologist.paparazzi.determineHandler
import org.junit.Rule
import org.junit.Test

class PlaylistDownloadScreenA11yScreenshotTest {
    private val maxPercentDifference = 1.0

    private val composeA11yExtension = ComposeA11yExtension()

    @get:Rule
    val paparazzi = WearPaparazzi(
        deviceConfig = RoundNonFullScreenDevice,
        maxPercentDifference = maxPercentDifference,
        renderExtensions = setOf(composeA11yExtension),
        snapshotHandler = WearSnapshotHandler(
            A11ySnapshotHandler(
                delegate = determineHandler(
                    maxPercentDifference = maxPercentDifference
                ),
                accessibilityStateFn = { composeA11yExtension.accessibilityState }
            )
        )
    )

    @Test
    fun playlistDownloadScreenPreviewLoading() {
        val scrollState = ScalingLazyListState()
        scrollState.forceState(0, -40)

        paparazzi.snapshot {
            PlayerLibraryPreview(state = scrollState) {
                PlaylistDownloadScreen(
                    playlistName = "Playlist name",
                    playlistDownloadScreenState = PlaylistDownloadScreenState.Loading(),
                    onDownloadButtonClick = { },
                    onCancelDownloadButtonClick = { },
                    onDownloadItemClick = { },
                    onShuffleButtonClick = { },
                    onPlayButtonClick = { },
                    focusRequester = FocusRequester(),
                    scalingLazyListState = scrollState
                )
            }
        }
    }

    @Test
    fun playlistDownloadScreenPreviewLoadedNoneDownloaded() {
        val scrollState = ScalingLazyListState()
        scrollState.forceState(0, -40)

        paparazzi.snapshot {
            PlayerLibraryPreview(state = scrollState) {
                PlaylistDownloadScreen(
                    playlistName = "Playlist name",
                    playlistDownloadScreenState = createPlaylistDownloadScreenStateLoaded(
                        playlistModel = playlistUiModel,
                        downloadMediaList = notDownloaded
                    ),
                    onDownloadButtonClick = { },
                    onCancelDownloadButtonClick = { },
                    onDownloadItemClick = { },
                    onShuffleButtonClick = { },
                    onPlayButtonClick = { },
                    focusRequester = FocusRequester(),
                    scalingLazyListState = scrollState,
                    downloadItemArtworkPlaceholder = rememberVectorPainter(
                        image = Icons.Default.MusicNote,
                        tintColor = Color.Blue
                    )
                )
            }
        }
    }

    @Test
    fun playlistDownloadScreenPreviewLoadedNoneDownloadedDownloading() {
        val scrollState = ScalingLazyListState()
        scrollState.forceState(0, -40)

        paparazzi.snapshot {
            PlayerLibraryPreview(state = scrollState) {
                PlaylistDownloadScreen(
                    playlistName = "Playlist name",
                    playlistDownloadScreenState = createPlaylistDownloadScreenStateLoaded(
                        playlistModel = playlistUiModel,
                        downloadMediaList = notDownloadedAndDownloading
                    ),
                    onDownloadButtonClick = { },
                    onCancelDownloadButtonClick = { },
                    onDownloadItemClick = { },
                    onShuffleButtonClick = { },
                    onPlayButtonClick = { },
                    focusRequester = FocusRequester(),
                    scalingLazyListState = scrollState,
                    downloadItemArtworkPlaceholder = rememberVectorPainter(
                        image = Icons.Default.MusicNote,
                        tintColor = Color.Blue
                    )
                )
            }
        }
    }

    @Test
    fun playlistDownloadScreenPreviewLoadedPartiallyDownloaded() {
        val scrollState = ScalingLazyListState()
        scrollState.forceState(0, -40)

        paparazzi.snapshot {
            PlayerLibraryPreview(state = scrollState) {
                PlaylistDownloadScreen(
                    playlistName = "Playlist name",
                    playlistDownloadScreenState = createPlaylistDownloadScreenStateLoaded(
                        playlistModel = playlistUiModel,
                        downloadMediaList = downloadedNotDownloaded
                    ),
                    onDownloadButtonClick = { },
                    onCancelDownloadButtonClick = { },
                    onDownloadItemClick = { },
                    onShuffleButtonClick = { },
                    onPlayButtonClick = { },
                    focusRequester = FocusRequester(),
                    scalingLazyListState = scrollState,
                    downloadItemArtworkPlaceholder = rememberVectorPainter(
                        image = Icons.Default.MusicNote,
                        tintColor = Color.Blue
                    )
                )
            }
        }
    }

    @Test
    fun playlistDownloadScreenPreviewLoadedPartiallyDownloadedDownloadingUnknownSize() {
        val scrollState = ScalingLazyListState()
        scrollState.forceState(0, -40)

        paparazzi.snapshot {
            PlayerLibraryPreview(state = scrollState) {
                PlaylistDownloadScreen(
                    playlistName = "Playlist name",
                    playlistDownloadScreenState = createPlaylistDownloadScreenStateLoaded(
                        playlistModel = playlistUiModel,
                        downloadMediaList = downloadedAndDownloadingUnknown
                    ),
                    onDownloadButtonClick = { },
                    onCancelDownloadButtonClick = { },
                    onDownloadItemClick = { },
                    onShuffleButtonClick = { },
                    onPlayButtonClick = { },
                    focusRequester = FocusRequester(),
                    scalingLazyListState = scrollState,
                    downloadItemArtworkPlaceholder = rememberVectorPainter(
                        image = Icons.Default.MusicNote,
                        tintColor = Color.Blue
                    )
                )
            }
        }
    }

    @Test
    fun playlistDownloadScreenPreviewLoadedPartiallyDownloadedDownloadingWaiting() {
        val scrollState = ScalingLazyListState()
        scrollState.forceState(0, -40)

        paparazzi.snapshot {
            PlayerLibraryPreview(state = scrollState) {
                PlaylistDownloadScreen(
                    playlistName = "Playlist name",
                    playlistDownloadScreenState = createPlaylistDownloadScreenStateLoaded(
                        playlistModel = playlistUiModel,
                        downloadMediaList = downloadedAndDownloadingWaiting
                    ),
                    onDownloadButtonClick = { },
                    onCancelDownloadButtonClick = { },
                    onDownloadItemClick = { },
                    onShuffleButtonClick = { },
                    onPlayButtonClick = { },
                    focusRequester = FocusRequester(),
                    scalingLazyListState = scrollState,
                    downloadItemArtworkPlaceholder = rememberVectorPainter(
                        image = Icons.Default.MusicNote,
                        tintColor = Color.Blue
                    )
                )
            }
        }
    }

    @Test
    fun playlistDownloadScreenPreviewLoadedFullyDownloaded() {
        val scrollState = ScalingLazyListState()
        scrollState.forceState(0, -40)

        paparazzi.snapshot {
            PlayerLibraryPreview(state = scrollState) {
                PlaylistDownloadScreen(
                    playlistName = "Playlist name",
                    playlistDownloadScreenState = createPlaylistDownloadScreenStateLoaded(
                        playlistModel = playlistUiModel,
                        downloadMediaList = downloaded
                    ),
                    onDownloadButtonClick = { },
                    onCancelDownloadButtonClick = { },
                    onDownloadItemClick = { },
                    onShuffleButtonClick = { },
                    onPlayButtonClick = { },
                    focusRequester = FocusRequester(),
                    scalingLazyListState = scrollState,
                    downloadItemArtworkPlaceholder = rememberVectorPainter(
                        image = Icons.Default.MusicNote,
                        tintColor = Color.Blue
                    )
                )
            }
        }
    }

    @Test
    fun playlistDownloadScreenPreviewFailed() {
        val scrollState = ScalingLazyListState()
        scrollState.forceState(0, -40)

        paparazzi.snapshot {
            PlayerLibraryPreview(state = scrollState) {
                PlaylistDownloadScreen(
                    playlistName = "Playlist name",
                    playlistDownloadScreenState = PlaylistDownloadScreenState.Failed(),
                    onDownloadButtonClick = { },
                    onCancelDownloadButtonClick = { },
                    onDownloadItemClick = { },
                    onShuffleButtonClick = { },
                    onPlayButtonClick = { },
                    focusRequester = FocusRequester(),
                    scalingLazyListState = scrollState
                )
            }
        }
    }
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
