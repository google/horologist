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

import com.google.android.horologist.networks.data.DataRequest
import com.google.android.horologist.networks.data.DataRequestRepository
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.okhttp.RequestTypeHolder.Companion.networkType
import com.google.android.horologist.networks.okhttp.RequestTypeHolder.Companion.requestType
import com.google.android.horologist.networks.rules.NetworkingRulesEngine
import okhttp3.Call
import okhttp3.Connection
import okhttp3.EventListener
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy

public class OkHttpEventListenerFactory(
    private val networkingRulesEngine: NetworkingRulesEngine,
    private val delegateEventListenerFactory: EventListener.Factory,
    private val dataRequestRepository: DataRequestRepository? = null,
) : EventListener.Factory {
    override fun create(call: Call): EventListener = Listener(
        delegateEventListenerFactory.create(
            call
        )
    )

    private inner class Listener(delegate: EventListener) : DelegatingEventListener(delegate) {
        private var bytesTransferred: Long = 0

        override fun connectFailed(
            call: Call,
            inetSocketAddress: InetSocketAddress,
            proxy: Proxy,
            protocol: Protocol?,
            ioe: IOException
        ) {
            networkingRulesEngine.logger.logNetworkEvent("connect failed $inetSocketAddress ${call.request().networkType}")

            if (proxy.type() == Proxy.Type.DIRECT) {
                networkingRulesEngine.reportConnectionFailure(
                    inetSocketAddress,
                    call.request().networkType
                )
            }

            super.connectFailed(call, inetSocketAddress, proxy, protocol, ioe)
        }

        override fun connectionAcquired(call: Call, connection: Connection) {
            val localAddress = connection.socket().localAddress
            val network = networkingRulesEngine.networkRepository.networkByAddress(localAddress)
            val networkType = network?.type ?: NetworkType.Unknown(localAddress.toString())

            val requestType = call.request().requestType
            call.request().networkType = networkType

            networkingRulesEngine.logger.debugNetworkEvent("HTTPS request $requestType ${networkType.typeName} $localAddress")

            super.connectionAcquired(call, connection)
        }

        override fun requestHeadersEnd(call: Call, request: Request) {
            bytesTransferred += request.headers.byteCount()

            super.requestHeadersEnd(call, request)
        }

        override fun requestBodyEnd(call: Call, byteCount: Long) {
            bytesTransferred += byteCount

            super.requestBodyEnd(call, byteCount)
        }

        override fun responseHeadersEnd(call: Call, response: Response) {
            bytesTransferred += response.headers.byteCount()

            super.responseHeadersEnd(call, response)
        }

        override fun responseBodyEnd(call: Call, byteCount: Long) {
            bytesTransferred += byteCount

            super.responseBodyEnd(call, byteCount)
        }

        override fun callEnd(call: Call) {
            recordBytes(call, null)

            super.callEnd(call)
        }

        override fun callFailed(call: Call, ioe: IOException) {
            recordBytes(call, ioe.message)

            super.callFailed(call, ioe)
        }

        @Suppress("UNUSED_PARAMETER")
        private fun recordBytes(call: Call, msg: String? = null) {
            val requestType = call.request().requestType
            val networkType = call.request().networkType ?: NetworkType.Unknown("unknown")

            networkingRulesEngine.logger.logNetworkResponse(
                requestType,
                networkType,
                bytesTransferred
            )
            dataRequestRepository?.storeRequest(
                DataRequest(
                    requestType,
                    networkType,
                    bytesTransferred
                )
            )
        }
    }
}
