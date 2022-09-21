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
import java.time.Instant

@ExperimentalHorologistNetworksApi
internal data class AggregatedRequests(
    val wifi: Int = 0,
    val cell: Int = 0,
    val wifiRequestedAt: Instant? = null,
    val cellRequestedAt: Instant? = null,
) {
    init {
        check(wifi >= 0 && cell >= 0)
    }

    val isNonZero: Boolean
        get() = wifi > 0 || cell > 0

    operator fun plus(request: HighBandwidthRequest): AggregatedRequests {
        return update(if (request.wifi) 1 else 0, if (request.cell) 1 else 0)
    }

    private fun update(
        additionalWifi: Int,
        additionalCell: Int
    ): AggregatedRequests {
        val newWifi = wifi + additionalWifi
        val newCell = cell + additionalCell

        val newWifiRequestedAt = when {
            newWifi == 0 && wifi > 0 -> null
            wifi == 0 && newWifi > 0 -> Instant.now()
            else -> wifiRequestedAt
        }
        val newCellRequestedAt = when {
            newCell == 0 && cell > 0 -> null
            cell == 0 && newCell > 0 -> Instant.now()
            else -> cellRequestedAt
        }

        return copy(
            wifi = newWifi,
            cell = newCell,
            wifiRequestedAt = newWifiRequestedAt,
            cellRequestedAt = newCellRequestedAt
        )
    }

    operator fun minus(request: HighBandwidthRequest): AggregatedRequests {
        return update(if (request.wifi) -1 else 0, if (request.cell) -1 else 0)
    }

    fun toRequest(): HighBandwidthRequest {
        return HighBandwidthRequest(wifi > 0, cell > 0)
    }
}
