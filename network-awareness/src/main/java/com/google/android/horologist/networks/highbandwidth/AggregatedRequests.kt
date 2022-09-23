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

@ExperimentalHorologistNetworksApi
internal data class AggregatedRequests(
    val wifi: Int = 0,
    val cell: Int = 0
) {
    init {
        check(wifi >= 0 && cell >= 0)
    }

    val isNonZero: Boolean
        get() = wifi > 0 || cell > 0

    operator fun plus(request: HighBandwidthRequest): AggregatedRequests {
        return copy(
            wifi = wifi + if (request.type.wifi) 1 else 0,
            cell = cell + if (request.type.cell) 1 else 0
        )
    }

    operator fun minus(request: HighBandwidthRequest): AggregatedRequests {
        return copy(
            wifi = wifi - if (request.type.wifi) 1 else 0,
            cell = cell - if (request.type.cell) 1 else 0
        )
    }

    fun toRequest(): HighBandwidthRequest {
        val type = when {
            wifi > 0 && cell > 0 -> HighBandwidthRequest.Type.All
            wifi > 0 -> HighBandwidthRequest.Type.WifiOnly
            cell > 0 -> HighBandwidthRequest.Type.CellOnly
            else -> throw IllegalStateException("must be cell or wifi at least")
        }

        return HighBandwidthRequest(type)
    }
}
