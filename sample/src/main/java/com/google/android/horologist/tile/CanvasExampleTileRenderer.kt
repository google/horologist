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

@file:OptIn(ExperimentalHorologistComposeToolsApi::class, ExperimentalHorologistTilesApi::class)

package com.google.android.horologist.tile

import android.content.Context
import android.graphics.Color.WHITE
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.wear.tiles.ColorBuilders
import androidx.wear.tiles.DeviceParametersBuilders.DeviceParameters
import androidx.wear.tiles.DimensionBuilders.ExpandedDimensionProp
import androidx.wear.tiles.LayoutElementBuilders
import androidx.wear.tiles.LayoutElementBuilders.Box
import androidx.wear.tiles.LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER
import androidx.wear.tiles.LayoutElementBuilders.Image
import androidx.wear.tiles.LayoutElementBuilders.VERTICAL_ALIGN_CENTER
import androidx.wear.tiles.ResourceBuilders.Resources
import androidx.wear.tiles.material.Text
import androidx.wear.tiles.material.Typography.TYPOGRAPHY_DISPLAY1
import com.google.android.horologist.compose.tools.ExperimentalHorologistComposeToolsApi
import com.google.android.horologist.compose.tools.LayoutElementPreview
import com.google.android.horologist.compose.tools.TileLayoutPreview
import com.google.android.horologist.compose.tools.WearLargeRoundDevicePreview
import com.google.android.horologist.compose.tools.WearPreviewDevices
import com.google.android.horologist.tile.CanvasExampleTileRenderer.Companion.CanvasId
import com.google.android.horologist.tiles.ExperimentalHorologistTilesApi
import com.google.android.horologist.tiles.canvas.canvasToImageResource
import com.google.android.horologist.tiles.render.SingleTileLayoutRenderer

class CanvasExampleTileRenderer(context: Context) :
    SingleTileLayoutRenderer<Unit, Unit>(
        context
    ) {
    override fun renderTile(
        state: Unit,
        deviceParameters: DeviceParameters
    ): LayoutElementBuilders.LayoutElement {
        return Box.Builder()
            .setHeight(Expanded)
            .setWidth(Expanded)
            .setVerticalAlignment(VERTICAL_ALIGN_CENTER)
            .setHorizontalAlignment(HORIZONTAL_ALIGN_CENTER)
            .addContent(radialFillImage())
            .addContent(
                Text.Builder(context, "Tile")
                    .setTypography(TYPOGRAPHY_DISPLAY1)
                    .setColor(ColorBuilders.argb(WHITE))
                    .build()
            )
            .build()
    }

    override fun Resources.Builder.produceRequestedResources(
        resourceResults: Unit,
        deviceParameters: DeviceParameters,
        resourceIds: MutableList<String>
    ) {
        val canvas = radialFillResource()

        addIdToImageMapping(CanvasId, canvas)
    }

    internal fun radialFillImage() = Image.Builder()
        .setResourceId(CanvasId)
        .setHeight(Expanded)
        .setWidth(Expanded)
        .build()

    internal fun radialFillResource() = canvasToImageResource(Size(100f, 100f), Density(context)) {
        radialFill()
    }

    companion object {
        const val CanvasId = "canvas"
        val Expanded = ExpandedDimensionProp.Builder().build()
    }
}

// https://www.answertopia.com/jetpack-compose/jetpack-compose-canvas-graphics-drawing-tutorial/
fun DrawScope.radialFill() {
    val canvasWidth = size.width
    val canvasHeight = size.height
    val radius = (canvasWidth / 2f).dp.toPx()
    val colorList: List<Color> = listOf(
        Color.Red, Color.Blue,
        Color.Magenta, Color.Yellow, Color.Green, Color.Cyan
    )

    val brush = Brush.radialGradient(
        colors = colorList,
        center = center,
        radius = radius,
        tileMode = TileMode.Repeated
    )

    drawCircle(
        brush = brush,
        center = center,
        radius = radius
    )
}

@WearLargeRoundDevicePreview
@Composable
fun ComposeRadialFillExample() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        radialFill()
    }
}

@OptIn(ExperimentalHorologistComposeToolsApi::class)
@WearPreviewDevices
@Composable
fun CanvasExampleTilePreview() {
    val context = LocalContext.current

    val renderer = remember {
        CanvasExampleTileRenderer(context)
    }

    TileLayoutPreview(
        Unit,
        Unit,
        renderer
    )
}

@IconSizePreview
@Composable
fun CanvasPreview() {
    val context = LocalContext.current

    val renderer = remember {
        CanvasExampleTileRenderer(context)
    }

    LayoutElementPreview(
        renderer.radialFillImage()
    ) {
        addIdToImageMapping(CanvasId, renderer.radialFillResource())
    }
}
