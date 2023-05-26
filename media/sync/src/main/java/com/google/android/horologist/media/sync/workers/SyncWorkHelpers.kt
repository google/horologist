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

package com.google.android.horologist.media.sync.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.ForegroundInfo
import androidx.work.NetworkType

private const val SyncNotificationId = 0
private const val SyncNotificationChannelID = "SyncNotificationChannel"

// All sync work needs an internet connectionS
internal val SyncConstraints
    get() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

/**
 * Foreground information for sync on lower API levels when sync workers are being
 * run with a foreground service
 */
internal fun Context.syncForegroundInfo(
    notificationTitle: String,
    @DrawableRes notificationIcon: Int,
    channelName: String,
    channelDescription: String
) = ForegroundInfo(
    SyncNotificationId,
    syncWorkNotification(
        notificationTitle = notificationTitle,
        notificationIcon = notificationIcon,
        channelName = channelName,
        channelDescription = channelDescription
    )
)

/**
 * Notification displayed on lower API levels when sync workers are being
 * run with a foreground service
 */
private fun Context.syncWorkNotification(
    notificationTitle: String,
    @DrawableRes notificationIcon: Int,
    channelName: String,
    channelDescription: String
): Notification {
    val channel = NotificationChannel(
        SyncNotificationChannelID,
        channelName,
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = channelDescription
    }
    // Register the channel with the system
    val notificationManager: NotificationManager? =
        getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

    notificationManager?.createNotificationChannel(channel)

    return NotificationCompat.Builder(
        this,
        SyncNotificationChannelID
    )
        .setSmallIcon(notificationIcon)
        .setContentTitle(notificationTitle)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()
}
