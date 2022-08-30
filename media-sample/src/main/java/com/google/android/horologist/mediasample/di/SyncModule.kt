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
import com.google.android.horologist.media.data.datasource.MediaLocalDataSource
import com.google.android.horologist.media.data.datasource.PlaylistLocalDataSource
import com.google.android.horologist.media.data.mapper.PlaylistMapper
import com.google.android.horologist.media.sync.api.ChangeListVersionRepository
import com.google.android.horologist.media.sync.api.CoroutineDispatcherProvider
import com.google.android.horologist.media.sync.api.NotificationConfigurationProvider
import com.google.android.horologist.media.sync.api.Syncable
import com.google.android.horologist.mediasample.R
import com.google.android.horologist.mediasample.data.api.NetworkChangeListService
import com.google.android.horologist.mediasample.data.datasource.PlaylistRemoteDataSource
import com.google.android.horologist.mediasample.data.repository.PlaylistRepositorySyncable
import com.google.android.horologist.mediasample.di.annotation.Dispatcher
import com.google.android.horologist.mediasample.di.annotation.UampDispatchers.IO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SyncModule {

    const val CHANGE_LIST_VERSION = 1

    @Singleton
    @Provides
    fun coroutineDispatcherProvider(
        @Dispatcher(IO) ioDispatcher: CoroutineDispatcher
    ): CoroutineDispatcherProvider =
        object : CoroutineDispatcherProvider {
            override fun getIODispatcher(): CoroutineDispatcher = ioDispatcher
        }

    @Singleton
    @Provides
    fun notificationConfigurationProvider(
        @ApplicationContext application: Context
    ): NotificationConfigurationProvider =
        object : NotificationConfigurationProvider {

            override fun getNotificationTitle(): String =
                application.getString(R.string.sync_notification_title)

            override fun getNotificationIcon(): Int = R.drawable.ic_uamp

            override fun getChannelName(): String =
                application.getString(R.string.sync_notification_channel_name)

            override fun getChannelDescription(): String =
                application.getString(R.string.sync_notification_channel_description)
        }

    @Singleton
    @Provides
    fun changeListVersionRepository(): ChangeListVersionRepository =
        object : ChangeListVersionRepository {

            override suspend fun getChangeListVersion(model: String): Int {
                // always return the same value for now, given change list endpoint is not available
                // in the API
                return CHANGE_LIST_VERSION
            }

            override suspend fun updateChangeListVersion(model: String, newVersion: Int) {
                // do nothing for now
            }
        }

    @Singleton
    @Provides
    fun syncables(
        playlistRepositorySyncable: PlaylistRepositorySyncable
    ): Array<Syncable> = arrayOf(
        playlistRepositorySyncable
    )

    @Provides
    fun playlistRepositorySyncable(
        playlistLocalDataSource: PlaylistLocalDataSource,
        playlistRemoteDataSource: PlaylistRemoteDataSource,
        networkChangeListService: NetworkChangeListService,
        mediaLocalDataSource: MediaLocalDataSource,
        playlistMapper: PlaylistMapper
    ): PlaylistRepositorySyncable =
        PlaylistRepositorySyncable(
            playlistLocalDataSource,
            playlistRemoteDataSource,
            networkChangeListService,
            mediaLocalDataSource,
            playlistMapper
        )
}
