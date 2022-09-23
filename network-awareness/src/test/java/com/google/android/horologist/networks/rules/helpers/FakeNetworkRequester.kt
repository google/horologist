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

package com.google.android.horologist.networks.rules.helpers

import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.data.NetworkType.Cell
import com.google.android.horologist.networks.data.NetworkType.Wifi
import com.google.android.horologist.networks.highbandwidth.HighBandwidthRequest
import com.google.android.horologist.networks.request.NetworkRequester
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalHorologistNetworksApi::class)
class FakeNetworkRequester(
    private val networkRepository: FakeNetworkRepository
) : NetworkRequester {
    public var supportedNetworks = listOf(Cell, Wifi)

    override fun clearRequest() {
        pinnedNetwork.value = null

        networkRepository.pinNetwork(null)
    }

    override fun setRequests(request: HighBandwidthRequest) {
        val newNetworkType = if (request.type.cell && supportedNetworks.contains(Cell)) {
            Cell
        } else if (request.type.wifi && supportedNetworks.contains(Wifi)) {
            Wifi
        } else {
            null
        }

        pinnedNetwork.value = newNetworkType
        networkRepository.pinNetwork(pinnedNetwork.value)
    }

    override val pinnedNetwork = MutableStateFlow<NetworkType?>(null)
}
