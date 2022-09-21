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

import com.google.android.horologist.networks.data.NetworkType

internal data class AggregatedNetworkCount(
    val wifi: Int = 0,
    val cell: Int = 0
) {
    operator fun plus(networkType: NetworkType?): AggregatedNetworkCount {
        return when (networkType) {
            NetworkType.Wifi -> copy(wifi = wifi + 1)
            NetworkType.Cell -> copy(cell = cell + 1)
            else -> this
        }
    }

    operator fun minus(networkType: NetworkType?): AggregatedNetworkCount {
        return when (networkType) {
            NetworkType.Wifi -> copy(wifi = wifi - 1)
            NetworkType.Cell -> copy(cell = cell - 1)
            else -> this
        }
    }

    fun toNetworkType(): NetworkType? {
        return if (cell > 0) {
            NetworkType.Cell
        } else if (wifi > 0) {
            NetworkType.Wifi
        } else {
            null
        }
    }
}