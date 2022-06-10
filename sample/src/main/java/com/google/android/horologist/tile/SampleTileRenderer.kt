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

@file:OptIn(ExperimentalHorologistComposeToolsApi::class)

package com.google.android.horologist.tile

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.wear.tiles.ColorBuilders.argb
import androidx.wear.tiles.DeviceParametersBuilders.DeviceParameters
import androidx.wear.tiles.LayoutElementBuilders
import androidx.wear.tiles.ModifiersBuilders.Clickable
import androidx.wear.tiles.ResourceBuilders.ImageResource
import androidx.wear.tiles.ResourceBuilders.Resources
import androidx.wear.tiles.material.Button
import androidx.wear.tiles.material.ButtonColors
import androidx.wear.tiles.material.ChipColors
import androidx.wear.tiles.material.Colors
import androidx.wear.tiles.material.CompactChip
import androidx.wear.tiles.material.Text
import androidx.wear.tiles.material.Typography
import androidx.wear.tiles.material.layouts.MultiButtonLayout
import androidx.wear.tiles.material.layouts.PrimaryLayout
import com.google.android.horologist.compose.tools.ExperimentalHorologistComposeToolsApi
import com.google.android.horologist.compose.tools.TileLayoutPreview
import com.google.android.horologist.compose.tools.WearPreviewDevices
import com.google.android.horologist.compose.tools.WearPreviewFontSizes
import com.google.android.horologist.tiles.SingleTileLayoutRenderer

class SampleTileRenderer(context: Context) :
    SingleTileLayoutRenderer<Int, ImageResource?>(context) {
    override fun renderTile(
        singleTileState: Int,
        deviceParameters: DeviceParameters,
        theme: Colors
    ): LayoutElementBuilders.LayoutElement {
        val clickable = Clickable.Builder()
            .setId("click")
            .build()

        return PrimaryLayout.Builder(deviceParameters)
            .setPrimaryLabelTextContent(
                Text.Builder(context, "Count: $singleTileState")
                    .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                    .setColor(argb(theme.primary))
                    .build()
            )
            .setContent(
                MultiButtonLayout.Builder()
                    .addButtonContent(
                        Button.Builder(context, clickable)
                            .setIconContent("icon1")
                            .setButtonColors(ButtonColors.secondaryButtonColors(theme))
                            .build()
                    )
                    .addButtonContent(
                        Button.Builder(context, clickable)
                            .setIconContent("icon2")
                            .setButtonColors(ButtonColors.secondaryButtonColors(theme))
                            .build()
                    )
                    .build()
            )
            .setPrimaryChipContent(
                CompactChip.Builder(context, "Action", clickable, deviceParameters)
                    .setChipColors(ChipColors.primaryChipColors(theme))
                    .build()
            )
            .build()
    }

    override fun Resources.Builder.produceRequestedResources(
        resourceResults: ImageResource?,
        deviceParameters: DeviceParameters,
        resourceIds: MutableList<String>
    ) {
        if (resourceResults != null) {
            addIdToImageMapping("image", resourceResults)
        }
    }
}

@WearPreviewDevices
@WearPreviewFontSizes
@Composable
fun SampleTilePreview() {
    val context = LocalContext.current

    val renderer = remember {
        SampleTileRenderer(context)
    }

    TileLayoutPreview(1, null, renderer)
}
