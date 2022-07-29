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

import com.google.android.horologist.mediasample.data.datasource.PlaylistLocalDataSource
import com.google.android.horologist.mediasample.data.datasource.PlaylistRemoteDataSource
import com.google.android.horologist.mediasample.data.mapper.PlaylistMapper
import com.google.android.horologist.mediasample.domain.PlaylistRepository
import com.google.android.horologist.mediasample.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class PlaylistRepositoryImpl(
    private val playlistLocalDataSource: PlaylistLocalDataSource,
    private val playlistRemoteDataSource: PlaylistRemoteDataSource,
) : PlaylistRepository {

    override fun getAll(): Flow<List<Playlist>> = flow {
        emitAll(
            if (playlistLocalDataSource.isEmpty()) {
                playlistRemoteDataSource.getPlaylists()
                    .map(PlaylistMapper::map)
                    .onEach(playlistLocalDataSource::insert)
            } else {
                playlistLocalDataSource.getAllPopulated()
                    .map { list ->
                        buildList { list.forEach { add(PlaylistMapper.map(it)) } }
                    }
            }
        )
    }

    override fun getAllDownloaded(): Flow<List<Playlist>> =
        playlistLocalDataSource.getAllDownloaded()
            .map { list ->
                buildList { list.forEach { add(PlaylistMapper.map(it)) } }
            }
}
