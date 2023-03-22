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

package com.google.android.horologist.media.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.data.database.model.PlaylistMediaEntity

/**
 * DAO for [PlaylistMediaEntity].
 */
@ExperimentalHorologistApi
@Dao
public interface PlaylistMediaDao {

    @Query(
        value = """
        DELETE FROM PlaylistMediaEntity
        WHERE playlistId in (:playlistIds)
    """
    )
    public fun deleteByPlaylistId(playlistIds: List<String>)

    @Query(
        value = """
        DELETE FROM PlaylistMediaEntity
        WHERE mediaId in (:mediaIds)
    """
    )
    public fun deleteByMediaId(mediaIds: List<String>)
}
