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
import com.google.android.horologist.networks.data.Networks
import com.google.android.horologist.networks.data.RequestType
import com.google.android.horologist.networks.rules.Allow
import com.google.android.horologist.networks.rules.NetworkingRules
import com.google.android.horologist.networks.rules.RequestCheck

/**
 * Custom networking rules for Uamp.
 */
object UampNetworkingRules : NetworkingRules {
    override fun isHighBandwidthRequest(requestType: RequestType): Boolean {
        return requestType is RequestType.MediaRequest
    }

    override fun checkValidRequest(
        requestType: RequestType,
        currentNetworkInfo: NetworkInfo
    ): RequestCheck {
        return Allow
    }

    override fun getPreferredNetwork(
        networks: Networks,
        requestType: RequestType
    ): NetworkStatus? {
        val wifi = networks.networks.firstOrNull { it.networkInfo is NetworkInfo.Wifi }
        return wifi ?: networks.networks.firstOrNull()
    }
}
