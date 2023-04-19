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

import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.model.Media
import com.google.android.horologist.media.model.Playlist
import kotlinx.coroutines.flow.Flow

/**
 * A repository of [Playlist].
 */
@ExperimentalHorologistApi
public interface PlaylistRepository {

    /**
     * Returns the [playlist][Playlist] with the supplied [id][playlistId], if there is any.
     */
    public suspend fun get(playlistId: String): Playlist?

    /**
     * Returns all [playlists][Playlist] available.
     */
    public fun getAll(): Flow<List<Playlist>>

    /**
     * Returns only [playlists][Playlist] that contain at least one [media][Media] with download in
     * progress or completed.
     */
    public fun getAllDownloaded(): Flow<List<Playlist>>
}
