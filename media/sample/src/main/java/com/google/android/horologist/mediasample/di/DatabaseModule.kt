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

package com.google.android.horologist.mediasample.di

import android.content.Context
import androidx.room.Room
import com.google.android.horologist.media.data.database.MediaDatabase
import com.google.android.horologist.media.data.database.dao.MediaDao
import com.google.android.horologist.media.data.database.dao.MediaDownloadDao
import com.google.android.horologist.media.data.database.dao.PlaylistDao
import com.google.android.horologist.media.data.database.dao.PlaylistMediaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val MEDIA_DATABASE_NAME = "media-database"

    @Provides
    @Singleton
    fun mediaDatabase(
        @ApplicationContext context: Context,
    ): MediaDatabase {
        return Room.databaseBuilder(
            context,
            MediaDatabase::class.java,
            MEDIA_DATABASE_NAME,
        )
            // Until stable, don't require incrementing MediaDatabase version.
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun mediaDownloadDao(
        database: MediaDatabase,
    ): MediaDownloadDao = database.mediaDownloadDao()

    @Provides
    @Singleton
    fun playlistDao(
        database: MediaDatabase,
    ): PlaylistDao = database.playlistDao()

    @Provides
    @Singleton
    fun playlistMediaDao(
        database: MediaDatabase,
    ): PlaylistMediaDao = database.playlistMediaDao()

    @Provides
    @Singleton
    fun mediaDao(
        database: MediaDatabase,
    ): MediaDao = database.mediaDao()
}
