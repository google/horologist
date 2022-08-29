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

package com.google.android.horologist.mediasample.data.mapper

import com.google.android.horologist.media.model.Media
import com.google.android.horologist.media.model.MediaDownload
import com.google.android.horologist.media.model.Playlist
import com.google.android.horologist.mediasample.data.database.dao.MediaDownloadDao.Companion.SIZE_UNKNOWN
import com.google.android.horologist.mediasample.data.database.model.MediaDownloadEntity

object MediaDownloadMapper {

    fun map(media: Media, mediaDownloadEntity: MediaDownloadEntity): MediaDownload = MediaDownload(
        media = media,
        status = MediaDownloadStatusMapper.map(mediaDownloadEntity),
        size = if (mediaDownloadEntity.size == SIZE_UNKNOWN) {
            MediaDownload.Size.Unknown
        } else {
            MediaDownload.Size.Known(mediaDownloadEntity.size)
        }
    )

    fun map(
        playlist: Playlist,
        mediaDownloadEntityList: List<MediaDownloadEntity>
    ): List<MediaDownload> =
        playlist.mediaList.map { media ->
            mediaDownloadEntityList.find { it.mediaId == media.id }?.let { mediaDownloadEntity ->
                map(media = media, mediaDownloadEntity = mediaDownloadEntity)
            } ?: MediaDownload(
                media = media,
                status = MediaDownload.Status.Idle,
                size = MediaDownload.Size.Unknown
            )
        }
}
