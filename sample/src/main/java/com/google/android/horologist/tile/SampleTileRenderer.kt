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

@file:Suppress("DEPRECATION")

package com.google.android.horologist.tile

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import androidx.wear.tiles.ColorBuilders.argb
import androidx.wear.tiles.DeviceParametersBuilders.DeviceParameters
import androidx.wear.tiles.LayoutElementBuilders
import androidx.wear.tiles.ModifiersBuilders.Clickable
import androidx.wear.tiles.ResourceBuilders.ImageResource
import androidx.wear.tiles.ResourceBuilders.Resources
import androidx.wear.tiles.material.Button
import androidx.wear.tiles.material.ButtonColors
import androidx.wear.tiles.material.ChipColors
import androidx.wear.tiles.material.CompactChip
import androidx.wear.tiles.material.Text
import androidx.wear.tiles.material.Typography
import androidx.wear.tiles.material.layouts.MultiButtonLayout
import androidx.wear.tiles.material.layouts.PrimaryLayout
import com.google.android.horologist.compose.tools.LayoutElementPreview
import com.google.android.horologist.compose.tools.TileLayoutPreview
import com.google.android.horologist.sample.R
import com.google.android.horologist.tile.SampleTileRenderer.Companion.Icon1
import com.google.android.horologist.tile.SampleTileRenderer.Companion.Image1
import com.google.android.horologist.tile.SampleTileRenderer.Companion.TileIcon
import com.google.android.horologist.tile.SampleTileRenderer.Companion.TileImage
import com.google.android.horologist.tiles.components.NoOpClickable
import com.google.android.horologist.tiles.images.drawableResToImageResource
import com.google.android.horologist.tiles.images.toImageResource
import com.google.android.horologist.tiles.render.SingleTileLayoutRenderer

class SampleTileRenderer(context: Context) :
    SingleTileLayoutRenderer<SampleTileRenderer.TileState, SampleTileRenderer.ResourceState>(
        context
    ) {
    override fun renderTile(
        state: TileState,
        deviceParameters: DeviceParameters
    ): LayoutElementBuilders.LayoutElement {
        return PrimaryLayout.Builder(deviceParameters)
            .setPrimaryLabelTextContent(
                Text.Builder(context, "Count: ${state.count}")
                    .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                    .setColor(argb(theme.primary))
                    .build()
            )
            .setContent(
                MultiButtonLayout.Builder()
                    .addButtonContent(
                        imageButton(NoOpClickable)
                    )
                    .addButtonContent(
                        iconButton(NoOpClickable)
                    )
                    .build()
            )
            .setPrimaryChipContent(
                CompactChip.Builder(context, "Action", NoOpClickable, deviceParameters)
                    .setChipColors(ChipColors.primaryChipColors(theme))
                    .build()
            )
            .build()
    }

    internal fun iconButton(clickable: Clickable) =
        Button.Builder(context, clickable)
            .setIconContent(Icon1)
            .setButtonColors(ButtonColors.secondaryButtonColors(theme))
            .build()

    internal fun imageButton(clickable: Clickable) =
        Button.Builder(context, clickable)
            .setImageContent(Image1)
            .setButtonColors(ButtonColors.secondaryButtonColors(theme))
            .build()

    override fun Resources.Builder.produceRequestedResources(
        resourceState: ResourceState,
        deviceParameters: DeviceParameters,
        resourceIds: MutableList<String>
    ) {
        addIdToImageMapping(Icon1, drawableResToImageResource(TileIcon))
        if (resourceState.image != null) {
            addIdToImageMapping(Image1, resourceState.image)
        }
    }

    data class TileState(val count: Int)

    data class ResourceState(val image: ImageResource?)

    companion object {
        const val Image1 = "image1"
        const val Icon1 = "icon1"
        const val TileIcon = R.drawable.ic_android
        const val TileImage = R.drawable.ic_tileicon
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun SampleTilePreview() {
    val context = LocalContext.current

    val tileState = remember { SampleTileRenderer.TileState(0) }

    val resourceState = remember {
        val image = BitmapFactory.decodeResource(context.resources, TileImage)
        SampleTileRenderer.ResourceState(image?.toImageResource())
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

@IconSizePreview
@Composable
fun SampleButtonImagePreview() {
    val context = LocalContext.current

    val renderer = remember {
        SampleTileRenderer(context)
    }

    LayoutElementPreview(
        renderer.imageButton(NoOpClickable)
    ) {
        addIdToImageMapping(
            Image1,
            drawableResToImageResource(TileImage)
        )
    }
}

@IconSizePreview
@Composable
fun SampleButtonIconPreview() {
    val context = LocalContext.current

    val renderer = remember {
        SampleTileRenderer(context)
    }

    LayoutElementPreview(
        renderer.iconButton(NoOpClickable)
    ) {
        addIdToImageMapping(
            Icon1,
            drawableResToImageResource(TileIcon)
        )
    }
}

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true,
    widthDp = 100,
    heightDp = 100
)
public annotation class IconSizePreview

@Preview(
    backgroundColor = 0xff000000,
    showBackground = true,
    widthDp = 192,
    heightDp = 100
)
public annotation class FullWidthPreview
