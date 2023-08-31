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

package com.google.android.horologist.media.data.datasource

import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.data.database.dao.MediaDownloadDao
import com.google.android.horologist.media.data.database.dao.MediaDownloadDao.Companion.DOWNLOAD_PROGRESS_END
import com.google.android.horologist.media.data.database.dao.MediaDownloadDao.Companion.DOWNLOAD_PROGRESS_START
import com.google.android.horologist.media.data.database.dao.MediaDownloadDao.Companion.SIZE_UNKNOWN
import com.google.android.horologist.media.data.database.model.MediaDownloadEntity
import com.google.android.horologist.media.data.database.model.MediaDownloadEntityStatus
import com.google.android.horologist.media.model.MediaDownload
import kotlinx.coroutines.flow.Flow

/**
 * Local data source of [MediaDownload].
 */
@ExperimentalHorologistApi
public class MediaDownloadLocalDataSource(
    private val mediaDownloadDao: MediaDownloadDao,
) {

    public fun get(mediaIds: List<String>): Flow<List<MediaDownloadEntity>> =
        mediaDownloadDao.getList(mediaIds)

    public suspend fun getAllDownloading(): List<MediaDownloadEntity> =
        mediaDownloadDao.getAllByStatus(MediaDownloadEntityStatus.Downloading)
            .distinctBy { it.mediaId }

    public suspend fun add(mediaId: String) {
        mediaDownloadDao.insert(
            MediaDownloadEntity(
                mediaId = mediaId,
                status = MediaDownloadEntityStatus.NotDownloaded,
                progress = DOWNLOAD_PROGRESS_START,
                size = SIZE_UNKNOWN,
            ),
        )
    }

    public suspend fun delete(mediaId: String) {
        mediaDownloadDao.delete(mediaId)
    }

    public suspend fun updateStatus(mediaId: String, status: MediaDownloadEntityStatus) {
        mediaDownloadDao.updateStatus(mediaId = mediaId, status = status)
    }

    public suspend fun updateProgress(mediaId: String, progress: Float, size: Long) {
        mediaDownloadDao.updateProgress(mediaId = mediaId, progress = progress, size = size)
    }

    public suspend fun setDownloaded(mediaId: String) {
        mediaDownloadDao.updateStatusAndProgress(
            MediaDownloadDao.StatusAndProgress(
                mediaId = mediaId,
                status = MediaDownloadEntityStatus.Downloaded,
                progress = DOWNLOAD_PROGRESS_END,
            ),
        )
    }
}
