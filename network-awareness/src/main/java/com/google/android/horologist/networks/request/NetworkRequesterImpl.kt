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

package com.google.android.horologist.networks.request

import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.data.networkType
import com.google.android.horologist.networks.highbandwidth.HighBandwidthRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@ExperimentalHorologistNetworksApi
public class NetworkRequesterImpl(
    private val connectivityManager: ConnectivityManager
) : NetworkRequester {
    private val requestedNetworks = MutableStateFlow<HighBandwidthRequest?>(null)
    override val pinnedNetwork: MutableStateFlow<NetworkType?> = MutableStateFlow(null)

    override fun clearRequest() {
        requestedNetworks.update {
            check(it != null)

            connectivityManager.unregisterNetworkCallback(networkCallback)
            pinnedNetwork.value = null

            null
        }
    }

    override fun setRequests(request: HighBandwidthRequest) {
        requestedNetworks.update {
            check(it == null)

            connectivityManager.requestNetwork(request.toNetworkRequest(), networkCallback)

            request
        }
    }

    private val networkCallback: NetworkCallback = object : NetworkCallback() {
        override fun onAvailable(network: Network) {
            val type = connectivityManager.networkType(network)

            pinnedNetwork.update { type }
        }

        override fun onUnavailable() {
            pinnedNetwork.update { null }
        }
    }
}
