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

package com.google.android.horologist.sample.tiles

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.wear.tiles.TileService.getUpdater
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.google.android.horologist.tiles.TileUpdateService
import com.google.android.horologist.tiles.TileUpdateWorker

class ExampleBroadcastReceiver : BroadcastReceiver() {
    val direct = false
    val service = false
    val work = true

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_POWER_CONNECTED ||
            intent.action == Intent.ACTION_POWER_DISCONNECTED
        ) {
            if (direct) {
                println("direct")
                forceTileUpdate(context.applicationContext)
            }

            if (service) {
                println("service")
                context.startService(Intent(context, TileUpdateService::class.java).apply {
                    this.putExtra("TILE_CLASS", ExampleTileService::class.qualifiedName)
                })
            }

            if (work) {
                println("work")
                val tileWorkRequest: WorkRequest =
                    OneTimeWorkRequestBuilder<TileUpdateWorker>()
                        .setExpedited(OutOfQuotaPolicy.DROP_WORK_REQUEST)
                        .setInputData(
                            Data.Builder()
                                .putString("TILE_CLASS", ExampleTileService::class.qualifiedName)
                                .build()
                        )
                        .build()

                WorkManager
                    .getInstance(context)
                    .enqueue(tileWorkRequest)
            }
        }
    }

    fun forceTileUpdate(applicationContext: Context) {
        getUpdater(applicationContext).requestUpdate(ExampleTileService::class.java)
    }
}
