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

package com.google.android.horologist.networks.data

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities

/**
 * Return the id of the Network, by exploration this is the
 * [Network.toString] method.
 */
internal val Network.id
    get() = this.toString()

/**
 * Return the type of the just discovered [Network] via
 * [ConnectivityManager.getNetworkCapabilities] tranport types.
 */
internal fun ConnectivityManager.networkType(network: Network): NetworkType? {
    val networkCapabilities = getNetworkCapabilities(network) ?: return null

    val wifi = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    val cell = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)

    val type = when {
        wifi -> NetworkType.Wifi
        cell -> NetworkType.Cell
        else -> null
    }
    return type
}
