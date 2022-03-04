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

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.wear.tiles.TileService

class TileUpdateService : Service() {
    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val tileClassName = intent?.getStringExtra("TILE_CLASS")
        if (tileClassName != null) {
            // TODO is this safe, as not an exported service
            @Suppress("UNCHECKED_CAST")
            val tileClass = Class.forName(tileClassName)  as Class<out TileService>

            TileService.getUpdater(applicationContext)
                .requestUpdate(tileClass)
        }

        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent?): IBinder? = null
}