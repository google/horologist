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

package com.google.android.horologist.mediasample.data.service.download

import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import com.google.android.horologist.mediasample.data.database.dao.PlaylistDownloadDao
import com.google.android.horologist.mediasample.data.mapper.PlaylistDownloadStateMapper
import com.google.android.horologist.mediasample.di.annotation.DownloadFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DownloadManagerListener(
    @DownloadFeature private val coroutineScope: CoroutineScope,
    private val playlistDownloadDao: PlaylistDownloadDao,
) : DownloadManager.Listener {

    override fun onDownloadChanged(
        downloadManager: DownloadManager,
        download: Download,
        finalException: Exception?
    ) {
        coroutineScope.launch {
            val mediaItemId = download.request.id
            val status = PlaylistDownloadStateMapper.map(download.state)
            playlistDownloadDao.updateStatusByMediaItemId(mediaItemId, status)
        }
    }

    override fun onDownloadRemoved(downloadManager: DownloadManager, download: Download) {
        coroutineScope.launch {
            val mediaItemId = download.request.id
            playlistDownloadDao.deleteByMediaItemId(mediaItemId)
        }
    }
}
