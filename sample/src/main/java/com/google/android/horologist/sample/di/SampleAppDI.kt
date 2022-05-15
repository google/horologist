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

import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.android.horologist.navsample.NavActivity
import com.google.android.horologist.networks.data.DataRequestRepository
import com.google.android.horologist.networks.logging.NetworkStatusLogger
import com.google.android.horologist.networks.status.NetworkRepository
import com.google.android.horologist.sample.MainActivity
import com.google.android.horologist.sample.media.MediaDataSource
import com.google.android.horologist.sample.media.PlayerRepositoryImpl

/**
 * Simple DI implementation - to be replaced by hilt.
 */
object SampleAppDI {

    fun inject(mainActivity: MainActivity) {
        mainActivity.playerRepositoryImplFactory = getPlayerRepositoryImplFactory(getMediaDataSource())
    }

    fun inject(navActivity: NavActivity) {
        navActivity.networkRepository = NetworkRepository.fromContext(
            application = navActivity,
            coroutineScope = navActivity.lifecycleScope,
            logger = NetworkStatusLogger.Logging
        )
        navActivity.dataRequestRepository = DataRequestRepository.InMemoryDataRequestRepository
    }

    private fun getPlayerRepositoryImplFactory(
        mediaDataSource: MediaDataSource
    ): PlayerRepositoryImpl.Factory =
        PlayerRepositoryImpl.Factory(mediaDataSource)

    private fun getMediaDataSource(): MediaDataSource = MediaDataSource()

    val PlayerRepositoryImplFactoryKey = object : CreationExtras.Key<PlayerRepositoryImpl.Factory> {}
    val NetworkRepositoryKey = object : CreationExtras.Key<NetworkRepository> {}
    val DataRequestRepositoryKey = object : CreationExtras.Key<DataRequestRepository> {}
}
