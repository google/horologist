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

import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import com.google.android.horologist.networks.data.NetworkInfo
import com.google.android.horologist.networks.data.NetworkStatus
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.data.RequestType
import com.google.android.horologist.networks.logging.NetworkStatusLogger
import com.google.android.horologist.networks.status.NetworkRepository

/**
 * Networking Rules that bridges between app specific rules
 * and specific network actions, like opening a network socket.
 */
@ExperimentalHorologistNetworksApi
public class NetworkingRulesEngine(
    internal val networkRepository: NetworkRepository,
    internal val logger: NetworkStatusLogger = NetworkStatusLogger.Logging,
    private val networkingRules: NetworkingRules = NetworkingRules.Lenient
) {
    public fun preferredNetwork(requestType: RequestType): NetworkStatus? {
        val networks = networkRepository.networkStatus.value

        return networkingRules.getPreferredNetwork(networks, requestType)
    }

    public fun checkValidRequest(
        requestType: RequestType,
        currentNetworkInfo: NetworkInfo?
    ): RequestCheck {
        return networkingRules.checkValidRequest(requestType, currentNetworkInfo ?: NetworkInfo.Unknown("unknown"))
    }

    public fun isHighBandwidthRequest(requestType: RequestType): Boolean {
        return networkingRules.isHighBandwidthRequest(requestType)
    }

    public fun supportedTypes(requestType: RequestType): List<NetworkType> {
        return buildList {
            if (checkValidRequest(requestType, NetworkInfo.Wifi("test")) is Allow) {
                add(NetworkType.Wifi)
            }
            if (checkValidRequest(requestType, NetworkInfo.Cellular("test")) is Allow) {
                add(NetworkType.Cell)
            }
        }
    }
}
