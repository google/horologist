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

package com.google.android.horologist.networks.highbandwidth

import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkRequest
import androidx.annotation.GuardedBy
import androidx.tracing.Trace
import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.data.NetworkType.Cell
import com.google.android.horologist.networks.data.NetworkType.Wifi
import com.google.android.horologist.networks.data.networkType
import com.google.android.horologist.networks.status.NetworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Implementation of `HighBandwidthNetworkMediator` that defers all logic to `ConnectivityManager`.
 * `ConnectivityManager` has an internal limit of 100 outstanding requests, so this implementation
 * shouldn't be used in that unlikely case.
 */
@ExperimentalHorologistNetworksApi
public class SimpleHighBandwidthNetworkMediator(
    private val connectivityManager: ConnectivityManager,
    private val networkRepository: NetworkRepository
) : HighBandwidthNetworkMediator {
    @GuardedBy("this")
    private var aggregatedRequests = AggregatedRequests()

    val requested: MutableStateFlow<HighBandwidthRequest?> = MutableStateFlow(null)

    @GuardedBy("this")
    private var pinnedCount = AggregatedNetworkCount()
    override val pinned: MutableStateFlow<NetworkType?> = MutableStateFlow(null)

    override fun requestHighBandwidthNetwork(request: HighBandwidthRequest): HighBandwithConnectionLease {
        val token = CallbackHighBandwithConnectionLease(request)
        val networkRequest = request.toNetworkRequest()

        registerRequest(networkRequest, token)

        return token
    }

    private fun registerRequest(
        networkRequest: NetworkRequest,
        callback: CallbackHighBandwithConnectionLease
    ) {
        connectivityManager.requestNetwork(networkRequest, callback)
    }

    private fun unregisterRequest(
        callback: CallbackHighBandwithConnectionLease
    ) {
        connectivityManager.unregisterNetworkCallback(callback)
    }

    private fun updateAggregatedRequests(fn: (AggregatedRequests) -> AggregatedRequests) {
        synchronized(this) {
            aggregatedRequests = fn(aggregatedRequests)

            requested.value = if (aggregatedRequests.isNonZero) {
                aggregatedRequests.toRequest()
            } else {
                null
            }
        }
    }

    private fun updatePinnedCount(fn: (AggregatedNetworkCount) -> AggregatedNetworkCount) {
        synchronized(this) {
            pinnedCount = fn(pinnedCount)

            pinned.update { current ->
                val new = pinnedCount.toNetworkType()

                if (current != new) {
                    if (current == null) {
                        Trace.beginAsyncSection("HighBandwidth", 0)
                    } else if (new == null) {
                        Trace.endAsyncSection("HighBandwidth", 0)
                    }
                }

                new
            }
        }
    }

    internal inner class CallbackHighBandwithConnectionLease(
        internal val request: HighBandwidthRequest
    ) :
        NetworkCallback(),
        HighBandwithConnectionLease {
        private val networkState = MutableStateFlow<NetworkType?>(null)
        private val closed = AtomicBoolean(false)

        init {
            updateAggregatedRequests {
                it + request
            }
        }

        override fun onAvailable(network: Network) {
            networkRepository.updateNetworkAvailability(network)

            val oldNetworkType = networkState.value

            val newNetworkType = connectivityManager.networkType(network)
            networkState.value = newNetworkType
            updatePinnedCount {
                it + newNetworkType - oldNetworkType
            }
        }

        override fun onUnavailable() {
            val oldNetworkType = networkState.value

            updatePinnedCount {
                it - oldNetworkType
            }

            networkState.value = null
        }

        override suspend fun awaitGranted(): NetworkType {
            return networkState.filterNotNull().first()
        }

        override fun close() {
            // Avoid any conditions closing this twice
            if (closed.compareAndSet(false, true)) {
                unregisterRequest(this)

                val oldNetworkType = networkState.value

                updatePinnedCount {
                    it - oldNetworkType
                }

                updateAggregatedRequests {
                    it - request
                }
            }
        }
    }

    private data class AggregatedNetworkCount(
        val wifi: Int = 0,
        val cell: Int = 0
    ) {
        operator fun plus(networkType: NetworkType?): AggregatedNetworkCount {
            return when (networkType) {
                Wifi -> copy(wifi = wifi + 1)
                Cell -> copy(cell = cell + 1)
                else -> this
            }
        }

        operator fun minus(networkType: NetworkType?): AggregatedNetworkCount {
            return when (networkType) {
                Wifi -> copy(wifi = wifi - 1)
                Cell -> copy(cell = cell - 1)
                else -> this
            }
        }

        fun toNetworkType(): NetworkType? {
            return if (cell > 0) {
                Cell
            } else if (wifi > 0) {
                Wifi
            } else {
                null
            }
        }
    }
}
