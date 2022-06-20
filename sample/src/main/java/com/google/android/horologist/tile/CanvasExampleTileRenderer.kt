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

@file:OptIn(ExperimentalHorologistComposeToolsApi::class, ExperimentalHorologistTilesApi::class,
    ExperimentalHorologistTilesApi::class
)

package com.google.android.horologist.tile

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.wear.tiles.DeviceParametersBuilders.DeviceParameters
import androidx.wear.tiles.DimensionBuilders.DpProp
import androidx.wear.tiles.DimensionBuilders.ExpandedDimensionProp
import androidx.wear.tiles.LayoutElementBuilders
import androidx.wear.tiles.LayoutElementBuilders.Box
import androidx.wear.tiles.LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER
import androidx.wear.tiles.LayoutElementBuilders.Image
import androidx.wear.tiles.LayoutElementBuilders.VERTICAL_ALIGN_CENTER
import androidx.wear.tiles.ResourceBuilders.Resources
import androidx.wear.tiles.material.Colors
import com.google.android.horologist.compose.tools.ExperimentalHorologistComposeToolsApi
import com.google.android.horologist.compose.tools.LayoutElementPreview
import com.google.android.horologist.compose.tools.TileLayoutPreview
import com.google.android.horologist.compose.tools.WearLargeRoundDevicePreview
import com.google.android.horologist.compose.tools.WearPreviewDevices
import com.google.android.horologist.tile.CanvasExampleTileRenderer.Companion.CanvasId
import com.google.android.horologist.tile.CanvasExampleTileRenderer.Companion.barPaint
import com.google.android.horologist.tile.CanvasExampleTileRenderer.Companion.days
import com.google.android.horologist.tile.CanvasExampleTileRenderer.Companion.textPaint
import com.google.android.horologist.tile.CanvasExampleTileRenderer.GoalState
import com.google.android.horologist.tiles.ExperimentalHorologistTilesApi
import com.google.android.horologist.tiles.canvas.canvasToImageResource
import com.google.android.horologist.tiles.render.SingleTileLayoutRenderer

class CanvasExampleTileRenderer(context: Context) :
    SingleTileLayoutRenderer<Unit, GoalState>(
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
            .addContent(goalChart())
            .build()
    }

    override fun Resources.Builder.produceRequestedResources(
        resourceResults: GoalState,
        deviceParameters: DeviceParameters,
        resourceIds: MutableList<String>
    ) {
        addIdToImageMapping(CanvasId, radialFillResource())
        addIdToImageMapping(GoalChartId, goalChartResource(resourceResults))
    }

    internal fun radialFillImage() = Image.Builder()
        .setResourceId(CanvasId)
        .setHeight(Expanded)
        .setWidth(Expanded)
        .build()

    internal fun goalChart() = Image.Builder()
        .setResourceId(GoalChartId)
        .setHeight(
            DpProp.Builder()
                .setValue(100f)
                .build()
        )
        .setWidth(
            DpProp.Builder()
                .setValue(160f)
                .build()
        )
        .setWidth(Expanded)
        .build()

    internal fun radialFillResource() = canvasToImageResource(Size(100f, 100f), Density(context)) {
        radialFill()
    }

    internal fun goalChartResource(state: GoalState) = canvasToImageResource(
        Size(320f, 200f),
        Density(context)
    ) {
        goalChart(state, textPaint, barPaint)
    }

    data class GoalState(val values: List<Int>)

    companion object {
        val textPaint = Paint().apply {
            color = Color.White
        }

        val barPaint = Paint().apply {
            color = Color(Colors.DEFAULT.primary)
            strokeWidth = 7f
            strokeCap = StrokeCap.Round
        }

        val days = "MTWTFSS".toList()

        const val CanvasId = "canvas"
        const val GoalChartId = "goals"
        val Expanded = ExpandedDimensionProp.Builder().build()
    }
}

// https://www.answertopia.com/jetpack-compose/jetpack-compose-canvas-graphics-drawing-tutorial/
fun DrawScope.radialFill() {
    val canvasWidth = size.width
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

@Composable
fun GoalChart(
    modifier: Modifier = Modifier,
    state: GoalState
) {
    Canvas(modifier = modifier) {
        goalChart(state, textPaint, barPaint)
    }
}

fun DrawScope.goalChart(state: GoalState, textPaint: Paint, barPaint: Paint) {
    val androidPaint = textPaint.asFrameworkPaint()
    val width = size.width
    val max = state.values.maxOrNull() ?: 10
    this.drawIntoCanvas {
        state.values.forEachIndexed { i, value ->
            val x = 50f + (width / 9) * i
            val label = days.getOrNull(i)?.toString().orEmpty()
            it.nativeCanvas.drawText(label, x, size.height * 0.9f, androidPaint)
            val height = 0.6f * (value.toFloat() / max) * size.height
            val bottom = size.height * 0.9f - 20f
            it.drawLine(
                Offset(x, bottom),
                Offset(x, bottom - height),
                barPaint
            )
        }
    }
}


@WearLargeRoundDevicePreview
@Composable
fun ComposeRadialFillExample() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        radialFill()
    }
}

@OptIn(ExperimentalHorologistComposeToolsApi::class, ExperimentalHorologistTilesApi::class)
@WearPreviewDevices
@Composable
fun CanvasExampleTilePreview() {
    val context = LocalContext.current

    val renderer = remember {
        CanvasExampleTileRenderer(context)
    }

    val goalState = GoalState(listOf(1, 1, 7, 5, 3, 0, 6))

    TileLayoutPreview(
        Unit,
        goalState,
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

@WearLargeRoundDevicePreview
@Composable
fun GoalChartPreview() {
    val state = remember { GoalState(listOf(5, 0, 5, 4, 5, 2, 3)) }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        GoalChart(
            modifier = Modifier
                .size(160.dp, 100.dp)
                .border(1.dp, Color.White), state = state
        )
    }
}
