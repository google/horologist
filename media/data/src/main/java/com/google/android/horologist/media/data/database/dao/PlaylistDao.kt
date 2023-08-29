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
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.data.database.model.MediaEntity
import com.google.android.horologist.media.data.database.model.PlaylistEntity
import com.google.android.horologist.media.data.database.model.PlaylistMediaEntity
import com.google.android.horologist.media.data.database.model.PopulatedPlaylist
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [PlaylistEntity].
 */
@ExperimentalHorologistApi
@Dao
public interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public suspend fun upsert(
        playlistEntity: PlaylistEntity,
        mediaEntityList: List<MediaEntity>,
        playlistMediaEntityList: List<PlaylistMediaEntity>,
    )

    @Transaction
    @Query(
        value = """
        SELECT * FROM PlaylistEntity
        WHERE playlistId = :playlistId
    """,
    )
    public suspend fun getPopulated(playlistId: String): PopulatedPlaylist?

    @Transaction
    @Query(
        value = """
        SELECT * FROM playlistentity
        WHERE playlistId = :playlistId
    """,
    )
    public fun getPopulatedStream(playlistId: String): Flow<PopulatedPlaylist?>

    @Transaction
    @Query(
        value = """
        SELECT * FROM PlaylistEntity
    """,
    )
    public fun getAllPopulated(): Flow<List<PopulatedPlaylist>>

    @Transaction
    @Query(
        value = """
        SELECT * FROM PlaylistEntity
        WHERE EXISTS (
            SELECT 1 FROM PlaylistMediaEntity
            WHERE PlaylistMediaEntity.playlistId = PlaylistEntity.playlistId
            AND EXISTS (
                SELECT 1 FROM MediaDownloadEntity
                WHERE MediaDownloadEntity.mediaId = PlaylistMediaEntity.mediaId
            )
        )
    """,
    )
    public fun getAllDownloaded(): Flow<List<PopulatedPlaylist>>

    @Query(
        value = """
        DELETE FROM PlaylistEntity
        WHERE playlistId in (:playlistIds)
    """,
    )
    public fun delete(playlistIds: List<String>)
}
