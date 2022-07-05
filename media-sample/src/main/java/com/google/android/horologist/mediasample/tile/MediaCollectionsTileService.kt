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

@file:OptIn(ExperimentalHorologistTilesApi::class)

package com.google.android.horologist.mediasample.tile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.wear.tiles.ActionBuilders
import androidx.wear.tiles.ActionBuilders.AndroidActivity
import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.ResourceBuilders.Resources
import androidx.wear.tiles.TileBuilders.Tile
import com.google.android.horologist.compose.tools.TileLayoutPreview
import com.google.android.horologist.compose.tools.WearPreviewDevices
import com.google.android.horologist.media.ui.tiles.MediaCollectionsTileRenderer
import com.google.android.horologist.media.ui.tiles.toTileColors
import com.google.android.horologist.mediasample.R
import com.google.android.horologist.mediasample.components.MediaActivity
import com.google.android.horologist.mediasample.ui.app.UampColors
import com.google.android.horologist.tiles.CoroutinesTileService
import com.google.android.horologist.tiles.ExperimentalHorologistTilesApi
import com.google.android.horologist.tiles.images.drawableResToImageResource

/**
 * A Tile with links to open the app, or two specific media collections (playlist, album).
 */
class MediaCollectionsTileService : CoroutinesTileService() {
    private lateinit var renderer: MediaCollectionsTileRenderer

    override fun onCreate() {
        super.onCreate()

        renderer = MediaCollectionsTileRenderer(this, UampColors.toTileColors())
    }

    /**
     * Render a Playlist primary button and two chips with direct links to collections.
     */
    override suspend fun tileRequest(requestParams: TileRequest): Tile {
        return renderer.renderTimeline(
            state = MediaCollectionsTileRenderer.MediaCollectionsState(
                R.string.horologist_sample_playlists,
                appLauncher(),
                MediaCollectionsTileRenderer.MediaCollection(
                    name = "Liked Songs",
                    id = "1",
                    action = appLauncher(collectionId = "1")
                ),
                MediaCollectionsTileRenderer.MediaCollection(
                    name = "Podcasts",
                    id = "2",
                    action = appLauncher(collectionId = "2")
                )
            ),
            requestParams = requestParams
        )
    }

    /**
     * Create a launcher to an activity, with an optional extra "collection" linking to
     * a screen to open.
     */
    private fun appLauncher(collectionId: String? = null) = ActionBuilders.LaunchAction.Builder()
        .setAndroidActivity(
            AndroidActivity.Builder()
                .setClassName(MediaActivity::class.java.name)
                .setPackageName(this.packageName)
                .apply {
                    if (collectionId != null) {
                        addKeyToExtraMapping(
                            MediaActivity.CollectionKey,
                            ActionBuilders.AndroidStringExtra.Builder()
                                .setValue(collectionId)
                                .build()
                        )
                    }
                }
                .build()
        )
        .build()

    /**
     * Show UAMP as AppIcon, and favourites and podcasts icons.
     */
    override suspend fun resourcesRequest(requestParams: ResourcesRequest): Resources {
        return renderer.produceRequestedResources(
            MediaCollectionsTileRenderer.ResourceState(
                R.drawable.ic_uamp,
                mapOf(
                    1 to drawableResToImageResource(R.drawable.ic_baseline_queue_music_24),
                    2 to drawableResToImageResource(R.drawable.ic_baseline_podcasts_24)
                )
            ),
            requestParams
        )
    }
}

@WearPreviewDevices
@Composable
fun SampleTilePreview() {
    val context = LocalContext.current

    val action = ActionBuilders.LaunchAction.Builder()
        .build()

    val tileState = remember {
        MediaCollectionsTileRenderer.MediaCollectionsState(
            chipName = R.string.horologist_sample_playlists,
            chipAction = action,
            collection1 = MediaCollectionsTileRenderer.MediaCollection(
                name = "Liked Songs",
                id = "1",
                action = action
            ),
            collection2 = MediaCollectionsTileRenderer.MediaCollection(
                name = "Podcasts",
                id = "2",
                action = action
            ),
        )
    }

    val resourceState = remember {
        MediaCollectionsTileRenderer.ResourceState(
            appIcon = R.drawable.ic_uamp,
            images = mapOf(
                1 to drawableResToImageResource(R.drawable.ic_baseline_queue_music_24),
                2 to drawableResToImageResource(R.drawable.ic_baseline_podcasts_24)
            )
        )
    }

    val renderer = remember {
        MediaCollectionsTileRenderer(
            context = context,
            materialTheme = UampColors.toTileColors()
        )
    }

    TileLayoutPreview(
        tileState,
        resourceState,
        renderer
    )
}
