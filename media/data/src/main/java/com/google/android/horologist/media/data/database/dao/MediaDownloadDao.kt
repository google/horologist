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
import androidx.room.Update
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.data.database.model.MediaDownloadEntity
import com.google.android.horologist.media.data.database.model.MediaDownloadEntityStatus
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [MediaDownloadEntity].
 */
@ExperimentalHorologistApi
@Dao
public interface MediaDownloadDao {

    @Query(
        value = """
        SELECT * FROM MediaDownloadEntity
        WHERE mediaId in (:mediaIds)
    """,
    )
    public fun getList(mediaIds: List<String>): Flow<List<MediaDownloadEntity>>

    @Query(
        value = """
        SELECT * FROM MediaDownloadEntity
        WHERE status = :status
    """,
    )
    public suspend fun getAllByStatus(
        status: MediaDownloadEntityStatus,
    ): List<MediaDownloadEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public suspend fun insert(mediaDownloadEntity: MediaDownloadEntity): Long

    @Query(
        """
        UPDATE MediaDownloadEntity
        SET status = :status
        WHERE mediaId = :mediaId
    """,
    )
    public suspend fun updateStatus(mediaId: String, status: MediaDownloadEntityStatus)

    @Query(
        """
        UPDATE MediaDownloadEntity
        SET progress = :progress,
        size = :size
        WHERE mediaId = :mediaId
    """,
    )
    public suspend fun updateProgress(mediaId: String, progress: Float, size: Long)

    @Update(entity = MediaDownloadEntity::class)
    public suspend fun updateStatusAndProgress(statusAndProgress: StatusAndProgress)

    @Query(
        """
        DELETE FROM MediaDownloadEntity
        WHERE mediaId = :mediaId
    """,
    )
    public suspend fun delete(mediaId: String)

    @Query(
        """
        DELETE FROM MediaDownloadEntity
        WHERE mediaId in (:mediaIds)
    """,
    )
    public suspend fun delete(mediaIds: List<String>)

    public data class StatusAndProgress(
        val mediaId: String,
        val status: MediaDownloadEntityStatus,
        val progress: Float,
    )

    public companion object {
        public const val DOWNLOAD_PROGRESS_START: Float = 0f
        public const val DOWNLOAD_PROGRESS_END: Float = 100f
        public const val SIZE_UNKNOWN: Long = -1L
    }
}
