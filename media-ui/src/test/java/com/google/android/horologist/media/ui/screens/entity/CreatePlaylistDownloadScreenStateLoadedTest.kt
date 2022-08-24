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

import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.state.model.DownloadMediaUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CreatePlaylistDownloadScreenStateLoadedTest {

    @Test
    fun givenNoDownloaded_thenDownloadMediaListStateIsNone() {
        // given
        val downloads = listOf(
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
            ),
            DownloadMediaUiModel.Downloading(
                id = "id 3",
                title = "Song name 3",
                artworkUri = "artworkUri",
                progress = DownloadMediaUiModel.Progress.InProgress("60"),
                size = DownloadMediaUiModel.Size.Known(sizeInBytes = 1280049)
            )
        )

        // when
        val result = createPlaylistDownloadScreenStateLoaded(
            playlistModel = PlaylistUiModel(
                id = "id",
                title = "title"
            ),
            downloadMediaList = downloads
        ).downloadMediaListState

        // then
        assertThat(result).isEqualTo(PlaylistDownloadScreenState.Loaded.DownloadMediaListState.None)
    }

    @Test
    fun givenMixed_thenDownloadMediaListStateIsPartially() {
        // given
        val downloads = listOf(
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
            ),
            DownloadMediaUiModel.Downloading(
                id = "id 3",
                title = "Song name 3",
                artworkUri = "artworkUri",
                progress = DownloadMediaUiModel.Progress.InProgress("60"),
                size = DownloadMediaUiModel.Size.Known(sizeInBytes = 1280049)
            )
        )

        // when
        val result = createPlaylistDownloadScreenStateLoaded(
            playlistModel = PlaylistUiModel(
                id = "id",
                title = "title"
            ),
            downloadMediaList = downloads
        ).downloadMediaListState

        // then
        assertThat(result).isEqualTo(PlaylistDownloadScreenState.Loaded.DownloadMediaListState.Partially)
    }

    @Test
    fun givenDownloadedAndDownloading_thenDownloadMediaListStateIsPartially() {
        // given
        val downloads = listOf(
            DownloadMediaUiModel.Downloaded(
                id = "id",
                title = "Song name",
                artist = "Artist name",
                artworkUri = "artworkUri"
            ),
            DownloadMediaUiModel.Downloading(
                id = "id 2",
                title = "Song name 2",
                artworkUri = "artworkUri",
                progress = DownloadMediaUiModel.Progress.InProgress("60"),
                size = DownloadMediaUiModel.Size.Known(sizeInBytes = 1280049)
            )
        )

        // when
        val result = createPlaylistDownloadScreenStateLoaded(
            playlistModel = PlaylistUiModel(
                id = "id",
                title = "title"
            ),
            downloadMediaList = downloads
        ).downloadMediaListState

        // then
        assertThat(result).isEqualTo(PlaylistDownloadScreenState.Loaded.DownloadMediaListState.Partially)
    }

    @Test
    fun givenDownloaded_thenDownloadMediaListStateIsFully() {
        // given
        val downloads = listOf(
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

        // when
        val result = createPlaylistDownloadScreenStateLoaded(
            playlistModel = PlaylistUiModel(
                id = "id",
                title = "title"
            ),
            downloadMediaList = downloads
        ).downloadMediaListState

        // then
        assertThat(result).isEqualTo(PlaylistDownloadScreenState.Loaded.DownloadMediaListState.Fully)
    }

    @Test
    fun givenEmptyDownloads_thenDownloadMediaListStateIsFully() {
        // given
        val downloads = emptyList<DownloadMediaUiModel>()

        // when
        val result = createPlaylistDownloadScreenStateLoaded(
            playlistModel = PlaylistUiModel(
                id = "id",
                title = "title"
            ),
            downloadMediaList = downloads
        ).downloadMediaListState

        // then
        assertThat(result).isEqualTo(PlaylistDownloadScreenState.Loaded.DownloadMediaListState.Fully)
    }

    @Test
    fun givenNoDownloading_thenDownloadsProgressIsIdle() {
        // given
        val downloads = listOf(
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

        // when
        val result = createPlaylistDownloadScreenStateLoaded(
            playlistModel = PlaylistUiModel(
                id = "id",
                title = "title"
            ),
            downloadMediaList = downloads
        ).downloadsProgress

        // then
        assertThat(result).isEqualTo(PlaylistDownloadScreenState.Loaded.DownloadsProgress.Idle)
    }

    @Test
    fun givenDownloading_thenDownloadsProgressIsInProgress() {
        // given
        val downloads = listOf(
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
            ),
            DownloadMediaUiModel.Downloading(
                id = "id 3",
                title = "Song name 3",
                artworkUri = "artworkUri",
                progress = DownloadMediaUiModel.Progress.InProgress("60"),
                size = DownloadMediaUiModel.Size.Known(sizeInBytes = 1280049)
            ),
            DownloadMediaUiModel.Downloading(
                id = "id 4",
                title = "Song name 4",
                artworkUri = "artworkUri",
                progress = DownloadMediaUiModel.Progress.InProgress("60"),
                size = DownloadMediaUiModel.Size.Known(sizeInBytes = 1280049)
            )
        )

        // when
        val result = createPlaylistDownloadScreenStateLoaded(
            playlistModel = PlaylistUiModel(
                id = "id",
                title = "title"
            ),
            downloadMediaList = downloads
        ).downloadsProgress

        // then
        assertThat(result).isEqualTo(
            PlaylistDownloadScreenState.Loaded.DownloadsProgress.InProgress(0.25F)
        )
    }

    @Test
    fun givenEmptyDownloads_thenDownloadsProgressIsIdle() {
        // given
        val downloads = emptyList<DownloadMediaUiModel>()

        // when
        val result = createPlaylistDownloadScreenStateLoaded(
            playlistModel = PlaylistUiModel(
                id = "id",
                title = "title"
            ),
            downloadMediaList = downloads
        ).downloadsProgress

        // then
        assertThat(result).isEqualTo(PlaylistDownloadScreenState.Loaded.DownloadsProgress.Idle)
    }
}
