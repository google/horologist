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
import android.net.NetworkRequest
import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.data.NetworkType.Unknown
import com.google.android.horologist.networks.data.networkType
import kotlinx.coroutines.flow.MutableStateFlow

@ExperimentalHorologistNetworksApi
public class NetworkRequesterImpl(
    private val connectivityManager: ConnectivityManager
) : NetworkRequester {
    override fun requestHighBandwidthNetwork(request: NetworkRequest): NetworkLease {
        val lease = NetworkLeaseImpl()

        connectivityManager.requestNetwork(request, lease)

        return lease
    }

    private inner class NetworkLeaseImpl : NetworkCallback(), NetworkLease {
        override val grantedNetwork: MutableStateFlow<Pair<Network, NetworkType>?> =
            MutableStateFlow(null)

        override fun onAvailable(network: Network) {
            grantedNetwork.value =
                Pair(network, connectivityManager.networkType(network) ?: Unknown)
        }

        override fun onUnavailable() {
            grantedNetwork.value = null
        }

        override fun close() {
            connectivityManager.unregisterNetworkCallback(this)
            grantedNetwork.value = null
        }
    }
}
