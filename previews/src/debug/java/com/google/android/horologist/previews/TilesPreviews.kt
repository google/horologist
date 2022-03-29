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

package com.google.android.horologist.previews

import android.content.res.Resources
import android.graphics.Color
import android.util.DisplayMetrics
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.tiles.ColorBuilders.ColorProp
import androidx.wear.tiles.DeviceParametersBuilders
import androidx.wear.tiles.DimensionBuilders.ExpandedDimensionProp
import androidx.wear.tiles.LayoutElementBuilders.Box
import androidx.wear.tiles.LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER
import androidx.wear.tiles.LayoutElementBuilders.Layout
import androidx.wear.tiles.LayoutElementBuilders.Text
import androidx.wear.tiles.LayoutElementBuilders.VERTICAL_ALIGN_CENTER
import androidx.wear.tiles.ModifiersBuilders.Background
import androidx.wear.tiles.ModifiersBuilders.Modifiers
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.StateBuilders
import androidx.wear.tiles.TileBuilders.Tile
import androidx.wear.tiles.TimelineBuilders.Timeline
import androidx.wear.tiles.TimelineBuilders.TimelineEntry
import kotlin.math.roundToInt

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun SamplePreview() {
    ShowTilePreviews({ sampleTile() })
}

fun sampleTile(): Tile {
    return Tile.Builder()
        .setTimeline(
            Timeline.Builder()
                .addTimelineEntry(
                    TimelineEntry.Builder()
                        .setLayout(
                            Layout.Builder()
                                .setRoot(
                                    Box.Builder()
                                        .setHeight(ExpandedDimensionProp.Builder().build())
                                        .setWidth(ExpandedDimensionProp.Builder().build())
                                        .setVerticalAlignment(VERTICAL_ALIGN_CENTER)
                                        .setHorizontalAlignment(HORIZONTAL_ALIGN_CENTER)
                                        .setModifiers(Modifiers.Builder()
                                            .setBackground(Background.Builder()
                                                .setColor(ColorProp.Builder()
                                                    .setArgb(Color.BLUE)
                                                    .build())
                                                .build())
                                            .build())
                                        .addContent(Text.Builder()
                                            .setText("Hey")
                                            .build())
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .build()
        )
        .setResourcesVersion("1")
        .setFreshnessIntervalMillis(300000)
        .build()
}

private fun requestParams(resources: Resources) = RequestBuilders.TileRequest.Builder()
    .setDeviceParameters(buildDeviceParameters(resources))
    .setState(StateBuilders.State.Builder().build())
    .build()

fun buildDeviceParameters(resources: Resources): DeviceParametersBuilders.DeviceParameters {
    val displayMetrics: DisplayMetrics = resources.displayMetrics
    val isScreenRound: Boolean = resources.configuration.isScreenRound
    return DeviceParametersBuilders.DeviceParameters.Builder()
        .setScreenWidthDp((displayMetrics.widthPixels / displayMetrics.density).roundToInt())
        .setScreenHeightDp((displayMetrics.heightPixels / displayMetrics.density).roundToInt())
        .setScreenDensity(displayMetrics.density)
        .setScreenShape(
            if (isScreenRound) DeviceParametersBuilders.SCREEN_SHAPE_ROUND
            else DeviceParametersBuilders.SCREEN_SHAPE_RECT
        )
        .setDevicePlatform(DeviceParametersBuilders.DEVICE_PLATFORM_WEAR_OS)
        .build()
}