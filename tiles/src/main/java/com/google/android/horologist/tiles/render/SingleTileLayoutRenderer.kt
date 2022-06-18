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

package com.google.android.horologist.tiles.render

import android.content.Context
import androidx.wear.tiles.DeviceParametersBuilders
import androidx.wear.tiles.LayoutElementBuilders.Layout
import androidx.wear.tiles.LayoutElementBuilders.LayoutElement
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.ResourceBuilders.Resources
import androidx.wear.tiles.TileBuilders.Tile
import androidx.wear.tiles.TimelineBuilders
import androidx.wear.tiles.material.Colors
import com.google.android.horologist.tiles.ExperimentalHorologistTilesApi

/**
 * A [TileLayoutRenderer] designed with typical but restrictive limitations, such as a single tile
 * in the timeline, and fixed resources that will be updated by changing ids instead of version.
 */
@ExperimentalHorologistTilesApi
public abstract class SingleTileLayoutRenderer<T, R>(
    /**
     * The context to avoid passing in through each render method.
     */
    public val context: Context
) : TileLayoutRenderer<T, R> {
    public val theme: Colors by lazy { createTheme() }

    public open val freshnessIntervalMillis = 0

    final override fun renderTimeline(
        state: T,
        requestParams: RequestBuilders.TileRequest,
    ): Tile {
        val rootLayout = renderTile(state, requestParams.deviceParameters!!)

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
            .setFreshnessIntervalMillis(freshnessIntervalMillis)
            .build()
    }

    /**
     * Create a material theme that should be applied to all components.
     */
    public open fun createTheme(): Colors = Colors.DEFAULT

    /**
     * Render a single tile as a LayoutElement, that will be the only item in the timeline.
     */
    public abstract fun renderTile(
        state: T,
        deviceParameters: DeviceParametersBuilders.DeviceParameters
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

    /**
     * Add resources directly to the builder.
     */
    public open fun Resources.Builder.produceRequestedResources(
        resourceResults: R,
        deviceParameters: DeviceParametersBuilders.DeviceParameters,
        resourceIds: MutableList<String>,
    ) {
    }
}

/**
 * A constant for non updating resources where each id will always contain the same content.
 */
public const val PERMANENT_RESOURCES_VERSION: String = "0"
