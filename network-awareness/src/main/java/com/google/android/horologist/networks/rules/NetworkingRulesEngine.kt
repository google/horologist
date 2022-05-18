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

import com.google.android.horologist.networks.data.NetworkStatus
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.data.RequestType
import com.google.android.horologist.networks.logging.NetworkStatusLogger
import com.google.android.horologist.networks.status.NetworkRepository
import java.net.InetSocketAddress

/**
 * Networking Rules that bridges between app specific rules
 * and specific network actions, like opening a network socket.
 */
public class NetworkingRulesEngine(
    internal val networkRepository: NetworkRepository,
    internal val logger: NetworkStatusLogger = NetworkStatusLogger.Logging,
    private val networkingRules: NetworkingRules = NetworkingRules.Conservative
) {
    public fun preferredNetwork(requestType: RequestType): NetworkStatus? {
        val networks = networkRepository.networkStatus.value

        return networkingRules.getPreferredNetwork(networks, requestType)
    }

    public fun checkValidRequest(
        requestType: RequestType,
        currentNetworkType: NetworkType?
    ): RequestCheck {
        return networkingRules.checkValidRequest(requestType, currentNetworkType ?: NetworkType.Unknown("unknown"))
    }

    public fun isHighBandwidthRequest(requestType: RequestType): Boolean {
        return networkingRules.isHighBandwidthRequest(requestType)
    }

    @Suppress("UNUSED_PARAMETER")
    public fun reportConnectionFailure(inetSocketAddress: InetSocketAddress, networkType: NetworkType?) {
        // TODO check for no internet on BLE and other scenarios
    }
}
