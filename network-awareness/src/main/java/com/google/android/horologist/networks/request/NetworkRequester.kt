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

import android.net.ConnectivityManager
import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.highbandwidth.HighBandwidthRequest
import com.google.android.horologist.networks.highbandwidth.HighBandwithConnectionLease
import kotlinx.coroutines.flow.StateFlow

/**
 * Slightly coroutine aware API for [ConnectivityManager.requestNetwork].
 *
 * Assumes a single owner, and allows setting and clearing the [HighBandwidthRequest]
 * and will returns a StateFlow of the [pinnedNetwork].
 */
@ExperimentalHorologistNetworksApi
public interface NetworkRequester {
    /**
     * Make a request for a high bandwidth network, with request details provided in
     * `request`. Returns a cancellation Token, that also allows waiting for a connection.
     */
    public fun requestHighBandwidthNetwork(request: HighBandwidthRequest): NetworkLease
}
