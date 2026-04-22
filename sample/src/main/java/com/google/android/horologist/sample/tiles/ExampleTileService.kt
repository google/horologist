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

import android.content.Context
import android.os.BatteryManager
import androidx.wear.protolayout.LayoutElementBuilders.Column
import androidx.wear.protolayout.LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER
import androidx.wear.protolayout.LayoutElementBuilders.Layout
import androidx.wear.protolayout.LayoutElementBuilders.LayoutElement
import androidx.wear.protolayout.LayoutElementBuilders.Text
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.protolayout.TimelineBuilders.TimelineEntry
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders.Tile
import com.google.android.horologist.tiles.SuspendingTileService

class ExampleTileService : SuspendingTileService() {
    private lateinit var batteryManager: BatteryManager

    override fun onCreate() {
        super.onCreate()

        batteryManager =
            applicationContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    }

    override suspend fun tileRequest(requestParams: RequestBuilders.TileRequest): Tile {
        return Tile.Builder()
            .setResourcesVersion("1")
            .setTileTimeline(
                Timeline.Builder()
                    .addTimelineEntry(
                        TimelineEntry.Builder()
                            .setLayout(
                                Layout.Builder().setRoot(mainLayout())
                                    .build(),
                            )
                            .build(),
                    )
                    .build(),
            )
            .build()
    }

    override suspend fun resourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest,
    ): ResourceBuilders.Resources = ResourceBuilders.Resources.Builder().setVersion("1").build()

    fun mainLayout(): LayoutElement {
        return Column.Builder()
            .addContent(
                Text.Builder()
                    .setText("Charging: " + batteryManager.isCharging)
                    .build(),
            )
            .setHorizontalAlignment(HORIZONTAL_ALIGN_CENTER)
            .build()
    }
}
