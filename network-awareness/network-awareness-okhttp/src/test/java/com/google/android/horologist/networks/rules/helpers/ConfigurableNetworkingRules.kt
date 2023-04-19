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

import com.google.android.horologist.networks.data.NetworkInfo
import com.google.android.horologist.networks.data.NetworkStatus
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.data.Networks
import com.google.android.horologist.networks.data.RequestType
import com.google.android.horologist.networks.rules.Allow
import com.google.android.horologist.networks.rules.Fail
import com.google.android.horologist.networks.rules.NetworkingRules
import com.google.android.horologist.networks.rules.RequestCheck

class ConfigurableNetworkingRules : NetworkingRules {
    val highBandwidthTypes: MutableMap<RequestType, Boolean> = mutableMapOf()
    val validRequests: MutableMap<Pair<RequestType, NetworkType>, Boolean> = mutableMapOf()
    val preferredNetworks: MutableMap<RequestType, NetworkType> = mutableMapOf()

    override fun isHighBandwidthRequest(requestType: RequestType): Boolean {
        val isHighBandwidth = highBandwidthTypes[requestType]
        return isHighBandwidth ?: false
    }

    override fun checkValidRequest(
        requestType: RequestType,
        currentNetworkInfo: NetworkInfo
    ): RequestCheck {
        val allowed = validRequests[Pair(requestType, currentNetworkInfo.type)] ?: true
        val requestCheck =
            if (allowed) Allow else Fail("not allowed $requestType on $currentNetworkInfo")
        return requestCheck
    }

    override fun getPreferredNetwork(
        networks: Networks,
        requestType: RequestType
    ): NetworkStatus? {
        val preferredNetwork = preferredNetworks[requestType]
        return if (preferredNetwork != null) {
            val found = networks.networks.find { it.networkInfo.type == preferredNetwork }
            found ?: networks.activeNetwork
        } else {
            networks.activeNetwork
        }
    }
}
