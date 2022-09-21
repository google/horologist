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

package com.google.android.horologist.mediasample.di.config

import com.google.android.horologist.networks.data.NetworkInfo
import com.google.android.horologist.networks.data.NetworkStatus
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.data.Networks
import com.google.android.horologist.networks.data.RequestType
import com.google.android.horologist.networks.data.RequestType.MediaRequest.Companion.DownloadRequest
import com.google.android.horologist.networks.data.RequestType.MediaRequest.Companion.LiveRequest
import com.google.android.horologist.networks.data.RequestType.UnknownRequest
import com.google.android.horologist.networks.rules.Allow
import com.google.android.horologist.networks.rules.Fail
import com.google.android.horologist.networks.rules.NetworkingRules
import com.google.android.horologist.networks.rules.RequestCheck

/**
 * Custom networking rules for Uamp.
 */
object UampNetworkingRules : NetworkingRules {
    override fun isHighBandwidthRequest(requestType: RequestType): Boolean {
        // For testing purposes fail if we get unknown requests
        check(requestType != UnknownRequest)

        return requestType is RequestType.MediaRequest || requestType is RequestType.ImageRequest
    }

    override fun checkValidRequest(
        requestType: RequestType,
        currentNetworkInfo: NetworkInfo
    ): RequestCheck {
        return if (requestType == DownloadRequest && currentNetworkInfo.type == NetworkType.BT) {
            Fail("Media Downloads not allowed over BT")
        } else {
            Allow
        }
    }

    override fun getPreferredNetwork(
        networks: Networks,
        requestType: RequestType
    ): NetworkStatus? {
        if (requestType is RequestType.MediaRequest) {
            return getPreferredNetworkForMedia(networks, requestType)
        } else if (requestType is RequestType.ImageRequest) {
            // arbitrary, mainly for testing
            return networks.networks.prefer(NetworkType.BT)
        } else {
            return networks.networks.prefer(NetworkType.Wifi)
        }
    }

    private fun getPreferredNetworkForMedia(
        networks: Networks,
        requestType: RequestType
    ): NetworkStatus? {
        val wifi = networks.networks.firstOrNull { it.networkInfo is NetworkInfo.Wifi }

        // Always prefer Wifi if active
        if (wifi != null) {
            return wifi
        }

        return when (requestType) {
            DownloadRequest -> {
                // For downloads force LTE as the backup to Wifi, to avoid slow downloads.
                networks.networks.firstOrNull {
                    it.networkInfo.type == NetworkType.Cell
                }
            }
            LiveRequest -> {
                // For live streaming, assume a low bandwidth and use power efficient BT
                networks.networks.firstOrNull {
                    it.networkInfo.type == NetworkType.BT
                }
            }
            else -> networks.networks.firstOrNull()
        }
    }
}

private fun List<NetworkStatus>.prefer(type: NetworkType): NetworkStatus? {
    return firstOrNull { it.networkInfo.type == type } ?: firstOrNull()
}
