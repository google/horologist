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

import androidx.wear.tiles.LayoutElementBuilders.Column
import androidx.wear.tiles.LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER
import androidx.wear.tiles.LayoutElementBuilders.Layout
import androidx.wear.tiles.LayoutElementBuilders.LayoutElement
import androidx.wear.tiles.LayoutElementBuilders.Text
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.ResourceBuilders
import androidx.wear.tiles.TileBuilders.Tile
import androidx.wear.tiles.TimelineBuilders.Timeline
import androidx.wear.tiles.TimelineBuilders.TimelineEntry
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

public class TestTileService : CoroutinesTileService() {
    var delayDuration = 0.seconds
    var started = 0
    var cancelled = 0
    var completed = 0

    override suspend fun tileRequest(requestParams: RequestBuilders.TileRequest): Tile {
        started++

        delay(delayDuration)

        return Tile.Builder()
            .setResourcesVersion(FAKE_VERSION)
            .setTimeline(
                Timeline.Builder()
                    .addTimelineEntry(
                        TimelineEntry.Builder()
                            .setLayout(
                                Layout.Builder().setRoot(mainLayout())
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .build().also {
                completed++
            }
    }

    override suspend fun resourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest
    ): ResourceBuilders.Resources = ResourceBuilders.Resources.Builder().setVersion(FAKE_VERSION)
        .build()

    private fun mainLayout(): LayoutElement {
        return Column.Builder()
            .addContent(
                Text.Builder()
                    .setText("Tile Visiblity: showing")
                    .build()
            )
            .setHorizontalAlignment(HORIZONTAL_ALIGN_CENTER)
            .build()
    }

    public companion object {
        public val FAKE_VERSION = "1"
    }
}
