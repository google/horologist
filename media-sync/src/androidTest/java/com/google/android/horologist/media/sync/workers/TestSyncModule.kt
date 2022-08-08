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

package com.google.android.horologist.media.sync.workers

import com.google.android.horologist.media.sync.api.ChangeListVersionRepository
import com.google.android.horologist.media.sync.api.CoroutineDispatcherProvider
import com.google.android.horologist.media.sync.api.NotificationConfigurationProvider
import com.google.android.horologist.media.sync.api.Syncable
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestSyncModule {

    @Singleton
    @Provides
    fun coroutineDispatcherProvider(): CoroutineDispatcherProvider =
        object : CoroutineDispatcherProvider {
            override fun getIODispatcher(): CoroutineDispatcher = Dispatchers.IO
        }

    @Singleton
    @Provides
    fun notificationConfigurationProvider(): NotificationConfigurationProvider =
        object : NotificationConfigurationProvider {
            override fun getNotificationTitle(): String = "title"

            override fun getNotificationIcon(): Int = android.R.drawable.ic_notification_clear_all

            override fun getChannelName(): String = "channel name"

            override fun getChannelDescription(): String = "channel description"
        }

    @Singleton
    @Provides
    fun changeListVersionRepository(): ChangeListVersionRepository =
        object : ChangeListVersionRepository {
            override suspend fun getChangeListVersion(model: String): Int = 1

            override suspend fun updateChangeListVersion(model: String, newVersion: Int) = Unit
        }

    @Singleton
    @Provides
    fun syncables(): Array<Syncable> = emptyArray()
}
