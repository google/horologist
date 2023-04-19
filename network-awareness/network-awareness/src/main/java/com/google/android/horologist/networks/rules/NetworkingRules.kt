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

package com.google.android.horologist.networks.rules

import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.networks.data.NetworkInfo
import com.google.android.horologist.networks.data.NetworkInfo.Bluetooth
import com.google.android.horologist.networks.data.NetworkInfo.Cellular
import com.google.android.horologist.networks.data.NetworkInfo.Wifi
import com.google.android.horologist.networks.data.NetworkStatus
import com.google.android.horologist.networks.data.Networks
import com.google.android.horologist.networks.data.RequestType
import com.google.android.horologist.networks.data.RequestType.MediaRequest
import com.google.android.horologist.networks.data.RequestType.MediaRequest.MediaRequestType.Download

/**
 * Implementation of app rules for network usage.  A way to implement logic such as
 * - Don't download large media items over BLE.
 * - Only use LTE for downloads if user enabled.
 * - Don't use metered LTE for logs and metrics.
 */
@ExperimentalHorologistApi
public interface NetworkingRules {
    /**
     * Is this request considered high bandwidth and should activate LTE or Wifi.
     */
    public fun isHighBandwidthRequest(requestType: RequestType): Boolean

    /**
     * Checks whether this request is allowed on the current network type.
     */
    public fun checkValidRequest(
        requestType: RequestType,
        currentNetworkInfo: NetworkInfo
    ): RequestCheck

    /**
     * Returns the preferred network for a request.
     *
     * Null means no suitable network.
     */
    public fun getPreferredNetwork(
        networks: Networks,
        requestType: RequestType
    ): NetworkStatus?

    /**
     * Lenient rules that allow most request types on any network but prefer
     * Wifi when available.
     */
    @ExperimentalHorologistApi
    public object Lenient : NetworkingRules {
        override fun isHighBandwidthRequest(requestType: RequestType): Boolean {
            return requestType is MediaRequest
        }

        override fun checkValidRequest(
            requestType: RequestType,
            currentNetworkInfo: NetworkInfo
        ): RequestCheck {
            return Allow
        }

        override fun getPreferredNetwork(
            networks: Networks,
            requestType: RequestType
        ): NetworkStatus? {
            val wifi = networks.networks.firstOrNull { it.networkInfo is Wifi }
            return wifi ?: networks.networks.firstOrNull()
        }
    }

    /**
     * Conservative rules that don't allow Streaming, and only allow Downloads
     * over high bandwidth networks.
     */
    @ExperimentalHorologistApi
    public object Conservative : NetworkingRules {
        override fun isHighBandwidthRequest(requestType: RequestType): Boolean {
            return requestType is MediaRequest
        }

        override fun checkValidRequest(
            requestType: RequestType,
            currentNetworkInfo: NetworkInfo
        ): RequestCheck {
            if (requestType is MediaRequest) {
                return when (requestType.type) {
                    Download -> {
                        // Only allow Downloads over Wifi
                        // BT will hog the limited bandwidth
                        // Cell may include charges and should be checked with user
                        if (currentNetworkInfo is Wifi) {
                            Allow
                        } else {
                            Fail("downloads only possible over Wifi")
                        }
                    }
                    MediaRequest.MediaRequestType.Stream -> {
                        // Only allow Stream over Wifi or BT
                        // BT may hog the limited bandwidth, but hopefully is small stream.
                        if (currentNetworkInfo is Wifi || currentNetworkInfo is Bluetooth) {
                            Allow
                        } else {
                            Fail("streaming only possible over Wifi or BT")
                        }
                    }
                    MediaRequest.MediaRequestType.Live -> {
                        // Only allow Live (continuous) Stream over BT
                        if (currentNetworkInfo is Bluetooth) {
                            Allow
                        } else {
                            Fail("live streams only possible over BT")
                        }
                    }
                }
            }

            return Allow
        }

        override fun getPreferredNetwork(
            networks: Networks,
            requestType: RequestType
        ): NetworkStatus? {
            val wifi = networks.networks.firstOrNull { it.networkInfo is Wifi }

            if (wifi != null) return wifi

            val cell = networks.networks.firstOrNull { it.networkInfo is Cellular }
            val ble = networks.networks.firstOrNull { it.networkInfo is Bluetooth }

            return if (requestType is MediaRequest) {
                if (requestType.type == Download) {
                    null
                } else {
                    ble
                }
            } else {
                ble ?: cell
            }
        }
    }
}
