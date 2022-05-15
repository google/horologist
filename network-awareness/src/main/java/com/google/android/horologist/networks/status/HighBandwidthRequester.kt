/*
 * Copyright 2022 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.horologist.networks.status

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.google.android.horologist.networks.logging.NetworkStatusLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.io.Closeable
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

public class HighBandwidthRequester(
    private val connectivityManager: ConnectivityManager,
    private val coroutineScope: CoroutineScope,
    private val logger: NetworkStatusLogger,
) {
    private val listeners: MutableSet<HighbandwidthListener> = Collections.newSetFromMap(ConcurrentHashMap())

    private var activeNetwork: MutableStateFlow<Network?> = MutableStateFlow(null)

    private var networkAvailableCallback: ConnectivityManager.NetworkCallback? = null
    private var networkRequestResult: Deferred<Unit>? = null
    private var highBandwidthRequests = 0

    private val timeoutMs = 3000

    @Synchronized
    public fun requestHighBandwidth(
        types: List<Int> = listOf(
            NetworkCapabilities.TRANSPORT_WIFI,
            NetworkCapabilities.TRANSPORT_CELLULAR
        ),
        wait: Boolean
    ): Closeable? {
        highBandwidthRequests++

        if (networkRequestResult == null) {
            logger.logNetworkEvent("Starting high bandwidth network request")
            this.networkRequestResult = coroutineScope.async {
                startCallback(types)
            }
        }

        try {
            if (wait) {
                runBlocking {
                    withTimeout(5000) {
                        logger.logNetworkEvent("waiting for high bandwidth network")
                        networkRequestResult!!.await()
                    }
                }
            }

            return object : Closeable {
                var closed = false
                override fun close() {
                    if (!closed) {
                        closed = true
                        freeHighBandwidth()
                    }
                }
            }
        } catch (tce: TimeoutCancellationException) {
            logger.logNetworkEvent("Timeout waiting for high bandwidth network", error = true)
            return null
        }
    }

    private suspend fun startCallback(types: List<Int>): Network? =
        suspendCoroutine { cont ->
            var first = true
            val networkAvailableCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    activeNetwork.value = network
                    if (first) {
                        cont.resume(network)
                        first = false
                        listeners.forEach { it.onHighbandwidthAvailable(network) }
                    }
                }

                override fun onUnavailable() {
                    activeNetwork.value = null
                    if (first) {
                        cont.resume(null)
                        first = false
                    }
                }
            }

            connectivityManager.requestNetwork(
                NetworkRequest.Builder()
                    .apply {
                        types.forEach {
                            addTransportType(it)
                        }
                    }
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build(),
                networkAvailableCallback,
                timeoutMs
            )

            this@HighBandwidthRequester.networkAvailableCallback = networkAvailableCallback
        }

    @Synchronized
    private fun freeHighBandwidth() {
        if (--highBandwidthRequests == 0) {
            logger.logNetworkEvent("Releasing high bandwidth network")
            connectivityManager.unregisterNetworkCallback(networkAvailableCallback!!)
            activeNetwork.value = null
            networkAvailableCallback = null
            networkRequestResult = null
            listeners.forEach { it.onHighbandwidthUnAvailable() }
        }
    }

    public fun addListener(highBandwidthListener: HighbandwidthListener) {
        listeners.add(highBandwidthListener)
    }

    public interface HighbandwidthListener {
        public fun onHighbandwidthAvailable(priorityNetwork: Network)
        public fun onHighbandwidthUnAvailable()
    }
}