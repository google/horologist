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
import androidx.wear.protolayout.DeviceParametersBuilders
import androidx.wear.protolayout.LayoutElementBuilders.Layout
import androidx.wear.protolayout.LayoutElementBuilders.LayoutElement
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.StateBuilders.State
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.material.Colors
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders.Tile
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import java.util.UUID

/**
 * A [TileLayoutRenderer] designed with typical but restrictive limitations, such as a single tile
 * in the timeline, and fixed resources that will be updated by changing ids instead of version.
 */
@ExperimentalHorologistApi
public abstract class SingleTileLayoutRenderer<T, R>(
    /**
     * The context to avoid passing in through each render method.
     */
    public val context: Context,
    public val debugResourceMode: Boolean = false
) : TileLayoutRenderer<T, R> {
    public val theme: Colors by lazy { createTheme() }

    public open val freshnessIntervalMillis: Long = 0L

    final override fun renderTimeline(
        state: T,
        requestParams: RequestBuilders.TileRequest
    ): Tile {
        val rootLayout = renderTile(state, requestParams.deviceConfiguration)

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
            .setResourcesVersion(
                if (debugResourceMode) {
                    UUID.randomUUID().toString()
                } else {
                    getResourcesVersionForTileState(state)
                }
            )
            .setState(createState())
            .setTileTimeline(singleTileTimeline)
            .setFreshnessIntervalMillis(freshnessIntervalMillis)
            .build()
    }

    public open fun getResourcesVersionForTileState(state: T): String = PERMANENT_RESOURCES_VERSION

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
        resourceState: R,
        requestParams: RequestBuilders.ResourcesRequest
    ): Resources {
        return Resources.Builder()
            .setVersion(requestParams.version)
            .apply {
                produceRequestedResources(
                    resourceState,
                    requestParams.deviceConfiguration,
                    requestParams.resourceIds
                )
            }
            .build()
    }

    /**
     * Add resources directly to the builder.
     */
    public open fun Resources.Builder.produceRequestedResources(
        resourceState: R,
        deviceParameters: DeviceParametersBuilders.DeviceParameters,
        resourceIds: MutableList<String>
    ) {
    }

    public open fun createState(): State = State.Builder().build()
}

/**
 * A constant for non updating resources where each id will always contain the same content.
 */
public const val PERMANENT_RESOURCES_VERSION: String = "0"
