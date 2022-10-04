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

package com.google.android.horologist.media.data.service.download

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.media3.exoplayer.offline.DownloadManager
import com.google.android.horologist.media.data.ExperimentalHorologistMediaDataApi
import com.google.android.horologist.media.data.database.dao.MediaDownloadDao.Companion.DOWNLOAD_PROGRESS_START
import com.google.android.horologist.media.data.datasource.MediaDownloadLocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Monitors the progress of downloads, polling [DownloadManager] every
 * [specified interval][UPDATE_INTERVAL_MILLIS] and uses [MediaDownloadLocalDataSource] to persist
 * the progress.
 */
@SuppressLint("UnsafeOptInUsageError")
@ExperimentalHorologistMediaDataApi
public class DownloadProgressMonitor(
    private val coroutineScope: CoroutineScope,
    private val mediaDownloadLocalDataSource: MediaDownloadLocalDataSource
) {

    private val handler = Handler(Looper.getMainLooper())
    private var running = false

    internal fun start(downloadManager: DownloadManager) {
        running = true
        update(downloadManager)
    }

    internal fun stop() {
        running = false
        handler.removeCallbacksAndMessages(null)
    }

    private fun update(downloadManager: DownloadManager) {
        coroutineScope.launch {
            val downloads = mediaDownloadLocalDataSource.getAllDownloading()

            if (downloads.isNotEmpty()) {
                downloads.forEach {
                    downloadManager.downloadIndex.getDownload(it.mediaId)?.let { download ->
                        mediaDownloadLocalDataSource.updateProgress(
                            mediaId = download.request.id,
                            progress = download.percentDownloaded
                                // it can return -1 (C.PERCENTAGE_UNSET)
                                .coerceAtLeast(DOWNLOAD_PROGRESS_START),
                            size = download.contentLength
                        )
                    }
                }
            } else {
                stop()
            }
        }

        if (running) {
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({ update(downloadManager) }, UPDATE_INTERVAL_MILLIS)
        }
    }

    private companion object {
        const val UPDATE_INTERVAL_MILLIS = 1000L
    }
}
