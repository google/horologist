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

package com.google.android.horologist.networks.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
public interface NetworkUsageDao {
    @Query("UPDATE DataUsage SET bytesTotal = bytesTotal + :bytes WHERE day = :day")
    public suspend fun updateBytes(day: Int, bytes: Long): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public suspend fun insert(media: DataUsage): Long

    @Query("SELECT * FROM DataUsage WHERE day = :day")
    public fun getRecords(day: Int): Flow<List<DataUsage>>
}
