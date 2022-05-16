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

package com.google.android.horologist.networks.status

import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.os.Build
import com.google.android.horologist.networks.data.NetworkStatus
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.data.Status

internal data class NetworkStatusBuilder(
    var id: String,
    var network: Network,
    var status: Status = Status.Unknown,
    var linkProperties: LinkProperties? = null,
    var networkCapabilities: NetworkCapabilities? = null,
    var type: NetworkType? = null,
) {
    private fun readTransportType(): NetworkType {
        val name = linkProperties?.interfaceName ?: "unknown"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val transportInfo = networkCapabilities?.transportInfo

            val wifiInfo = transportInfo as? WifiInfo

            if (wifiInfo != null) {
                return NetworkType.Wifi(name, wifiInfo.ssid)
            }
        }

        //    lo usually stands for the loopback interface (localhost)
        //    wlan usually stands for a wireless networking interface
        //    rmnet interfaces are usually associated with cellular connections and usb tethering
        //    sit interfaces are associated with tunneling IPv6 over IPv4
        //    p2p interfaces are usually associated with peer-to-peer connections (perhaps your Android device's WiFi Direct support?)

        val metered = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) == false

        return when {
            isTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.Wifi(name, null)
            isTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.Cellular(name, metered = metered)
            name.startsWith("rmnet") -> NetworkType.Cellular(name, metered = metered)
            name.startsWith("wlan") -> NetworkType.Wifi(name, null)
            isTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> NetworkType.Bluetooth(name)
            else -> NetworkType.Unknown(name)
        }
    }

    private fun isTransport(transport: Int): Boolean {
        return networkCapabilities?.hasTransport(transport) == true
    }

    internal fun buildNetworkStatus(): NetworkStatus {
        return NetworkStatus(
            id = id,
            status = status,
            type = readTransportType(),
            addresses = linkProperties?.linkAddresses?.map { it.address }.orEmpty(),
            capabilities = networkCapabilities,
            linkProperties = linkProperties,
            bindSocket = { socket -> network.bindSocket(socket) }
        )
    }
}
