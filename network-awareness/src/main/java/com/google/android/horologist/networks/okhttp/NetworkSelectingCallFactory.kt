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

package com.google.android.horologist.networks.okhttp

import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import com.google.android.horologist.networks.data.DataRequestRepository
import com.google.android.horologist.networks.data.NetworkStatus
import com.google.android.horologist.networks.data.RequestType
import com.google.android.horologist.networks.highbandwidth.HighBandwidthNetworkMediator
import com.google.android.horologist.networks.okhttp.impl.FailedCall
import com.google.android.horologist.networks.okhttp.impl.HighBandwidthCall
import com.google.android.horologist.networks.okhttp.impl.NetworkAwareEventListenerFactory
import com.google.android.horologist.networks.okhttp.impl.NetworkSpecificSocketFactory
import com.google.android.horologist.networks.okhttp.impl.RequestTypeHolder.Companion.withDefaultRequestType
import com.google.android.horologist.networks.okhttp.impl.RequestVerifyingInterceptor
import com.google.android.horologist.networks.okhttp.impl.StandardCall
import com.google.android.horologist.networks.rules.Fail
import com.google.android.horologist.networks.rules.NetworkingRulesEngine
import com.google.android.horologist.networks.status.NetworkRepository
import kotlinx.coroutines.CoroutineScope
import okhttp3.Call
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@ExperimentalHorologistNetworksApi
public class NetworkSelectingCallFactory(
    internal val networkingRulesEngine: NetworkingRulesEngine,
    internal val highBandwidthNetworkMediator: HighBandwidthNetworkMediator,
    private val networkRepository: NetworkRepository,
    dataRequestRepository: DataRequestRepository?,
    rootClient: OkHttpClient,
    internal val coroutineScope: CoroutineScope,
    internal val timeout: Duration = 3.seconds
) : Call.Factory {
    private val defaultClient = rootClient.newBuilder()
        .addNetworkInterceptor(
            RequestVerifyingInterceptor(
                networkingRulesEngine = networkingRulesEngine
            )
        )
        .eventListenerFactory(
            NetworkAwareEventListenerFactory(
                networkingRulesEngine = networkingRulesEngine,
                dataRequestRepository = dataRequestRepository,
                delegateEventListenerFactory = rootClient.eventListenerFactory,
                networkRepository = networkRepository
            )
        )
        .build()

    private val clients = ConcurrentHashMap<String, OkHttpClient>()

    override fun newCall(request: Request): Call {
        val finalRequest = request.withDefaultRequestType(RequestType.UnknownRequest)

        val requestType = finalRequest.requestType

        val highBandwidthRequest = networkingRulesEngine.isHighBandwidthRequest(requestType)

        return if (highBandwidthRequest) {
            HighBandwidthCall(this, finalRequest)
        } else {
            // Can make call immediately
            newDirectCall(finalRequest)
        }
    }

    internal fun newDirectCall(request: Request): Call {
        val requestType = request.requestType

        val networkStatus =
            networkingRulesEngine.preferredNetwork(requestType)

        if (networkStatus == null) {
            val networks =
                networkRepository.networkStatus.value.networks.map {
                    it.networkInfo.type
                }
            val reason = "No suitable network for $requestType in $networks"
            return FailedCall(this, request, reason)
        }

        val requestCheck = networkingRulesEngine.checkValidRequest(requestType, networkStatus.networkInfo)
        if (requestCheck is Fail) {
            val reason = "Unable to use ${networkStatus.networkInfo.type} for $requestType"
            return FailedCall(this, request, reason)
        }

        val client = clientForNetwork(networkStatus)

        val call = client.newCall(request)

        return StandardCall(this, call)
    }

    private fun clientForNetwork(networkStatus: NetworkStatus): Call.Factory {
        return clients.computeIfAbsent(networkStatus.id) {
            defaultClient.newBuilder()
                .connectionPool(ConnectionPool())
                .socketFactory(
                    NetworkSpecificSocketFactory(
                        networkStatus = networkStatus
                    )
                )
                .build()
        }
    }
}
