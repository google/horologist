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

import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import com.google.android.horologist.networks.data.NetworkType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Mediator for High Bandwidth requests, such as Cell or Wifi.
 *
 * By default Wear will use BT connection when available, even when temporarily connecting
 * via Wifi or Cell would be more power efficient and performance.
 */
@ExperimentalHorologistNetworksApi
public interface HighBandwidthNetworkMediator {
    /**
     * The current resulting network specifically from high bandwidth requests.
     */
    public val pinned: Flow<Set<NetworkType>>

    /**
     * Make a request for a high bandwidth network, with request details provided in
     * `request`. Returns a cancellation Token, that also allows waiting for a connection.
     */
    public fun requestHighBandwidthNetwork(request: HighBandwidthRequest): HighBandwidthConnectionLease
}
