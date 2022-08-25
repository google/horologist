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

package com.google.android.horologist.networks.okhttp

import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import com.google.android.horologist.networks.data.RequestType
import com.google.android.horologist.networks.okhttp.RequestTypeHolder.Companion.requestType
import com.google.android.horologist.networks.rules.NetworkingRulesEngine
import com.google.android.horologist.networks.rules.NoSuitableNetwork
import com.google.android.horologist.networks.status.HighBandwidthRequester
import okhttp3.Interceptor
import okhttp3.Response

@ExperimentalHorologistNetworksApi
public class NetworkEstablishingInterceptor(
    private val networkingRulesEngine: NetworkingRulesEngine,
    private val highBandwidthRequester: HighBandwidthRequester
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val requestType = request.requestType

        return withValidNetwork(requestType) {
            chain.proceed(request)
        }
    }

    public fun <T> withValidNetwork(requestType: RequestType, block: () -> T): T {
        return if (networkingRulesEngine.isHighBandwidthRequest(requestType)) {
            val token = highBandwidthRequester.requestHighBandwidth(wait = true)
                ?: throw NoSuitableNetwork("Unable to request high bandwidth network")

            token.use {
                block()
            }
        } else {
            block()
        }
    }
}
