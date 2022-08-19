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
import com.google.android.horologist.mediasample.data.database.mapper.MediaDownloadEntityStatusMapper
import com.google.android.horologist.mediasample.data.database.model.MediaDownloadEntityStatus
import com.google.android.horologist.mediasample.data.datasource.MediaDownloadLocalDataSource
import com.google.android.horologist.mediasample.di.annotation.DownloadFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DownloadManagerListener(
    @DownloadFeature private val coroutineScope: CoroutineScope,
    private val mediaDownloadLocalDataSource: MediaDownloadLocalDataSource,
    private val downloadProgressMonitor: DownloadProgressMonitor
) : DownloadManager.Listener {

    override fun onInitialized(downloadManager: DownloadManager) {
        downloadProgressMonitor.start(downloadManager)
    }

    override fun onIdle(downloadManager: DownloadManager) {
        downloadProgressMonitor.stop()
    }

    override fun onDownloadChanged(
        downloadManager: DownloadManager,
        download: Download,
        finalException: Exception?
    ) {
        coroutineScope.launch {
            val mediaId = download.request.id
            val status = MediaDownloadEntityStatusMapper.map(download.state)

            if (status == MediaDownloadEntityStatus.Downloaded) {
                mediaDownloadLocalDataSource.setDownloaded(mediaId)
            } else {
                mediaDownloadLocalDataSource.updateStatus(mediaId, status)
            }
        }

        downloadProgressMonitor.start(downloadManager)
    }

    override fun onDownloadRemoved(downloadManager: DownloadManager, download: Download) {
        coroutineScope.launch {
            val mediaId = download.request.id
            mediaDownloadLocalDataSource.delete(mediaId)
        }
    }

    fun onServiceCreated(downloadManager: DownloadManager) {
        downloadProgressMonitor.start(downloadManager)
    }

    fun onServiceDestroyed() {
        downloadProgressMonitor.stop()
    }
}
