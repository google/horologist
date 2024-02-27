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

package com.google.android.horologist.mediasample.data.service.tile

import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ActionBuilders.AndroidActivity
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.TileBuilders.Tile
import coil.ImageLoader
import com.google.android.horologist.media.repository.PlaylistRepository
import com.google.android.horologist.media.ui.tiles.MediaCollectionsTileRenderer
import com.google.android.horologist.media.ui.tiles.toTileColors
import com.google.android.horologist.mediasample.BuildConfig
import com.google.android.horologist.mediasample.R
import com.google.android.horologist.mediasample.ui.app.MediaActivity
import com.google.android.horologist.mediasample.ui.app.UampColors
import com.google.android.horologist.tiles.SuspendingTileService
import com.google.android.horologist.tiles.images.loadImageResource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * A Tile with links to open the app, or two specific media collections (playlist, album).
 *
 * Links to the MediaActivity, with extras for collection and optionally mediaId.
 */
@AndroidEntryPoint
class MediaCollectionsTileService : SuspendingTileService() {

    @Inject
    lateinit var playlistRepository: PlaylistRepository

    @Inject
    internal lateinit var imageLoader: ImageLoader

    private val renderer: MediaCollectionsTileRenderer = MediaCollectionsTileRenderer(
        context = this,
        materialTheme = UampColors.toTileColors(),
        debugResourceMode = BuildConfig.DEBUG,
    )

    /**
     * Render a Playlist primary button and two chips with direct links to collections.
     */
    override suspend fun tileRequest(requestParams: TileRequest): Tile {
        val playlists = playlistRepository.getAll().first()

        val firstPlaylist = playlists.first()
        val firstSong = firstPlaylist.mediaList.first()

        val lastPlaylist = playlists.last()

        return renderer.renderTimeline(
            state = MediaCollectionsTileRenderer.MediaCollectionsState(
                R.string.sample_playlists,
                appLauncher(),
                MediaCollectionsTileRenderer.MediaCollection(
                    name = firstSong.title,
                    artworkId = firstSong.id,
                    action = appLauncher {
                        addStringExtra(MediaActivity.CollectionKey, firstPlaylist.id)
                        addStringExtra(MediaActivity.MediaIdKey, firstSong.id)
                    },
                ),
                MediaCollectionsTileRenderer.MediaCollection(
                    name = lastPlaylist.name,
                    artworkId = lastPlaylist.id,
                    action = appLauncher {
                        addStringExtra(MediaActivity.CollectionKey, lastPlaylist.id)
                    },
                ),
            ),
            requestParams = requestParams,
        )
    }

    private fun AndroidActivity.Builder.addStringExtra(key: String, value: String) {
        addKeyToExtraMapping(
            key,
            ActionBuilders.AndroidStringExtra.Builder().setValue(value).build(),
        )
    }

    /**
     * Create a launcher to an activity, with an optional extra "collection" linking to
     * a screen to open.
     */
    private fun appLauncher(
        extrasBuilder: AndroidActivity.Builder.() -> Unit = {},
    ) = ActionBuilders.LaunchAction.Builder().setAndroidActivity(
        AndroidActivity.Builder().setClassName(MediaActivity::class.java.name)
            .setPackageName(this.packageName).apply {
                extrasBuilder()
            }.build(),
    ).build()

    /**
     * Show UAMP as AppIcon, and favourites and podcasts icons.
     */
    override suspend fun resourcesRequest(requestParams: ResourcesRequest): Resources {
        val playlists = playlistRepository.getAll().first()

        val firstPlaylist = playlists.first()
        val firstSong = firstPlaylist.mediaList.first()

        val lastPlaylist = playlists.last()

        val songResource = imageLoader.loadImageResource(this, firstSong.artworkUri)
        val albumResource = imageLoader.loadImageResource(this, lastPlaylist.artworkUri)

        return renderer.produceRequestedResources(
            MediaCollectionsTileRenderer.ResourceState(
                com.google.android.horologist.logo.R.drawable.ic_stat_horologist,
                mapOf(
                    firstSong.id to songResource,
                    lastPlaylist.id to albumResource,
                ),
            ),
            requestParams,
        )
    }
}
