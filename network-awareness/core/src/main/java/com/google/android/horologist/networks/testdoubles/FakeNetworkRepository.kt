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

import android.net.Network
import com.google.android.horologist.networks.data.NetworkInfo
import com.google.android.horologist.networks.data.NetworkStatus
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.data.Networks
import com.google.android.horologist.networks.data.Status
import com.google.android.horologist.networks.status.NetworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import java.net.InetAddress

public class FakeNetworkRepository : NetworkRepository {
    private var defaultNetworks = listOf(BtNetwork)
    private var pinned: NetworkType? = null

    override val networkStatus: MutableStateFlow<Networks> =
        MutableStateFlow(Networks(defaultNetworks.firstOrNull(), defaultNetworks))

    override fun networkByAddress(localAddress: InetAddress): NetworkStatus? {
        return networkStatus.value.networks.firstOrNull {
            it.addresses.contains(localAddress)
        }
    }

    override fun updateNetworkAvailability(network: Network) {
    }

    public fun pinNetwork(value: NetworkType?) {
        synchronized(this) {
            pinned = value
            update()
        }
    }

    public fun setDefaultNetworks(networks: List<NetworkStatus>) {
        synchronized(this) {
            defaultNetworks = networks
            update()
        }
    }

    private fun update() {
        val addNetwork =
            pinned != null && defaultNetworks.find { it.networkInfo.type == pinned } == null
        val networks =
            if (addNetwork) defaultNetworks + (if (pinned == NetworkType.Cell) CellNetwork else WifiNetwork) else defaultNetworks

        networkStatus.value = Networks(defaultNetworks.firstOrNull(), networks)
    }

    internal companion object {
        internal val WifiNetwork = NetworkStatus(
            id = "wifi1",
            status = Status.Available,
            networkInfo = NetworkInfo.Wifi("wifi1"),
            addresses = listOf(),
            capabilities = null,
            linkProperties = null,
            bindSocket = {},
        )
        internal val CellNetwork = NetworkStatus(
            id = "cell1",
            status = Status.Available,
            networkInfo = NetworkInfo.Cellular("cell1"),
            addresses = listOf(),
            capabilities = null,
            linkProperties = null,
            bindSocket = {},
        )
        internal val BtNetwork = NetworkStatus(
            id = "bt1",
            status = Status.Available,
            networkInfo = NetworkInfo.Bluetooth("bt1"),
            addresses = listOf(),
            capabilities = null,
            linkProperties = null,
            bindSocket = {},
        )
    }
}
