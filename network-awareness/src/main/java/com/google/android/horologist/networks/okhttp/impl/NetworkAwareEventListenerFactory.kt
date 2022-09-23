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

package com.google.android.horologist.networks.okhttp.impl

import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import com.google.android.horologist.networks.data.DataRequestRepository
import com.google.android.horologist.networks.okhttp.highBandwidthConnectionLease
import com.google.android.horologist.networks.rules.NetworkingRulesEngine
import com.google.android.horologist.networks.status.NetworkRepository
import okhttp3.Call
import okhttp3.EventListener
import java.io.IOException

/**
 * Internal [EventListener] that logs requests as well as closing High Bandwidth Requests.
 */
@ExperimentalHorologistNetworksApi
public class NetworkAwareEventListenerFactory(
    networkingRulesEngine: NetworkingRulesEngine,
    networkRepository: NetworkRepository,
    private val delegateEventListenerFactory: EventListener.Factory,
    dataRequestRepository: DataRequestRepository? = null
) : NetworkLoggingEventListenerFactory(
    networkingRulesEngine.logger,
    networkRepository,
    delegateEventListenerFactory,
    dataRequestRepository
) {
    override fun create(call: Call): EventListener = Listener(
        delegateEventListenerFactory.create(call)
    )

    private inner class Listener(
        delegate: EventListener
    ) : NetworkLoggingEventListenerFactory.Listener(delegate) {
        override fun callEnd(call: Call) {
            super.callEnd(call)

            call.request().highBandwidthConnectionLease?.close()
        }

        override fun callFailed(call: Call, ioe: IOException) {
            super.callFailed(call, ioe)

            call.request().highBandwidthConnectionLease?.close()
        }
    }
}
