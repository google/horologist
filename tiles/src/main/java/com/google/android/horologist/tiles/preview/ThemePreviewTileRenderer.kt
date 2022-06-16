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

@file:OptIn(ExperimentalHorologistTilesApi::class)

package com.google.android.horologist.tiles.preview

import android.content.Context
import androidx.wear.tiles.ColorBuilders
import androidx.wear.tiles.DeviceParametersBuilders.DeviceParameters
import androidx.wear.tiles.DimensionBuilders.ExpandedDimensionProp
import androidx.wear.tiles.LayoutElementBuilders.Box
import androidx.wear.tiles.LayoutElementBuilders.Column
import androidx.wear.tiles.LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER
import androidx.wear.tiles.LayoutElementBuilders.LayoutElement
import androidx.wear.tiles.LayoutElementBuilders.VERTICAL_ALIGN_CENTER
import androidx.wear.tiles.ModifiersBuilders.Clickable
import androidx.wear.tiles.ModifiersBuilders.Clickable.Builder
import androidx.wear.tiles.ResourceBuilders
import androidx.wear.tiles.material.Button
import androidx.wear.tiles.material.ButtonColors
import androidx.wear.tiles.material.Chip
import androidx.wear.tiles.material.ChipColors
import androidx.wear.tiles.material.Colors
import androidx.wear.tiles.material.CompactChip
import androidx.wear.tiles.material.Text
import androidx.wear.tiles.material.Typography
import com.google.android.horologist.tiles.ExperimentalHorologistTilesApi
import com.google.android.horologist.tiles.R
import com.google.android.horologist.tiles.images.drawableResToImageResource
import com.google.android.horologist.tiles.render.SingleTileLayoutRenderer

/**
 * Tile that renders components with typical layouts and a theme colour.
 */
public class ThemePreviewTileRenderer(context: Context, private val thisTheme: Colors) :
    SingleTileLayoutRenderer<Unit, Unit>(context) {
    override fun createTheme(): Colors = thisTheme

    internal val DummyClickable: Clickable = Builder()
        .setId("click")
        .build()

    override fun renderTile(
        state: Unit,
        deviceParameters: DeviceParameters
    ): LayoutElement {
        return Box.Builder()
            .setWidth(ExpandedDimensionProp.Builder().build())
            .setHeight(ExpandedDimensionProp.Builder().build())
            .setHorizontalAlignment(HORIZONTAL_ALIGN_CENTER)
            .setVerticalAlignment(VERTICAL_ALIGN_CENTER)
            .addContent(
                Column.Builder()
                    .addContent(title())
                    .addContent(primaryIconButton())
                    .addContent(primaryChip(deviceParameters))
                    .addContent(secondaryCompactChip(deviceParameters))
                    .addContent(secondaryIconButton())
                    .build()
            )
            .build()
    }

    internal fun title() = Text.Builder(context, "Title")
        .setTypography(Typography.TYPOGRAPHY_CAPTION1)
        .setColor(ColorBuilders.argb(theme.onSurface))
        .build()

    internal fun primaryChip(
        deviceParameters: DeviceParameters
    ) = Chip.Builder(context, DummyClickable, deviceParameters)
        .setPrimaryTextIconContent("Primary Chip", Icon)
        .setChipColors(ChipColors.primaryChipColors(theme))
        .build()

    internal fun secondaryCompactChip(
        deviceParameters: DeviceParameters
    ) = CompactChip.Builder(context, "Secondary Chip", DummyClickable, deviceParameters)
        .setChipColors(ChipColors.secondaryChipColors(theme))
        .build()

    internal fun primaryIconButton() =
        Button.Builder(context, DummyClickable)
            .setIconContent(Icon)
            .setButtonColors(ButtonColors.primaryButtonColors(theme))
            .build()

    internal fun secondaryIconButton() =
        Button.Builder(context, DummyClickable)
            .setIconContent(Icon)
            .setButtonColors(ButtonColors.secondaryButtonColors(theme))
            .build()

    override fun ResourceBuilders.Resources.Builder.produceRequestedResources(
        resourceResults: Unit,
        deviceParameters: DeviceParameters,
        resourceIds: MutableList<String>
    ) {
        addIdToImageMapping(Icon, drawableResToImageResource(R.drawable.ic_nordic))
    }

    private companion object {
        private const val Icon = "icon"
    }
}
