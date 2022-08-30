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

package com.google.android.horologist.media.data.repository

import com.google.android.horologist.media.data.ExperimentalHorologistMediaDataApi
import com.google.android.horologist.media.data.datasource.PlaylistLocalDataSource
import com.google.android.horologist.media.data.mapper.PlaylistMapper
import com.google.android.horologist.media.model.Playlist
import com.google.android.horologist.media.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@ExperimentalHorologistMediaDataApi
public class PlaylistRepositoryImpl(
    private val playlistLocalDataSource: PlaylistLocalDataSource,
    private val playlistMapper: PlaylistMapper
) : PlaylistRepository {

    override suspend fun get(playlistId: String): Playlist? =
        playlistLocalDataSource.getPopulated(playlistId)?.let(playlistMapper::map)

    override fun getAll(): Flow<List<Playlist>> =
        playlistLocalDataSource.getAllPopulated()
            .map { it.map(playlistMapper::map) }

    override fun getAllDownloaded(): Flow<List<Playlist>> =
        playlistLocalDataSource.getAllDownloaded()
            .map { it.map(playlistMapper::map) }
}
