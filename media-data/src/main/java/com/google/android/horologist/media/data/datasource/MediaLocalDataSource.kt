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

package com.google.android.horologist.media.data.datasource

import androidx.room.RoomDatabase
import androidx.room.withTransaction
import com.google.android.horologist.media.data.ExperimentalHorologistMediaDataApi
import com.google.android.horologist.media.data.database.dao.MediaDao
import com.google.android.horologist.media.data.database.dao.MediaDownloadDao
import com.google.android.horologist.media.data.database.dao.PlaylistMediaDao
import com.google.android.horologist.media.data.database.mapper.MediaEntityMapper
import com.google.android.horologist.media.model.Media

/**
 * Local data source of [Media].
 */
@ExperimentalHorologistMediaDataApi
public class MediaLocalDataSource(
    private val roomDatabase: RoomDatabase,
    private val mediaDao: MediaDao,
    private val playlistMediaDao: PlaylistMediaDao,
    private val mediaDownloadDao: MediaDownloadDao
) {

    public suspend fun upsert(mediaList: List<Media>): Unit =
        mediaDao.upsert(mediaList.map(MediaEntityMapper::map))

    public suspend fun delete(mediaIds: List<String>) {
        roomDatabase.withTransaction {
            mediaDownloadDao.delete(mediaIds)
            playlistMediaDao.deleteByMediaId(mediaIds)
            mediaDao.delete(mediaIds)
        }
    }
}
