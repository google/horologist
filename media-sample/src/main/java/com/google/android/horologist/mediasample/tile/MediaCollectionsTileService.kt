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

package com.google.android.horologist.mediasample.tile

import androidx.wear.tiles.ActionBuilders
import androidx.wear.tiles.ActionBuilders.AndroidActivity
import androidx.wear.tiles.ActionBuilders.AndroidIntExtra
import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.ResourceBuilders.Resources
import androidx.wear.tiles.TileBuilders.Tile
import com.google.android.horologist.media3.R
import com.google.android.horologist.media3.tiles.MediaCollectionsTileRenderer
import com.google.android.horologist.media3.tiles.MediaCollectionsTileRenderer.MediaCollectionsState
import com.google.android.horologist.media3.tiles.MediaCollectionsTileRenderer.ResourceState
import com.google.android.horologist.mediasample.components.MediaActivity
import com.google.android.horologist.mediasample.ui.app.UampColors
import com.google.android.horologist.tiles.CoroutinesTileService
import com.google.android.horologist.tiles.images.drawableResToImageResource
import com.google.android.horologist.tiles.theme.toTileColors

class MediaCollectionsTileService : CoroutinesTileService() {
    private lateinit var renderer: MediaCollectionsTileRenderer

    override fun onCreate() {
        super.onCreate()

        renderer = MediaCollectionsTileRenderer(this, UampColors.toTileColors())
    }

    override suspend fun tileRequest(requestParams: TileRequest): Tile {
        return renderer.renderTimeline(
            state = MediaCollectionsState(
                MediaCollectionsTileRenderer.MediaCollection(
                    "Liked Songs",
                    1,
                    R.drawable.ic_baseline_queue_music_24,
                    collectionLauncher(1)
                ),
                MediaCollectionsTileRenderer.MediaCollection(
                    "Podcasts",
                    2,
                    R.drawable.ic_baseline_podcasts_24,
                        collectionLauncher(2)
                )
            ),
            requestParams = requestParams
        )
    }

    private fun collectionLauncher(collectionId: Int) = ActionBuilders.LaunchAction.Builder()
        .setAndroidActivity(
            AndroidActivity.Builder()
                .setClassName(MediaActivity::class.java.name)
                .setPackageName(this.packageName)
                .addKeyToExtraMapping(
                    "collection", AndroidIntExtra.Builder()
                    .setValue(collectionId)
                    .build()
                )
                .build()
        )
        .build()

    override suspend fun resourcesRequest(requestParams: ResourcesRequest): Resources {
        return renderer.produceRequestedResources(
            ResourceState(
                mapOf(
                    1 to drawableResToImageResource(R.drawable.ic_baseline_queue_music_24),
                    2 to drawableResToImageResource(R.drawable.ic_baseline_podcasts_24)
                )
            ),
            requestParams
        )
    }
}
