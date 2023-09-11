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

package com.google.android.horologist.mediasample.work

import coil.ImageLoader
import com.google.android.horologist.compose.tools.coil.FakeImageLoader
import com.google.android.horologist.mediasample.data.api.UampService
import com.google.android.horologist.mediasample.di.NetworkModule
import com.google.android.horologist.networks.data.DataRequestRepository
import com.google.android.horologist.networks.data.InMemoryDataRequestRepository
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.testdoubles.FakeNetworkRepository
import com.google.android.horologist.networks.highbandwidth.HighBandwidthConnectionLease
import com.google.android.horologist.networks.highbandwidth.HighBandwidthNetworkMediator
import com.google.android.horologist.networks.logging.NetworkStatusLogger
import com.google.android.horologist.networks.okhttp.impl.FailedCall
import com.google.android.horologist.networks.request.HighBandwidthRequest
import com.google.android.horologist.networks.status.NetworkRepository
import com.google.android.horologist.test.toolbox.testdoubles.FakeUampService
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.Call
import okhttp3.Request
import javax.inject.Singleton
import kotlin.time.Duration

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NetworkModule::class],
)
object FakeNetworkModule {
    @Singleton
    @Provides
    fun uampService(): UampService = FakeUampService()

    @Singleton
    @Provides
    fun imageLoader(): ImageLoader = FakeImageLoader.NotFound

    @Singleton
    @Provides
    fun callFactory(): Call.Factory = object : Call.Factory {
        override fun newCall(request: Request): Call {
            return FailedCall(this, request, "Test")
        }
    }

    @Singleton
    @Provides
    fun highBandwidthRequester(): HighBandwidthNetworkMediator = object : HighBandwidthNetworkMediator {
        override val pinned: Flow<Set<NetworkType>> = flowOf()

        override fun requestHighBandwidthNetwork(request: HighBandwidthRequest): HighBandwidthConnectionLease {
            return object : HighBandwidthConnectionLease {
                override suspend fun awaitGranted(timeout: Duration): Boolean {
                    return false
                }

                override fun close() {
                }
            }
        }
    }

    @Singleton
    @Provides
    fun networkRepository(): NetworkRepository = FakeNetworkRepository()

    @Provides
    fun networkLogger(): NetworkStatusLogger = NetworkStatusLogger.InMemory()

    @Singleton
    @Provides
    fun dataRequestRepository(): DataRequestRepository =
        InMemoryDataRequestRepository()
}
