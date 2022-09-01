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
import com.google.android.horologist.networks.data.NetworkStatus
import com.google.android.horologist.networks.data.NetworkInfo
import com.google.android.horologist.networks.data.Networks
import com.google.android.horologist.networks.data.Status
import com.google.android.horologist.networks.status.HighBandwidthRequesting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.io.Closeable

@OptIn(ExperimentalHorologistNetworksApi::class)
class TestHighBandwidthRequester(private val networkStatus: MutableStateFlow<Networks>) :
    HighBandwidthRequesting {
    private val available = mutableListOf(
        NetworkInfo.wifi,
        NetworkInfo.cell
    )

    override fun requestHighBandwidth(requestedTypes: List<String>, wait: Boolean): Closeable? {
        var addedNetwork: NetworkStatus? = null

        networkStatus.update { networks ->
            val currentTypes = networks.networks.map { it.type.typeName }

            val noCurrentConnections = currentTypes.intersect(requestedTypes).isEmpty()
            val possibleConnections = available.intersect(requestedTypes)

            if (noCurrentConnections) {
                val networkType = possibleConnections.first()

                addedNetwork = when (networkType) {
                    NetworkInfo.wifi -> WifiNetwork
                    NetworkInfo.cell -> CellNetwork
                    else -> null
                }

                if (addedNetwork != null) {
                    return@update networks.copy(networks = networks.networks + (addedNetwork as NetworkStatus))
                }
            }

            networks
        }

        return Closeable {
            if (addedNetwork != null) {
                networkStatus.update { networks ->
                    networks.copy(networks = networks.networks - (addedNetwork as NetworkStatus))
                }
            }
        }
    }

    companion object {
        val WifiNetwork = NetworkStatus(id = "wifi1", Status.Available, NetworkInfo.Wifi("wifi1"), listOf(), null, null, {})
        val CellNetwork = NetworkStatus(id = "cell1", Status.Available, NetworkInfo.Cellular("cell1"), listOf(), null, null, {})
    }
}