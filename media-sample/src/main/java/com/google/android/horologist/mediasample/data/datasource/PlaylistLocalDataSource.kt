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

package com.google.android.horologist.mediasample.data.datasource

import com.google.android.horologist.mediasample.data.database.dao.PlaylistDao
import com.google.android.horologist.mediasample.data.database.mapper.MediaEntityMapper
import com.google.android.horologist.mediasample.data.database.mapper.PlaylistEntityMapper
import com.google.android.horologist.mediasample.data.database.mapper.PlaylistMediaEntityMapper
import com.google.android.horologist.mediasample.data.database.model.PopulatedPlaylist
import com.google.android.horologist.mediasample.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistLocalDataSource(
    private val playlistDao: PlaylistDao,
) {
    suspend fun isEmpty(): Boolean = playlistDao.getCount() == 0

    suspend fun insert(playlists: List<Playlist>) {
        playlists.forEach { playlist ->
            playlistDao.insert(
                PlaylistEntityMapper.map(playlist),
                playlist.mediaList.map(MediaEntityMapper::map),
                playlist.mediaList.map { PlaylistMediaEntityMapper.map(playlist, it) }
            )
        }
    }

    fun getPopulated(playlistId: String): Flow<PopulatedPlaylist> =
        playlistDao.getPopulated(playlistId)

    fun getAllPopulated(): Flow<List<PopulatedPlaylist>> = playlistDao.getAllPopulated()

    fun getAllDownloaded(): Flow<List<PopulatedPlaylist>> =
        playlistDao.getAllDownloaded()
}
