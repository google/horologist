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

package com.google.android.horologist.sample.di

import android.app.Activity
import com.google.android.horologist.navsample.NavActivity
import com.google.android.horologist.networks.data.DataRequestRepository
import com.google.android.horologist.networks.logging.NetworkStatusLogger
import com.google.android.horologist.networks.status.NetworkRepository
import com.google.android.horologist.sample.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope

/**
 * Simple DI implementation - to be replaced by hilt.
 */
object SampleAppDI {
    fun inject(mainActivity: MainActivity) {
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun inject(navActivity: NavActivity) {
        // TODO these should be application scoped, and not the activity lifecycle.
        // Do not use GlobalScope in real applition code.
        navActivity.dataRequestRepository = getDataRequestRepository()
        navActivity.networkRepository = getNetworkRepository(navActivity, GlobalScope)
    }

    private fun getNetworkRepository(activity: Activity, coroutineScope: CoroutineScope): NetworkRepository {
        return NetworkRepository.fromContext(activity, coroutineScope, NetworkStatusLogger.Logging)
    }

    private fun getDataRequestRepository(): DataRequestRepository {
        return DataRequestRepository.InMemoryDataRequestRepository
    }
}
