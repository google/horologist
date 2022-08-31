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

@file:OptIn(ExperimentalHorologistNetworksApi::class)

package com.google.android.horologist.networks.rules

import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import com.google.android.horologist.networks.data.DataRequestRepository
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.data.Networks
import com.google.android.horologist.networks.data.RequestType
import com.google.android.horologist.networks.data.RequestType.MediaRequest.MediaRequestType.Download
import com.google.android.horologist.networks.logging.NetworkStatusLogger
import com.google.android.horologist.networks.okhttp.NetworkSelectingCallFactory
import com.google.android.horologist.networks.okhttp.RequestTypeHolder.Companion.networkType
import com.google.android.horologist.networks.okhttp.RequestTypeHolder.Companion.requestType
import com.google.android.horologist.networks.rules.helpers.ConfigurableNetworkingRules
import com.google.android.horologist.networks.rules.helpers.TestHighBandwidthRequester
import com.google.android.horologist.networks.rules.helpers.DeadEndInterceptor
import com.google.common.truth.Truth
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Test

class NetworkSelectingCallFactoryTest {
    private val networkStatus =
        MutableStateFlow(Networks(activeNetwork = null, networks = listOf()))
    private val logger = NetworkStatusLogger.InMemory()
    private val networkingRules = ConfigurableNetworkingRules()
    private val highBandwidthRequester = TestHighBandwidthRequester(networkStatus)
    private val dataRequestRepository = DataRequestRepository.InMemoryDataRequestRepository()

    private val networkingRulesEngine = NetworkingRulesEngine(
        networkStatus = networkStatus,
        logger = logger,
        networkingRules = networkingRules
    )

    private val deadEndInterceptor = DeadEndInterceptor()

    private val rootClient = OkHttpClient.Builder()
        .addInterceptor(deadEndInterceptor)
        .build()

    private val callFactory = NetworkSelectingCallFactory(
        networkingRulesEngine = networkingRulesEngine,
        highBandwidthRequester = highBandwidthRequester,
        dataRequestRepository = dataRequestRepository,
        rootClient = rootClient
    )

    @Test
    fun normalConnectionForImages() {
        val request = Request.Builder()
            .requestType(RequestType.ImageRequest)
            .build()

        callFactory.newCall(request).execute()

        val networkType = request.networkType

        Truth.assertThat(networkType?.typeName).isEqualTo(NetworkType.ble)
    }

    @Test
    fun requestHighBandwidthForDownloads() {
        val request = Request.Builder()
            .requestType(RequestType.MediaRequest(Download))
            .build()

        callFactory.newCall(request).execute()

        val networkType = request.networkType

        Truth.assertThat(networkType?.typeName).isEqualTo(NetworkType.wifi)
    }
}