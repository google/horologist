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

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.tracing.traceAsync
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.google.android.horologist.media.sync.api.ChangeListVersionRepository
import com.google.android.horologist.media.sync.api.CoroutineDispatcherProvider
import com.google.android.horologist.media.sync.api.NotificationConfigurationProvider
import com.google.android.horologist.media.sync.api.Syncable
import com.google.android.horologist.media.sync.api.Synchronizer
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

/**
 * Syncs the data layer by delegating to the appropriate repository instances with
 * sync functionality.
 */
@HiltWorker
public class SyncWorker
    @AssistedInject
    constructor(
        @Assisted private val appContext: Context,
        @Assisted workerParams: WorkerParameters,
        private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
        private val notificationConfigurationProvider: NotificationConfigurationProvider,
        private val changeListVersionRepository: ChangeListVersionRepository,
        private val syncables: Array<Syncable>,
    ) : CoroutineWorker(appContext, workerParams), Synchronizer {

        override suspend fun getForegroundInfo(): ForegroundInfo =
            appContext.syncForegroundInfo(
                notificationTitle = notificationConfigurationProvider.getNotificationTitle(),
                notificationIcon = notificationConfigurationProvider.getNotificationIcon(),
                channelName = notificationConfigurationProvider.getChannelName(),
                channelDescription = notificationConfigurationProvider.getChannelDescription(),
            )

        override suspend fun doWork(): Result = withContext(coroutineDispatcherProvider.getIODispatcher()) {
            traceAsync("SyncWorker", 0) {
                // First sync the repositories in parallel
                val deferredSyncCalls = Array(syncables.size) { index ->
                    async { syncables[index].sync() }
                }

                val syncedSuccessfully = awaitAll(*deferredSyncCalls).all { it }

                if (syncedSuccessfully) {
                    Result.success()
                } else {
                    Result.retry()
                }
            }
        }

        override suspend fun getChangeListVersions(model: String): Int =
            changeListVersionRepository.getChangeListVersion(model)

        override suspend fun updateChangeListVersions(model: String, version: Int): Unit =
            changeListVersionRepository.updateChangeListVersion(
                model = model,
                newVersion = version,
            )

        public companion object {
            /**
             * Expedited one time work to sync data on app startup
             */
            public fun startUpSyncWork(): OneTimeWorkRequest =
                OneTimeWorkRequestBuilder<DelegatingWorker>()
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .setConstraints(SyncConstraints)
                    .setInputData(SyncWorker::class.delegatedData())
                    .build()
        }
    }
