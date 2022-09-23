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

package com.google.android.horologist.networks.okhttp.impl

import androidx.annotation.GuardedBy
import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import com.google.android.horologist.networks.highbandwidth.HighBandwidthConnectionLease
import com.google.android.horologist.networks.highbandwidth.HighBandwidthRequest
import com.google.android.horologist.networks.okhttp.NetworkSelectingCallFactory
import com.google.android.horologist.networks.okhttp.highBandwidthConnectionLease
import com.google.android.horologist.networks.okhttp.impl.RequestTypeHolder.Companion.requestType
import com.google.android.horologist.networks.okhttp.requestType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import okio.AsyncTimeout
import okio.Timeout
import java.io.IOException

/**
 * A deferred call that needs to wait for [HighBandwidthConnectionLease.awaitGranted]
 * or timeout before continuing. This gives the ConnectivityManager
 * time to bring up a high bandwidth network in response to an
 * OkHttp call.
 */
@ExperimentalHorologistNetworksApi
internal class HighBandwidthCall(
    private val callFactory: NetworkSelectingCallFactory,
    private val request: Request
) : Call {
    @GuardedBy("this")
    private var cancelled = false

    @GuardedBy("this")
    private var call: Call? = null

    override fun cancel() {
        synchronized(this) {
            cancelled = true
            call?.cancel()
        }
        request.highBandwidthConnectionLease?.close()
    }

    override fun clone(): Call {
        // Remove network and lease from new request
        val cleanRequest = request.newBuilder().requestType(request.requestType).build()
        return callFactory.newCall(cleanRequest)
    }

    override fun enqueue(responseCallback: Callback) {
        callFactory.coroutineScope.launch(Dispatchers.Default) {
            val token = requestNetwork()

            token.awaitGranted(callFactory.timeout)

            synchronized(this) {
                if (cancelled) {
                    request.highBandwidthConnectionLease?.close()
                } else {
                    callFactory.newDirectCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            responseCallback.onFailure(this@HighBandwidthCall, e)
                        }

                        override fun onResponse(call: Call, response: Response) {
                            responseCallback.onResponse(this@HighBandwidthCall, response)
                        }
                    })
                }
            }
        }
    }

    override fun execute(): Response {
        throw IOException("High Bandwidth Requests are not supported with execute")
    }

    private fun requestNetwork(): HighBandwidthConnectionLease {
        val requestType = request.requestType
        val types = HighBandwidthRequest.from(
            callFactory.networkingRulesEngine.supportedTypes(requestType)
        ).copy(url = request.url)

        val token =
            callFactory.highBandwidthNetworkMediator.requestHighBandwidthNetwork(request = types)
        request.highBandwidthConnectionLease = token

        return token
    }

    override fun isCanceled(): Boolean = synchronized(this) { cancelled }

    override fun isExecuted(): Boolean = synchronized(this) { call?.isExecuted() ?: false }

    override fun request(): Request = request

    override fun timeout(): Timeout = synchronized(this) { call?.timeout() ?: AsyncTimeout() }
}
