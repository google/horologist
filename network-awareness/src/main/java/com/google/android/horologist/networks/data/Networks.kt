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

package com.google.android.horologist.networks.data

import java.net.InetAddress

public data class Networks(val activeNetwork: NetworkStatus?, val networks: List<NetworkStatus>) {
    public fun findNetworkByAddress(localSocketAddress: InetAddress): NetworkStatus? {
        return networks.find { networkStatus ->
            networkStatus.addresses.find {
                it == localSocketAddress
            } != null
        }
    }

    public val status: Status
        get() = networks.firstOrNull { it.status == Status.Available }?.status ?: Status.Lost
}
