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

package com.google.android.horologist.tiles.preview

import android.content.Context
import androidx.wear.protolayout.ColorBuilders
import androidx.wear.protolayout.DeviceParametersBuilders.DeviceParameters
import androidx.wear.protolayout.DimensionBuilders.ExpandedDimensionProp
import androidx.wear.protolayout.LayoutElementBuilders.Box
import androidx.wear.protolayout.LayoutElementBuilders.Column
import androidx.wear.protolayout.LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER
import androidx.wear.protolayout.LayoutElementBuilders.LayoutElement
import androidx.wear.protolayout.LayoutElementBuilders.VERTICAL_ALIGN_CENTER
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.material.Button
import androidx.wear.protolayout.material.ButtonColors
import androidx.wear.protolayout.material.Chip
import androidx.wear.protolayout.material.ChipColors
import androidx.wear.protolayout.material.Colors
import androidx.wear.protolayout.material.CompactChip
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import com.google.android.horologist.tiles.R
import com.google.android.horologist.tiles.components.NoOpClickable
import com.google.android.horologist.tiles.images.drawableResToImageResource
import com.google.android.horologist.tiles.render.SingleTileLayoutRenderer

/**
 * Tile that renders components with typical layouts and a theme color.
 */
public class ThemePreviewTileRenderer(context: Context, private val thisTheme: Colors) :
    SingleTileLayoutRenderer<Unit, Unit>(context) {
        override fun createTheme(): Colors = thisTheme

        override fun renderTile(
            state: Unit,
            deviceParameters: DeviceParameters,
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
                        .build(),
                )
                .build()
        }

        internal fun title() = Text.Builder(context, "Title")
            .setTypography(Typography.TYPOGRAPHY_CAPTION1)
            .setColor(ColorBuilders.argb(theme.onSurface))
            .build()

        internal fun primaryChip(
            deviceParameters: DeviceParameters,
        ) = Chip.Builder(context, NoOpClickable, deviceParameters)
            .setPrimaryLabelContent("Primary Chip")
            .setIconContent(Icon)
            .setChipColors(ChipColors.primaryChipColors(theme))
            .build()

        internal fun secondaryCompactChip(
            deviceParameters: DeviceParameters,
        ) = CompactChip.Builder(context, "Secondary Chip", NoOpClickable, deviceParameters)
            .setChipColors(ChipColors.secondaryChipColors(theme))
            .build()

        internal fun primaryIconButton() =
            Button.Builder(context, NoOpClickable)
                .setIconContent(Icon)
                .setButtonColors(ButtonColors.primaryButtonColors(theme))
                .build()

        internal fun secondaryIconButton() =
            Button.Builder(context, NoOpClickable)
                .setIconContent(Icon)
                .setButtonColors(ButtonColors.secondaryButtonColors(theme))
                .build()

        override fun ResourceBuilders.Resources.Builder.produceRequestedResources(
            resourceState: Unit,
            deviceParameters: DeviceParameters,
            resourceIds: MutableList<String>,
        ) {
            addIdToImageMapping(Icon, drawableResToImageResource(R.drawable.ic_nordic))
        }

        private companion object {
            private const val Icon = "icon"
        }
    }
