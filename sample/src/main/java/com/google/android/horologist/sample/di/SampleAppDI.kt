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

import android.content.Context
import android.net.ConnectivityManager
import com.google.android.horologist.components.SampleApplication
import com.google.android.horologist.navsample.NavActivity
import com.google.android.horologist.networks.InMemoryStatusLogger
import com.google.android.horologist.networks.data.DataRequestRepository
import com.google.android.horologist.networks.data.InMemoryDataRequestRepository
import com.google.android.horologist.networks.highbandwidth.StandardHighBandwidthNetworkMediator
import com.google.android.horologist.networks.logging.NetworkStatusLogger
import com.google.android.horologist.networks.okhttp.NetworkSelectingCallFactory
import com.google.android.horologist.networks.request.NetworkRequesterImpl
import com.google.android.horologist.networks.rules.NetworkingRules
import com.google.android.horologist.networks.rules.NetworkingRulesEngine
import com.google.android.horologist.networks.status.NetworkRepository
import com.google.android.horologist.networks.status.NetworkRepositoryImpl
import com.google.android.horologist.sample.MainActivity
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import kotlin.time.Duration.Companion.seconds

/**
 * Simple DI implementation - to be replaced by hilt.
 */
object SampleAppDI {
    @Suppress("UNUSED_PARAMETER")
    fun inject(mainActivity: MainActivity) {
    }

    @Suppress("UNUSED_PARAMETER")
    fun inject(navActivity: NavActivity) {
    }

    private fun getNetworkRepository(
        context: Context,
        coroutineScope: CoroutineScope
    ): NetworkRepository {
        return NetworkRepositoryImpl.fromContext(context, coroutineScope)
    }

    private fun getDataRequestRepository(): DataRequestRepository {
        return InMemoryDataRequestRepository()
    }

    private fun getNetworkAwareCallFactory(
        context: Context,
        networkRepository: NetworkRepository,
        networkLogger: NetworkStatusLogger,
        dataRequestRepository: DataRequestRepository,
        coroutineScope: CoroutineScope,
        okHttpClient: OkHttpClient
    ): Call.Factory {
        val networkingRules = NetworkingRules.Conservative

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkRequester = NetworkRequesterImpl(
            connectivityManager
        )

        val highBandwidthRequester = StandardHighBandwidthNetworkMediator(
            networkRequester = networkRequester,
            logger = networkLogger,
            coroutineScope = coroutineScope,
            delayToRelease = 3.seconds
        )

        val networkingRulesEngine = NetworkingRulesEngine(
            networkRepository = networkRepository,
            logger = networkLogger,
            networkingRules = networkingRules
        )

        return NetworkSelectingCallFactory(
            networkingRulesEngine = networkingRulesEngine,
            highBandwidthNetworkMediator = highBandwidthRequester,
            dataRequestRepository = dataRequestRepository,
            rootClient = okHttpClient,
            networkRepository = networkRepository,
            coroutineScope = coroutineScope
        )
    }

    fun inject(sampleApplication: SampleApplication) {
        sampleApplication.okHttpClient = OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().also { interceptor ->
                    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                }
            )
            .build()
        sampleApplication.coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        sampleApplication.networkLogger = InMemoryStatusLogger()
        sampleApplication.dataRequestRepository = getDataRequestRepository()
        sampleApplication.networkRepository = getNetworkRepository(
            context = sampleApplication,
            coroutineScope = sampleApplication.coroutineScope
        )
        sampleApplication.networkAwareCallFactory = getNetworkAwareCallFactory(
            context = sampleApplication,
            networkRepository = sampleApplication.networkRepository,
            networkLogger = sampleApplication.networkLogger,
            dataRequestRepository = sampleApplication.dataRequestRepository,
            coroutineScope = sampleApplication.coroutineScope,
            okHttpClient = sampleApplication.okHttpClient
        )
        sampleApplication.moshi = Moshi.Builder().build()
    }
}
