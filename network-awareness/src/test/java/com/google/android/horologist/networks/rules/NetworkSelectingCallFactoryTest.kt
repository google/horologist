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

@file:OptIn(ExperimentalHorologistApi::class, ExperimentalCoroutinesApi::class)

package com.google.android.horologist.networks.rules

import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.networks.data.InMemoryDataRequestRepository
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.data.NetworkType.BT
import com.google.android.horologist.networks.data.RequestType
import com.google.android.horologist.networks.data.RequestType.MediaRequest.Companion.DownloadRequest
import com.google.android.horologist.networks.data.RequestType.MediaRequest.MediaRequestType.Download
import com.google.android.horologist.networks.highbandwidth.StandardHighBandwidthNetworkMediator
import com.google.android.horologist.networks.okhttp.NetworkSelectingCallFactory
import com.google.android.horologist.networks.okhttp.impl.RequestTypeHolder.Companion.requestType
import com.google.android.horologist.networks.okhttp.networkInfo
import com.google.android.horologist.networks.rules.helpers.ConfigurableNetworkingRules
import com.google.android.horologist.networks.rules.helpers.DeadEndInterceptor
import com.google.android.horologist.networks.rules.helpers.FakeNetworkRepository
import com.google.android.horologist.networks.rules.helpers.FakeNetworkRequester
import com.google.android.horologist.networks.rules.helpers.TestLogger
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

class NetworkSelectingCallFactoryTest {
    private val testScope = TestScope()
    private val networkRepository = FakeNetworkRepository()
    private val logger = TestLogger()
    private val networkingRules = ConfigurableNetworkingRules()
    private val networkRequester = FakeNetworkRequester(networkRepository)
    private val highBandwidthRequester = StandardHighBandwidthNetworkMediator(
        logger,
        networkRequester,
        testScope,
        3.seconds
    )
    private val dataRequestRepository = InMemoryDataRequestRepository()

    private val networkingRulesEngine = NetworkingRulesEngine(
        networkRepository = networkRepository,
        logger = logger,
        networkingRules = networkingRules
    )

    private val deadEndInterceptor = DeadEndInterceptor

    private val rootClient = OkHttpClient.Builder()
        .addInterceptor(deadEndInterceptor)
        .build()

    private val callFactory = NetworkSelectingCallFactory(
        networkingRulesEngine = networkingRulesEngine,
        highBandwidthNetworkMediator = highBandwidthRequester,
        dataRequestRepository = dataRequestRepository,
        rootClient = rootClient,
        networkRepository = networkRepository,
        coroutineScope = testScope
    )

    @Test
    fun normalConnectionForImages() {
        val request = Request.Builder()
            .url("https://example.org/image.png")
            .requestType(RequestType.ImageRequest)
            .build()

        callFactory.newCall(request).execute()

        val networkType = request.networkInfo

        assertThat(networkType?.type).isEqualTo(BT)
    }

    @Test
    fun executeFailsOnHighBandwidthCalls() {
        networkingRules.highBandwidthTypes[RequestType.MediaRequest(Download)] = true

        val request = Request.Builder()
            .url("https://example.org/music.mp3")
            .requestType(RequestType.MediaRequest(Download))
            .build()

        assertThrows("High Bandwidth Requests are not supported with execute", IOException::class.java) {
            callFactory.newCall(request).execute()
        }
    }

    @Test
    fun enqueueNormalConnectionForImages() {
        val request = Request.Builder()
            .url("https://example.org/image.png")
            .requestType(RequestType.ImageRequest)
            .build()

        callFactory.newCall(request).executeAsync()

        val networkType = request.networkInfo

        assertThat(networkType?.type).isEqualTo(BT)
    }

    @Test
    fun enqueueRequestHighBandwidthForDownloads() {
        networkingRules.preferredNetworks[RequestType.MediaRequest(Download)] = NetworkType.Wifi
        networkingRules.highBandwidthTypes[RequestType.MediaRequest(Download)] = true
        networkRequester.supportedNetworks = listOf(NetworkType.Wifi)

        val request = Request.Builder()
            .url("https://example.org/music.mp3")
            .requestType(RequestType.MediaRequest(Download))
            .build()

        val response = callFactory.newCall(request).executeAsync()
        response.close()

        val networkType = request.networkInfo

        assertThat(networkType?.type).isEqualTo(NetworkType.Wifi)

//        assertThat(highBandwidthRequester.pinned.value).isEmpty()
    }

    @Test
    fun enqueueRequestHighBandwidthForDownloadsButFails() {
        networkingRules.preferredNetworks[DownloadRequest] = NetworkType.Wifi
        networkingRules.highBandwidthTypes[DownloadRequest] = true
        networkingRules.validRequests[Pair(DownloadRequest, BT)] = false
        networkRequester.supportedNetworks = listOf()

        val request = Request.Builder()
            .url("https://example.org/music.mp3")
            .requestType(RequestType.MediaRequest(Download))
            .build()

        val thrown = assertThrows(IOException::class.java) {
            callFactory.newCall(request).executeAsync()
        }

        assertThat(thrown).hasMessageThat().isEqualTo("Unable to use BT for media-download")

//        assertThat(highBandwidthRequester.pinned.value).isNull()
    }
}

private fun Call.executeAsync(): Response {
    val future = CompletableFuture<Response>()

    enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            future.completeExceptionally(e)
        }

        override fun onResponse(call: Call, response: Response) {
            future.complete(response)
        }
    })

    return try {
        future.get(5, TimeUnit.SECONDS)
    } catch (ee: ExecutionException) {
        throw ee.cause!!
    }
}
