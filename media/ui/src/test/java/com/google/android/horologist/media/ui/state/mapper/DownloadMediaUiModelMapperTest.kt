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

package com.google.android.horologist.media.ui.state.mapper

import com.google.android.horologist.media.model.Media
import com.google.android.horologist.media.model.MediaDownload
import com.google.android.horologist.media.ui.state.model.DownloadMediaUiModel
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DownloadMediaUiModelMapperTest {

    @Test
    fun givenStatusIdle_thenMapsCorrectly() {
        // given
        val id = "id"
        val title = "title"
        val artist = "artist"
        val artworkUri = "artworkUri"
        val mediaDownload = MediaDownload(
            media = Media(
                id = id,
                uri = "",
                title = title,
                artist = artist,
                artworkUri = artworkUri,
            ),
            status = MediaDownload.Status.Idle,
            size = MediaDownload.Size.Unknown,
        )

        // when
        val result = DownloadMediaUiModelMapper.map(mediaDownload)

        // then
        assertThat(result).isEqualTo(
            DownloadMediaUiModel.NotDownloaded(
                id = id,
                title = title,
                artist = artist,
                artworkUri = artworkUri,
            ),
        )
    }

    @Test
    fun givenStatusInProgressZeroSizeUnknown_thenMapsCorrectly() {
        // given
        val id = "id"
        val title = "title"
        val artist = "artist"
        val artworkUri = "artworkUri"
        val mediaDownload = MediaDownload(
            media = Media(
                id = id,
                uri = "",
                title = title,
                artist = artist,
                artworkUri = artworkUri,
            ),
            status = MediaDownload.Status.InProgress(progress = 0F),
            size = MediaDownload.Size.Unknown,
        )

        // when
        val result = DownloadMediaUiModelMapper.map(mediaDownload)

        // then
        assertThat(result).isEqualTo(
            DownloadMediaUiModel.Downloading(
                id = id,
                title = title,
                artworkUri = artworkUri,
                progress = DownloadMediaUiModel.Progress.Waiting,
                size = DownloadMediaUiModel.Size.Unknown,
            ),
        )
    }

    @Test
    fun givenStatusCompleted_thenMapsCorrectly() {
        // given
        val id = "id"
        val title = "title"
        val artist = "artist"
        val artworkUri = "artworkUri"
        val mediaDownload = MediaDownload(
            media = Media(
                id = id,
                uri = "",
                title = title,
                artist = artist,
                artworkUri = artworkUri,
            ),
            status = MediaDownload.Status.Completed,
            size = MediaDownload.Size.Unknown,
        )

        // when
        val result = DownloadMediaUiModelMapper.map(mediaDownload)

        // then
        assertThat(result).isEqualTo(
            DownloadMediaUiModel.Downloaded(
                id = id,
                title = title,
                artist = artist,
                artworkUri = artworkUri,
            ),
        )
    }
}
