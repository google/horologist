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
import com.google.android.horologist.networks.data.networkType
import com.google.android.horologist.networks.status.NetworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import java.time.Instant
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

    override val requested: MutableStateFlow<HighBandwidthRequest?> = MutableStateFlow(null)

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

    private fun updateAggregatedRequests(fn: (AggregatedRequests) -> AggregatedRequests): AggregatedRequests {
        return synchronized(this) {
            aggregatedRequests = fn(aggregatedRequests)

            requested.value = if (aggregatedRequests.isNonZero) {
                aggregatedRequests.toRequest()
            } else {
                null
            }

            aggregatedRequests
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
        private var statusAtRequest: AggregatedRequests = updateAggregatedRequests {
            it + request
        }
        private val networkState = MutableStateFlow<NetworkType?>(null)
        private val closed = AtomicBoolean(false)

        override fun onAvailable(network: Network) {
            val newNetworkType = connectivityManager.networkType(network)

            networkRepository.updateNetworkAvailability(network)

            val oldNetworkType = networkState.value

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

        override suspend fun awaitGranted(): NetworkType? {
            if (isAlreadyTimedOut()) {
                return null
            }

            return networkState.filterNotNull().first()
        }

        private fun isAlreadyTimedOut(): Boolean {
            val cutoff = Instant.now().minusSeconds(3)
            val wifiRequestedAt = statusAtRequest.wifiRequestedAt
            val cellRequestedAt = statusAtRequest.cellRequestedAt
            val wifiTimedOut = !request.wifi || wifiRequestedAt != null && cutoff > wifiRequestedAt
            val cellTimedOut = !request.cell || cellRequestedAt != null && cutoff > cellRequestedAt

            return wifiTimedOut && cellTimedOut
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
}
