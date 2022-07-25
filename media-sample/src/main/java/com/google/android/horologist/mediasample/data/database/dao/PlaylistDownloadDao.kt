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
import com.google.android.horologist.mediasample.data.database.model.PlaylistDownloadEntity
import com.google.android.horologist.mediasample.data.database.model.PlaylistDownloadState
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDownloadDao {

    @Query(
        value = """
        SELECT * FROM playlist_download
        WHERE playlistId = :playlistId
    """
    )
    fun getStream(playlistId: String): Flow<List<PlaylistDownloadEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(playlistDownloadEntities: PlaylistDownloadEntity): Long

    @Query(
        value = """
            DELETE FROM playlist_download
            WHERE mediaItemId = :mediaItemId
        """
    )
    suspend fun deleteByMediaItemId(mediaItemId: String)

    @Query(
        value = """
        UPDATE playlist_download
        SET status = :status
        WHERE mediaItemId = :mediaItemId
    """
    )
    suspend fun updateStatusByMediaItemId(mediaItemId: String, status: PlaylistDownloadState)
}
