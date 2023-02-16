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

import androidx.core.net.toUri
import com.google.android.horologist.media.data.ExperimentalHorologistMediaDataApi
import com.google.android.horologist.media.data.datasource.Media3DownloadDataSource
import com.google.android.horologist.media.data.datasource.MediaDownloadLocalDataSource
import com.google.android.horologist.media.data.datasource.PlaylistLocalDataSource
import com.google.android.horologist.media.data.mapper.PlaylistDownloadMapper
import com.google.android.horologist.media.model.Playlist
import com.google.android.horologist.media.model.PlaylistDownload
import com.google.android.horologist.media.repository.PlaylistDownloadRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@ExperimentalHorologistMediaDataApi
public class PlaylistDownloadRepositoryImpl(
    private val coroutineScope: CoroutineScope,
    private val playlistLocalDataSource: PlaylistLocalDataSource,
    private val mediaDownloadLocalDataSource: MediaDownloadLocalDataSource,
    private val media3DownloadDataSource: Media3DownloadDataSource,
    private val playlistDownloadMapper: PlaylistDownloadMapper
) : PlaylistDownloadRepository {

    @OptIn(FlowPreview::class)
    override fun get(playlistId: String): Flow<PlaylistDownload?> =
        playlistLocalDataSource.getPopulatedStream(playlistId).flatMapMerge { populatedPlaylist ->
            if (populatedPlaylist != null) {
                combine(
                    flowOf(populatedPlaylist),
                    mediaDownloadLocalDataSource.get(
                        populatedPlaylist.mediaList
                            .map { it.mediaId }.toList()
                    )
                ) { _, mediaDownloadList ->
                    playlistDownloadMapper.map(populatedPlaylist, mediaDownloadList)
                }
            } else {
                flowOf(null)
            }
        }

    override fun download(playlist: Playlist) {
        coroutineScope.launch {
            for (media in playlist.mediaList) {
                mediaDownloadLocalDataSource.add(mediaId = media.id)

                media3DownloadDataSource.download(media.id, media.uri.toUri())
            }
        }
    }

    override fun remove(playlist: Playlist) {
        for (media in playlist.mediaList) {
            media3DownloadDataSource.removeDownload(media.id)
        }
    }
}
