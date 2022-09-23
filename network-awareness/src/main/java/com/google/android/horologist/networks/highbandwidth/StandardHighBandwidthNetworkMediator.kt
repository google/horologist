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

import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.logging.NetworkStatusLogger
import com.google.android.horologist.networks.request.NetworkLease
import com.google.android.horologist.networks.request.NetworkRequester
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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
        val grantedNetworks = it.types.values.mapNotNull { countAndLease -> countAndLease.lease?.grantedNetwork }
        if (grantedNetworks.isEmpty()) {
            flowOf(setOf())
        } else {
            combine(grantedNetworks) { networks ->
                networks.mapNotNull { it?.second }.toSet()
            }
        }
    }

    private data class CountAndLease(
        val count: Int = 0,
        val lease: NetworkLease? = null
    ) {
        init {
            if (lease != null) {
                check(count > 0)
            } else {
                check(count == 0)
            }
        }
    }

    private data class Requests(
        val types: Map<HighBandwidthRequest.Type, CountAndLease> = mapOf(
            HighBandwidthRequest.Type.All to CountAndLease(),
            HighBandwidthRequest.Type.CellOnly to CountAndLease(),
            HighBandwidthRequest.Type.WifiOnly to CountAndLease()
        )
    ) {
        fun update(
            type: HighBandwidthRequest.Type,
            fn: (CountAndLease) -> CountAndLease
        ): Requests {
            val currentCountAndLease = types[type]!!

            val newCountAndLease = fn(currentCountAndLease)

            return copy(types = types.plus(type to newCountAndLease))
        }
    }

    override fun requestHighBandwidthNetwork(
        request: HighBandwidthRequest
    ): HighBandwidthConnectionLease {
        requests.update {
            processRequest(it, request)
        }

        val lease = requests.value.types[request.type]?.lease!!

        return SingleHighBandwidthConnectionLease(request, lease)
    }

    private fun processRequest(
        requests: Requests,
        request: HighBandwidthRequest
    ): Requests {
        return requests.update(request.type) {
            it.copy(
                count = it.count + 1,
                lease = it.lease
                    ?: makeHighBandwidthNetwork(request)
            )
        }
    }

    private fun makeHighBandwidthNetwork(request: HighBandwidthRequest): NetworkLease {
        logger.logNetworkEvent("Requesting High Bandwidth Network for ${request.type}")
        return networkRequester.requestHighBandwidthNetwork(request.toNetworkRequest())
    }

    private fun releaseHighBandwidthNetwork(
        request: HighBandwidthRequest
    ) {
        requests.update {
            processRelease(it, request)
        }
    }

    private fun processRelease(
        requests: Requests,
        request: HighBandwidthRequest
    ): Requests {
        return requests.update(request.type) {
            val newLease = if (it.count == 1) {
                releaseHighBandwidthNetwork(request, it.lease!!)
                null
            } else {
                it.lease
            }

            it.copy(
                count = it.count - 1,
                lease = newLease
            )
        }
    }

    private fun releaseHighBandwidthNetwork(request: HighBandwidthRequest, lease: NetworkLease) {
        logger.logNetworkEvent("Releasing High Bandwidth Network for ${request.type}")
        lease.close()
    }

    private inner class SingleHighBandwidthConnectionLease(
        private val request: HighBandwidthRequest,
        private val lease: NetworkLease
    ) :
        HighBandwidthConnectionLease {
        private val closed = AtomicBoolean(false)

        override suspend fun awaitGranted(timeout: Duration): Boolean {
            val timeoutMillis = lease.acquiredAt.toEpochMilli() + timeout.inWholeMilliseconds - System.currentTimeMillis()

            if (timeoutMillis <= 0L) {
                return false
            }

            return withTimeoutOrNull(timeoutMillis) {
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
