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

package com.google.android.horologist.tiles

import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.ResourceBuilders.Resources
import androidx.wear.tiles.TileBuilders.Tile

/**
 * A base class for a synchronous tile and resource render phase.
 *
 * It assumes all async suspending work is done by the TileService, and given a single
 * representation of the state, creates a 100% reproducible tile and resources.
 *
 * This is designed to allow precise fast Android Studio previews as well as screenshot tests.
 */
@ExperimentalHorologistTilesApi
public interface TileLayoutRenderer<T, R> {
    /**
     * Produce a Timeline for the given tile request and the given state.
     *
     * The state may either represent a single tile or a timeline, in which case the renderer
     * is responsible for determining how many tiles in the timeline.
     *
     * @param tileState the state of the tile, typically a data class
     * @param requestParams the incoming request params.
     */
    public fun renderTimeline(
        tileState: T,
        requestParams: RequestBuilders.TileRequest,
    ): Tile

    /**
     * Produce resources for the given request. The implementation should read
     * [androidx.wear.tiles.RequestBuilders.ResourcesRequest.getResourceIds] and if not empty
     * only return the requested resources.
     *
     * @param resourceResults the state of the resources, typically a data class containing loaded
     * bitmaps.
     * @param requestParams the incoming request params.
     */
    public fun produceRequestedResources(
        resourceResults: R,
        requestParams: RequestBuilders.ResourcesRequest,
    ): Resources
}
