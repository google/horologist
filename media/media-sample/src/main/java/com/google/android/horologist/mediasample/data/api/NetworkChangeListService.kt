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

package com.google.android.horologist.mediasample.data.api

import com.google.android.horologist.media.model.Playlist
import com.google.android.horologist.media.sync.api.NetworkChangeList
import com.google.android.horologist.mediasample.di.SyncModule

/**
 * Retrieves a [NetworkChangeList] based on the difference of the data stored locally and in remote.
 *
 * This code is to supplement the lack of an endpoint in the API used by this sample app to return
 * the change list.
 *
 * It does NOT check the contents of the entities in order to avoid excessive updates on the
 * database.
 */
class NetworkChangeListService {

    fun getForPlaylist(
        localPlaylists: List<Playlist>,
        remotePlaylists: List<Playlist>
    ): List<NetworkChangeList> = buildList {
        val remotePlaylistsSet = remotePlaylists.toSet()

        (localPlaylists subtract remotePlaylistsSet).forEach { playlist ->
            add(
                NetworkChangeList(
                    id = playlist.id,
                    changeListVersion = getChangeListVersion(),
                    isDelete = true
                )
            )
        }

        for (playlist in remotePlaylistsSet) {
            add(
                NetworkChangeList(
                    id = playlist.id,
                    changeListVersion = getChangeListVersion(),
                    isDelete = false
                )
            )
        }
    }

    fun getForMedia(
        localPlaylists: List<Playlist>,
        remotePlaylists: List<Playlist>
    ): List<NetworkChangeList> = buildList {
        val localMediaList = localPlaylists.flatMap(Playlist::mediaList)
        val remoteMediaSet = remotePlaylists.flatMap(Playlist::mediaList).toSet()

        (localMediaList subtract remoteMediaSet).forEach { playlist ->
            add(
                NetworkChangeList(
                    id = playlist.id,
                    changeListVersion = getChangeListVersion(),
                    isDelete = true
                )
            )
        }

        for (playlist in remoteMediaSet) {
            add(
                NetworkChangeList(
                    id = playlist.id,
                    changeListVersion = getChangeListVersion(),
                    isDelete = false
                )
            )
        }
    }

    private fun getChangeListVersion() = SyncModule.CHANGE_LIST_VERSION + 1
}
