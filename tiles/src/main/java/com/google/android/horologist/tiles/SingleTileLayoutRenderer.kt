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

import android.content.Context
import androidx.wear.tiles.DeviceParametersBuilders
import androidx.wear.tiles.LayoutElementBuilders.Layout
import androidx.wear.tiles.LayoutElementBuilders.LayoutElement
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.ResourceBuilders.Resources
import androidx.wear.tiles.TileBuilders.Tile
import androidx.wear.tiles.TimelineBuilders
import androidx.wear.tiles.material.Colors

public abstract class SingleTileLayoutRenderer<T, R>(
    public val context: Context
) : TileLayoutRenderer<T, R> {
    final override fun renderTimeline(
        tileState: T,
        requestParams: RequestBuilders.TileRequest,
    ): Tile {
        val theme = createTheme()

        val rootLayout = renderTile(tileState, requestParams.deviceParameters!!, theme)

        val singleTileTimeline = TimelineBuilders.Timeline.Builder()
            .addTimelineEntry(
                TimelineBuilders.TimelineEntry.Builder()
                    .setLayout(
                        Layout.Builder()
                            .setRoot(rootLayout)
                            .build()
                    )
                    .build()
            )
            .build()

        return Tile.Builder()
            .setResourcesVersion(PERMANENT_RESOURCES_VERSION)
            .setTimeline(singleTileTimeline)
            .build()
    }

    internal open fun createTheme(): Colors = Colors.DEFAULT

    public abstract fun renderTile(
        singleTileState: T,
        deviceParameters: DeviceParametersBuilders.DeviceParameters,
        theme: Colors
    ): LayoutElement

    final override fun produceRequestedResources(
        resourceResults: R,
        requestParams: RequestBuilders.ResourcesRequest
    ): Resources {
        return Resources.Builder()
            .setVersion(PERMANENT_RESOURCES_VERSION)
            .apply {
                produceRequestedResources(
                    resourceResults,
                    requestParams.deviceParameters!!,
                    requestParams.resourceIds
                )
            }
            .build()
    }

    public abstract fun Resources.Builder.produceRequestedResources(
        resourceResults: R,
        deviceParameters: DeviceParametersBuilders.DeviceParameters,
        resourceIds: MutableList<String>,
    ): Unit
}

public const val PERMANENT_RESOURCES_VERSION: String = "0"
