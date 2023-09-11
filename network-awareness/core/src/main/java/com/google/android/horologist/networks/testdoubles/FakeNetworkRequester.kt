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

package com.google.android.horologist.networks.testdoubles

import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.data.NetworkType.Cell
import com.google.android.horologist.networks.data.NetworkType.Wifi
import com.google.android.horologist.networks.request.HighBandwidthRequest
import com.google.android.horologist.networks.request.NetworkLease
import com.google.android.horologist.networks.request.NetworkReference
import com.google.android.horologist.networks.request.NetworkRequester
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Instant

public class FakeNetworkRequester(
    private val networkRepository: FakeNetworkRepository,
) : NetworkRequester {
    public var supportedNetworks: List<NetworkType> = listOf(Cell, Wifi)

    override fun requestHighBandwidthNetwork(request: HighBandwidthRequest): NetworkLease {
        val newNetworkType = if (request.type.cell && supportedNetworks.contains(Cell)) {
            Cell
        } else if (request.type.wifi && supportedNetworks.contains(Wifi)) {
            Wifi
        } else {
            null
        }

        networkRepository.pinNetwork(newNetworkType)
        val networkReference = if (newNetworkType != null) {
            NetworkReference("1", newNetworkType)
        } else {
            null
        }

        return object : NetworkLease {
            override val acquiredAt: Instant = Instant.now()

            override val grantedNetwork = MutableStateFlow(networkReference)

            override fun close() {
                grantedNetwork.value = null
                networkRepository.pinNetwork(null)
            }
        }
    }
}
