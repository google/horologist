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

package com.google.android.horologist.tiles

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.wear.tiles.TileService
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters

class TileUpdateWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    val tileClassName = workerParams.inputData.getString("TILE_CLASS")

    override suspend fun doWork(): Result {
        if (tileClassName != null) {
            @Suppress("UNCHECKED_CAST")
            val tileClass = Class.forName(tileClassName) as Class<out TileService>

            println("Tile from service $tileClass")
            TileService.getUpdater(applicationContext).requestUpdate(tileClass)
        }

        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return createForegroundInfo()
    }

    private fun createNotificationChannel(
        channelId: String, name: String
    ): NotificationChannel {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        return NotificationChannel(
            channelId, name, NotificationManager.IMPORTANCE_LOW
        ).also { channel ->
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val context = applicationContext
        createNotificationChannel("101", "Tile Update")
        val notification: Notification = Notification.Builder(context, "101")
            .setContentTitle("Tile Update")
            .setSmallIcon(R.drawable.ic_android)
            .setOngoing(true)
            .build()
        return ForegroundInfo(101, notification)
    }
}