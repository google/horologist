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

@file:OptIn(ExperimentalHorologistApi::class)

package com.google.android.horologist.compose.tools

import android.content.res.Resources
import android.graphics.Color
import android.util.DisplayMetrics
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.wear.tiles.ColorBuilders.argb
import androidx.wear.tiles.DeviceParametersBuilders
import androidx.wear.tiles.DimensionBuilders.ExpandedDimensionProp
import androidx.wear.tiles.LayoutElementBuilders
import androidx.wear.tiles.LayoutElementBuilders.Box
import androidx.wear.tiles.LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER
import androidx.wear.tiles.LayoutElementBuilders.LayoutElement
import androidx.wear.tiles.LayoutElementBuilders.VERTICAL_ALIGN_CENTER
import androidx.wear.tiles.ModifiersBuilders.Background
import androidx.wear.tiles.ModifiersBuilders.Modifiers
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.ResourceBuilders
import androidx.wear.tiles.StateBuilders.State
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TimelineBuilders
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.render.TileLayoutRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlin.math.roundToInt

/**
 * Preview a [TileLayoutRenderer] by providing the complete state for tile and resources.
 * Any bitmaps should be preloaded from test resources and passed in via [resourceState] as
 * Bitmap or ImageResource.
 */
@Composable
public fun <T, R> TileLayoutPreview(state: T, resourceState: R, renderer: TileLayoutRenderer<T, R>) {
    val context = LocalContext.current
    val resources = context.resources

    val requestParams = remember { requestParams(resources) }

    val tile = remember(state) { renderer.renderTimeline(state, requestParams) }
    val resourceParams =
        remember(tile.resourcesVersion) { resourceParams(resources, tile.resourcesVersion) }
    val tileResources =
        remember(state) { renderer.produceRequestedResources(resourceState, resourceParams) }

    TilePreview(tile, tileResources)
}

/**
 * Preview a Tile by providing the final proto representation of tiles and resources.
 */
@Composable
public fun TilePreview(
    tile: TileBuilders.Tile,
    tileResources: ResourceBuilders.Resources
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            FrameLayout(it).apply {
                this.setBackgroundColor(Color.BLACK)
            }
        },
        update = {
            val tileRenderer = androidx.wear.tiles.renderer.TileRenderer(
                /* uiContext = */ it.context,
                /* layout = */ tile.timeline?.timelineEntries?.first()?.layout!!,
                /* resources = */ tileResources,
                /* loadActionExecutor = */ Dispatchers.IO.asExecutor(),
                /* loadActionListener = */ {}
            )

            tileRenderer.inflate(it)
        }
    )
}

/**
 * Preview a smaller tile component such as a Button, that is not full screen.
 */
@ExperimentalHorologistApi
@Composable
public fun LayoutElementPreview(
    element: LayoutElement,
    @ColorInt windowBackgroundColor: Int = Color.DKGRAY,
    tileResourcesFn: ResourceBuilders.Resources.Builder.() -> Unit = {}
) {
    val root = Box.Builder()
        .setModifiers(
            Modifiers.Builder().setBackground(
                Background.Builder()
                    .setColor(argb(windowBackgroundColor))
                    .build()
            ).build()
        )
        .setHorizontalAlignment(HORIZONTAL_ALIGN_CENTER)
        .setVerticalAlignment(VERTICAL_ALIGN_CENTER)
        .setHeight(ExpandedDimensionProp.Builder().build())
        .setWidth(ExpandedDimensionProp.Builder().build())
        .addContent(element)
        .build()

    LayoutRootPreview(root, tileResourcesFn)
}

/**
 * Preview a root layout component such as a PrimaryLayout, that is full screen.
 */
@Composable
public fun LayoutRootPreview(
    root: LayoutElement,
    tileResourcesFn: ResourceBuilders.Resources.Builder.() -> Unit = {}
) {
    val tile = remember {
        TileBuilders.Tile.Builder()
            .setResourcesVersion(PERMANENT_RESOURCES_VERSION)
            .setTimeline(
                TimelineBuilders.Timeline.Builder().addTimelineEntry(
                    TimelineBuilders.TimelineEntry.Builder()
                        .setLayout(
                            LayoutElementBuilders.Layout.Builder()
                                .setRoot(root)
                                .build()
                        ).build()
                ).build()
            ).build()
    }

    val tileResources = ResourceBuilders.Resources.Builder()
        .apply(tileResourcesFn)
        .build()
    TilePreview(tile, tileResources)
}

internal const val PERMANENT_RESOURCES_VERSION = "0"

private fun requestParams(resources: Resources) =
    RequestBuilders.TileRequest.Builder().setDeviceParameters(buildDeviceParameters(resources))
        .setState(State.Builder().build()).build()

private fun resourceParams(resources: Resources, version: String) =
    RequestBuilders.ResourcesRequest.Builder().setDeviceParameters(buildDeviceParameters(resources))
        .setVersion(version).build()

public fun buildDeviceParameters(resources: Resources): DeviceParametersBuilders.DeviceParameters {
    val displayMetrics: DisplayMetrics = resources.displayMetrics
    val isScreenRound: Boolean = resources.configuration.isScreenRound
    return DeviceParametersBuilders.DeviceParameters.Builder()
        .setScreenWidthDp((displayMetrics.widthPixels / displayMetrics.density).roundToInt())
        .setScreenHeightDp((displayMetrics.heightPixels / displayMetrics.density).roundToInt())
        .setScreenDensity(displayMetrics.density).setScreenShape(
            if (isScreenRound) DeviceParametersBuilders.SCREEN_SHAPE_ROUND
            else DeviceParametersBuilders.SCREEN_SHAPE_RECT
        ).setDevicePlatform(DeviceParametersBuilders.DEVICE_PLATFORM_WEAR_OS).build()
}
