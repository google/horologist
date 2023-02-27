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

package com.google.android.horologist.media.data.datasource

import androidx.room.RoomDatabase
import androidx.room.withTransaction
import com.google.android.horologist.media.data.ExperimentalHorologistMediaDataApi
import com.google.android.horologist.media.data.database.dao.PlaylistDao
import com.google.android.horologist.media.data.database.dao.PlaylistMediaDao
import com.google.android.horologist.media.data.database.mapper.MediaEntityMapper
import com.google.android.horologist.media.data.database.mapper.PlaylistEntityMapper
import com.google.android.horologist.media.data.database.mapper.PlaylistMediaEntityMapper
import com.google.android.horologist.media.data.database.model.PopulatedPlaylist
import com.google.android.horologist.media.model.Playlist
import kotlinx.coroutines.flow.Flow

/**
 * Local data source of [Playlist].
 */
@ExperimentalHorologistMediaDataApi
public class PlaylistLocalDataSource(
    private val roomDatabase: RoomDatabase,
    private val playlistDao: PlaylistDao,
    private val playlistMediaDao: PlaylistMediaDao
) {

    public suspend fun upsert(playlists: List<Playlist>) {
        for (playlist in playlists) {
            playlistDao.upsert(
                PlaylistEntityMapper.map(playlist),
                playlist.mediaList.map(MediaEntityMapper::map),
                playlist.mediaList.map { PlaylistMediaEntityMapper.map(playlist, it) }
            )
        }
    }

    public suspend fun getPopulated(playlistId: String): PopulatedPlaylist? =
        playlistDao.getPopulated(playlistId)

    public fun getPopulatedStream(playlistId: String): Flow<PopulatedPlaylist?> =
        playlistDao.getPopulatedStream(playlistId)

    public fun getAllPopulated(): Flow<List<PopulatedPlaylist>> =
        playlistDao.getAllPopulated()

    public fun getAllDownloaded(): Flow<List<PopulatedPlaylist>> =
        playlistDao.getAllDownloaded()

    public suspend fun delete(playlistIds: List<String>) {
        roomDatabase.withTransaction {
            playlistMediaDao.deleteByPlaylistId(playlistIds)
            playlistDao.delete(playlistIds)
        }
    }
}
