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
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.offline.DownloadIndex
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import androidx.media3.exoplayer.workmanager.WorkManagerScheduler
import androidx.room.Room
import com.google.android.horologist.media3.logging.ErrorReporter
import com.google.android.horologist.media3.logging.TransferListener
import com.google.android.horologist.mediasample.data.database.DownloadDatabase
import com.google.android.horologist.mediasample.data.database.dao.PlaylistDownloadDao
import com.google.android.horologist.mediasample.data.datasource.Media3DownloadDataSource
import com.google.android.horologist.mediasample.data.datasource.PlaylistDownloadLocalDataSource
import com.google.android.horologist.mediasample.data.service.download.DownloadManagerListener
import com.google.android.horologist.mediasample.data.service.download.MediaDownloadService
import com.google.android.horologist.mediasample.di.annotation.DownloadFeature
import com.google.android.horologist.networks.data.RequestType
import com.google.android.horologist.networks.okhttp.NetworkAwareCallFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.Call
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DownloadModule {

    private const val DOWNLOAD_WORK_MANAGER_SCHEDULER_WORK_NAME = "mediasample_download"
    private const val DOWNLOAD_DATABASE_NAME = "download-database"

    @DownloadFeature
    @Singleton
    @Provides
    fun downloadDataSourceFactory(
        okHttp: Call.Factory,
        @DownloadFeature transferListener: TransferListener,
    ): DataSource.Factory = OkHttpDataSource.Factory(
        NetworkAwareCallFactory(
            delegate = okHttp,
            defaultRequestType = RequestType.MediaRequest(RequestType.MediaRequest.MediaRequestType.Download)
        )
    ).setTransferListener(transferListener)

    @DownloadFeature
    @Provides
    fun transferListener(errorReporter: ErrorReporter): TransferListener =
        TransferListener(errorReporter)

    @DownloadFeature
    @Singleton
    @Provides
    fun threadPool(): ExecutorService = Executors.newCachedThreadPool()

    @Singleton
    @Provides
    fun downloadNotificationHelper(
        @ApplicationContext applicationContext: Context,
    ): DownloadNotificationHelper =
        DownloadNotificationHelper(
            applicationContext,
            MediaDownloadService.MEDIA_DOWNLOAD_CHANNEL_ID
        )

    @DownloadFeature
    @Singleton
    @Provides
    fun databaseProvider(
        @ApplicationContext application: Context,
    ): DatabaseProvider = StandaloneDatabaseProvider(application)

    @Singleton
    @Provides
    fun downloadManager(
        @ApplicationContext applicationContext: Context,
        @DownloadFeature databaseProvider: DatabaseProvider,
        downloadCache: Cache,
        @DownloadFeature dataSourceFactory: DataSource.Factory,
        @DownloadFeature threadPool: ExecutorService,
        downloadManagerListener: DownloadManagerListener,
    ) = DownloadManager(
        applicationContext,
        databaseProvider,
        downloadCache,
        dataSourceFactory,
        threadPool
    ).also {
        it.addListener(downloadManagerListener)
    }

    @Provides
    fun downloadIndex(downloadManager: DownloadManager) = downloadManager.downloadIndex

    @Singleton
    @Provides
    fun workManagerScheduler(
        @ApplicationContext applicationContext: Context,
    ) = WorkManagerScheduler(applicationContext, DOWNLOAD_WORK_MANAGER_SCHEDULER_WORK_NAME)

    @Singleton
    @Provides
    fun downloadDataSource(
        @ApplicationContext applicationContext: Context,
        downloadIndex: DownloadIndex
    ) = Media3DownloadDataSource(
        applicationContext,
        MediaDownloadService::class.java,
        downloadIndex
    )

    @Provides
    @Singleton
    fun downloadDatabase(
        @ApplicationContext context: Context,
    ): DownloadDatabase {
        return Room.databaseBuilder(
            context,
            DownloadDatabase::class.java,
            DOWNLOAD_DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun playlistDownloadLocalDataSource(
        playlistDownloadDao: PlaylistDownloadDao,
    ): PlaylistDownloadLocalDataSource = PlaylistDownloadLocalDataSource(playlistDownloadDao)

    @Provides
    @Singleton
    fun playlistDownloadDao(
        database: DownloadDatabase,
    ): PlaylistDownloadDao = database.playlistDownloadDao()

    @DownloadFeature
    @Provides
    @Singleton
    fun coroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Provides
    @Singleton
    fun downloadManagerListener(
        @DownloadFeature coroutineScope: CoroutineScope,
        playlistDownloadDao: PlaylistDownloadDao,
    ): DownloadManagerListener = DownloadManagerListener(coroutineScope, playlistDownloadDao)
}
