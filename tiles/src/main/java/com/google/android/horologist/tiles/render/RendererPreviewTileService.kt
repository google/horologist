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

@file:OptIn(ExperimentalHorologistApi::class)

package com.google.android.horologist.tiles.render

import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.ResourceBuilders.Resources
import androidx.wear.tiles.TileBuilders.Tile
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService

/**
 * A base class for a Preview only TileService using a TileLayoutRenderer.
 */
public abstract class RendererPreviewTileService<T, R, S : TileLayoutRenderer<T, R>> :
    SuspendingTileService() {
    private lateinit var renderer: S

    override fun onCreate() {
        super.onCreate()

        renderer = createTileRenderer()
    }

    public abstract fun createTileRenderer(): S

    public abstract suspend fun createTileState(): T

    public abstract suspend fun createResourcesInput(): R

    override suspend fun tileRequest(requestParams: TileRequest): Tile {
        return renderer.renderTimeline(createTileState(), requestParams)
    }

    override suspend fun resourcesRequest(requestParams: ResourcesRequest): Resources {
        return renderer.produceRequestedResources(
            createResourcesInput(),
            requestParams
        )
    }
}
