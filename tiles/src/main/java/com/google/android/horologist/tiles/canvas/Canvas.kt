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

package com.google.android.horologist.tiles.canvas

import android.graphics.Bitmap
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.wear.protolayout.ResourceBuilders.ImageResource
import com.google.android.horologist.tiles.images.toImageResource
import android.graphics.Canvas as AndroidCanvas
import androidx.compose.ui.graphics.Canvas as ComposeCanvas

/**
 * Render an element normally drawn within a Compose Canvas into a Bitmap.
 *
 * This allows shared elements between the app and tiles.
 *
 * @param bitmap the destination to draw into.
 * @param density the compose density to use when drawing.
 * @param size bounds relative to the current canvas to draw within.
 * @param onDraw the render logic.
 */
public fun drawToBitmap(
    bitmap: Bitmap,
    density: Density,
    size: Size,
    onDraw: DrawScope.() -> Unit
) {
    val androidCanvas = AndroidCanvas(bitmap)
    val composeCanvas = ComposeCanvas(androidCanvas)
    val canvasDrawScope = CanvasDrawScope()

    canvasDrawScope.draw(density, LayoutDirection.Ltr, composeCanvas, size) {
        onDraw()
    }
}

/**
 * Render an element normally drawn within a Compose Canvas into a Bitmap and then
 * convert to a Tiles ImageResource.
 *
 * @param size the size of the bitmap desired in pixels.
 * @param density the compose density to use when drawing.
 * @param onDraw the render logic.
 */
@RequiresApi(VERSION_CODES.O)
public fun canvasToImageResource(
    size: Size,
    density: Density,
    onDraw: DrawScope.() -> Unit
): ImageResource {
    return Bitmap.createBitmap(
        size.width.toInt(),
        size.height.toInt(),
        Bitmap.Config.RGB_565,
        false
    ).apply {
        drawToBitmap(bitmap = this, density = density, size = size, onDraw = onDraw)
    }.toImageResource()
}
