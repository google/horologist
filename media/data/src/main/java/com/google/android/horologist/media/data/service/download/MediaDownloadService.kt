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
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.scheduler.Scheduler
import androidx.media3.exoplayer.workmanager.WorkManagerScheduler
import com.google.android.horologist.annotations.ExperimentalHorologistApi

/**
 * Implementation of [DownloadService] that:
 * - notifies [DownloadManagerListener] of service creation and destruction;
 * - uses [DownloadNotificationHelper] to build a [Notification];
 * - uses [WorkManagerScheduler] as [Scheduler];
 *
 * @param foregroundNotificationId see [DownloadService].
 * @param foregroundNotificationUpdateInterval see [DownloadService].
 * @param channelId see [DownloadService].
 * @param channelNameResourceId see [DownloadService].
 * @param channelDescriptionResourceId see [DownloadService].
 * @param notificationIcon see `smallIcon` in [DownloadNotificationHelper.buildProgressNotification].
 */
@SuppressLint("UnsafeOptInUsageError")
@ExperimentalHorologistApi
public abstract class MediaDownloadService(
    foregroundNotificationId: Int,
    foregroundNotificationUpdateInterval: Long,
    channelId: String?,
    @StringRes channelNameResourceId: Int,
    @StringRes channelDescriptionResourceId: Int,
    @DrawableRes private val notificationIcon: Int,
) : DownloadService(
    foregroundNotificationId,
    foregroundNotificationUpdateInterval,
    channelId,
    channelNameResourceId,
    channelDescriptionResourceId,
),
    LifecycleOwner {
        private val dispatcher = ServiceLifecycleDispatcher(this)

        override fun onCreate() {
            dispatcher.onServicePreSuperOnCreate()

            super.onCreate()

            downloadManagerListener.onDownloadServiceCreated(downloadManager)
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            dispatcher.onServicePreSuperOnStart()
            return super.onStartCommand(intent, flags, startId)
        }

        override val lifecycle: Lifecycle
            get() = dispatcher.lifecycle

        override fun onDestroy() {
            downloadManagerListener.onDownloadServiceDestroyed()

            dispatcher.onServicePreSuperOnDestroy()

            super.onDestroy()
        }

        override fun getScheduler(): Scheduler = workManagerScheduler

        override fun getForegroundNotification(
            downloads: MutableList<Download>,
            notMetRequirements: Int,
        ): Notification = downloadNotificationHelper.buildProgressNotification(
            this,
            notificationIcon,
            downloadIntent,
            null,
            downloads,
            notMetRequirements,
        )

        /**
         * Used to notify of this service creation and destruction.
         */
        protected abstract val downloadManagerListener: DownloadManagerListener

        /**
         * See [DownloadService.getScheduler].
         */
        protected abstract val workManagerScheduler: WorkManagerScheduler

        /**
         * Used to build a [Notification] required by [DownloadService.getForegroundNotification].
         */
        protected abstract val downloadNotificationHelper: DownloadNotificationHelper

        /**
         * See `contentIntent` in [DownloadNotificationHelper.buildProgressNotification].
         */
        protected abstract val downloadIntent: PendingIntent
    }
