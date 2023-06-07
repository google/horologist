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

import android.annotation.SuppressLint
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
import com.google.android.horologist.media.data.datasource.MediaDownloadLocalDataSource
import com.google.android.horologist.media.data.service.download.DownloadManagerListener
import com.google.android.horologist.media.data.service.download.DownloadProgressMonitor
import com.google.android.horologist.media3.logging.ErrorReporter
import com.google.android.horologist.media3.logging.TransferListener
import com.google.android.horologist.media3.service.NetworkAwareDownloadListener
import com.google.android.horologist.mediasample.data.service.download.MediaDownloadServiceImpl
import com.google.android.horologist.mediasample.di.annotation.Dispatcher
import com.google.android.horologist.mediasample.di.annotation.DownloadFeature
import com.google.android.horologist.mediasample.di.annotation.UampDispatchers.IO
import com.google.android.horologist.mediasample.ui.AppConfig
import com.google.android.horologist.networks.data.RequestType.MediaRequest.Companion.DownloadRequest
import com.google.android.horologist.networks.highbandwidth.HighBandwidthNetworkMediator
import com.google.android.horologist.networks.okhttp.NetworkAwareCallFactory
import com.google.android.horologist.networks.rules.NetworkingRulesEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import okhttp3.CacheControl
import okhttp3.Call
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Provider
import javax.inject.Singleton

@SuppressLint("UnsafeOptInUsageError")
@Module
@InstallIn(SingletonComponent::class)
object DownloadModule {

    private const val DOWNLOAD_WORK_MANAGER_SCHEDULER_WORK_NAME = "mediasample_download"

    @DownloadFeature
    @Singleton
    @Provides
    fun downloadDataSourceFactory(
        callFactory: Call.Factory,
        @DownloadFeature transferListener: TransferListener
    ): DataSource.Factory = OkHttpDataSource.Factory(
        NetworkAwareCallFactory(
            delegate = callFactory,
            defaultRequestType = DownloadRequest
        )
    )
        .setCacheControl(CacheControl.Builder().noCache().noStore().build())
        .setTransferListener(transferListener)

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
        @ApplicationContext applicationContext: Context
    ): DownloadNotificationHelper =
        DownloadNotificationHelper(
            applicationContext,
            MediaDownloadServiceImpl.MEDIA_DOWNLOAD_CHANNEL_ID
        )

    @DownloadFeature
    @Singleton
    @Provides
    fun databaseProvider(
        @ApplicationContext application: Context
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
        appConfig: AppConfig,
        networkAwareListener: Provider<NetworkAwareDownloadListener>
    ) = DownloadManager(
        applicationContext,
        databaseProvider,
        downloadCache,
        dataSourceFactory,
        threadPool
    ).also {
        it.addListener(downloadManagerListener)
        if (appConfig.strictNetworking != null) {
            it.addListener(networkAwareListener.get())
        }
    }

    @Provides
    fun downloadIndex(downloadManager: DownloadManager): DownloadIndex = downloadManager.downloadIndex

    @Singleton
    @Provides
    fun workManagerScheduler(
        @ApplicationContext applicationContext: Context
    ) = WorkManagerScheduler(applicationContext, DOWNLOAD_WORK_MANAGER_SCHEDULER_WORK_NAME)

    @DownloadFeature
    @Provides
    @Singleton
    fun coroutineScope(
        @Dispatcher(IO) ioDispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(SupervisorJob() + ioDispatcher)

    @Provides
    @Singleton
    fun downloadManagerListener(
        @DownloadFeature coroutineScope: CoroutineScope,
        mediaDownloadLocalDataSource: MediaDownloadLocalDataSource,
        downloadProgressMonitor: DownloadProgressMonitor
    ): DownloadManagerListener = DownloadManagerListener(
        coroutineScope = coroutineScope,
        mediaDownloadLocalDataSource = mediaDownloadLocalDataSource,
        downloadProgressMonitor = downloadProgressMonitor
    )

    @Provides
    @Singleton
    fun downloadProgressMonitor(
        @DownloadFeature coroutineScope: CoroutineScope,
        mediaDownloadLocalDataSource: MediaDownloadLocalDataSource
    ): DownloadProgressMonitor = DownloadProgressMonitor(
        coroutineScope = coroutineScope,
        mediaDownloadLocalDataSource = mediaDownloadLocalDataSource
    )

    @Provides
    @Singleton
    fun networkAwareListener(
        errorReporter: ErrorReporter,
        highBandwithRequester: HighBandwidthNetworkMediator,
        networkingRulesEngine: NetworkingRulesEngine
    ): NetworkAwareDownloadListener = NetworkAwareDownloadListener(
        errorReporter,
        highBandwithRequester,
        networkingRulesEngine
    )
}
