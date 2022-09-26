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

/**
 * Slightly coroutine aware API for [ConnectivityManager.requestNetwork].
 *
 * Subscription with a listener is replaced with a closable lease, with the current network.
 */
@ExperimentalHorologistNetworksApi
public interface NetworkRequester {
    /**
     * Make a request for a high bandwidth network, with request details provided in
     * `request`. Returns a cancellation Token, that also allows waiting for a connection.
     */
    public fun requestHighBandwidthNetwork(request: HighBandwidthRequest): NetworkLease
}
