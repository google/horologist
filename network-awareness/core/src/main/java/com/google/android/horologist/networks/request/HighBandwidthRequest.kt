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

import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.data.RequestType

/**
 * A single request for high bandwidth networks, with specific usable transport types and
 * the originating request type.
 */
@ExperimentalHorologistApi
public data class HighBandwidthRequest(
    val type: Type = Type.All,
    val requestType: RequestType? = null,
    val url: String? = null,
) {
    public fun toNetworkRequest(): NetworkRequest {
        return NetworkRequest.Builder()
            .apply {
                if (type.cell) {
                    addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                }
                if (type.wifi) {
                    addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                }
            }
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
    }

    public enum class Type(public val wifi: Boolean, public val cell: Boolean) {
        WifiOnly(true, false),
        CellOnly(false, true),
        All(true, true),
    }

    public companion object {
        public val All: HighBandwidthRequest = HighBandwidthRequest(Type.All)
        public val Wifi: HighBandwidthRequest = HighBandwidthRequest(Type.WifiOnly)
        public val Cell: HighBandwidthRequest = HighBandwidthRequest(Type.CellOnly)

        public fun from(supportedTypes: List<NetworkType>): HighBandwidthRequest {
            val wifi = supportedTypes.contains(NetworkType.Wifi)
            val cell = supportedTypes.contains(NetworkType.Cell)

            val type = when {
                wifi && cell -> Type.All
                wifi -> Type.WifiOnly
                cell -> Type.CellOnly
                else -> throw IllegalStateException("must be cell or wifi at least")
            }
            return HighBandwidthRequest(type)
        }
    }
}
