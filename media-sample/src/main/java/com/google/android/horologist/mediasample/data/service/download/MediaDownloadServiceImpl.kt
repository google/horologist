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

import android.app.PendingIntent
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import androidx.media3.exoplayer.workmanager.WorkManagerScheduler
import com.google.android.horologist.media.data.service.download.DownloadManagerListener
import com.google.android.horologist.media.data.service.download.MediaDownloadService
import com.google.android.horologist.media3.navigation.IntentBuilder
import com.google.android.horologist.mediasample.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MediaDownloadServiceImpl : MediaDownloadService(
    foregroundNotificationId = MEDIA_DOWNLOAD_FOREGROUND_NOTIFICATION_ID,
    foregroundNotificationUpdateInterval = DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    channelId = MEDIA_DOWNLOAD_CHANNEL_ID,
    channelNameResourceId = MEDIA_DOWNLOAD_CHANNEL_NAME,
    channelDescriptionResourceId = MEDIA_DOWNLOAD_CHANNEL_DESCRIPTION_NOT_PROVIDED,
    notificationIcon = MEDIA_DOWNLOAD_NOTIFICATION_ICON
) {

    @Inject
    lateinit var downloadManagerParam: DownloadManager

    @Inject
    lateinit var workManagerSchedulerParam: WorkManagerScheduler

    @Inject
    lateinit var downloadNotificationHelperParam: DownloadNotificationHelper

    @Inject
    lateinit var intentBuilder: IntentBuilder

    @Inject
    lateinit var downloadManagerListenerParam: DownloadManagerListener

    override fun getDownloadManager(): DownloadManager = downloadManagerParam

    override val downloadManagerListener: DownloadManagerListener
        get() = downloadManagerListenerParam

    override val workManagerScheduler: WorkManagerScheduler
        get() = workManagerSchedulerParam

    override val downloadIntent: PendingIntent
        get() = intentBuilder.buildDownloadIntent()

    override val downloadNotificationHelper: DownloadNotificationHelper
        get() = downloadNotificationHelperParam

    companion object {
        private const val MEDIA_DOWNLOAD_FOREGROUND_NOTIFICATION_ID = 1
        const val MEDIA_DOWNLOAD_CHANNEL_ID = "download_channel"
        private const val MEDIA_DOWNLOAD_CHANNEL_NAME = R.string.media_download_channel_name
        private const val MEDIA_DOWNLOAD_CHANNEL_DESCRIPTION_NOT_PROVIDED = 0
        private const val MEDIA_DOWNLOAD_NOTIFICATION_ICON = R.drawable.ic_uamp_headset
    }
}
