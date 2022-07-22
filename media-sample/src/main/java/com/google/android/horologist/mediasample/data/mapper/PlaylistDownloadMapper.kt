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

package com.google.android.horologist.mediasample.data.mapper

import com.google.android.horologist.mediasample.data.database.model.PlaylistDownloadEntity
import com.google.android.horologist.mediasample.domain.model.Playlist
import com.google.android.horologist.mediasample.domain.model.PlaylistDownload

object PlaylistDownloadMapper {

    fun map(
        playlist: Playlist,
        playlistDownloadEntityList: List<PlaylistDownloadEntity>
    ): PlaylistDownload = PlaylistDownload(
        playlist = playlist,
        buildList {
            playlist.mediaItems.forEach { mediaItem ->
                val status =
                    playlistDownloadEntityList.firstOrNull { it.mediaItemId == mediaItem.id }
                        ?.let { PlaylistDownloadStatusMapper.map(it.status) }
                        ?: PlaylistDownload.Status.Idle

                add(Pair(mediaItem, status))
            }
        }
    )
}
