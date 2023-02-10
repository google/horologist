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
import androidx.datastore.core.DataStore
import com.google.android.horologist.media.data.database.MediaDatabase
import com.google.android.horologist.media.data.database.dao.MediaDao
import com.google.android.horologist.media.data.database.dao.MediaDownloadDao
import com.google.android.horologist.media.data.database.dao.PlaylistDao
import com.google.android.horologist.media.data.database.dao.PlaylistMediaDao
import com.google.android.horologist.media.data.datasource.Media3DownloadDataSource
import com.google.android.horologist.media.data.datasource.MediaDownloadLocalDataSource
import com.google.android.horologist.media.data.datasource.MediaLocalDataSource
import com.google.android.horologist.media.data.datasource.PlaylistLocalDataSource
import com.google.android.horologist.media.data.mapper.MediaExtrasMapper
import com.google.android.horologist.media.data.mapper.MediaExtrasMapperNoopImpl
import com.google.android.horologist.media.data.mapper.MediaMapper
import com.google.android.horologist.media.data.mapper.PlaylistDownloadMapper
import com.google.android.horologist.media.data.mapper.PlaylistMapper
import com.google.android.horologist.media.data.repository.MediaDownloadRepositoryImpl
import com.google.android.horologist.media.data.repository.PlaylistDownloadRepositoryImpl
import com.google.android.horologist.media.data.repository.PlaylistRepositoryImpl
import com.google.android.horologist.media.repository.MediaDownloadRepository
import com.google.android.horologist.media.repository.PlaylistDownloadRepository
import com.google.android.horologist.media.repository.PlaylistRepository
import com.google.android.horologist.mediasample.data.api.NetworkChangeListService
import com.google.android.horologist.mediasample.data.api.UampService
import com.google.android.horologist.mediasample.data.datasource.PlaylistRemoteDataSource
import com.google.android.horologist.mediasample.data.service.download.MediaDownloadServiceImpl
import com.google.android.horologist.mediasample.di.annotation.Dispatcher
import com.google.android.horologist.mediasample.di.annotation.UampDispatchers.IO
import com.google.android.horologist.mediasample.domain.SettingsRepository
import com.google.android.horologist.mediasample.domain.proto.SettingsProto.Settings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Singleton
    @Provides
    fun playlistDownloadRepository(
        @ForApplicationScope coroutineScope: CoroutineScope,
        playlistLocalDataSource: PlaylistLocalDataSource,
        mediaDownloadLocalDataSource: MediaDownloadLocalDataSource,
        media3DownloadDataSource: Media3DownloadDataSource,
        playlistDownloadMapper: PlaylistDownloadMapper
    ): PlaylistDownloadRepository =
        PlaylistDownloadRepositoryImpl(
            coroutineScope = coroutineScope,
            playlistLocalDataSource = playlistLocalDataSource,
            mediaDownloadLocalDataSource = mediaDownloadLocalDataSource,
            media3DownloadDataSource = media3DownloadDataSource,
            playlistDownloadMapper = playlistDownloadMapper
        )

    @Singleton
    @Provides
    fun mediaDownloadRepository(
        media3DownloadDataSource: Media3DownloadDataSource
    ): MediaDownloadRepository =
        MediaDownloadRepositoryImpl(
            media3DownloadDataSource = media3DownloadDataSource
        )

    @Singleton
    @Provides
    fun playlistRepositoryImpl(
        playlistDownloadLocalDataSource: PlaylistLocalDataSource,
        playlistMapper: PlaylistMapper
    ): PlaylistRepositoryImpl =
        PlaylistRepositoryImpl(
            playlistLocalDataSource = playlistDownloadLocalDataSource,
            playlistMapper = playlistMapper
        )

    @Singleton
    @Provides
    fun playlistRepository(
        playlistRepositoryImpl: PlaylistRepositoryImpl
    ): PlaylistRepository = playlistRepositoryImpl

    @Singleton
    @Provides
    fun settingsRepository(
        prefsDataStore: DataStore<Settings>
    ) =
        SettingsRepository(prefsDataStore)

    @Singleton
    @Provides
    fun media3DownloadDataSource(
        @ApplicationContext applicationContext: Context
    ) = Media3DownloadDataSource(
        applicationContext,
        MediaDownloadServiceImpl::class.java
    )

    @Provides
    @Singleton
    fun mediaDownloadLocalDataSource(
        mediaDownloadDao: MediaDownloadDao
    ): MediaDownloadLocalDataSource = MediaDownloadLocalDataSource(mediaDownloadDao)

    @Singleton
    @Provides
    fun playlistLocalDataSource(
        mediaDatabase: MediaDatabase,
        playlistDao: PlaylistDao,
        playlistMediaDao: PlaylistMediaDao
    ): PlaylistLocalDataSource =
        PlaylistLocalDataSource(
            roomDatabase = mediaDatabase,
            playlistDao = playlistDao,
            playlistMediaDao = playlistMediaDao
        )

    @Singleton
    @Provides
    fun playlistRemoteDataSource(
        uampService: UampService,
        @Dispatcher(IO) ioDispatcher: CoroutineDispatcher
    ): PlaylistRemoteDataSource =
        PlaylistRemoteDataSource(
            ioDispatcher = ioDispatcher,
            uampService = uampService
        )

    @Singleton
    @Provides
    fun mediaLocalDataSource(
        mediaDatabase: MediaDatabase,
        mediaDao: MediaDao,
        playlistMediaDao: PlaylistMediaDao,
        mediaDownloadDao: MediaDownloadDao
    ): MediaLocalDataSource = MediaLocalDataSource(
        roomDatabase = mediaDatabase,
        mediaDao = mediaDao,
        playlistMediaDao = playlistMediaDao,
        mediaDownloadDao = mediaDownloadDao
    )

    @Provides
    fun playlistDownloadMapper(playlistMapper: PlaylistMapper): PlaylistDownloadMapper =
        PlaylistDownloadMapper(playlistMapper)

    @Provides
    fun mediaExtrasMapper(): MediaExtrasMapper = MediaExtrasMapperNoopImpl

    @Provides
    fun mediaMapper(mediaExtrasMapper: MediaExtrasMapper): MediaMapper = MediaMapper(mediaExtrasMapper)

    @Provides
    fun playlistMapper(mediaMapper: MediaMapper): PlaylistMapper = PlaylistMapper(mediaMapper)

    @Singleton
    @Provides
    fun networkChangeListService(): NetworkChangeListService = NetworkChangeListService()

    @Provides
    @Dispatcher(IO)
    fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
