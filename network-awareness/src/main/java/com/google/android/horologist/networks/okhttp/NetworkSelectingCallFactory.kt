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
import com.google.android.horologist.networks.okhttp.RequestTypeHolder.Companion.requestType
import com.google.android.horologist.networks.rules.NetworkingRulesEngine
import com.google.android.horologist.networks.status.HighBandwidthRequesting
import com.google.android.horologist.networks.status.NetworkRepository
import okhttp3.Call
import okhttp3.Callback
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import okio.Timeout
import java.util.concurrent.ConcurrentHashMap

@ExperimentalHorologistNetworksApi
public class NetworkSelectingCallFactory(
    private val networkingRulesEngine: NetworkingRulesEngine,
    private val highBandwidthRequester: HighBandwidthRequesting,
    private val networkRepository: NetworkRepository,
    dataRequestRepository: DataRequestRepository?,
    rootClient: OkHttpClient
) : Call.Factory {
    private val defaultClient = rootClient.newBuilder()
        .addNetworkInterceptor(
            RequestVerifyingInterceptor(
                networkingRulesEngine = networkingRulesEngine
            )
        )
        .eventListenerFactory(
            OkHttpEventListenerFactory(
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

        val networkStatus =
            networkingRulesEngine.preferredNetwork(requestType)

        if (networkStatus == null) {
            val networks =
                networkingRulesEngine.networkStatus.value.networks.map {
                    it.type.typeName
                }
            val reason = "No suitable network for $requestType in $networks"
            return FailedCall(finalRequest, reason)
        }

        val client = clientForNetwork(networkStatus)

        val call = client.newCall(finalRequest)

        return ThisCall(call)
    }

    private fun clientForNetwork(networkStatus: NetworkStatus): Call.Factory {
        return clients.computeIfAbsent(networkStatus.id) {
            defaultClient.newBuilder()
                .connectionPool(ConnectionPool())
                .apply {
                    // Add first, since it's logically unrelated to other requests (may not hold)
                    // and allows us to add a test interceptor at the end
                    interceptors().add(
                        0,
                        NetworkEstablishingInterceptor(
                            networkingRulesEngine = networkingRulesEngine,
                            highBandwidthRequester = highBandwidthRequester
                        )
                    )
                }
                .socketFactory(
                    NetworkSpecificSocketFactory(
                        networkStatus = networkStatus
                    )
                )
                .build()
        }
    }

    private inner class ThisCall(private val delegate: Call) : Call {
        override fun cancel(): Unit = delegate.cancel()

        override fun clone(): Call = newCall(request())

        override fun enqueue(responseCallback: Callback) {
            return delegate.enqueue(responseCallback)
        }

        override fun execute(): Response {
            return delegate.execute()
        }

        override fun isCanceled(): Boolean = delegate.isCanceled()

        override fun isExecuted(): Boolean = delegate.isExecuted()

        override fun request(): Request = delegate.request()

        override fun timeout(): Timeout = delegate.timeout()
    }

    private inner class FailedCall(private val request: Request, private val message: String) :
        Call {
        private var cancelled = false
        private var executed = false

        override fun cancel() {
            cancelled = true
        }

        override fun clone(): Call = newCall(request())

        override fun enqueue(responseCallback: Callback) {
            responseCallback.onFailure(this, IOException(message))
        }

        override fun execute(): Response {
            throw IOException(message)
        }

        override fun isCanceled(): Boolean = cancelled

        override fun isExecuted(): Boolean = executed

        override fun request(): Request = request

        override fun timeout(): Timeout = Timeout.NONE
    }
}
