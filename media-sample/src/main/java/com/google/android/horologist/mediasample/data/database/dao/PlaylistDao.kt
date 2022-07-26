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

package com.google.android.horologist.mediasample.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.google.android.horologist.mediasample.data.database.model.MediaEntity
import com.google.android.horologist.mediasample.data.database.model.PlaylistEntity
import com.google.android.horologist.mediasample.data.database.model.PlaylistMediaEntity
import com.google.android.horologist.mediasample.data.database.model.PopulatedPlaylist
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Query(
        value = """
        SELECT COUNT(*) FROM playlistentity
    """
    )
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(
        playlistEntity: PlaylistEntity,
        mediaEntityList: List<MediaEntity>,
        playlistMediaEntityList: List<PlaylistMediaEntity>
    )

    @Transaction
    @Query(
        value = """
        SELECT * FROM playlistentity
        WHERE playlistId = :playlistId
    """
    )
    fun getPopulated(playlistId: String): Flow<PopulatedPlaylist>

    @Transaction
    @Query(
        value = """
        SELECT * FROM playlistentity
    """
    )
    fun getAllPopulated(): Flow<List<PopulatedPlaylist>>
}
