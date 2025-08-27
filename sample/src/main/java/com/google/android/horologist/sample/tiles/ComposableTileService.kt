/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.sample.tiles

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.FilledIconButton
import androidx.wear.compose.material3.Text
import androidx.wear.protolayout.DimensionBuilders.dp
import androidx.wear.protolayout.DimensionBuilders.expand
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.TileBuilders.Tile
import com.google.android.horologist.tiles.SuspendingTileService
import com.google.android.horologist.tiles.composable.ServiceComposableBitmapRenderer
import com.google.android.horologist.tiles.images.toImageResource
import java.util.UUID

class ComposableTileService : SuspendingTileService() {
    private lateinit var renderer: ServiceComposableBitmapRenderer
    val ComposeId = "circleCompose"

    override fun onCreate() {
        super.onCreate()

        renderer = ServiceComposableBitmapRenderer(this.application, this)
    }

    /** This method returns a Tile object, which describes the layout of the Tile. */
    override suspend fun tileRequest(requestParams: TileRequest): Tile {
        val layoutElement =
            LayoutElementBuilders.Box.Builder().setWidth(expand()).setHeight(expand()).addContent(
                LayoutElementBuilders.Image.Builder().setWidth(dp(100f)).setHeight(dp(100f))
                    .setResourceId(ComposeId).build(),
            ).build()

        return Tile.Builder().setResourcesVersion(UUID.randomUUID().toString())
            .setTileTimeline(Timeline.fromLayoutElement(layoutElement)).build()
    }

    override suspend fun resourcesRequest(requestParams: ResourcesRequest): Resources {
        // Add images to the Resources object, and return
        val circleComposeBitmap = circleCompose()
        return Resources.Builder().setVersion(requestParams.version).apply {
            if (circleComposeBitmap != null) {
                addIdToImageMapping(
                    ComposeId,
                    circleComposeBitmap.toImageResource(),
                )
            }
        }.build()
    }

    private suspend fun circleCompose(): ImageBitmap? =
        renderer.renderComposableToBitmap(DpSize(100.dp, 100.dp)) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(Color.DarkGray)
                }
                FilledIconButton(onClick = {}) {
                    Text("\uD83D\uDC6A")
                }
            }
        }
}
