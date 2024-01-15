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

package com.google.android.horologist.tile

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.wear.protolayout.material.Colors
import androidx.wear.tiles.tooling.preview.Preview
import androidx.wear.tiles.tooling.preview.TilePreviewData
import androidx.wear.tiles.tooling.preview.TilePreviewHelper
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.horologist.tiles.preview.ThemePreviewTileRenderer

val SampleTheme =
    Colors(0xFF981F68.toInt(), 0xFFFFFFFF.toInt(), 0xFF1C1B1F.toInt(), 0xFFFFFFFF.toInt())

@Preview(device = WearDevices.SMALL_ROUND, fontScale = 1.24f)
@Preview(device = WearDevices.LARGE_ROUND, fontScale = 0.94f)
@Composable
public fun SampleThemePreview(context: Context) = TilePreviewData {
    val renderer = ThemePreviewTileRenderer(context, SampleTheme)

    TilePreviewHelper.singleTimelineEntryTileBuilder(
        renderer.renderTile(
            Unit,
            it.deviceConfiguration,
        ),
    ).build()
}
