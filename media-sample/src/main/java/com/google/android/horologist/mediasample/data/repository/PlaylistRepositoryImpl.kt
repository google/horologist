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

package com.google.android.horologist.mediasample.data.repository

import com.google.android.horologist.media.sync.api.Syncable
import com.google.android.horologist.media.sync.api.Synchronizer
import com.google.android.horologist.media.sync.api.changeListSync
import com.google.android.horologist.mediasample.data.api.NetworkChangeListService
import com.google.android.horologist.mediasample.data.datasource.MediaLocalDataSource
import com.google.android.horologist.mediasample.data.datasource.PlaylistLocalDataSource
import com.google.android.horologist.mediasample.data.datasource.PlaylistRemoteDataSource
import com.google.android.horologist.mediasample.data.mapper.PlaylistMapper
import com.google.android.horologist.mediasample.domain.PlaylistRepository
import com.google.android.horologist.mediasample.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistLocalDataSource: PlaylistLocalDataSource,
    private val playlistRemoteDataSource: PlaylistRemoteDataSource,
    private val networkChangeListService: NetworkChangeListService,
    private val mediaLocalDataSource: MediaLocalDataSource
) : PlaylistRepository, Syncable {

    override suspend fun get(playlistId: String): Playlist? =
        playlistLocalDataSource.getPopulated(playlistId)?.let(PlaylistMapper::map)

    override fun getAll(): Flow<List<Playlist>> =
        playlistLocalDataSource.getAllPopulated()
            .map { it.map(PlaylistMapper::map) }

    override fun getAllDownloaded(): Flow<List<Playlist>> =
        playlistLocalDataSource.getAllDownloaded()
            .map { it.map(PlaylistMapper::map) }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        val localPlaylists = playlistLocalDataSource.getAllPopulated()
            .first()
            .map(PlaylistMapper::map)

        val remotePlaylists = playlistRemoteDataSource.getPlaylists()
            .map(PlaylistMapper::map)
            .first()

        return synchronizer.changeListSync(
            models = listOf(MEDIA_SYNC_MODEL_NAME, PLAYLIST_SYNC_MODEL_NAME),
            changeListFetcher = { model, _ ->
                if (model == PLAYLIST_SYNC_MODEL_NAME) {
                    networkChangeListService.getForPlaylist(
                        localPlaylists = localPlaylists,
                        remotePlaylists = remotePlaylists
                    )
                } else {
                    networkChangeListService.getForMedia(
                        localPlaylists = localPlaylists,
                        remotePlaylists = remotePlaylists
                    )
                }
            },
            modelDeleter = { model, ids ->
                if (model == PLAYLIST_SYNC_MODEL_NAME) {
                    playlistLocalDataSource.delete(ids)
                } else {
                    mediaLocalDataSource.delete(ids)
                }
            },
            modelUpdater = { model, ids ->
                if (model == PLAYLIST_SYNC_MODEL_NAME) {
                    playlistLocalDataSource.upsert(remotePlaylists.filter { ids.contains(it.id) })
                } else {
                    mediaLocalDataSource.upsert(
                        remotePlaylists
                            .flatMap(Playlist::mediaList)
                            .filter { ids.contains(it.id) }
                            .distinct()
                    )
                }
            }
        )
    }

    companion object {
        private const val PLAYLIST_SYNC_MODEL_NAME = "Playlist"
        private const val MEDIA_SYNC_MODEL_NAME = "Media"
    }
}
