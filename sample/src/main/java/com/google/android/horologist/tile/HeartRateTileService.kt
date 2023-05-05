/*
 * Copyright 2023 The Android Open Source Project
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

import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.expression.AnimationParameterBuilders
import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.TileBuilders
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tile.HeartRateTileRenderer.Companion.ANIMATION_DURATION_MILLIS
import com.google.android.horologist.tiles.SuspendingTileService

@ExperimentalHorologistApi
/** Service to provide HeartRate Tile.  */
class HeartRateTileService : SuspendingTileService() {
    private lateinit var renderer: HeartRateTileRenderer

    override fun onCreate() {
        super.onCreate()

        renderer = HeartRateTileRenderer(this)
    }

    private val animationSpec = AnimationParameterBuilders.AnimationSpec.Builder()
        .setAnimationParameters(
            AnimationParameterBuilders.AnimationParameters.Builder()
                .setDurationMillis(ANIMATION_DURATION_MILLIS)
                .build()
        )
        .build()

    override suspend fun tileRequest(requestParams: TileRequest): TileBuilders.Tile {
        return renderer.renderTimeline(Unit, requestParams)
    }

    override suspend fun resourcesRequest(requestParams: ResourcesRequest): ResourceBuilders.Resources {
        return renderer.produceRequestedResources(
            Unit,
            requestParams
        )
    }
}