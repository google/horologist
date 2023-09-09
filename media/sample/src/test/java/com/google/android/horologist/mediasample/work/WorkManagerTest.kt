/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.mediasample.work

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result.success
import androidx.work.WorkManager
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import com.google.android.horologist.media.sync.workers.SyncWorker
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@HiltAndroidTest
class WorkManagerTest : BaseAppTest() {
    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        WorkManagerTestInitHelper.initializeTestWorkManager(context)
    }

    @Test
    fun refreshJob() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val workManager = WorkManager.getInstance(context)

        val workRequest = SyncWorker.startUpSyncWork()

        val worker: CoroutineWorker = TestListenableWorkerBuilder.from(context, workRequest)
            .setWorkerFactory(workManager.configuration.workerFactory)
            .build() as CoroutineWorker

        val result = runBlocking {
            worker.doWork()
        }

        assertEquals(success(), result)
    }
}
