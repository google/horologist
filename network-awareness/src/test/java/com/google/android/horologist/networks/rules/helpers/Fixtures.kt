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

@file:OptIn(ExperimentalHorologistNetworksApi::class)

package com.google.android.horologist.networks.rules.helpers

import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import com.google.android.horologist.networks.data.NetworkStatus
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.data.Networks
import com.google.android.horologist.networks.data.Status

object Fixtures {
    fun networks(default: NetworkStatus?, vararg rest: NetworkStatus): Networks {
        return Networks(default, listOfNotNull(default, *rest))
    }

    val wifi = NetworkStatus(
        id = "wlan0",
        status = Status.Available,
        type = NetworkType.Wifi("wifi", null),
        addresses = listOf(),
        capabilities = null,
        linkProperties = null,
        bindSocket = {}
    )

    val bt = NetworkStatus(
        id = "bt",
        status = Status.Available,
        type = NetworkType.Bluetooth("bt"),
        addresses = listOf(),
        capabilities = null,
        linkProperties = null,
        bindSocket = {}
    )

    val cell = NetworkStatus(
        id = "cell",
        status = Status.Available,
        type = NetworkType.Cellular("cell", false),
        addresses = listOf(),
        capabilities = null,
        linkProperties = null,
        bindSocket = {}
    )
}
