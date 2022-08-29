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

package com.google.android.horologist.media.repository

import com.google.android.horologist.media.ExperimentalHorologistMediaApi
import com.google.android.horologist.media.model.Playlist
import com.google.android.horologist.media.model.PlaylistDownload
import kotlinx.coroutines.flow.Flow

/**
 * A repository of [PlaylistDownload].
 */
@ExperimentalHorologistMediaApi
public interface PlaylistDownloadRepository {

    /**
     * Returns a [PlaylistDownload] with the supplied [id][playlistId], if there is any.
     */
    public fun get(playlistId: String): Flow<PlaylistDownload?>

    /**
     * Request the download of the supplied [playlist][Playlist].
     */
    public fun download(playlist: Playlist)

    /**
     * Request the download removal of the supplied [playlist][Playlist].
     */
    public fun remove(playlist: Playlist)
}
