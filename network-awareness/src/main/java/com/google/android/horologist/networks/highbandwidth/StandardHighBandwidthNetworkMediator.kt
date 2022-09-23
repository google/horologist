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

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.google.android.horologist.networks.highbandwidth

import android.net.Network
import androidx.annotation.GuardedBy
import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.logging.NetworkStatusLogger
import com.google.android.horologist.networks.request.NetworkLease
import com.google.android.horologist.networks.request.NetworkRequester
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration

/**
 * Implementation of `HighBandwidthNetworkMediator` that locally aggregates requests and
 * then makes specific network requests when requests moves from 0 to 1 and back.
 */
@ExperimentalHorologistNetworksApi
public class StandardHighBandwidthNetworkMediator(
    private val logger: NetworkStatusLogger,
    private val networkRequester: NetworkRequester
) : HighBandwidthNetworkMediator {
    private val requests: MutableStateFlow<Requests> = MutableStateFlow(Requests())

    override val pinned: Flow<Set<NetworkType>> = requests.flatMapLatest {
        it.leases
    }

    data class CountAndLease(
        val count: Int = 0,
        val lease: NetworkLease? = null,
    )

    data class Requests(
        val wifiOnly: CountAndLease = CountAndLease(),
        val cellOnly: CountAndLease = CountAndLease(),
        val either: CountAndLease = CountAndLease(),
    ) {
        fun forRequest(request: HighBandwidthRequest): CountAndLease {

        }

        fun withUpdated(
            request: HighBandwidthRequest,
            existing: CountAndLease
        ): Requests {
            if (request.isCellOnly) {
                return this.copy()
            } else if (request.isWifiOnly) {
                return this.copy()
            } else {
                return this.copy()
            }
        }

        val leases: Flow<Set<NetworkType>>
            get() {
                val leaseNetworks = listOfNotNull(
                    wifiOnly.lease?.grantedNetwork,
                    cellOnly.lease?.grantedNetwork,
                    either.lease?.grantedNetwork
                )
                return combine(leaseNetworks) { networks ->
                    networks.mapNotNull {
                        it?.second
                    }.toSet()
                }
            }
    }

    override fun requestHighBandwidthNetwork(
        request: HighBandwidthRequest
    ): HighBandwidthConnectionLease {
        requests.update {
            processRequest(it, request)
        }

        val lease = requests.value.forRequest(request).lease!!

        return SingleHighBandwidthConnectionLease(request, lease)
    }

    private fun processRequest(
        requests: Requests,
        request: HighBandwidthRequest
    ): Requests {
        val existing = requests.forRequest(request)

        return requests.withUpdated(request, existing)
    }

    private fun releaseHighBandwidthNetwork(
        request: HighBandwidthRequest
    ) {
        requests.update {
            processRelease(it, request)
        }
    }

    private fun processRelease(
        it: Requests,
        request: HighBandwidthRequest
    ): Requests {
        return it
    }

    private inner class SingleHighBandwidthConnectionLease(
        private val request: HighBandwidthRequest,
        private val lease: NetworkLease
    ) :
        HighBandwidthConnectionLease {
        private val closed = AtomicBoolean(false)

        override suspend fun awaitGranted(timeout: Duration): Boolean {
            return withTimeoutOrNull(timeout) {
                lease.grantedNetwork.filterNotNull().first()
            } != null
        }

        override fun close() {
            // Avoid any conditions closing this twice
            if (closed.compareAndSet(false, true)) {
                releaseHighBandwidthNetwork(request)
            }
        }
    }
}
