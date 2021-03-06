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
import com.google.android.horologist.media.ui.state.model.MediaUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class EntityScreenStateTest {

    @Test
    fun givenUnavailableDownloads_thenDownloadStateIsNone() {
        // given
        val downloads = listOf(
            DownloadMediaUiModel.Unavailable(
                MediaUiModel(
                    id = "id",
                    title = "Song name",
                    artist = "Artist name",
                    artworkUri = "artworkUri",
                )
            ),
            DownloadMediaUiModel.Unavailable(
                MediaUiModel(
                    id = "id 2",
                    title = "Song name 2",
                    artist = "Artist name 2",
                    artworkUri = "artworkUri",
                )
            ),
        )

        // when
        val result = EntityScreenState.Loaded(
            playlistUiModel = PlaylistUiModel(
                id = "id",
                title = "title",
            ),
            downloadList = downloads,
            downloading = false
        ).downloadsState

        // then
        assertThat(result).isEqualTo(EntityScreenState.Loaded.DownloadsState.None)
    }

    @Test
    fun givenMixedDownloads_thenDownloadStateIsPartially() {
        // given
        val downloads = listOf(
            DownloadMediaUiModel.Available(
                MediaUiModel(
                    id = "id",
                    title = "Song name",
                    artist = "Artist name",
                    artworkUri = "artworkUri",
                )
            ),
            DownloadMediaUiModel.Unavailable(
                MediaUiModel(
                    id = "id 2",
                    title = "Song name 2",
                    artist = "Artist name 2",
                    artworkUri = "artworkUri",
                )
            ),
        )

        // when
        val result = EntityScreenState.Loaded(
            playlistUiModel = PlaylistUiModel(
                id = "id",
                title = "title",
            ),
            downloadList = downloads,
            downloading = false
        ).downloadsState

        // then
        assertThat(result).isEqualTo(EntityScreenState.Loaded.DownloadsState.Partially)
    }

    @Test
    fun givenAvailableDownloads_thenDownloadStateIsFully() {
        // given
        val downloads = listOf(
            DownloadMediaUiModel.Available(
                MediaUiModel(
                    id = "id",
                    title = "Song name",
                    artist = "Artist name",
                    artworkUri = "artworkUri",
                )
            ),
            DownloadMediaUiModel.Available(
                MediaUiModel(
                    id = "id 2",
                    title = "Song name 2",
                    artist = "Artist name 2",
                    artworkUri = "artworkUri",
                )
            ),
        )

        // when
        val result = EntityScreenState.Loaded(
            playlistUiModel = PlaylistUiModel(
                id = "id",
                title = "title",
            ),
            downloadList = downloads,
            downloading = false
        ).downloadsState

        // then
        assertThat(result).isEqualTo(EntityScreenState.Loaded.DownloadsState.Fully)
    }

    @Test
    fun givenEmptyDownloads_thenDownloadStateIsFully() {
        // given
        val downloads = emptyList<DownloadMediaUiModel>()

        // when
        val result = EntityScreenState.Loaded(
            playlistUiModel = PlaylistUiModel(
                id = "id",
                title = "title",
            ),
            downloadList = downloads,
            downloading = false
        ).downloadsState

        // then
        assertThat(result).isEqualTo(EntityScreenState.Loaded.DownloadsState.Fully)
    }
}
