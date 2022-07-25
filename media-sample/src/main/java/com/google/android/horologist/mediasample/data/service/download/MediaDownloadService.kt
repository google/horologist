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

import android.app.Notification
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.scheduler.Scheduler
import androidx.media3.exoplayer.workmanager.WorkManagerScheduler
import com.google.android.horologist.media3.navigation.IntentBuilder
import com.google.android.horologist.mediasample.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MediaDownloadService : DownloadService(
    MEDIA_DOWNLOAD_FOREGROUND_NOTIFICATION_ID,
    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    MEDIA_DOWNLOAD_CHANNEL_ID,
    MEDIA_DOWNLOAD_CHANNEL_NAME,
    MEDIA_DOWNLOAD_CHANNEL_DESCRIPTION_NOT_PROVIDED,
) {
    @Inject
    lateinit var downloadManagerParam: DownloadManager

    @Inject
    lateinit var workManagerScheduler: WorkManagerScheduler

    @Inject
    lateinit var downloadNotificationHelper: DownloadNotificationHelper

    @Inject
    lateinit var intentBuilder: IntentBuilder

    override fun getDownloadManager(): DownloadManager = downloadManagerParam

    override fun getScheduler(): Scheduler = workManagerScheduler

    override fun getForegroundNotification(
        downloads: MutableList<Download>,
        notMetRequirements: Int
    ): Notification = downloadNotificationHelper.buildProgressNotification(
        this,
        MEDIA_DOWNLOAD_NOTIFICATION_ICON,
        intentBuilder.buildDownloadIntent(),
        null,
        downloads,
        notMetRequirements
    )

    companion object {
        private const val MEDIA_DOWNLOAD_FOREGROUND_NOTIFICATION_ID = 1
        const val MEDIA_DOWNLOAD_CHANNEL_ID = "download_channel"
        private const val MEDIA_DOWNLOAD_CHANNEL_NAME =
            R.string.horologist_media_download_channel_name
        private const val MEDIA_DOWNLOAD_CHANNEL_DESCRIPTION_NOT_PROVIDED = 0
        private const val MEDIA_DOWNLOAD_NOTIFICATION_ICON =
            R.drawable.ic_baseline_music_note_24_white
    }
}
