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
import androidx.datastore.preferences.core.Preferences
import androidx.media3.exoplayer.offline.DownloadIndex
import com.google.android.horologist.mediasample.data.api.UampService
import com.google.android.horologist.mediasample.data.database.dao.MediaDownloadDao
import com.google.android.horologist.mediasample.data.database.dao.PlaylistDao
import com.google.android.horologist.mediasample.data.datasource.Media3DownloadDataSource
import com.google.android.horologist.mediasample.data.datasource.MediaDownloadLocalDataSource
import com.google.android.horologist.mediasample.data.datasource.PlaylistLocalDataSource
import com.google.android.horologist.mediasample.data.datasource.PlaylistRemoteDataSource
import com.google.android.horologist.mediasample.data.repository.PlaylistDownloadRepositoryImpl
import com.google.android.horologist.mediasample.data.repository.PlaylistRepositoryImpl
import com.google.android.horologist.mediasample.data.service.download.MediaDownloadService
import com.google.android.horologist.mediasample.domain.PlaylistDownloadRepository
import com.google.android.horologist.mediasample.domain.PlaylistRepository
import com.google.android.horologist.mediasample.domain.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
        media3DownloadDataSource: Media3DownloadDataSource
    ): PlaylistDownloadRepository =
        PlaylistDownloadRepositoryImpl(
            coroutineScope,
            playlistLocalDataSource,
            mediaDownloadLocalDataSource,
            media3DownloadDataSource
        )

    @Singleton
    @Provides
    fun playlistRepository(
        playlistDownloadLocalDataSource: PlaylistLocalDataSource,
        playlistRemoteDataSource: PlaylistRemoteDataSource,
    ): PlaylistRepository =
        PlaylistRepositoryImpl(playlistDownloadLocalDataSource, playlistRemoteDataSource)

    @Singleton
    @Provides
    fun settingsRepository(
        prefsDataStore: DataStore<Preferences>
    ) =
        SettingsRepository(prefsDataStore)

    @Singleton
    @Provides
    fun media3DownloadDataSource(
        @ApplicationContext applicationContext: Context,
        downloadIndex: DownloadIndex
    ) = Media3DownloadDataSource(
        applicationContext,
        MediaDownloadService::class.java,
        downloadIndex
    )

    @Provides
    @Singleton
    fun mediaDownloadLocalDataSource(
        mediaDownloadDao: MediaDownloadDao,
    ): MediaDownloadLocalDataSource = MediaDownloadLocalDataSource(mediaDownloadDao)

    @Singleton
    @Provides
    fun playlistLocalDataSource(playlistDao: PlaylistDao): PlaylistLocalDataSource =
        PlaylistLocalDataSource(playlistDao)

    @Singleton
    @Provides
    fun playlistRemoteDataSource(
        uampService: UampService,
    ): PlaylistRemoteDataSource =
        PlaylistRemoteDataSource(
            ioDispatcher = Dispatchers.IO,
            uampService = uampService,
        )
}
