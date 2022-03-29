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

package com.google.android.horologist.previews

import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.wear.compose.material.Text
import androidx.wear.tiles.ResourceBuilders
import androidx.wear.tiles.TileBuilders.Tile
import androidx.wear.tiles.renderer.TileRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

@Composable
fun ShowTilePreviews(
    tile: () -> Tile,
    resources: () -> ResourceBuilders.Resources = { emptyResources() },
) {
    Column(modifier = Modifier
        .size(250.dp, 600.dp)
        .background(Color.DarkGray)) {

        Text(text = "Square Small")

        Box(modifier = Modifier
            .size(198.dp)
//            .clip(CircleShape)
            .background(Color.Black)) {
            ShowSingleTilePreview(tile(), resources())
        }

        Text(text = "Circle Large")

        Box(modifier = Modifier
            .size(224.dp)
            .clip(CircleShape)
            .background(Color.Black)) {
            ShowSingleTilePreview(tile(), resources())
        }
    }
}

@Composable
fun ShowSingleTilePreview(
    tile: Tile,
    resources: ResourceBuilders.Resources
) {
    AndroidView(factory = { context ->
        FrameLayout(context).also {
            val tileRenderer = TileRenderer(
                /* uiContext = */ context,
                /* layout = */ tile.timeline?.timelineEntries?.first()?.layout!!,
                /* resources = */ resources,
                /* loadActionExecutor = */ Dispatchers.IO.asExecutor(),
                /* loadActionListener = */ {}
            )

            tileRenderer.inflate(it)
        }
    })
}

fun emptyResources(): ResourceBuilders.Resources {
    return ResourceBuilders.Resources.Builder()
        .setVersion("1")
        .build()
}