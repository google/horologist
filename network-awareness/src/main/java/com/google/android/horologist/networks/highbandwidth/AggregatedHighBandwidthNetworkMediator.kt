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

import androidx.annotation.GuardedBy
import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.logging.NetworkStatusLogger
import com.google.android.horologist.networks.request.NetworkRequester
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Implementation of `HighBandwidthNetworkMediator` that locally aggregates requests and
 * then makes specific network requests when requests moves from 0 to 1 and back.
 */
@ExperimentalHorologistNetworksApi
public class AggregatedHighBandwidthNetworkMediator(
    private val logger: NetworkStatusLogger,
    private val networkRequester: NetworkRequester
) : HighBandwidthNetworkMediator {
    @GuardedBy("this")
    private var requests = AggregatedRequests()
    override val requested: MutableStateFlow<HighBandwidthRequest?> = MutableStateFlow(null)
    override val pinned: StateFlow<NetworkType?> = networkRequester.pinnedNetwork

    override fun requestHighBandwidthNetwork(
        request: HighBandwidthRequest
    ): HighBandwithConnectionLease {
        registerRequest(request)

        return SingleHighBandwithConnectionLease(request)
    }

    private fun registerRequest(request: HighBandwidthRequest) {
        synchronized(this) {
            requests += request
            update()
        }
    }

    private fun unregisterRequest(request: HighBandwidthRequest) {
        synchronized(this) {
            requests -= request
            update()
        }
    }

    private fun update() {
        requested.update { currentRequested ->
            if (requests.isNonZero) {
                val newRequested = requests.toRequest()
                if (currentRequested == null) {
                    val request = newRequested
                    requested.value = request
                    logger.logNetworkEvent("Requesting High Bandwidth Network for $request")
                    networkRequester.setRequests(request)
                }
                newRequested
            } else {
                if (currentRequested != null) {
                    requested.value = null
                    logger.logNetworkEvent("Releasing High Bandwidth Network")
                    networkRequester.clearRequest()
                }
                null
            }
        }
    }

    private inner class SingleHighBandwithConnectionLease(private val request: HighBandwidthRequest) : HighBandwithConnectionLease {
        private val closed = AtomicBoolean(false)

        override suspend fun awaitGranted(): NetworkType {
            return networkRequester.pinnedNetwork.filterNotNull().first()
        }

        override fun close() {
            // Avoid any conditions closing this twice
            if (closed.compareAndSet(false, true)) {
                unregisterRequest(request)
            }
        }
    }
}
