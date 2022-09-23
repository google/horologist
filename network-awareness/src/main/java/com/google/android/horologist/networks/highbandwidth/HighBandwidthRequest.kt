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

import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.data.RequestType
import okhttp3.HttpUrl

/**
 * A single request for high bandwidth networks, with specific usable transport types and
 * the originating request type.
 */
@ExperimentalHorologistNetworksApi
public data class HighBandwidthRequest(
    val wifi: Boolean = true,
    val cell: Boolean = true,
    val requestType: RequestType? = null,
    val url: HttpUrl? = null
) {
    val isCellOnly: Boolean
    get

    public fun toNetworkRequest(): NetworkRequest {
        return NetworkRequest.Builder()
            .apply {
                if (cell) {
                    addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                }
                if (wifi) {
                    addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                }
            }
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
    }

    init {
        check(wifi || cell)
    }

    public companion object {
        public val All: HighBandwidthRequest = HighBandwidthRequest(wifi = true, cell = true)
        public val Wifi: HighBandwidthRequest = HighBandwidthRequest(wifi = true, cell = false)
        public val Cell: HighBandwidthRequest = HighBandwidthRequest(wifi = false, cell = true)

        public fun from(supportedTypes: List<NetworkType>): HighBandwidthRequest {
            return HighBandwidthRequest(
                wifi = supportedTypes.contains(NetworkType.Wifi),
                cell = supportedTypes.contains(NetworkType.Cell)
            )
        }
    }
}
