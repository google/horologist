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

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.BatteryManager
import androidx.core.app.TaskStackBuilder
import androidx.wear.protolayout.ActionBuilders.LoadAction
import androidx.wear.protolayout.ColorBuilders
import androidx.wear.protolayout.DeviceParametersBuilders
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.LayoutElementBuilders.Column
import androidx.wear.protolayout.LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER
import androidx.wear.protolayout.LayoutElementBuilders.Layout
import androidx.wear.protolayout.LayoutElementBuilders.LayoutElement
import androidx.wear.protolayout.ModifiersBuilders.Clickable
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.StateBuilders.State
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.protolayout.TimelineBuilders.TimelineEntry
import androidx.wear.protolayout.expression.AppDataKey
import androidx.wear.protolayout.expression.DynamicBuilders.DynamicInt32
import androidx.wear.protolayout.expression.DynamicDataBuilders.DynamicDataValue
import androidx.wear.protolayout.material.Chip
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders.Tile
import com.google.android.horologist.sample.MainActivity
import com.google.android.horologist.tiles.SuspendingTileService

class ExampleTileService : SuspendingTileService() {
    private lateinit var batteryManager: BatteryManager

    override fun onCreate() {
        super.onCreate()

        batteryManager =
            applicationContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    }

    @SuppressLint("RestrictedApi")
    override suspend fun tileRequest(requestParams: RequestBuilders.TileRequest): Tile {
        if (requestParams.currentState.lastClickableId == OpenItemId) {
            // TODO work this out.
//            val dynamicDataValue: DynamicDataValue<*>? = requestParams.currentState.keyToValueMapping[ItemKey]
//            val itemKey: DynamicDataValue<DynamicInt32> = dynamicDataValue.toDynamicDataValueProto()
            openActivity()
        }

        return Tile.Builder()
            .setResourcesVersion("1")
            .setTileTimeline(
                Timeline.Builder()
                    .addTimelineEntry(
                        TimelineEntry.Builder()
                            .setLayout(
                                mainLayout(requestParams)
                            )
                            .build()
                    )
                    .build()
            )
            .build()
    }

    private fun mainLayout(requestParams: RequestBuilders.TileRequest) =
        Layout.Builder().setRoot(mainLayout(requestParams.deviceConfiguration))
            .build()

    private fun openActivity() {
        TaskStackBuilder.create(this)
            .addNextIntentWithParentStack(Intent(
                this,
                MainActivity::class.java
            ))
            .startActivities()
    }

    override suspend fun resourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest
    ): ResourceBuilders.Resources = ResourceBuilders.Resources.Builder().setVersion("1").build()

    fun mainLayout(deviceParameters: DeviceParametersBuilders.DeviceParameters): LayoutElement {
        return Column.Builder()
            .setWidth(DimensionBuilders.expand())
            .addContent(
                Text.Builder(this, "Charging: " + batteryManager.isCharging)
                    .setTypography(Typography.TYPOGRAPHY_BODY1)
                    .setColor(ColorBuilders.argb(Color.RED))
                    .build()
            )
            .addContent(
                Chip.Builder(this, openActivityClickable(), deviceParameters)
                    .setPrimaryLabelContent("Open")
                    .build()
            )
            .setHorizontalAlignment(HORIZONTAL_ALIGN_CENTER)
            .build()
    }

    private fun openActivityClickable(): Clickable {
        return Clickable.Builder()
            .setOnClick(LoadAction.Builder()
                .setRequestState(State.Builder()
                    .addKeyToValueMapping(ItemKey, DynamicDataValue.fromInt(1))
                    .build())
                .build())
            .setId(OpenItemId)
            .build()
    }

    companion object {
        val ItemKey = AppDataKey<DynamicInt32>("item")
        val OpenItemId = "openItem"
    }
}
