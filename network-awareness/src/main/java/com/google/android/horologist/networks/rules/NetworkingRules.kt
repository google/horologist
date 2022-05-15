/*
 * Copyright 2022 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.horologist.networks.rules

import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.data.RequestType
import com.google.android.horologist.networks.data.NetworkStatus
import com.google.android.horologist.networks.data.Networks

/**
 * Implementation of app rules for network usage.  A way to implement logic such as
 * - Don't download large media items over BLE.
 * - Only use LTE for downloads if user enabled.
 * - Don't use metered LTE for logs and metrics.
 */
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
        currentNetworkType: NetworkType
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
    public object Lenient : NetworkingRules {
        override fun isHighBandwidthRequest(requestType: RequestType): Boolean {
            return requestType is RequestType.MediaRequest
        }

        override fun checkValidRequest(
            requestType: RequestType,
            currentNetworkType: NetworkType
        ): RequestCheck {
            return Allow
        }

        override fun getPreferredNetwork(
            networks: Networks,
            requestType: RequestType
        ): NetworkStatus? {
            return networks.networks.firstOrNull { it.type is NetworkType.Wifi }
        }
    }

    /**
     * Conservative rules that don't allow Streaming, and only allow Downloads
     * over high bandwidth networks.
     */
    public object Conservative : NetworkingRules {
        override fun isHighBandwidthRequest(requestType: RequestType): Boolean {
            return requestType is RequestType.MediaRequest
        }

        override fun checkValidRequest(
            requestType: RequestType,
            currentNetworkType: NetworkType
        ): RequestCheck {
            if (requestType is RequestType.MediaRequest) {
                return if (requestType.type == RequestType.MediaRequest.MediaRequestType.Download) {
                    if (currentNetworkType is NetworkType.Wifi || currentNetworkType is NetworkType.Cellular) {
                        Allow
                    } else {
                        Fail("downloads only possible over Wifi/LTE")
                    }
                } else {
                    Fail("streaming disabled")
                }
            }

            return Allow
        }

        override fun getPreferredNetwork(
            networks: Networks,
            requestType: RequestType
        ): NetworkStatus? {
            val cell = networks.networks.firstOrNull { it.type is NetworkType.Cellular }
            val ble = networks.networks.firstOrNull { it.type is NetworkType.Bluetooth }

            return networks.networks.firstOrNull { it.type is NetworkType.Wifi }
                ?: if (requestType is RequestType.MediaRequest) {
                    cell
                } else {
                    ble
                }
        }
    }
}
