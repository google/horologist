/*
 * Copyright 2021 The Android Open Source Project
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

package com.google.android.horologist.tile

import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.ResourceBuilders.Resources
import androidx.wear.tiles.TileBuilders.Tile
import coil.ImageLoader
import com.google.android.horologist.tiles.CoroutinesTileService
import com.google.android.horologist.tiles.loadImageResource

class SampleTileService : CoroutinesTileService() {
    private lateinit var renderer: SampleTileRenderer
    private lateinit var imageLoader: ImageLoader

    private var count = 0

    override fun onCreate() {
        super.onCreate()

        renderer = SampleTileRenderer(this)
        imageLoader = ImageLoader(this)
    }

    override suspend fun tileRequest(requestParams: TileRequest): Tile {
        if (requestParams.state?.lastClickableId == "click") {
            count++
        }

        return renderer.renderTimeline(count, requestParams)
    }

    override suspend fun resourcesRequest(requestParams: ResourcesRequest): Resources {
        val imageResource = imageLoader.loadImageResource(
            this,
            "https://wordpress.org/openverse/image/34896de8-afb0-494c-af63-17b73fc14124/"
        )

        return renderer.produceRequestedResources(imageResource, requestParams)
    }
}
