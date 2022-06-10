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
import android.graphics.BitmapFactory
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
import com.google.android.horologist.sample.R
import com.google.android.horologist.tiles.SingleTileLayoutRenderer
import com.google.android.horologist.tiles.toImageResource

class SampleTileRenderer(context: Context) :
    SingleTileLayoutRenderer<SampleTileRenderer.TileState, SampleTileRenderer.ResourceState>(
        context
    ) {
    override fun renderTile(
        singleTileState: TileState,
        deviceParameters: DeviceParameters,
        theme: Colors
    ): LayoutElementBuilders.LayoutElement {
        val clickable = Clickable.Builder()
            .setId("click")
            .build()

        return PrimaryLayout.Builder(deviceParameters)
            .setPrimaryLabelTextContent(
                Text.Builder(context, "Count: ${singleTileState.count}")
                    .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                    .setColor(argb(theme.primary))
                    .build()
            )
            .setContent(
                MultiButtonLayout.Builder()
                    .addButtonContent(
                        Button.Builder(context, clickable)
                            .setIconContent("image")
                            .setButtonColors(ButtonColors.secondaryButtonColors(theme))
                            .build()
                    )
                    .addButtonContent(
                        Button.Builder(context, clickable)
                            .setIconContent("image")
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
        resourceResults: ResourceState,
        deviceParameters: DeviceParameters,
        resourceIds: MutableList<String>
    ) {
        if (resourceResults.image != null) {
            addIdToImageMapping("image", resourceResults.image)
        }
    }

    data class TileState(val count: Int)

    data class ResourceState(val image: ImageResource?)
}

@WearPreviewDevices
@WearPreviewFontSizes
@Composable
fun SampleTilePreview() {
    val context = LocalContext.current

    val tileState = remember { SampleTileRenderer.TileState(0) }

    val resourceState = remember {
        val image =
            BitmapFactory.decodeResource(context.resources, R.drawable.ic_uamp).toImageResource()
        SampleTileRenderer.ResourceState(image)
    }

    val renderer = remember {
        SampleTileRenderer(context)
    }

    TileLayoutPreview(
        tileState,
        resourceState,
        renderer
    )
}
