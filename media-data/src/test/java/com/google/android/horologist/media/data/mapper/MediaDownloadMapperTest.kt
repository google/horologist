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

@file:OptIn(ExperimentalHorologistMediaDataApi::class)

package com.google.android.horologist.media.data.mapper

import com.google.android.horologist.media.data.ExperimentalHorologistMediaDataApi
import com.google.android.horologist.media.data.database.model.MediaDownloadEntity
import com.google.android.horologist.media.data.database.model.MediaDownloadEntityStatus
import com.google.android.horologist.media.model.Media
import com.google.android.horologist.media.model.MediaDownload
import com.google.android.horologist.media.model.Playlist
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MediaDownloadMapperTest {

    @Test
    fun `given MediaDownloadEntity size is unknown then maps correctly`() {
        // given
        val media = Media(
            id = "mediaId",
            uri = "mediaUrl",
            title = "title",
            artist = "artist",
            artworkUri = "artworkUrl"
        )

        val mediaDownloadEntity = MediaDownloadEntity(
            mediaId = "mediaId",
            status = MediaDownloadEntityStatus.NotDownloaded,
            progress = 0F,
            size = -1L
        )

        // when
        val result = MediaDownloadMapper.map(media, mediaDownloadEntity)

        // then
        assertThat(result).isEqualTo(
            MediaDownload(
                media = media,
                status = MediaDownload.Status.Idle,
                size = MediaDownload.Size.Unknown
            )
        )
    }

    @Test
    fun `given MediaDownloadEntity size is known then maps correctly`() {
        // given
        val media = Media(
            id = "mediaId",
            uri = "mediaUrl",
            title = "title",
            artist = "artist",
            artworkUri = "artworkUrl"
        )

        val size = 12345L
        val mediaDownloadEntity = MediaDownloadEntity(
            mediaId = "mediaId",
            status = MediaDownloadEntityStatus.NotDownloaded,
            progress = 0F,
            size = size
        )

        // when
        val result = MediaDownloadMapper.map(media, mediaDownloadEntity)

        // then
        assertThat(result).isEqualTo(
            MediaDownload(
                media = media,
                status = MediaDownload.Status.Idle,
                size = MediaDownload.Size.Known(size)
            )
        )
    }

    @Test
    fun `given Playlist then maps correctly`() {
        // given
        val id = "id"
        val name = "name"
        val artworkUri = "artworkUri"
        val media = Media(
            id = "mediaId",
            uri = "mediaUrl",
            title = "title",
            artist = "artist",
            artworkUri = "artworkUrl"
        )

        val playlist = Playlist(
            id = id,
            name = name,
            artworkUri = artworkUri,
            mediaList = listOf(media)
        )

        val size = 12345L
        val mediaDownloadEntity = MediaDownloadEntity(
            mediaId = "mediaId",
            status = MediaDownloadEntityStatus.NotDownloaded,
            progress = 0F,
            size = size
        )

        // when
        val result = MediaDownloadMapper.map(playlist, listOf(mediaDownloadEntity))

        // then
        assertThat(result).isEqualTo(
            listOf(
                MediaDownload(
                    media = media,
                    status = MediaDownload.Status.Idle,
                    size = MediaDownload.Size.Known(size)
                )
            )
        )
    }
}
